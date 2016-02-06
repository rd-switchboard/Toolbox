<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:rif="http://ands.org.au/standards/rif-cs/registryObjects"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"	
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:rns="http://rns.nii.ac.jp/ns#"
  xmlns:foaf="http://xmlns.com/foaf/0.1/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:kaken="http://kaken.nii.ac.jp/ns#"
  xmlns:owl="http://www.w3.org/2002/07/owl#"
  xmlns:oai="http://www.openarchives.org/OAI/2.0/"  
  xmlns="http://www.openarchives.org/OAI/2.0/"  
  exclude-result-prefixes="xs xsi xsl rdf rdfs rns foaf dcterms kaken owl oai rif">

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
    <!-- Select researcher key -->	
    <xsl:variable name="recordKey" select="normalize-space(./oai:metadata/rdf:RDF/rns:Researcher/@rdf:about)"/>

    <!-- create OAI Record object -->
    <record>

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
        <registryObjects>

          <!-- create RIF:CS Registry objectss schema location -->
          <xsl:attribute name="xsi:schemaLocation">
            <xsl:text>http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd</xsl:text>
          </xsl:attribute>
			
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
	
            <!-- create RIF:CS Party Object -->
            <party>
	     
              <!-- create RIF:CS Collection type attribute -->
              <xsl:attribute name="type">
                <xsl:text>person</xsl:text>
              </xsl:attribute>

              <name type="primary">
                <!-- create Title object -->
		<xsl:apply-templates select=".//foaf:lastName[@xml:lang='en']"/>
		<xsl:apply-templates select=".//foaf:firstName[@xml:lang='en']"/>
              </name>

            </party>	
          </registryObject>
        </registryObjects>
      </metadata>
    </record>

  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/name/namePart type="family"  -->
  <!-- =========================================== -->

  <xsl:template match="foaf:lastName">
    <namePart>
      <xsl:attribute name="type">
        <xsl:text>family</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="normalize-space(./node())"/>
    </namePart>	
  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/name/namePart type="family"  -->
  <!-- =========================================== -->

  <xsl:template match="foaf:firstName">
    <namePart>
      <xsl:attribute name="type">
        <xsl:text>given</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="normalize-space(./node())"/>
    </namePart>	
  </xsl:template>

</xsl:stylesheet>

