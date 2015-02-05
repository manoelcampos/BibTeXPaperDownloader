package com.manoelcampos.bibtexpaperdownloader.repository;

/**
 * 
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
*/
public class PaperNotAvailableForDownloadException extends Exception {
    public static final String DEFAULT_MSG = 
            "The paper tried to download isn't available for you. Maybe you don't have access for the paper using the current network or account.";
    public PaperNotAvailableForDownloadException() {
        this(DEFAULT_MSG);
    }
    
     public PaperNotAvailableForDownloadException(String message) {
         super(message);
     }
    
}
