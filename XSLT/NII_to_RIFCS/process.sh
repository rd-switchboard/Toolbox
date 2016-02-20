#!/bin/bash

java -jar rdf2oai-1.0.0.jar --input-file=creators.rdf --output-file=researchers.xml --input-encoding=UTF-8 --output-encoding=UTF-8 --set-spec=NII --format-output

mkdir researchers
xsltproc nii_researcher_rifcs.xsl researchers.xml 2>/dev/null | xmllint --format --output researchers/researchers.xml -

java -jar import_ands-1.4.0.jar researchers.properties

mkdir publications
java -jar xml-split-1.0.0.jar -i publications.txt -P publications/oai_ -r OAI-PMH

mkdir publications2
for f in publications/*
do
  echo "Processing $f file..."

  filename=$(basename "$f")
 
  xsltproc nii_publication_rifcs.xsl $f > publications2/$filename
done

java -jar import_ands-1.4.0.jar publications.properties

