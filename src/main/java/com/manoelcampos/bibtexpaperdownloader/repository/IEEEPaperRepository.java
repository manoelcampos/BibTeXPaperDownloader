/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.HttpUtils;
import com.manoelcampos.bibtexpaperdownloader.InvalidPaperIdException;
import java.io.IOException;

/**
 * Implementa métodos para acesso aos papers na base de dados do IEEE.
 * @author manoelcampos
 */
public class IEEEPaperRepository implements PaperRepository {
    public static final String PAPER_PAGE1 = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=%s";
    public static final String PAPER_PAGE2 = "http://ieeexplore.ieee.org/xpls/icp.jsp?arnumber=%s";
    //"http://ieeexplore.ieee.org/ielx7/{punumber}/{isnumber}/{arnumber (8 digitos)}.pdf?tp=&arnumber=%s&isnumber=%s";
    public static final String DOWNLOAD_URL = "http://ieeexplore.ieee.org/ielx7/%s/%s/%s.pdf?tp=&arnumber=%s&isnumber=%s";

    /**
     * Obtém a URL para download o paper, indicado pelo parâmetro paperId, da base do IEEE.
     *
     * @param paperId Id do paper a ser baixado.
     * @param paperTitle Título do paper.
     * @return Retorna a URL para download do paper indicado.
     * @throws IOException Exceção lançada se não for possível obter o conteúdo da página
     * Web do paper.
     * @throws InvalidPaperIdException Se o id de um paper a ser baixado é inválido.
     * Por exemplo, papers do IEEE tem id's inteiros.
     * @throws PaperNotAvailableForDownloadException
     */
    @Override
    public String getPaperDownloadUrl(String paperId, String paperTitle) throws PaperNotAvailableForDownloadException, InvalidPaperIdException, IOException {
        String url;
        String html;
        String punumber;
        String isnumber;
        String paperIdStr;
        url = String.format(IEEEPaperRepository.PAPER_PAGE1, paperId);
        try {
            html = HttpUtils.getWebPageContent(url);
            System.out.println("IEEE PaperPage1: " + url);
        } catch (IOException e) {
            throw new IOException("Não foi possível obter o conteúdo da página do paper no IEEE a partir da URL " + url, e);
        }
        
        String signIn = "<a tabindex=\"0\" class=\"pdf\">Full Text <b>Sign-In or Purchase<\\/b><\\/a>";
        boolean available = 
                HttpUtils.getInformationFromWebPageContent(html, signIn).equals("");
        if(!available){
            throw new PaperNotAvailableForDownloadException();
        }
        punumber = HttpUtils.getInformationFromWebPageContent(html, "punumber=\\d+");
        isnumber = HttpUtils.getInformationFromWebPageContent(html, "isnumber=\\d+");
        if (punumber.isEmpty() || isnumber.isEmpty()) {
            url = String.format(IEEEPaperRepository.PAPER_PAGE2, paperId);
            System.out.println("IEEE PaperPage2: " + url);
            html = HttpUtils.getWebPageContent(url);
            punumber = (!punumber.isEmpty() ? punumber : HttpUtils.getInformationFromWebPageContent(html, "punumber=\\d+"));
            isnumber = (!isnumber.isEmpty() ? punumber : HttpUtils.getInformationFromWebPageContent(html, "isnumber=\\d+"));
        }
        
        try {
            paperIdStr = String.format("%08d", Integer.parseInt(paperId));
        } catch (NumberFormatException e) {
            throw new InvalidPaperIdException("O Id do paper " + paperTitle + " é inválido: " + paperId);
        }
        url = String.format(IEEEPaperRepository.DOWNLOAD_URL, 
                punumber, isnumber, paperIdStr, paperId, isnumber);
        System.out.println("IEEE Download URL: " + url);
        System.out.println("IEEE isnumber: " + isnumber + " punumber: " + punumber);
        return url;
    }
    
}
