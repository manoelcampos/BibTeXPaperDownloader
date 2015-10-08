package com.manoelcampos.bibtexpaperdownloader.repository;

/**
 * Implements an interface to get the URL of papers from IEEE repository.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class IEEE extends AbstractPaperRepository {
    /*
    public static final String TEMPLATE_OF_PAPER_PAGE_URL = "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=%s";
    public static final String PAPER_PAGE2 = "http://ieeexplore.ieee.org/xpls/icp.jsp?arnumber=%s";
    //"http://ieeexplore.ieee.org/ielx7/{punumber}/{isnumber}/{arnumber (8 digitos)}.pdf?tp=&arnumber=%s&isnumber=%s";
    public static final String DOWNLOAD_URL = "http://ieeexplore.ieee.org/ielx7/%s/%s/%s.pdf?tp=&arnumber=%s&isnumber=%s";
    */
    
    @Override
    public String getRegexToIdentifyUnallowedPaperAccess() {
        return "<a tabindex=\"0\" class=\"pdf\">Full Text <b>Sign-In or Purchase<\\/b><\\/a>";
    }

    @Override
    public String getRegexToExtractPdfUrlFromPaperWebPage() {
        return
            "<frame src=\"(http:\\/\\/ieeexplore\\.ieee\\.org\\/.*\\.pdf.*arnumber=.*)\" frameborder=";
    }    
}
