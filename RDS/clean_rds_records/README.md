# RDS: Clean RDS Records

Tool will read a file, containing all existing RDS keys, exported from a Neo4j Database. 
Then it will process all registry objects from a MySQL database and compare them with keys list.
If registry object key does not exists in the list, the registry object will be deleted. 
The progra, will delete the keys in chunks, the size of the chunk can be set in the program propertyes
(the 256 is default).

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd RDS/clean_rds_records
mvn package
```

The program archive will be avaliable in `RDS/clean_rds_records/target/clean_rds_records-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp clean_rds_records-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf clean_rds_records-1.0.0.tar.bz2
```

Before you can start the program, you must edit clean_rds_records.propertis file, located in properties folder.

The possible properties are:

* host: MySQL hostname. The default is localhost.
* user: MySQL user name. Can not be empty
* password: MySQL password. Can not be empty
* base_url: Base site URL. Can not be empty
* session: Session ID, obtained from the site cookies
* max_objects: maximum number of objects per transaction. The bigger number will speed up the process, but might result in site crash because of insufficient memory. The default is 256
* input: The path to the csv file, containing list of existing keys. The default is rds_keys.csv

To stat the program, execute `java -jar clean_rds_records-{program_version}.jar [{properties_file}]` 

For example:

```
cd clean_rds_records-1.0.0
# Edit program properties
vi properties/clean_rds_records.propertis

# Create a list with exising keys
...

# Execute the program
java -jar clean_rds_records-1.0.0.jar
```

