<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:variable name="allResults" select="document(/list/entry/@name)//metric" />
  <xsl:variable name="request" select="document('list_request.xml')/list/url/@name" />
  
  <xsl:template match="/">
    <xsl:variable name="name" select="'reqiest_1'" />
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="styles.css" />
      </head>
      <body>
        <div class="dashboard">
          <div class="profile">
            <h2>Метрики</h2>
            <p><xsl:value-of select="/list/@date"/></p>
          </div>
          <div class="schedule-table">
            <h2>Адрес <xsl:value-of select="/list/@url"/></h2>
            <table>
              <thead>
                <tr bgcolor="#9acd32">
                    <th>Наименование запроса</th>
                    <th>Количество пользователей</th>
                    <th>Среднее значение (сек.)</th>
                    <th>Максимальное допустимое значение (сек.)</th>
                </tr>
              </thead>
              <tbody>
                <xsl:for-each select="$request">
                  <xsl:variable name="pos" select="position(  )"/>
                    <xsl:variable name="table" select='$allResults[$pos]/@reqiest' />
                    <tr>
                      <td><xsl:value-of select="$allResults[@reqiest = $request[$pos]]/@reqiest"/></td>
                      <td><xsl:value-of select="count($allResults[@reqiest = $request[$pos]])"/></td>
                      <td><xsl:value-of select="sum($allResults[@reqiest = $request[$pos]]) div count($allResults[@reqiest = $request[$pos]])"/></td>
                      <td><xsl:value-of select="$allResults[@reqiest = $request[$pos]]/time/@max_time"/></td>
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
