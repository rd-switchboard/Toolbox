<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"	
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:oai="http://www.openarchives.org/OAI/2.0/"  
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rss="http://my.netscape.com/rdf/simple/0.9/"
  xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
  exclude-result-prefixes="xs xsi xsl oai rdf rss">

  <!-- =========================================== -->
  <!-- Configuration                               -->
  <!-- =========================================== -->

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <OAI-PMH 
    xmlns="http://www.openarchives.org/OAI/2.0/" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd">

    <xsl:template match="/">
      <xsl:apply-templates select="rdf:RDF/rss:Researcher" />
    </xsl:template>

    <xsl:template match="rdf:RDF/rss:Researcher">
      <oai:Researcher>
        <xsl:value-of select="rss:title" />
      </oai:Researcher>
    </xsl:template>

  </OAI-PMH>
</xsl:stylesheet>

