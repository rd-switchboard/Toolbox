# S3: Find Object

Tool is designed to find a alll objects in a S3 bucket, containing specyfed string. The selected objects will be automatically downloaded from a S3 and stored in a local S3 folder with exactl S3 local path. 

The S3 bucket will be identified by a bucket name and a optinal prefix to limit the search. If prefix will be ommited, the program will search in a entire bucket. 

If Search string will contain spaces, the entire string will need to be inside single or double quotes. Same quotes inside a string must be escaped with \. 

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd S3/find_object
mvn package
```

The program archive will be avaliable in `S3/find_object/target/find_object-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp find_object-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf find_object-1.0.0.tar.bz2
```

To stat the program, execute `java -jar find_object-{program_version}.jar {bucket_name[/bucket_prefix]} "{search string}"` with desired bucked, prefix and search string. The prefix can be ommited, in this case, the program will search in a entire bucket.

For example:

```
cd find_object-1.0.0
java -jar find_object-1.0.0.jar my_bycket/my_prefix "text to search"
```




