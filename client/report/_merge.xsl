<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:variable name="stand" select="concat('./stands/', list/@stand, '/_request.xml')" />

    <xsl:variable name="allMetric" select="document(/list/entry/@name)//metric" />
    <xsl:variable name="request" select="document($stand)//list/url/@name" />

    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="../../styles.css"/>
                <link rel="icon" type="image/x-icon" href="../../favicon.ico"/>
                <title><xsl:value-of select="list/@stand"/></title>
            </head>
            <body>
                <div class="dashboard">
                    <div class="profile">
                        <h2>Service Level Agreement</h2>
                        <p><xsl:value-of select="list/@date"/></p>
                    </div>
                    <div class="schedule-table">
                        <h2>Адрес <xsl:value-of select="list/@url"/></h2>
                        <xsl:for-each select="$request">
                            <xsl:value-of select="$request"/>
                        </xsl:for-each>
                        <table>
                            <thead>
                                <tr>
                                    <th>Наименование запроса</th>
                                    <th>Запрос</th>
                                    <th>Количество пользователей</th>
                                    <th>Целевое время отклика (с)</th>
                                    <th>Максимальное время (с)</th>
                                    <th>Среднее время отклика (с)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <xsl:for-each select="$request">
                                    <xsl:variable name="pos" select="position()"/>
                                    <tr>
                                        <td><xsl:value-of select="$allMetric[@requestName = $request[$pos]]//@request"/></td>
                                        <td><xsl:value-of select="$allMetric[@requestName = $request[$pos]]//@requestName"/></td>
                                        <td><xsl:value-of select="count($allMetric[@requestName = $request[$pos]])"/></td>
                                        <td><xsl:value-of select="$allMetric[@requestName = $request[$pos]]//result_time/@max_time"/></td>
                                        <td>
                                            <xsl:for-each select="$allMetric[@requestName = $request[$pos]]//result_time">
                                                <xsl:sort select="." data-type="number" order="descending"/>
                                                <xsl:if test="position()=1">
                                                    <xsl:value-of select="."/>
                                                </xsl:if>
                                            </xsl:for-each>
                                        </td>
                                        <td><xsl:value-of select="sum($allMetric[@requestName = $request[$pos]]//result_time) div count($allMetric[@requestName = $request[$pos]])"/></td>
                                    </tr>
                                </xsl:for-each>
                            </tbody>
                        </table>
                    </div>
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
