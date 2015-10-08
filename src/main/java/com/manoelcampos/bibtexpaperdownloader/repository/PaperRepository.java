package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.BibTexPapersDownloader;
import com.manoelcampos.bibtexpaperdownloader.Paper;
import org.jbibtex.BibTeXEntry;

/**
 * Interface to get URL of papers from IEEE, Elsevier, ACM, etc.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public interface PaperRepository {
    Paper getPaperInstance(final BibTexPapersDownloader bibtex, final BibTeXEntry bibEntry);
    @Override
    String toString();
    String getRegexToIdentifyUnallowedPaperAccess();
    String getRegexToExtractPdfUrlFromPaperWebPage();
}
