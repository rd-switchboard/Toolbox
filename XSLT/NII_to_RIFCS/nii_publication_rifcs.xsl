<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"	
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:oai="http://www.openarchives.org/OAI/2.0/"  
  xmlns:irdb="http://irdb.nii.ac.jp/oai"	
  xmlns:rif="http://ands.org.au/standards/rif-cs/registryObjects"
  xmlns="http://www.openarchives.org/OAI/2.0/"  
  exclude-result-prefixes="xs xsi xsl oai rif irdb">

  <!-- =========================================== -->
  <!-- Configuration                               -->
  <!-- =========================================== -->

  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <xsl:param name="global_group" select="'NII'"/>
  <xsl:param name="originatingSource" select="'NII'"/> 

  <!-- =========================================== -->
  <!-- Default template: copy everything           -->
  <!-- =========================================== -->

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>


  <!-- =========================================== -->
  <!-- RegistryObjects (root) Template             -->
  <!-- =========================================== -->
    
  <xsl:template match="oai:record">

    <!-- define recordKey variable -->	
    <!-- standart OAI key -->
    <!-- <xsl:variable name="recordKey" select="./oai:header/oai:identifier/text()"/> -->
    <!-- IR_URI key -->	
    <xsl:variable name="recordKey" select="./oai:metadata/irdb:junii2/irdb:IR_URI/text()"/>

    <!-- create OAI Record object -->
    <record xmlns="http://www.openarchives.org/OAI/2.0/">

      <!-- Generate OAI Header -->
      <!-- <xsl:copy-of select="./oai:header"/> -->
      <header>
        <identifier>
          <xsl:copy-of select="$recordKey"/>
        </identifier>
        <xsl:copy-of select="./oai:header/oai:datestamp"/>
        <xsl:copy-of select="./oai:header/oai:setSpec"/>
      </header>	

      <!-- create OAI Metadaya object -->
      <metadata>	

        <!-- create RIF:CS Registry objects collection -->
        <registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
          <!-- create RIF:CS Registry object -->
          <registryObject>
	
            <!-- create RIF:CS Registry object group attribute -->
            <xsl:attribute name="group">
              <xsl:value-of select="$global_group"/>
	    </xsl:attribute>

            <!-- create RIF:CS Key object -->
            <key>
              <xsl:copy-of select="$recordKey"/>
            </key>

            <!-- create RIF:CS Originating Source Object -->	
	    <originatingSource> 	            
              <xsl:value-of select="$originatingSource"/>    
            </originatingSource>
	
            <!-- create RIF:CS Collection Object -->
            <collection>

              <!-- create RIF:CS Collection type attribute -->
              <xsl:attribute name="type">
                <xsl:text>publication</xsl:text>
              </xsl:attribute>

              <!-- create Title object -->
              <xsl:apply-templates select=".//irdb:junii2/irdb:title"/>

              <!-- create Related object -->
              <xsl:apply-templates select=".//irdb:junii2/irdb:creator[@id]"/>

            </collection>	
          </registryObject>
        </registryObjects>
      </metadata>
    </record>
	    
  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/name/namePart(primary) -->
  <!-- =========================================== -->

  <xsl:template match="irdb:title">
    <name xmlns="http://ands.org.au/standards/rif-cs/registryObjects">
      <xsl:attribute name="type">
          <xsl:text>primary</xsl:text>
        </xsl:attribute>
      <namePart> 
        <xsl:attribute name="type">
          <xsl:text>title</xsl:text>
        </xsl:attribute>
	<xsl:value-of select="normalize-space(./node())"/>
      </namePart>	
    </name>
  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/relatedObject template       -->
  <!-- =========================================== -->

  <xsl:template match="irdb:creator">
    <relatedObject xmlns="http://ands.org.au/standards/rif-cs/registryObjects">
      <key>
        <xsl:value-of select="normalize-space(./@id)"/>
      </key>
      <relation> 
        <xsl:attribute name="type">
          <xsl:text>isProducedBy</xsl:text>
        </xsl:attribute>
        <description>  
	  <!-- <xsl:text>creator</xsl:text> -->
          <xsl:value-of select="normalize-space(./text())"/>
        </description>
      </relation>
    </relatedObject>
  </xsl:template>
	
</xsl:stylesheet>

