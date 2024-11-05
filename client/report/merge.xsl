<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html"/>

  <xsl:variable name="allResults" select="document(/list/entry/@name)//metric" />

  <xsl:template match="/">
    <xsl:variable name="name" select="'reqiest_1'" />
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="styles_2.css" />
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
                </tr>
              </thead>
              <tbody>
                <tr>
                    <td><xsl:value-of select="$name"/></td>
                    <td><xsl:value-of select="count($allResults[@testName = $name][@outcome = 'Passed'])"/></td>
                    <td><xsl:value-of select="count($allResults[@testName = $name][@outcome = 'Failed'])"/></td>
                    <td><xsl:value-of select="count($allResults[@testName = $name][@outcome = 'Failed'])"/></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
