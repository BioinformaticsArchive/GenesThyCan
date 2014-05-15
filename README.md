GenesThyCan
===========

-----------
Background
-----------

We developed a large-scale text mining system to generate a molecular profiling of thyroid cancer subtypes. The system first uses a subtype classification method for the thyroid cancer literature, which employs a scoring scheme to assign different subtypes to articles. We then applied gene normalization libraries, GNAT (Hakenberg J et al) and Moara (Neves ML),  to extract genes and PathNER (Wu C et al) to extract pathway mentions from thyroid cancer literature. We associated those text-mined genes/pathways to different thyroid cancer subtypes and successfully unveiled important genes and pathways that are not present in manually annotated databases or most recent review articles.


-----------
Systems description
-----------

The system is built upon the lightweight text mining framework Textpipe (Gerner M et al, 2012). We implemented four annotators, including GNAT wrapper, Moara wrapper, PathNER wrapper and TC subtype annotator. 


-----------
System setup
-----------
1. Install and setup GNAT; you will need to run the GNAT dictionary services; 
2. Install and setup Moara; you will need to setup a database server to host the information in Moara;
3. Install and setup PathNER; 
4. Get all the libraries for each component and configure the classpath/build path in Eclipse;
5. Put relevant data files/model files under the project folder (get them from each component’s installation;
6. Currently the system needs to run each component separately and store the results into a local database, the main class is uk.ac.man.textpipe.TextPipe, to run, you need to specify the following parameters (subtype classification annotator as an example): 

--databaseDocs "select * from tc_text" --annotator TcSubtypeClassifier --dbHost-articles mydbhost --dbUsername-articles mydbusername --dbPassword-articles mydbpass --dbSchema-articles mydb --compute resultdb resulttable --dbHost-db resultdbhost --dbUsername-db resultdbuser --dbPassword-db resultdbpass --dbSchema-db resultdb --report 100 --clearCompute

For detail meanings of the parameters, please refer to Textpipe’s documentation. 
7. Install the database tables. I provide a dump .sql file for your reference. You can directly import the file into your own database. The database include the corpus data, TC related supplementary concepts and subtype classification data and genes/pathways recognition data. 
8. I also attached the scripts and intermediate data files used in this study, please find them under the “scripts” folder. 


-----------
Paper
-----------
Please cite the following paper:
Wu C, Schwartz J-M, Nenadic G: Molecular profiling of thyroid cancer subtypes using large-scale text mining. BMC Syst Biol , submitted.


-----------
Contact
-----------

If you have any questions, please do not hesitate to contact me via : chengkun.wu@manchester.ac.uk



-----------
References
-----------

Gerner M, Sarafraz F, Bergman CM, Nenadic G: BioContext: an integrated text mining system for large-scale extraction and contextualisation of biomolecular events. Bioinformatics 2012, 28:2154–2161.

Hakenberg J, Gerner M, Haeussler M, Solt I, Plake C, Schroeder M, Gonzalez G, Nenadic G, Bergman CM: The GNAT library for local and remote gene mention normalization. Bioinformatics 2011, 27:2769–2771.

Neves ML, Carazo J-M, Pascual-Montano A: Moara: a Java library for extracting and normalizing gene and protein mentions. BMC Bioinformatics 2010, 11:157.

Wu C, Schwartz J-M, Nenadic G: PathNER: a tool for systematic identification of biological pathway mentions in the literature. BMC Syst Biol 2013, 7:S2.
