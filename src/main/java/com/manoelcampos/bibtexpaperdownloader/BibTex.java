package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepositoryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.CharacterFilterReader;
import org.jbibtex.Key;
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
     */
    public String keyValueToStr(Value value) {
        if (value == null) {
            return "";
        }
        return value.toUserString();
    }
    
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
        String paperTitle;
        String url;
        Key paperId;
        for (BibTeXEntry entry : entries) {
            paperId = entry.getKey();
            paperTitle = this.keyValueToStr(entry.getField(BibTeXEntry.KEY_TITLE));
            System.out.printf("%d - PaperID: %s\n", ++i, paperId);
            System.out.println("\tTitle: " + paperTitle);
            System.out.print("\tYear:  " + this.keyValueToStr(entry.getField(BibTeXEntry.KEY_YEAR)));
            System.out.println("\tDOI:   " + this.keyValueToStr(entry.getField(BibTeXEntry.KEY_DOI)));
            try {
                url = repository.getPaperDownloadUrl(paperId.getValue(), paperTitle);
                /*
                TODO: adicionar o caminho de cada arquivo baixado na entrada 
                do paper no arquivo bib, assim, ao importar o bib no mendeley
                ou outra ferramenta, ela já vai importar automaticamente o PDF.
                */
                HttpUtils.downloadFile(url, downloadDir + i +"-" +validateFileName(paperTitle) + ".pdf");
            } catch (PaperNotAvailableForDownloadException ex) {
                System.out.println("Paper "+paperId +". "+ex.getLocalizedMessage());
            }
        }
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
