#!/bin/bash 

xsltproc -v nii_researcher_rifcs.xsl creators-small.rdf 2>error.txt | xmllint --format --output output-creators-sample.xml -
