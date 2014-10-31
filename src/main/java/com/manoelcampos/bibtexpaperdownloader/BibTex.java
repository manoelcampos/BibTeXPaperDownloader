package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.BibTeXParser;
import org.jbibtex.CharacterFilterReader;
import org.jbibtex.Key;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.StringValue;
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
    private String repositoryName;
    private String bibFileName;
        
    /**
     * Instancia um objeto para fazer o parse de um arquivo bibtex
     * e já tenta realizar o parse dele.
     * @param bibFileName Nome do arquivo bibtex a ser feito o parse.
     * @param repositoryName Nome do repositório de onde os papers do bibtex serão baixados.
     * @throws java.io.FileNotFoundException
     * @throws org.jbibtex.ParseException
     * @see com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory
     */
    public BibTex(String bibFileName, String repositoryName) throws FileNotFoundException, ParseException {
        this.repositoryName = repositoryName;
        this.bibFileName = bibFileName;
        try{
            reader = new FileReader(bibFileName);
        } catch(FileNotFoundException e){
            throw new FileNotFoundException("Arquivo bibtex " + bibFileName + " não encontrado.");
        }
        
        try(CharacterFilterReader filterReader = new CharacterFilterReader(reader)){
            parser = this.createBibTexParser();
            database = parser.parse(filterReader);
        } catch(Exception e){
            throw new ParseException(
                 "Não foi possível fazer o parse do arquivo bib "+bibFileName+
                 ". Provavelmente o arquivo é inválido\n");        
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
     * @return the reader
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * @param reader the reader to set
     */
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * @return the database
     */
    public BibTeXDatabase getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(BibTeXDatabase database) {
        this.database = database;
    }

    /**
     * @return the parser
     */
    public BibTeXParser getParser() {
        return parser;
    }

    /**
     * @param parser the parser to set
     */
    public void setParser(BibTeXParser parser) {
        this.parser = parser;
    }

    /**
     * Converte para string um valor obtido de uma entrada de um item
     * em um arquivo bibtex.
     * @param value Valor de uma entrada bibtex a ser convertido para string.
     * @return Retorna o valor da entrada como string
     * @throws org.jbibtex.ParseException
     * @throws java.io.UnsupportedEncodingException
     */
    public String keyValueToStr(Value value) throws ParseException, UnsupportedEncodingException {
        if (value == null) {
            return "";
        }
        
        //Convertendo uma string latex para uma string regular (sem caracteres especiais do latex)
        String str = value.toUserString();
        //str = StringEscapeUtils.unescapeHtml4(str);
        //str = StringEscapeUtils.unescapeXml(str);
        //str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
        if(str.indexOf('\\') > -1 || str.indexOf('{') > -1){
            LaTeXParser latexParser = new LaTeXParser();
            List<LaTeXObject> latexObjects = latexParser.parse(str);
            LaTeXPrinter latexPrinter = new LaTeXPrinter();
            str = latexPrinter.print(latexObjects);
        }
        
        return str;
    }
    
    /**
     * Validates a file name, removing not allowed characters
     * and returning a valid file name.
     * @param fileName The file name to be validated.
     * @return The valid file name.
     */
    public String validateFileName(String fileName){
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", " ");
    }

    /**
     * Processa um determinado arquivo bibtex, baixando
     * os papers especificados nele direto da sua base de dados online.
     *
     * @param downloadDir Diretório de destino para baixar os papers.
     * @throws IOException Exceção lançada se não for possível salvar o paper baixado
     * (devido a um caminho inválido ou falta de acesso de escrita).
     * @throws ParseException Exceção lançcada se houver algum erro no arquivo bib
     * que impossibilite o processamento do mesmo (se o arquivo for inválido).
     * @throws InvalidPaperIdException Se o id de um paper a ser baixado é inválido.
     * Por exemplo, papers do IEEE tem id's inteiros.
     */
    public void processBibFile(String downloadDir) throws IOException, ParseException, InvalidPaperIdException, ClassNotFoundException, InstantiationException {
        PaperRepository repository = PaperRepositoryFactory.getInstance(getRepositoryName());
        if (!downloadDir.equals("") && downloadDir.charAt(downloadDir.length() - 1) != File.separatorChar) {
            downloadDir += File.separatorChar;
        }
        Map<Key, BibTeXEntry> entryMap = this.getDatabase().getEntries();
        System.out.println("\nParse-----------------------");
        Collection<BibTeXEntry> entries = entryMap.values();
        int i = 0;
        String paperTitle, fileName;
        String url;
        Key paperId;
        String pdfFileFormat =
                "%s%0"+String.valueOf(entries.size()).length()+"d-%s.pdf";
        for (BibTeXEntry entry : entries) {
            paperId = entry.getKey();
            paperTitle = this.keyValueToStr(entry.getField(BibTeXEntry.KEY_TITLE));
            System.out.printf("%d - PaperID: %s\n", ++i, paperId);
            System.out.println("\tTitle: " + paperTitle);
            System.out.print("\tYear:  " + this.keyValueToStr(entry.getField(BibTeXEntry.KEY_YEAR)));
            System.out.println("\tDOI:   " + this.keyValueToStr(entry.getField(BibTeXEntry.KEY_DOI)));
            try {
                url = repository.getPaperDownloadUrl(paperId.getValue(), paperTitle);
                if(url.equals("")){
                    throw new PaperNotAvailableForDownloadException();
                }
                fileName = String.format(
                    pdfFileFormat, downloadDir, i, validateFileName(paperTitle));
                if(HttpUtils.downloadFile(url, fileName)){
                    setFieldValue(entry, "file", fileName);
                }
            } catch (PaperNotAvailableForDownloadException ex) {
                System.out.println("Paper "+paperId +". "+ex.getLocalizedMessage());
            }
        }
        System.out.println("Bibtex update with paper PDF path: " + bibFileName);
        this.save(bibFileName);
    }
    
    /**
     * Save the changes in the parsed bibtex to a file
     * @param fileName The name of the file to be saved.
     * @return Returns true if the file was successfully saved
     * @throws java.io.FileNotFoundException 
     * @throws java.io.IOException 
     */
    public boolean save(String fileName) throws FileNotFoundException, IOException{
        try(FileWriter writer = new FileWriter(fileName)){
          BibTeXFormatter bibtexFormatter = new org.jbibtex.BibTeXFormatter();
          bibtexFormatter.format(database, writer);
        }
        return true;
    }
    
    /**
     * Set a value to a bibtex key into a specific bibtex entry.
     * @param entry Bibtex entry to set a value for a specific key
     * @param keyName Name of the key to be set
     * @param valueStr Value to be set on the key
     */
    private void setFieldValue(BibTeXEntry entry, String keyName, String valueStr) {
        Key key = new Key(keyName);
        Value bibValue = entry.getField(key);
        if(bibValue != null){
            entry.removeField(key);
        }
        entry.addField(key, 
                new StringValue(valueStr, StringValue.Style.BRACED));
    }       

    /**
     * @return the repositoryName
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * @param repositoryName the repositoryName to set
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
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
}
