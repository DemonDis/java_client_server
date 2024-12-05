<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <!-- Определение пути к файлу запроса -->
    <xsl:variable name="stand" select="concat('./stands/', list/@stand, '/_request.xml')" />
    
    <!-- Загружаем все метрики и запросы -->
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
                        
                        <!-- Выводим все запросы -->
                        <xsl:for-each select="$request">
                            <xsl:value-of select="."/><br/>
                        </xsl:for-each>

                        <!-- Таблица с метриками -->
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
                                <!-- Цикл по запросам -->
                                <xsl:for-each select="$request">
                                    <xsl:variable name="currentRequest" select="."/>
                                    <xsl:variable name="metricsForRequest" select="$allMetric[@requestName = $currentRequest]"/>

                                    <tr>
                                        <!-- Название запроса -->
                                        <td><xsl:value-of select="$metricsForRequest//@request"/></td>
                                        <!-- Запрос -->
                                        <td><xsl:value-of select="$metricsForRequest//@requestName"/></td>
                                        <!-- Количество пользователей -->
                                        <td><xsl:value-of select="count($metricsForRequest)"/></td>
                                        <!-- Максимальное время отклика -->
                                        <td>
                                            <xsl:value-of select="$metricsForRequest//result_time/@max_time"/>
                                        </td>
                                        <!-- Максимальное время -->
                                        <td>
                                            <xsl:for-each select="$metricsForRequest//result_time">
                                                <xsl:sort select="." data-type="number" order="descending"/>
                                                <xsl:if test="position()=1">
                                                    <xsl:value-of select="."/>
                                                </xsl:if>
                                            </xsl:for-each>
                                        </td>
                                        <!-- Среднее время отклика -->
                                        <td>
                                            <xsl:value-of select="sum($metricsForRequest//result_time) div count($metricsForRequest)"/>
                                        </td>
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
