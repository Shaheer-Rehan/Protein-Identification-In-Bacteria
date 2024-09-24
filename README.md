# Competence Feature Identification In Bacteria Using Hadoop and MapReduce
This project aims to identify bacterial organisms that may exhibit the "competence" feature, a physiological state where organisms can take up exogenous genetic material. The analysis uses input files containing protein information, specifically looking for occurrences of the Gene Ontology (GO) term GO:0030420 ("establishment of competence for transformation") and its related subterms. The application is built using Hadoop and MapReduce for scalable analysis of thousands of organisms.

## Input Data
The source files for the application contain the gene ontology (GO) annotation data for different bacteria. They contain lists of protein IDs and their corresponding GO IDs along with additional data in a tab-separated values (TSV) format.

### Input Files
- **Escherichia coli K-12:** Escherichia_coli_K-12_ecocyc_83333.gaf
- **Bacillus subtilis 168:** Bacillus_subtilis_168-224308.gaf
- **Bacillus amyloliquefaciens FZB42:** Bacillus_amyloliquefaciens_FZB42-326423.gaf
- **Bacillus licheniformis ATCC 14580:** Bacillus_licheniformis_ATCC_14580-279010.gaf
- **Bacillus megaterium DSM 319:** Bacillus_megaterium_DSM_319-592022.gaf
- **Geobacillus kaustophilus HTA426:** Geobacillus_kaustophilus_HTA426-235909.gaf
- **Geobacillus thermodenitrificans NG80:** Geobacillus_thermodenitrificans_NG80_2-420246.gaf  
Each file contains protein information, where each line includes the protein ID and its associated GO term.

## Functionality
The application processes the input GAF files using Hadoop’s MapReduce framework. The GOCountMapper class maps the input files to the GO terms and counts occurrences of the GO:0030420 term and its children terms (specified below). The IntSumReducer class sums up these counts for each organism and outputs the results.

## Target Terms
- **GO:0030420** - establishment of competence for transformation
- **GO:0045809** - positive regulation of competence for transformation
- **GO:0045304** - competence for transformation
- **GO:0045808** - negative regulation of competence for transformation

## Workflow
1. **Mapper:** Processes each line of the input files, checking for the presence of the above GO terms. If found, the mapper outputs the organism name, protein ID, and GO term.
2. **Reducer:** Aggregates and sums the occurrences of the GO terms across the input files for each organism.

## Requirements
To run this project, the following dependencies and libraries are required:

### System Requirements
- Java 8 or higher
- Hadoop 3.x

### Libraries and Tools
- **Hadoop Common:** Core Hadoop libraries
- **Hadoop MapReduce:** To handle the MapReduce jobs
- **Apache HDFS (Hadoop Distributed File System):** For input and output file storage

## Setup and Installation
1. **Install Hadoop:** Download and install Hadoop from the Apache Hadoop website. (https://hadoop.apache.org/releases.html)
2. **Configure Hadoop:** Follow the official setup guide to configure the core-site.xml and hdfs-site.xml files.
3. **Compile the Java Code:**  
javac -classpath $(hadoop classpath) -d . GOCount.java
jar -cvf gocount.jar -C . .
4. **Run the Application:** Submit the job to Hadoop using the following command:   
hadoop jar gocount.jar GOCount <input_directory> <output_directory>  
Replace <input_directory> with the HDFS path to your input files and <output_directory> with the path for storing results.

## Output
The output will display the counts of GO:0030420 and its subterms for each organism in the input files, identifying those organisms that may exhibit the competence feature.
