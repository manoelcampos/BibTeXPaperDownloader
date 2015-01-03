/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader;

import com.manoelcampos.bibtexpaperdownloader.repository.PaperNotAvailableForDownloadException;
import com.manoelcampos.bibtexpaperdownloader.repository.PaperRepository;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author manoelcampos
 */
public class Paper {
    private String id;
    private String title;
    private Integer year;
    private String doi;
    private String paperWebPageUrl;
    private PaperRepository paperRepository;
    private String regexToIdentifyUnallowedPaperAccess;
    private String regexToGetPdfUrlFromPaperWebPage;
    
    public Paper(PaperRepository paperRepository){
        this.paperRepository = paperRepository;
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
     * @return the paperWebPageUrl
     */
    public String getPaperWebPageUrl() {
        return paperWebPageUrl;
    }

    /**
     * @param paperPageUrl the paperWebPageUrl to set
     * @return 
     */
    public Paper setPaperPageUrl(String paperPageUrl) {
        this.paperWebPageUrl = paperPageUrl;
        return this;
    }

    /**
     * @return the paperRepository
     */
    public PaperRepository getPaperRepository() {
        return paperRepository;
    }

    /**
     * @param paperRepository the paperRepository to set
     * @return 
     */
    public Paper setPaperRepository(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
        return this;
    }

    /**
     *
     * @return 
     * @throws IOException
     */
    private String getPaperPageHtml() throws IOException {
        try {
            return HttpUtils.getWebPageContent(paperWebPageUrl);
        } catch (IOException e) {
            throw new IOException("It wasn't possible to get the paper page content from the URL " + paperWebPageUrl, e);
        }
    }

    /**
     * @return the regexToIdentifyUnallowedPaperAccess
     */
    public String getRegexToIdentifyUnallowedPaperAccess() {
        return regexToIdentifyUnallowedPaperAccess;
    }

    /**
     * @param regexToIdentifyUnallowedPaperAccess the regexToIdentifyUnallowedPaperAccess to set
     * @return 
     */
    public Paper setRegexToIdentifyUnallowedPaperAccess(String regexToIdentifyUnallowedPaperAccess) {
        this.regexToIdentifyUnallowedPaperAccess = regexToIdentifyUnallowedPaperAccess;
        return this;
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
    public String getPaperPdfUrl() throws IOException, PaperNotAvailableForDownloadException {
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
            throw new IOException("It wasn't possible to access the paper page from URL " + paperWebPageUrl, e);            
        }
    }

    /**
     * 
     * @return
     * @throws IOException 
     * @see Paper#getPaperPdfUrl() 
     */
    private String getPaperPdfUrlInternal() throws IOException {
        return HttpUtils.getInformationFromWebPageContent(getPaperPageHtml(), regexToGetPdfUrlFromPaperWebPage);
    }

    /**
     * @return the regexToGetPdfUrlFromPaperWebPage
     */
    public String getRegexToGetPdfUrlFromPaperWebPage() {
        return regexToGetPdfUrlFromPaperWebPage;
    }

    /**
     * @param regexToGetPdfUrlFromPaperWebPage the regexToGetPdfUrlFromPaperWebPage to set
     */
    public void setRegexToGetPdfUrlFromPaperWebPage(String regexToGetPdfUrlFromPaperWebPage) {
        this.regexToGetPdfUrlFromPaperWebPage = regexToGetPdfUrlFromPaperWebPage;
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
}
