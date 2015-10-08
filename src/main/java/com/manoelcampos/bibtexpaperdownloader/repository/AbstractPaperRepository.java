package com.manoelcampos.bibtexpaperdownloader.repository;

import com.manoelcampos.bibtexpaperdownloader.BibTexPapersDownloader;
import com.manoelcampos.bibtexpaperdownloader.Paper;
import org.jbibtex.BibTeXEntry;

/**
 * 
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail.com>
 */
public abstract class AbstractPaperRepository implements PaperRepository {

    @Override
    public Paper getPaperInstance(final BibTexPapersDownloader bibtex, final BibTeXEntry bibEntry) {
        Paper paper = new Paper(this, bibtex, bibEntry);
        paper.setId(bibEntry.getKey().toString());
        paper.setTitle(bibEntry.getField(BibTeXEntry.KEY_TITLE).toUserString());
        paper.setDoi(bibEntry.getField(BibTeXEntry.KEY_DOI).toUserString());
        paper.setYear(Integer.parseInt(bibEntry.getField(BibTeXEntry.KEY_YEAR).toUserString()));
        return paper;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}
