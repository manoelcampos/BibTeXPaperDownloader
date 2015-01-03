package com.manoelcampos.bibtexpaperdownloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbibtex.ParseException;

/**
 * Aplicação de linha de comando para fazer o downloadPapersInBibFile de arquivos bibtex (*.bib)
 e baixar os papers definidos nele direto de suas respectivas bases de dados online.
 * Atualmente só é possível baixar papers da base do IEEE
 * e mesmo assim, alguns papers não conseguem ser baixados.
 * 
 * A aplicação só funciona se o usuário já tiver livre acesso
 * aos papers nas suas respectivas bases de dados.
 * Logo, não é objetivo da mesma burlar qualquer mecanismo de proteção
 * dos direitos autorais. Desta a forma, a aplicação não tenta driblar
 * qualquer mecanismo de proteção do acesso aos papers,
 * ela simplesmente automatiza o processo de download dos mesmos.
 * 
 * @author manoelcampos
 */
public class Main {
   private String bibFileName;
   private String downloadDir;
   private String repositoryName = "IEEE";
    
   public static void showUsage(){
       System.out.println("Usage:");
       System.out.println("\tapp bibFileName [paperOutputDir] [database]");
       System.out.println("The default value for paperOutputDir is /tmp");
       System.out.println("Database can be: IEEE (default value)");
   }
   
   public Main(String args[]) throws ParseException, ClassNotFoundException, InstantiationException, IOException, FileNotFoundException, InvalidPaperIdException {
        getComandLineParameters(args);
        downloadPapersInBibFile();   
   }
   
   /**
     * Executa a aplicação para processar o arquivo bib e baixar os papers.
     * @param args Recebe como parâmetros de linha de comando:
     *   0 - Nome do arquivo bib a ser processado.
     *   1 - Diretório de destino para baixar os papers.
     *   Se omitido assume que deve-se baixar no diretório atual.
     *   2 - Nome da base de dados que contém os papers indicados no arquivo bib 
     *   (se omitido assume que a base é IEEE).
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
        BibTex bibtex = new BibTex(bibFileName, repositoryName);
        bibtex.setDownloadDir(downloadDir);
        bibtex.downloadListOfPapers();
    }

    private void getComandLineParameters(String[] args) throws IllegalArgumentException {
        bibFileName = getCommandLineParam(args, 0, "");
        downloadDir = getCommandLineParam(args, 1, "/tmp/");
        repositoryName = getCommandLineParam(args, 2, repositoryName);
        if("".equals(bibFileName))
            throw new IllegalArgumentException("BibTex file name is a required command line parameter.");
    }
    
   /**
     * Obtém um parâmetro de uma posição especificada
     * dentro do vetor de parâmetros de linha de comando
     * recebidos pelo main.
     * @param args Vetor de parâmetros de linha de comando recebido pelo main.
     * @param i Posição a ser acessada dentro do vetor de parâmetros.
     * @param defaultValue Valor padrão a ser retornado caso o parâmetro
     * da posição i não exista.
     * @return Retorna o valor do parâmetro da posição i, caso exista.
     * Caso contrário, retorna o valor do parâmetro defaultValue.
     */
    private String getCommandLineParam(String args[], int i, String defaultValue){
        if(args.length > i){
            return args[i].trim();
        }
        return (defaultValue != null ? defaultValue : "");
    }    
}
