#!/bin/bash 

xsltproc -v nii_researcher_rifcs.xsl researchers-small.xml 2>researchers-error.txt | xmllint --format --output output-researchers-small.xml -
