# RDS: Sync SOLR Index

Tool will Sync SOLR Index with RDS MySQL Database

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd RDS/sync_solr
mvn package
```

The program archive will be avaliable in `RDS/sync_solr/target/sync_solr-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp sync_solr-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf sync_solr-1.0.0.tar.bz2
```

Before you can start the program, you must edit sync_solr.propertis file, located in properties folder.

The possible properties are:

* base.url: Base site URL. Ca not be empty
* data_source_id: Data Source ID what needs to be sync. Can not be empty
* attempts: Number of maximum attempts. The default is 10
* delay: Delay between attempts. The default is 30

To stat the program, execute `java -jar sync_solr-{program_version}.jar [{properties_file}]` 

For example:

```
# swithc to project folder
cd sync_solr-1.0.0

# Edit program properties
vi properties/sync_solr.propertis

# Execute the program
java -jar sync_solr-1.0.0.jar
```

