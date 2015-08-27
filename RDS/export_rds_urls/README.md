# RDS: Export RDS URL's

Tool will export URL's SLUG and ID parts for each Registry Object in the RDS Database

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd RDS/export_rds_urls
mvn package
```

The program archive will be avaliable in `RDS/export_rds_urls/target/export_rds_urls-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp export_rds_urls-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf export_rds_urls-1.0.0.tar.bz2
```

Before you can start the program, you must edit export_rds_urls.propertis file, located in properties folder.

The possible properties are:

* mysql.host: MySQL hostname. The default is localhost.
* mysql.username: MySQL user name. Can not be empty
* mysql.password: MySQL password. Can not be empty
* mysql.database: MySQL database. The deafult is dbs_registry
* output.name: Output file name. The Default is rds_urls.csv
* output.delemiter: Output delemiter. The Default is ','

To stat the program, execute `java -jar export_rds_urls-{program_version}.jar [{properties_file}]` 

For example:

```
# swithc to project folder
cd export_rds_urls-1.0.0

# Edit program properties
vi properties/export_rds_urls.propertis

# Execute the program
java -jar export_rds_urls-1.0.0.jar
```

