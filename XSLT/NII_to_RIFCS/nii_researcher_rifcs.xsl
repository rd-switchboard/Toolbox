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
  exclude-result-prefixes="xs xsl rdf rdfs rns foaf dcterms kaken owl">

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
    <xsl:element name="record" namespace="http://www.openarchives.org/OAI/2.0/">

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
       
      <!--  <xsl:element name="registryObjects" namespace="http://ands.org.au/standards/rif-cs/registryObjects">
          <xsl:namespace name="xsi" select="'http://www.w3.org/2001/XMLSchema-instance'"/> -->
                
 
         <!-- <xsl:copy-of select="namespace::xsi"/>  -->
       <!-- <registryObjects> -->

          <!-- create RIF:CS Registry object namespace -->
        <!--  <xsl:attribute name="xmlns">
            <xsl:text>http://ands.org.au/standards/rif-cs/registryObjects</xsl:text>
          </xsl:attribute> -->

        <!-- create xsi object namespace -->
         <!--   <xsl:attribute name="xmlns:xsi">
            <xsl:text>http://www.w3.org/2001/XMLSchema-instance</xsl:text>
          </xsl:attribute> -->

          <!-- create RIF:CS Registry object schema location -->
         <!-- <xsl:attribute name="xsi:schemaLocation">
            <xsl:text>http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd</xsl:text>
          </xsl:attribute> -->
			
          <!-- create RIF:CS Registry object -->
	  <registryObject>
          <!-- <xsl:element name="registryObject" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> -->
            <!-- create RIF:CS Registry object group attribute -->
            <xsl:attribute name="group">
              <xsl:value-of select="$global_group"/>
            </xsl:attribute>

            <!-- create RIF:CS Key object -->
	    <xsl:element name="key" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> 	            
              <xsl:copy-of select="$recordKey"/>
            </xsl:element>

            <!-- create RIF:CS Originating Source Object -->	
	    <xsl:element name="originatingSource" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> 	            
              <xsl:value-of select="$originatingSource"/>    
            </xsl:element>
	
            <!-- create RIF:CS Party Object -->
            <xsl:element name="party" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> 	            
	     
              <!-- create RIF:CS Collection type attribute -->
              <xsl:attribute name="type">
                <xsl:text>person</xsl:text>
              </xsl:attribute>

              <xsl:element name="name" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> 	            
                <xsl:attribute name="type">
                  <xsl:text>primary</xsl:text>
                </xsl:attribute>
                <!-- create Title object -->
		<xsl:apply-templates select=".//foaf:lastName[@xml:lang='en']"/>
		<xsl:apply-templates select=".//foaf:firstName[@xml:lang='en']"/>
              </xsl:element>

            </xsl:element>	
          </registryObject>
        </registryObjects>
      </metadata>
    </xsl:element>

  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/name/namePart type="family"  -->
  <!-- =========================================== -->

  <xsl:template match="foaf:lastName">
    <xsl:element name="namePart" namespace="http://ands.org.au/standards/rif-cs/registryObjects"> 
      <xsl:attribute name="type">
        <xsl:text>family</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="normalize-space(./node())"/>
    </xsl:element>	
  </xsl:template>

  <!-- =========================================== -->
  <!-- RegistryObject/name/namePart type="family"  -->
  <!-- =========================================== -->

  <xsl:template match="foaf:firstName">
    <xsl:element name="namePart" namespace="http://ands.org.au/standards/rif-cs/registryObjects">
      <xsl:attribute name="type">
        <xsl:text>given</xsl:text>
      </xsl:attribute>
      <xsl:value-of select="normalize-space(./node())"/>
    </xsl:element>	
  </xsl:template>

</xsl:stylesheet>

