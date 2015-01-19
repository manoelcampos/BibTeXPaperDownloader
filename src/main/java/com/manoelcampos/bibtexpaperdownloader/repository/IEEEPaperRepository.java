/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.BibTex;
import com.manoelcampos.bibtexpaperdownloader.Paper;
import org.jbibtex.BibTeXEntry;

/**
 * Implementa métodos para acesso aos papers na base de dados do IEEE.
 * @author manoelcampos
 */
public class IEEEPaperRepository implements PaperRepository {
    private final String name;
    /*
    public static final String PAPER_PAGE_URL_TEMPLATE = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=%s";
    public static final String PAPER_PAGE2 = "http://ieeexplore.ieee.org/xpls/icp.jsp?arnumber=%s";
    //"http://ieeexplore.ieee.org/ielx7/{punumber}/{isnumber}/{arnumber (8 digitos)}.pdf?tp=&arnumber=%s&isnumber=%s";
    public static final String DOWNLOAD_URL = "http://ieeexplore.ieee.org/ielx7/%s/%s/%s.pdf?tp=&arnumber=%s&isnumber=%s";
    */
    
    public static final String PAPER_PAGE_URL_TEMPLATE = "http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=%s";
    
    public IEEEPaperRepository(){
        this.name = "IEEE";
    }
    
    @Override
    public Paper getPaperInstance(BibTex bibtex, BibTeXEntry bibEntry) {
        Paper paper = new Paper(bibtex, bibEntry);
        paper.setId(bibEntry.getKey().toString());
        paper.setPaperWebPageUrl(generatePaperUrlFromPaperId(paper.getId()));        
        paper.setTitle(bibEntry.getField(BibTeXEntry.KEY_TITLE).toString());
        paper.setRegexToIdentifyUnallowedPaperAccess(generateRegexToIdentifyUnallowedPaperAccess());
        paper.setRegexToGetPdfUrlFromPaperWebPage(generateRegexToGetPdfUrlFromPaperWebPage());
        return paper;
    }

    private String generatePaperUrlFromPaperId(String paperId) {
        return String.format(IEEEPaperRepository.PAPER_PAGE_URL_TEMPLATE, paperId);
    }

    private String generateRegexToIdentifyUnallowedPaperAccess() {
        return "<a tabindex=\"0\" class=\"pdf\">Full Text <b>Sign-In or Purchase<\\/b><\\/a>";
    }

    private String generateRegexToGetPdfUrlFromPaperWebPage() {
        return
            "<frame src=\"(http:\\/\\/ieeexplore\\.ieee\\.org\\/.*\\.pdf.*arnumber=.*)\" frameborder=";
    }

    @Override
    public String getName() {
        return name;
    }
    
}
