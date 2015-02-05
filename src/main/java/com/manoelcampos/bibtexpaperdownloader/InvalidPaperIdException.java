package com.manoelcampos.bibtexpaperdownloader;

/**
 *
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class InvalidPaperIdException extends Exception {
    public InvalidPaperIdException(){
        super();
    }
    
    public InvalidPaperIdException(String message){
        super(message);
    }
    
}
