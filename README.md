# Toolbox
Fancy tools that you can use to achieve great things: We have developed them tools
to make the RD-Switchboard operation easier. Please feel free to use these tools
under the MIT license. 

This Document contains only list of existsing projects in this repository. Please refer to the 
actual project README.md file for a information about how to install and use each tool. The project README.md 
located in each project folder.

# RDS

## [RDS: Clean RDS Records](https://github.com/rd-switchboard/Toolbox/tree/master/RDS/clean_rds_records)

Tool will read a file, containing all existing RDS keys, exported from a Neo4j Database. 
Then it will process all registry objects from a MySQL database and compare them with keys list.
If registry object key does not exists in the list, the registry object will be deleted. 
The progra, will delete the keys in chunks, the size of the chunk can be set in the program propertyes
(the 256 is default).

## [RDS: Export RDS URL's](https://github.com/rd-switchboard/Toolbox/tree/master/RDS/export_rds_urls)

Tool will export URL's SLUG and ID parts for each Registry Object in the RDS Database

## [RDS: Sync SOLR Index](https://github.com/rd-switchboard/Toolbox/tree/master/RDS/sync_solr)

Tool will Sync SOLR Index with RDS MySQL Database

## [RDS: Import Browser](https://github.com/rd-switchboard/Toolbox/tree/master/RDS/import_browser)

Tool will Import Records from XML files locaed in S3 Bucket into RDS Database. Each Record must be in RIF:CS XML 
format and the whole XML file must be in OAI:PMH format. Optinal XSLT crosswalk can be provided to change record format
into RIF:CS before uploading the file into RDS website, if records has been harvested in different format.

# AWS S3

## [S3: Remove Public Access](https://github.com/rd-switchboard/Toolbox/tree/master/S3/remove_public_access)

Tool is designed to revoke public acess for all objects located in the some AWS S3 Bucket. 
The selection can be limited by prefix, what usually is jus folder name. 

## [S3: Test Crosswalk](https://github.com/rd-switchboard/Toolbox/tree/master/S3/test_crosswalk)

Tool will download a single XML object from a S3 Bucket and will apply a Crosswalk to it. 
The result can be printed to stdout or to the file.




