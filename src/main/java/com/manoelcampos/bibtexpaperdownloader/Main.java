package com.manoelcampos.bibtexpaperdownloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.jbibtex.ParseException;

/**
 * Aplicação de linha de comando para fazer o parse de arquivos bibtex (*.bib)
 * e baixar os papers definidos nele direto de suas respectivas bases de dados online.
 * Atualmente só é possível baixar papers da base do IEEE
 * e mesmo assim, alguns papers não conseguem ser baixados.
 * 
 * A aplicação só funciona se o usuário já tiver livre acesso
 * aos papers nas suas respectivas bases de dados.
 * Logo, não é objetivo da mesma burlar qualquer mecanismo de proteção
 * dos direitos autorais. Desta a forma, a aplicação não tenta driblar
 * qualquer mecanismo de proteção do acesso aos papers,
 * ela simplesmente automatiza o processo de download dos mesmos.
 * 
 * @author manoelcampos
 */
public class Main {
   /**
     * Executa a aplicação para processar o arquivo bib e baixar os papers.
     * @param args Recebe como parâmetros de linha de comando:
     *   0 - Nome do arquivo bib a ser processado.
     *   1 - Diretório de destino para baixar os papers.
     *   Se omitido assume que deve-se baixar no diretório atual.
     *   2 - Nome da base de dados que contém os papers indicados no arquivo bib 
     *   (se omitido assume que a base é IEEE).
     */
    public static void main(String args[]) {
        String bibFileName = 
            getCommandLineParam(args, 0, 
                "/Users/manoelcampos/Dropbox/UBI/tese/survey-vm-placement/search0-ieee.bib");
        String downloadDir = getCommandLineParam(args, 1, "/tmp/");
        String repositoryName = getCommandLineParam(args, 2, "IEEE");
        
        try {
            BibTex bibtex = new BibTex(bibFileName, repositoryName);
            bibtex.processBibFile(downloadDir);
        } catch (FileNotFoundException ex) {
            System.err.printf("Arquivo bib %s não encontrado\n", bibFileName);
        } catch (ParseException ex) {
            System.err.printf("Não foi possível fazer o parse do arquivo bib %s. Provavelmente o arquivo é inválido\n", bibFileName);
        } catch (InvalidPaperIdException | IOException | ClassNotFoundException | InstantiationException ex) {
            System.err.println(ex);
        }
        //Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
    }    
    
   /**
     * Obtém um parâmetro de uma posição especificada
     * dentro do vetor de parâmetros de linha de comando
     * recebidos pelo main.
     * @param args Vetor de parâmetros de linha de comando recebido pelo main.
     * @param i Posição a ser acessada dentro do vetor de parâmetros.
     * @param defaultValue Valor padrão a ser retornado caso o parâmetro
     * da posição i não exista.
     * @return Retorna o valor do parâmetro da posição i, caso exista.
     * Caso contrário, retorna o valor do parâmetro defaultValue.
     */
    public static String getCommandLineParam(String args[], int i, String defaultValue){
        if(args.length > i){
            return args[i];
        }
        return defaultValue;
    }    
}
