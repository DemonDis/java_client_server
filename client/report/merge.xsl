<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:variable name="allResults" select="document(/list/entry/@name)//metric" />
  <xsl:variable name="allResults_" select="document(/list/entry/@name)" />
  <xsl:variable name="all_request" select="document('list_request.xml')/list/url/@name" />

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
                    <th>Сумма секунд</th>
                    <th>Среднее значение</th>
                    <th>Максимальное значение</th>
                </tr>
              </thead>
              <tbody>
                <xsl:for-each select="$all_request">
                  <xsl:variable name="pos" select="position(  )"/>
                    <xsl:variable name="table" select='$allResults[$pos]/@reqiest' />
                    <tr>
                      <td><xsl:value-of select="$allResults[@reqiest = $all_request[$pos]]/@reqiest"/></td>
                      <td><xsl:value-of select="count($allResults[@reqiest = $all_request[$pos]])"/></td>
                      <td><xsl:value-of select="sum($allResults[@reqiest = $all_request[$pos]])"/></td>
                      <td><xsl:value-of select="sum($allResults[@reqiest = $all_request[$pos]]) div count($allResults[@reqiest = $all_request[$pos]])"/></td>
                      <td>
                        <xsl:for-each select="$allResults[@reqiest = $all_request[$pos]]//time">
                          <xsl:sort select="." data-type="number" order="descending"/>
                          <xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
                        </xsl:for-each>
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
