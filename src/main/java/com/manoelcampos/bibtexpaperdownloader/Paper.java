package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.ParseException;
import org.jbibtex.StringValue;
import org.jbibtex.Value;

/**
 *
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class Paper {
    private Integer index = 0;
    private String id;
    private String title;
    private Integer year;
    private String doi;
    private String localFileName;

    private final BibTeXEntry bibTeXEntry;
    private final BibTexPapersDownloader bibtex;
    private final PaperRepository repository;
    
    public Paper(final PaperRepository sourceRepository, 
            final BibTexPapersDownloader bibtex, 
            final BibTeXEntry paperBibTeXEntry){
        this.repository = sourceRepository;
        this.bibTeXEntry = paperBibTeXEntry;
        this.bibtex = bibtex;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param paperId the id to set
     * @return 
     */
    public Paper setId(String paperId) {
        this.id = paperId;
        return this;
    }
    
    /**
     *
     * @return 
     * @throws IOException
     */
    private String getPaperPageHtml() throws IOException {
        try {
            return HttpUtils.getWebPageHtmlContent(getUrl());
        } catch (IOException e) {
            throw new IOException("It wasn't possible to get the paper page content from the URL " + getUrl(), e);
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    public boolean isPaperAccessAllowed() throws IOException {
        return StringUtils.isBlank(
                HttpUtils.getInformationFromWebPageContent(getPaperPageHtml(), 
                        repository.getRegexToIdentifyUnallowedPaperAccess()));
    }

    /**
     *
     * @return 
     * @throws java.io.IOException 
     * @throws com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException 
     */
    private String getPaperPdfUrl() throws IOException, PaperNotAvailableForDownloadException {
        if(!isPaperAccessAllowed()){
            throw new PaperNotAvailableForDownloadException(
                "The paper tried to download isn't available for you. Maybe you "+
                "don't have access for the paper using the current network or account."+
                "\nURL: " + getUrl());
        }
        
        try{
            String url = getPaperPdfUrlInternal();
            if(StringUtils.isBlank(url))
                throw new PaperNotAvailableForDownloadException(); 
            return url;
        } catch(IOException e){
            throw new IOException("It wasn't possible to access the paper page from URL " + getUrl(), e);            
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     * @see Paper#getPaperPdfUrl() 
     */
    private String getPaperPdfUrlInternal() throws IOException {
        return HttpUtils.getInformationFromWebPageContent(
                getPaperPageHtml(), repository.getRegexToExtractPdfUrlFromPaperWebPage());
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     * @return 
     */
    public Paper setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year the year to set
     * @return 
     */
    public Paper setYear(Integer year) {
        this.year = year;
        return this;
    }

    /**
     * @return the doi
     */
    public String getDoi() {
        return doi;
    }

    /**
     * @param doi the doi to set
     * @return 
     */
    public Paper setDoi(String doi) {
        this.doi = doi;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d - PaperID: %s\n", getOrderInsideBibTexFile(), this.getId()));
        sb.append(String.format("\tTitle: %s\n", this.getTitle()));
        sb.append(String.format("\tYear:  %s", this.getYear()));
        sb.append(String.format("\tDOI:   %s\n", this.getDoi()));
        sb.append(String.format("\tURL:   %s\n", this.getUrl()));
        return sb.toString();
    }

    public boolean downloadAndIfSuccessfulSetLocalFileNameAndUrl() throws PaperNotAvailableForDownloadException, IOException {
        final String fileName = generatePaperPdfLocalFileName();
        if(HttpUtils.downloadFile(getPaperPdfUrl(), fileName)) {
            this.setLocalFileNameInBibTexEntry(fileName);
            setFieldValue("url", getUrl());
            return true;
        }
        return false;
    }

    private String generatePaperPdfLocalFileName() {
        String fileName;
        fileName = String.format(pdfLocalFileNameFormat(), 
                bibtex.getDownloadDir(), getOrderInsideBibTexFile(), 
                FileSystemUtils.validateFileName(title.trim()));
        return fileName;
    }
    
    private String pdfLocalFileNameFormat() {
        return "%s%0"+String.valueOf(bibtex.numberOfPapers()).length()+"d-%s.pdf";
    }

    /**
     * @return the localFileName
     */
    public String getLocalFileName() {
        return localFileName;
    }

    /**
     * @return the index
     */
    public Integer getOrderInsideBibTexFile() {
        return index;
    }

    /**
     * @param orderInsideBibTexFile the orderInsideBibTexFile to set
     */
    public void setOrderInsideBibTexFile(Integer orderInsideBibTexFile) {
        this.index = orderInsideBibTexFile;
    }

    /**
     * Set a value to a bibtex key into a specific bibtex bibTeXEntry.
     *
     * @param keyName Name of the key to be set
     * @param valueStr Value to be set on the key
     */
    public void setFieldValue(final String keyName, final String valueStr) {
        if(bibTeXEntry == null)
            throw new RuntimeException("The paper is not linked to a BibTeX entry. The bibTeXEntry field is null.");
        
        Key key = new Key(keyName);
        Value bibValue = bibTeXEntry.getField(key);
        if (bibValue != null) {
            bibTeXEntry.removeField(key);
        }
        bibTeXEntry.addField(key, new StringValue(valueStr, StringValue.Style.BRACED));
    }

    /**
     * @param localFileName the localFileName to set
     */
    public void setLocalFileNameInBibTexEntry(final String localFileName) {
        this.localFileName = localFileName;
        setFieldValue("file", localFileName);
    }

    /**
     * 
     * Converts to String a value get from a BibTeX entry.
     *
     * @param value Value of a field from a BibTeX entry.
     * @return Returns the value as string
     * @throws org.jbibtex.ParseException
     * @throws java.io.UnsupportedEncodingException
     */
    public String keyValueToStr(Value value) throws ParseException, UnsupportedEncodingException {
        String str = (value == null ? "" : value.toUserString());
        if (str.indexOf('\\') > -1 || str.indexOf('{') > -1) {
            LaTeXParser latexParser = new LaTeXParser();
            List<LaTeXObject> latexObjects = latexParser.parse(str);
            LaTeXPrinter latexPrinter = new LaTeXPrinter();
            str = latexPrinter.print(latexObjects);
        }
        return str;
    }

    public String getUrl() {
        return bibTeXEntry.getField(BibTeXEntry.KEY_URL).toUserString();
    }

}
