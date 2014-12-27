/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader;

/**
 *
 * @author manoelcampos
 */
public class InvalidPaperIdException extends Exception {
    public InvalidPaperIdException(){
        super();
    }
    
    public InvalidPaperIdException(String message){
        super(message);
    }
    
}
