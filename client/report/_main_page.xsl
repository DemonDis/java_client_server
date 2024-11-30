<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:variable name="stands" select="document(/list/stand)//@name" />

    <xsl:template match="/">

        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="styles.css"/>
            </head>
            <body>
                <xsl:for-each select="$stands">
                    <xsl:variable name="pos" select="position()"/>
                    <h1><xsl:value-of select="$stands[$pos]"/></h1>
                </xsl:for-each>
            </body>
    </html>

    </xsl:template>

</xsl:stylesheet>
