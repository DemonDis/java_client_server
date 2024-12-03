<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:variable name="stands" select="document(/list)//stand" />

    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="styles_main.css"/>
                <link rel="icon" type="image/x-icon" href="favicon.ico"/>
                <title>Highload</title>
            </head>
            <body>
                <div class="dashboard">
                    <div class="schedule-table">
                        <h2>Стенды</h2>
                        <table>
                            <xsl:for-each select="$stands">
                                <xsl:variable name="pos" select="position()"/>
                                <tr class="inv_item">
                                    <td>📂 <xsl:value-of select="$stands[$pos]//@name"/></td>
                                    <td><xsl:value-of select="$stands[$pos]//@url"/></td>
                                    <td><xsl:value-of select="$stands[$pos]//@date"/></td>
                                </tr>
                            </xsl:for-each>
                        </table>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
