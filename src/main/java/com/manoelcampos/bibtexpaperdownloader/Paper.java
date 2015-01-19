/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
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
 * @author manoelcampos
 */
public class Paper {
    private Integer index = 0;
    private String id;
    private String title;
    private Integer year;
    private String doi;
    private String localFileName;
    private String paperWebPageUrl;
    private String regexToIdentifyUnallowedPaperAccess;
    private String regexToGetPdfUrlFromPaperWebPage;
    private final BibTeXEntry bibEntry;
    private final BibTex bibtex;
    
    public Paper(BibTex bibtex, BibTeXEntry bibEntry){
        this.bibEntry = bibEntry;
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
            return HttpUtils.getWebPageContent(getPaperWebPageUrl());
        } catch (IOException e) {
            throw new IOException("It wasn't possible to get the paper page content from the URL " + getPaperWebPageUrl(), e);
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    public boolean isPaperAccessAllowed() throws IOException {
        return StringUtils.isBlank(HttpUtils.getInformationFromWebPageContent(getPaperPageHtml(), getRegexToIdentifyUnallowedPaperAccess()));
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
                "\nURL: " + getPaperWebPageUrl());
        }
        
        try{
            String url = getPaperPdfUrlInternal();
            if(StringUtils.isBlank(url))
                throw new PaperNotAvailableForDownloadException(); 
            return url;
        } catch(IOException e){
            throw new IOException("It wasn't possible to access the paper page from URL " + getPaperWebPageUrl(), e);            
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     * @see Paper#getPaperPdfUrl() 
     */
    private String getPaperPdfUrlInternal() throws IOException {
        return HttpUtils.getInformationFromWebPageContent(getPaperPageHtml(), getRegexToGetPdfUrlFromPaperWebPage());
    }

    /**
     * @return the regexToGetPdfUrlFromPaperWebPage
     */
    private String getRegexToGetPdfUrlFromPaperWebPage() {
        return regexToGetPdfUrlFromPaperWebPage;
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
        sb.append(String.format("\tDOI:   %s", this.getDoi()));
        return sb.toString();
    }

    public boolean downloadAndIfSuccessfulSetLocalFileName() throws PaperNotAvailableForDownloadException, IOException {
        final String fileName = generatePaperPdfLocalFileName();
        if(HttpUtils.downloadFile(getPaperPdfUrl(), fileName)) {
            this.setLocalFileName(fileName);
            return true;
        }
        return false;
    }

    private String generatePaperPdfLocalFileName() {
        String fileName;
        fileName = String.format(pdfLocalFileNameFormat(), 
                bibtex.getDownloadDir(), getOrderInsideBibTexFile(), 
                FileSystemUtils.validateFileName(title));
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
     * Set a value to a bibtex key into a specific bibtex bibEntry.
     *
     * @param keyName Name of the key to be set
     * @param valueStr Value to be set on the key
     */
    public void setFieldValue(String keyName, String valueStr) {
        Key key = new Key(keyName);
        Value bibValue = bibEntry.getField(key);
        if (bibValue != null) {
            bibEntry.removeField(key);
        }
        bibEntry.addField(key, new StringValue(valueStr, StringValue.Style.BRACED));
    }

    /**
     * @param localFileName the localFileName to set
     */
    public void setLocalFileName(final String localFileName) {
        this.localFileName = localFileName;
        setFieldValue("file", localFileName);
    }

    /**
     * @return the regexToIdentifyUnallowedPaperAccess
     */
    public String getRegexToIdentifyUnallowedPaperAccess() {
        return regexToIdentifyUnallowedPaperAccess;
    }

    /**
     * @param regexToIdentifyUnallowedPaperAccess the regexToIdentifyUnallowedPaperAccess to set
     */
    public void setRegexToIdentifyUnallowedPaperAccess(String regexToIdentifyUnallowedPaperAccess) {
        this.regexToIdentifyUnallowedPaperAccess = regexToIdentifyUnallowedPaperAccess;
    }

    /**
     * @param regexToGetPdfUrlFromPaperWebPage the regexToGetPdfUrlFromPaperWebPage to set
     */
    public void setRegexToGetPdfUrlFromPaperWebPage(String regexToGetPdfUrlFromPaperWebPage) {
        this.regexToGetPdfUrlFromPaperWebPage = regexToGetPdfUrlFromPaperWebPage;
    }

    /**
     * @return the paperWebPageUrl
     */
    public String getPaperWebPageUrl() {
        return paperWebPageUrl;
    }

    /**
     * @param paperWebPageUrl the paperWebPageUrl to set
     */
    public void setPaperWebPageUrl(String paperWebPageUrl) {
        this.paperWebPageUrl = paperWebPageUrl;
    }

    /**
     * Converte para string um valor obtido de uma entrada de um item
     * em um arquivo bibtex.
     *
     * @param value Valor de uma entrada bibtex a ser convertido para string.
     * @return Retorna o valor da entrada como string
     * @throws org.jbibtex.ParseException
     * @throws java.io.UnsupportedEncodingException
     */
    public String keyValueToStr(Value value) throws ParseException, UnsupportedEncodingException {
        String str = value == null ? "" : value.toUserString();
        if (str.indexOf('\\') > -1 || str.indexOf('{') > -1) {
            LaTeXParser latexParser = new LaTeXParser();
            List<LaTeXObject> latexObjects = latexParser.parse(str);
            LaTeXPrinter latexPrinter = new LaTeXPrinter();
            str = latexPrinter.print(latexObjects);
        }
        return str;
    }
    
}
