package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatterBuilder;
import org.jbibtex.BibTeXParser;
import org.jbibtex.CharacterFilterReader;
import org.jbibtex.ParseException;

/**
 * Download papers cataloged into a BibTex file from a given official paper repository on the Web
 * (such as IEEE or Science Direct)
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class BibTexPapersDownloader {
    private BibTeXParser parser;
    private Reader reader;
    private BibTeXDatabase database;
    private String bibFileName;
    private String downloadDir;
    private final PaperRepository repository;
        
    /**
     * 
     * @param bibFileNameContainingThePapersToDownload Name of BibTeX file to be parsed.
     * @param classNameOfRepositoryWhereToDownloadThePapers Name of the class of the web repository
     * where the papers in the bibtex file have to be downloaded.
     * For instance, IEEE, ACM, Elsevier, etc.
     * @throws java.io.FileNotFoundException
     * @throws org.jbibtex.ParseException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @see com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory
     */
    public BibTexPapersDownloader(
            final String bibFileNameContainingThePapersToDownload, 
            final String classNameOfRepositoryWhereToDownloadThePapers) throws FileNotFoundException, ParseException, ClassNotFoundException, InstantiationException {
        this.repository = 
                PaperRepositoryFactory.getInstance(
                        classNameOfRepositoryWhereToDownloadThePapers);
        this.setBibFileNameAndCreateBibFileReader(bibFileNameContainingThePapersToDownload);        
        this.createBibTexParserAndParseIt(bibFileNameContainingThePapersToDownload);
    }

    private void createBibTexParserAndParseIt(final String bibFileName) throws ParseException {
        try (final CharacterFilterReader filterReader = new CharacterFilterReader(reader)) {
            parser = this.getBibTexParserInstance();
            database = parser.parse(filterReader);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new ParseException(
                    "It was not possible to pase the bibtex file " + bibFileName + ". Maybe the file is invalid\n");        
        }
    }

    private void setBibFileNameAndCreateBibFileReader(final String bibFileName) throws FileNotFoundException {
        this.bibFileName = bibFileName;
        try {
            reader = new FileReader(bibFileName);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Bibtex file " + bibFileName + " not found.");
        }
    }

    private BibTeXParser getBibTexParserInstance() throws ParseException {
        return new BibTeXParser() {
            @Override
            public void checkStringResolution(org.jbibtex.Key key, org.jbibtex.BibTeXString string) {
                if(string == null){
                    System.err.println("Unresolved string: \"" + key.getValue() + "\"");
                }
            }

            @Override
            public void checkCrossReferenceResolution(org.jbibtex.Key key, org.jbibtex.BibTeXEntry entry) {
                if(entry == null){
                    System.err.println("Unresolved cross-reference: \"" + key.getValue() + "\"");
                }
            }
        };
    }      

    /**
     *
     * @throws IOException 
     * @throws ParseException 
     * @throws InvalidPaperIdException
     */
    public void downloadAllPapers() throws IOException, ParseException, InvalidPaperIdException {
        System.out.printf(
                "\nDownloading %d papers from %s respository to %s\n", 
                getEntriesCollection().size(), repository, downloadDir);
        System.out.printf("Origin BibTeX file: %s\n\n", bibFileName);
        int i = 0;
        for (BibTeXEntry bibEntry : getEntriesCollection()) {
            i++;
            Paper paper = repository.getPaperInstance(this, bibEntry);
            paper.setOrderInsideBibTexFile(i);
            try {
                System.out.println(paper);
                paper.downloadAndIfSuccessfulSetLocalFileNameAndUrl();
            } catch (PaperNotAvailableForDownloadException ex) {
                System.out.println("Paper " + paper.getTitle() + ". " + ex.getLocalizedMessage());
            }
        }
        this.saveChangesInBibTexFile();
    }

    private Collection<BibTeXEntry> getEntriesCollection() {
        return database.getEntries().values();
    }
    
    public int numberOfPapers(){
        return getEntriesCollection().size();
    }

    /**
     * Save the changes in the parsed bibtex to a file
     * @return Returns true if the file was successfully saved
     * @throws java.io.FileNotFoundException 
     * @throws java.io.IOException 
     */
    public boolean saveChangesInBibTexFile() throws FileNotFoundException, IOException{
        try(Writer writer = new FileWriter(bibFileName)){
          BibTeXFormatterBuilder builder = new BibTeXFormatterBuilder();
          database.accept(builder.buildConciseFormatter(writer));
        }
        System.out.printf("\nBibtex updated to include paper's PDF paths\n\n");
        
        return true;
    }
    

    /**
     * @return the bibFileName
     */
    public String getBibFileName() {
        return bibFileName;
    }

    /**
     * @param bibFileName the bibFileName to set
     */
    public void setBibFileName(final String bibFileName) {
        this.bibFileName = bibFileName;
    }

    /**
     * @return the downloadDir
     */
    public String getDownloadDir() {
        return downloadDir;
    }

    /**
     * @param downloadDir the downloadDir to set
     */
    public void setDownloadDir(final String downloadDir) {
        this.downloadDir = FileSystemUtils.insertTrailBackslach(downloadDir);
    }
}
