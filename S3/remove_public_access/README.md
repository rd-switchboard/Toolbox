# S3: Remove Public Access

Tool is designed to revoke public acess for all objects located in the some AWS S3 Bucket. 
The selection can be limited by prefix, what usually is jus folder name. 

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd S3/remove_public_access
mvn package
```

The program archive will be avaliable in `S3/remove_public_access/target/remove_public_access-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp remove_public_access-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf remove_public_access-1.0.0.tar.bz2
```

To stat the program, execute `java -jar remove_public_access-{program_version}.jar {bucket name} [{key}]` with desired bucked and, optionaly, key name

For example:

```
cd remove_public_access-1.0.0
java -jar remove_public_access-1.0.0.jar my_bycket my_key
```




