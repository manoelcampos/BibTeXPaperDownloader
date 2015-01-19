package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.BibTeXParser;
import org.jbibtex.CharacterFilterReader;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.ParseException;
import org.jbibtex.Value;

/**
 * Classe com funções para facilitar o parse de arquivos bibtex (*.bib)
 * @author manoelcampos
 */
public class BibTex {
    private BibTeXParser parser;
    private Reader reader;
    private BibTeXDatabase database;
    private String bibFileName;
    private String downloadDir;
    private final PaperRepository repository;
        
    /**
     * Instancia um objeto para fazer o parse de um arquivo bibtex
     * e já tenta realizar o parse dele.
     * @param bibFileName Nome do arquivo bibtex a ser feito o parse.
     * @param repositoryName
     * @throws java.io.FileNotFoundException
     * @throws org.jbibtex.ParseException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @see com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory
     */
    public BibTex(String bibFileName, String repositoryName) throws FileNotFoundException, ParseException, ClassNotFoundException, InstantiationException {
        this.repository = PaperRepositoryFactory.getInstance(repositoryName);
        this.setBibFileNameAndCreateBibFileReader(bibFileName);        
        this.createBibTexParserAndParseIt(bibFileName);
    }

    private void createBibTexParserAndParseIt(String bibFileName1) throws ParseException {
        try (final CharacterFilterReader filterReader = new CharacterFilterReader(reader)) {
            parser = this.createBibTexParser();
            database = parser.parse(filterReader);
        } catch (Exception e) {
            throw new ParseException("It was not possible to pase the bibtex file " + bibFileName1 + ". Maybe the file is invalid\n");        
        }
    }

    private void setBibFileNameAndCreateBibFileReader(String bibFileName1) throws FileNotFoundException {
        this.bibFileName = bibFileName1;
        try {
            reader = new FileReader(bibFileName1);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Bibtex file " + bibFileName1 + " not found.");
        }
    }

    /**
     * Instancia e retorna um objeto para fazer o parse de um arquivo bibtex (*.bib)
     * @return Retorna o objeto BibTexParser criado.
     */
    private BibTeXParser createBibTexParser() throws ParseException {
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
     * Processa um determinado arquivo bibtex, baixando
     * os papers especificados nele direto da sua base de dados online.
     *
     * @throws IOException Exceção lançada se não for possível salvar o paper baixado
     * (devido a um caminho inválido ou falta de acesso de escrita).
     * @throws ParseException Exceção lançcada se houver algum erro no arquivo bib
     * que impossibilite o processamento do mesmo (se o arquivo for inválido).
     * @throws InvalidPaperIdException Se o id de um paper a ser baixado é inválido.
     * Por exemplo, papers do IEEE tem id's inteiros.
     */
    public void downloadAllPapers() throws IOException, ParseException, InvalidPaperIdException {
        System.out.println("\nParse-----------------------");
        int i = 0;
        for (BibTeXEntry bibEntry : getEntriesCollection()) {
            i++;
            Paper paper = repository.getPaperInstance(this, bibEntry);
            paper.setOrderInsideBibTexFile(i);
            try {
                System.out.println(paper);
                paper.downloadAndIfSuccessfulSetLocalFileName();
            } catch (PaperNotAvailableForDownloadException ex) {
                System.out.println("Paper " + paper.getTitle() + ". " + ex.getLocalizedMessage());
            }
        }
        this.save();
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
    public boolean save() throws FileNotFoundException, IOException{
        try(FileWriter writer = new FileWriter(bibFileName)){
          BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
          bibtexFormatter.format(database, writer);
        }
        System.out.println("Bibtex updated to include paper's PDF paths: " + bibFileName);
        
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
    public void setBibFileName(String bibFileName) {
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
    public void setDownloadDir(String downloadDir) {
        this.downloadDir = FileSystemUtils.insertTrailBackslach(downloadDir);
    }
}
