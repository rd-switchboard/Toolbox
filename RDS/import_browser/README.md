# RDS: Import Browser

Tool will Import Records from XML files locaed in S3 Bucket into RDS Database. Each Record must be in RIF:CS XML 
format and the whole XML file must be in OAI:PMH format. Optinal XSLT crosswalk can be provided to change record format
into RIF:CS before uploading the file into RDS website, if records has been harvested in different format.

The files can be harvested via RD-Switchboard Harvester programm, in this case they will be stord into bucked as 
`/{Source Name}/{Metadata Type}/{Set Name}/{File number}.xml

Optinal XSLT crosswalk can be provided to change the metadata format before importing data into RDS.

Multiple properties files can be created to support import from different data sources

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd RDS/import_browser
mvn package
```

The program archive will be avaliable in `RDS/import_browser/target/import_browser-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp import_browser-1.3.0.tar.bz2 ~/
cd ~
tar -xjvf import_browser-1.3.0.tar.bz2
```

Before you can start the program, you must create a properties file in properties folder for each data set you are importing.

The possible properties are:

* data.source.id: Data Source id. Can not be empty
* base.url: Base site URL. For example: http://rd-switchboard
* session.id: Session ID from RDS MySQL database
* crosswalk: Optinal path to XSLT Crosswalk
* aws.access.key: Optinal AWS Access Key
* aws.secret.key: Optinal AWS Secret Key
* s3.bucket: S3 Bucket name
* s3.prefix: S3 Prefix to select only required files from the bucket

To stat the program, execute `java -jar import_browser-{program_version}.jar {properties_file}` 

For example:

```
# swithc to project folder
cd import_browser-1.0.0

# Edit program properties
cp properties/import_browser.propertis properties/import_ands.propertis
vi properties/import_ands.propertis

# Execute the program
java -jar import_browser-1.0.0.jar properties/import_ands.propertis
```

