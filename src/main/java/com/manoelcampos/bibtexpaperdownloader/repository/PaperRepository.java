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
 * Representa um repositório de papers como IEEE, Elsevier, ACM, etc.
 * @author manoelcampos
 */
public interface PaperRepository {
    Paper getPaperInstance(BibTeXEntry bibEntry) throws PaperNotAvailableForDownloadException, InvalidPaperIdException, IOException;
}
