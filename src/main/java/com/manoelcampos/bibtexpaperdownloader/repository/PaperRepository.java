/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.InvalidPaperIdException;
import java.io.IOException;

/**
 * Representa um repositório de papers como IEEE, Elsevier, ACM, etc.
 * @author manoelcampos
 */
public interface PaperRepository {
    public String getPaperDownloadUrl(String paperId, String paperTitle) throws PaperNotAvailableForDownloadException, InvalidPaperIdException, IOException;
}
