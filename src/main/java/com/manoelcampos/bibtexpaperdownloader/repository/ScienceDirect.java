package com.manoelcampos.bibtexpaperdownloader.repository;

/**
 * Implements an interface to get the URL of papers from Science Direct repository.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class ScienceDirect extends AbstractPaperRepository {
    @Override
    public String getRegexToIdentifyUnallowedPaperAccess() {
        return "science\\?_ob=ShoppingCartURL&_method=add&_eid=.*";
    }

    @Override
    public String getRegexToExtractPdfUrlFromPaperWebPage() {
        //http://www.sciencedirect.com/science/article/pii/S1084804515000284/pdfft?md5=82804e8d757f263cf037b084e044910d&pid=1-s2.0-S1084804515000284-main.pdf
        return "pdfurl=\"(http:\\/\\/www\\.sciencedirect\\.com\\/science\\/article\\/pii\\/.{1,20}\\/pdf.*\\?md5=.*&pid=.*-main\\.pdf)\"(?!\\>) queryStr"; 
    }
    
}
