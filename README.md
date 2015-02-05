# BibTeX Paper Downloader
Automates the download of papers from scientific repositories such as IEEE, ACM, Elsevier, Science Direct, etc.
Different papers repositories, such as IEEE, allow us to search for papers and save a BibTeX file of the selected papers we desire.

During research processess, to download a lot of papers from these repositories is a routine. The manual process of downloading one by one paper from a search result is a boring task. Due to this, this application aims to automate this process.
The researcher can search for the desired papers in some paper repository and, using the repository web site, download a BibTeX file containing the list of papers found.

This BibTeX file then can be passed to the BibTeX Paper Downloader and it will download each paper registered in the BibTeX file.

#Notice
Currently, only the IEEE repository is supported.
The application only works with BibTeX files created by the paper's repositories.

For instance, to download papers from IEEE, the application requires a BibTeX created in the IEEE web site.
The IEEE site puts the paper ID as the key for each entry in the BibTeX file.
By this way, the application uses this key to discover the paper URL in the IEEE portal.

#How to use
You need a Java Virtual Machine (JVM) to run the application.
At a terminal you can type:

```bash
java -jar BibTexPaperDownloader-1.0-SNAPSHOT-jar-with-dependencies.jar BibTeXFileName [PaperOutputDirectory] [RepositoryName]
```
- BibTeXFileName is the path of a BibTeX file containing the papers to be downloaded from a specified respository
- PapersDownloadDir is the directory where to download the papers
- RepositoryName is the name of the repository that hosts the papers to be downloaded. Currently, only the IEEE respository is supported.

This help can be shown in the command line, typing:

```bash
java -jar BibTexPaperDownloader-1.0-SNAPSHOT-jar-with-dependencies.jar 
```

#License
[The MIT License (MIT)](http://opensource.org/licenses/MIT)

#Disclaimer
The intent of my application is not (and never will be) to contour the paper's access lock imposed by the repositories.
It only automates the download of papers that you already have access, by using your university network or your personal account and subscriptions in the paper repositories. All the papers in these repositories are commonly copyrighted. 
IF YOU DON'T ALREADY HAVE ACCESS TO THE PAPERS, THIS APPLICATION WILL BE USELESS TO YOU.
IT IS NOT INTENDED TO BYPASS THE PAPERS ACCESS CONTROL.

The application is provided as is without any warranty. The author is not liable for any misuse, damages or losses caused by the application.