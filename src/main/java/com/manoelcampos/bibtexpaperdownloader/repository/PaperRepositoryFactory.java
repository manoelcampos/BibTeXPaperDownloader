package com.manoelcampos.bibtexpaperdownloader.repository;

/**
 * Get instances of paper repositories.
 * @author Manoel Campos da Silva Filho <manoelcampos at gmail dot com>
 */
public class PaperRepositoryFactory {
    /**
     * Gets an instance of a repository by its class name,
     * handling and re-throwing the exceptions.
     * @param repositoryClassName
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException 
     * @see PaperRepositoryFactory#getInstanceInternal(java.lang.String) 
     */
    public static PaperRepository getInstance(final String repositoryClassName) throws ClassNotFoundException, InstantiationException {
        try{
            return PaperRepositoryFactory.getInstanceInternal(repositoryClassName);
        } catch(ClassNotFoundException e){
            throw new ClassNotFoundException(
                "The repository " + repositoryClassName + " is not supported or the name isn't correct.", e);
        } catch(InstantiationException | IllegalAccessException e){
            throw new InstantiationException(
                "Erro trying to instantiate the repository " + repositoryClassName + ".");
        }
    }
    
    /**
     * Gets an instance of a repository by its class name.
     * 
     * @param repositoryClassName Name of the class repository.
     * @return The instantiated repository
     * @throws ClassNotFoundException Thrown when there isn't any
     * class with the specified name.
     * @throws InstantiationException Thrown when the repository
     * cant be instantiated. Commonly it will be thrown when
     * the repository class doesn't have a default public constructor.
     */  
    private static PaperRepository getInstanceInternal(final String repositoryClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> klass = getRepositoryClass(repositoryClassName);
        return (PaperRepository)klass.newInstance();
    }

    private static Class<?> getRepositoryClass(final String repositoryName) throws ClassNotFoundException {
        Class<?> klass = Class.forName(getRepositoryClassName(repositoryName));
        return klass;
    }

    private static String getRepositoryClassName(final String repositoryName) {
        String className;
        String pkg = PaperRepository.class.getPackage().getName();
        className = pkg + "." + repositoryName;
        return className;
    }
}
