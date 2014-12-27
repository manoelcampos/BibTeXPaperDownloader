/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.InvalidPaperIdException;
import com.manoelcampos.bibtexpaperdownloader.Paper;
import java.io.IOException;
import org.jbibtex.BibTeXEntry;

/**
 * Implementa métodos para acesso aos papers na base de dados do IEEE.
 * @author manoelcampos
 */
public class IEEEPaperRepository implements PaperRepository {
    /*
    public static final String PAPER_PAGE_URL_TEMPLATE = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=%s";
    public static final String PAPER_PAGE2 = "http://ieeexplore.ieee.org/xpls/icp.jsp?arnumber=%s";
    //"http://ieeexplore.ieee.org/ielx7/{punumber}/{isnumber}/{arnumber (8 digitos)}.pdf?tp=&arnumber=%s&isnumber=%s";
    public static final String DOWNLOAD_URL = "http://ieeexplore.ieee.org/ielx7/%s/%s/%s.pdf?tp=&arnumber=%s&isnumber=%s";
    */
    
    public static final String PAPER_PAGE_URL_TEMPLATE = "http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=%s";
    
    /**
     * Obtém a URL para download o paper, indicado pelo parâmetro paperId, da base do IEEE.
     *
     * @param bibEntry
     * @return Retorna a URL para download do paper indicado.
     * @throws IOException Exceção lançada se não for possível obter o conteúdo da página
     * Web do paper.
     * @throws InvalidPaperIdException Se o id de um paper a ser baixado é inválido.
     * Por exemplo, papers do IEEE tem id's inteiros.
     * @throws PaperNotAvailableForDownloadException
     */
    @Override
    public Paper getPaperInstance(BibTeXEntry bibEntry) throws PaperNotAvailableForDownloadException, InvalidPaperIdException, IOException {
        Paper paper = new Paper(this);
        paper.setId(bibEntry.getKey().toString());
        paper.setTitle(bibEntry.getField(BibTeXEntry.KEY_TITLE).toString());
        paper.setPaperPageUrl(generatePaperUrlFromPaperId(paper.getId()));
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
    
}
