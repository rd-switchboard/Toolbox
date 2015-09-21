# RDS: Clean RDS Records

Tool will run periodically to check ussage quotas and issue or remove a temporray API blocks if needed.

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd RDS/check_limits
mvn package
```

The program archive will be avaliable in `RDS/check_limits/target/check_limits-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp check_limits-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf check_limits-1.0.0.tar.bz2
```

Before you can start the program, you must edit check_limits.propertis file, located in properties folder.

The possible properties are:

* host: MySQL hostname. The default is localhost.
* user: MySQL user name. Can not be empty
* password: MySQL password. Can not be empty
* database: Name of MySQL database. The default is dbs_graph

To stat the program, execute `java -jar check_limits-{program_version}.jar [{properties_file}]` 

For example:

```
# Move to the software folder
cd check_limits-1.0.0

# Edit program properties
vi properties/check_limits.propertis

# Execute the program
java -jar check_limits-1.0.0.jar
```

