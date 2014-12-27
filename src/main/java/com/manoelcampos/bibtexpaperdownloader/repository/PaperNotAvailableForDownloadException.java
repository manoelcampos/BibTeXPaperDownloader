/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;

/**
 *
 * @author manoelcampos
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
