/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manoelcampos.bibtexpaperdownloader.repository;


/**
 * Cria instâncias para repositórios de papers.
 * @author manoelcampos
 */
public class PaperRepositoryFactory {
    /**
     * Sufixo que deve existir no nome de cada classe de repositório.
     */
    public static final String CLASS_SUFIX = "PaperRepository";
    

    /**
     * Obtém uma instância de repositório, manipulando
     * as exceções que podem ser geradas.
     * @param repositoryName
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException 
     * @see PaperRepositoryFactory#getInstanceInternal(java.lang.String) 
     */
    public static PaperRepository getInstance(String repositoryName) throws ClassNotFoundException, InstantiationException {
        try{
            return PaperRepositoryFactory.getInstanceInternal(repositoryName);
        } catch(ClassNotFoundException e){
            throw new ClassNotFoundException(
                "O repositório de nome " + repositoryName + " não é suportado ou não existe.", e);
        } catch(InstantiationException | IllegalAccessException e){
            throw new InstantiationException(
                "Erro ao tentar criar repositório" + repositoryName + ".");
        }
    }
    
    /**
     * Obtém uma instância de um repositório de papers
     * a partir do nome do repositório.
     * @param repositoryName Nome do repositório a ser obtida uma instância.
     * Se o nome não contiver o @see CLASS_SUFIX no final,
     * tal sufix será automaticamente incluído.
     * @return Retorna uma instância do repositório indicado.
     * @throws ClassNotFoundException Exceção lançada quando
     * não existe uma classe para o repositório informado,
     * indicando que o nome do repositório é inválido
     * ou o mesmo não é suportado ainda.
     * @throws InstantiationException Lançada quando ocorre algum erro ao instanciar
     * dinamicamente um objeto da classe do repositório.
     * Normalmente ocorrerá apenas se o repositório não possui um construtor padrão
     * público.
     */  
    private static PaperRepository getInstanceInternal(String repositoryName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        repositoryName = insertSufixInRepositoryName(repositoryName);
        Class<?> klass = getRepositoryClass(repositoryName);
        return (PaperRepository)klass.newInstance();
    }

    private static Class<?> getRepositoryClass(String repositoryName) throws ClassNotFoundException {
        Class<?> klass = Class.forName(getRepositoryClassName(repositoryName));
        return klass;
    }

    private static String getRepositoryClassName(String repositoryName) {
        String className;
        String pkg = PaperRepository.class.getPackage().getName();
        className = pkg + "." + repositoryName;
        return className;
    }

    private static String insertSufixInRepositoryName(String repositoryName) {
        if(!repositoryName.endsWith(CLASS_SUFIX)){
            repositoryName += CLASS_SUFIX;
        }
        return repositoryName;
    }
}
