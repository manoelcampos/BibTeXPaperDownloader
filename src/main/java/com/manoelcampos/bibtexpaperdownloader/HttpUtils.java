package com.manoelcampos.bibtexpaperdownloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class HttpUtils {
    static {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }
    
    private static BufferedReader sendRequest(URL url) throws IOException{
        URLConnection conn = url.openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
        conn.addRequestProperty("Host", url.getHost());
        conn.connect();
        BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
        return bf;
    }

    /**
     *
     * @param url The page url
     * @return The HTMl code of the page 
     * @throws java.net.MalformedURLException Thrown when the URL is invalid
     */
    public static String getWebPageHtmlContent(String url) throws MalformedURLException, IOException {
        String line;
        try (final BufferedReader is = sendRequest(new URL(url))) {
            try (final StringWriter os = new StringWriter()) {
                while ((line = is.readLine()) != null) {
                    os.append(line);
                }
                return os.toString();
            }
        } catch (MalformedURLException e) {
            throw new MalformedURLException("Invalid URL " + url);
        } catch (IOException e) {
            throw new IOException("Error trying to write in the local buffer to store the page HTML from " + url, e);
        }
    }

    /**
     * Using a specified regex expression, 
     * gets a specific information from an HTML code.
     *
     * @param html HTML code to be parsed.
     * @param regex Regular expression to get the desired information from the HTML code.
     * @return The information extracted from applying the regex to the HTML code.
     * Returns a empty string if the information is not found.
     */
    public static String getInformationFromWebPageContent(String html, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return (matcher.groupCount()==0 ? matcher.group() : matcher.group(1));
        }
        return "";
    }

    /**
     * Downloads the file specified by the URL.
     *
     * @param url The URL of the remote file.
     * @param fileName Name to save the download file locally.
     * @return True if the file was downloaded and false otherwise.
     * @throws java.net.MalformedURLException Thrown when the informed URL is invalid.
     */
    public static boolean downloadFile(String url, String fileName) throws MalformedURLException, IOException {
        URL u = new URL(url);
        try (final InputStream is = new BufferedInputStream(u.openStream())) {
            try (final OutputStream os = new FileOutputStream(fileName)) {
                byte[] b = new byte[1024];
                int len;
                while ((len = is.read(b, 0, b.length)) != -1) {
                    os.write(b, 0, len);
                }
            }
        } catch (MalformedURLException e) {
            throw new MalformedURLException("Invalid URL " + url);
        } catch (IOException e) {
            throw new IOException("Error trying to access the file " + fileName, e);
        }
        return true;
    }
    
}
