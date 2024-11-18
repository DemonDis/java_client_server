<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:variable name="allMetric" select="document(/list/entry/@name)//metric" />
    <xsl:variable name="request" select="document('_request.xml')//list/url/@name" />

    <xsl:template match="/">

        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="styles.css"/>
            </head>
            <body>
                <div class="dashboard">
                    <div class="profile">
                        <h2>SLA</h2>
                        <p><xsl:value-of select="list/@date"/></p>
                    </div>
                    <div class="schedule-table">
                        <h2>Адрес <xsl:value-of select="list/@url"/></h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Наименование запроса</th>
                                    <th>Запрос</th>
                                    <th>Количество пользователей</th>
                                    <th>Целевое время отклика (сек.)</th>
                                    <th>Максимальное время (сек.)</th>
                                    <th>Среднее время отклика (сек.)</th>
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
