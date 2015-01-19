/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader;

import java.io.File;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author manoelcampos
 */
public class FileSystemUtils {

    /**
     *
     * @param directory the value of directory
     * @return 
     */
    public static String insertTrailBackslach(String directory) {
        if (StringUtils.isNotBlank(directory) && directory.endsWith(File.separator)) {
            directory += File.separatorChar;
        }
        return directory;
    }

    /**
     * Validates a file name, removing not allowed characters
     * and returning a valid file name.
     * @param fileName The file name to be validated.
     * @return The valid file name.
     */
    public static String validateFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", " ");
    }
    
}
