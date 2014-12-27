/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * Classe com métodos para facilitar o uso do protocolo HTTP.
 * @author manoelcampos
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
     * Obtém o código HTML da página da URL especificada.
     *
     * @param urlStr URL da página que deve-se obter o código HTML.
     * @return Retorna o código HTML da página especificada.
     * @throws java.net.MalformedURLException Exceção lançada quando a URL indicada é inválida.
     */
    public static String getWebPageContent(String urlStr) throws MalformedURLException, IOException {
        String line;
        try (final BufferedReader is = sendRequest(new URL(urlStr))) {
            try (final StringWriter os = new StringWriter()) {
                while ((line = is.readLine()) != null) {
                    os.append(line);
                }
                return os.toString();
            }
        } catch (MalformedURLException e) {
            throw new MalformedURLException("URL inválida: " + urlStr);
        } catch (IOException e) {
            throw new IOException("Erro ao tentar gravar buffer para armazenamento do conteúdo da página indicada pela URL " + urlStr, e);
        }
    }

    /**
     * Obtém uma determinada informação
     * a partir do código HTML de uma página Web.
     *
     * @param html Código HTML da página a ser processada.
     * @param regex Expressão regular indicando a informação que deve ser obtida da página.
     * @return Retorna a informação obtida da página, a partir da aplicação da regex
     * no código HTML da mesma. Caso a informação não seja localizada,
     * retorna uma string vazia.
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
     * Baixar o arquivo da URL especificada e salva com o nome
     * fornecido em fileName.
     *
     * @param url URL da página que deve-se obter o código HTML.
     * @param fileName Nome para salvar o arquivo.
     * @return Retorna true se o arquivo foi baixado com sucesso e false em
     * caso contrário.
     * @throws java.net.MalformedURLException Exceção lançada quando a URL indicada é inválida.
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
            throw new MalformedURLException("URL inválida: " + url);
        } catch (IOException e) {
            throw new IOException("Erro ao tentar acessar o arquivo " + fileName, e);
        }
        return true;
    }
    
}
