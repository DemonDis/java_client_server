<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="1.0">

  <xsl:output indent ="yes" method="xml"/>
  <xsl:strip-space elements ="*"/>

  <xsl:param name="InputFileTwo">./Sample1.xml</xsl:param>

  <xsl:template match="/">
    <xsl:call-template name="ConcatFiles"/>
  </xsl:template>

  <xsl:template name="ConcatFiles">
    <xsl:variable name="tempStoreDocTwo" select ="document($InputFileTwo)/rootNode/header" />

    <xsl:element name="rootNode">
      <xsl:element name="header">

        <xsl:for-each select="rootNode/header/*">
          <xsl:variable name="pos" select="position()" />
          <xsl:choose>

            <xsl:when test="./@agg = 'sum'">
              <xsl:variable name="tempElementDocTwo" select ="$tempStoreDocTwo/*[$pos]"/>
              <xsl:element name="{name(.)}"> / 
                <xsl:value-of select=". + $tempElementDocTwo"/>
              </xsl:element>
            </xsl:when>

            <xsl:otherwise>
              <xsl:element name="{name(.)}">
                <xsl:value-of select="."/>
              </xsl:element>
            </xsl:otherwise>

          </xsl:choose>
        </xsl:for-each>

      </xsl:element>
    </xsl:element>

  </xsl:template>

</xsl:stylesheet>
