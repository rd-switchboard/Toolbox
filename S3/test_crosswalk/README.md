# S3: Test Crosswalk

Tool will download a single XML object from a S3 Bucket and will apply a Crosswalk to it. 
The result can be printed to stdout or to the file.

#### Requirements

Program requires Java 1.7 and Apache Maven 3.0.5

Program has been tested on Ubuntu Linux 14.04 and should work on any other linux as well

#### Build and ussage

To build the program simple run `mvn package` from the program folder.

For example:

```
cd S3/test_crosswalk
mvn package
```

The program archive will be avaliable in `S3/test_crosswalk/target/test_crosswalk-{program_version}.tar.gz` (or bz2)

To install, copy the archive to desired location and untar it.

For example:

```
cd target
cp test_crosswalk-1.0.0.tar.bz2 ~/
cd ~
tar -xjvf test_crosswalk-1.0.0.tar.bz2
```

Before you can start the program, you must edit test_crosswalk.propertis file, located in properties folder.

The possible properties are:
 
* aws.access.key: AWS Access Key
* aws.access.key: AWS Seecret Key
* s3.bucket: S3 Bucket name
* s3.key: S3 Key name
* crosswalk: Crosswalk file name
* out: Output file name

If `aws.access.key` and `aws.access.key` properties will be leaved empty, the program will try to obtain them from an Instance IAM Role The `out` property can be empty or equal to `stdout` keyword, in this case, the processed XML will be sent to stdout.

To stat the program, execute `java -jar test_crosswalk-{program_version}.jar [{properties_file}]` 

For example:

```
cd test_crosswalk-1.0.0
java -jar test_crosswalk-1.0.0.jar my_bycket my_key
```

