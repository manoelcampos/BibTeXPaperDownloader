package com.manoelcampos.bibtexpaperdownloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbibtex.ParseException;

/**
 * Command line application to automate the download of papers, specified
 * into a bibtex file, from a given paper web repository
 * such as IEEE, ACM, Elsevier, etc.
 * 
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class Main {
   public static final String DEFAULT_DOWNLOAD_DIR = "/tmp/";
   public static final String DEFAULT_REPOSITORY = "IEEE";

   private String bibFileName;
   private String downloadDir;
   private String repositoryName = DEFAULT_REPOSITORY;
    
   public static void showUsage(){
       System.out.println("Usage:");
       System.out.println("\tjava -jar app_jar_file.jar BibTeXFileName [PapersDownloadDir] [RepositoryName]");
       System.out.println("\t\t- BibTeXFileName is the path of a BibTeX file "
            + "containing the papers to be downloaded from a specified respository");
       System.out.println("\t\t- PapersDownloadDir is the directory where to "
            + "download the papers (default value is "+DEFAULT_DOWNLOAD_DIR+")");
       System.out.println(
            "\t\t- RepositoryName is the name of the repository that hosts the papers to "
            + "be downloaded. Currently, only "+DEFAULT_REPOSITORY+" (the default value");
   }
   
   public Main(String args[]) throws ParseException, ClassNotFoundException, InstantiationException, IOException, FileNotFoundException, InvalidPaperIdException {
        getComandLineParameters(args);
        downloadPapersInBibFile();   
   }
   
   /**
     * Executes the command line application to parse the bibtex file
     * and download the papers.
     * 
     * @param args The command line arguments, in order:<br/>
     *   1º - Name of the bibtex file to be processed.<br/>
     *   2º - Destination directory where to download the papers.<br/>
     *   3º - Name of the repository where to download the papers.
     * @see Main#showUsage() 
     */
    public static void main(String args[]) {
        try {
            new Main(args);
        }catch(IllegalArgumentException e){
            showUsage();
        } catch (ParseException|InvalidPaperIdException|IOException|ClassNotFoundException|InstantiationException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }    

    private void downloadPapersInBibFile() throws FileNotFoundException, ParseException, ClassNotFoundException, InstantiationException, IOException, InvalidPaperIdException {
        BibTexPapersDownload bibtex = new BibTexPapersDownload(bibFileName, repositoryName);
        bibtex.setDownloadDir(downloadDir);
        bibtex.downloadAllPapers();
    }

    private void getComandLineParameters(String[] args) throws IllegalArgumentException {
        bibFileName = getCommandLineParam(args, 0, "");
        downloadDir = getCommandLineParam(args, 1, DEFAULT_DOWNLOAD_DIR);
        repositoryName = getCommandLineParam(args, 2, repositoryName);
        if("".equals(bibFileName))
            throw new IllegalArgumentException("BibTex file name is a required command line parameter.");
    }
    
   /**
     * Gets the value of a given command line parameter in the 
     * command line arguments array, handling exceptions
     * in the case where the parameter doesn't exist.
     * 
     * @param args Command line parameters array.
     * @param i Index of the desired param.
     * @param defaultValue Default value to be returned in case of the parameter
     * does not exist.
     * @return The parameter value of the default value (when the parameter does not exist)
     */
    private String getCommandLineParam(String args[], int i, String defaultValue){
        if(args.length > i){
            return args[i].trim();
        }
        return (defaultValue != null ? defaultValue : "");
    }    
}
