#!/bin/bash 

xsltproc -v nii_publication_rifcs.xsl publications-small.xml 2>publications-error.txt | xmllint --format --output output-publications-small.xml -
