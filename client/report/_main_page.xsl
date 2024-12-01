<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:variable name="stands" select="document(/list)//stand" />

    <xsl:template match="/">

        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="styles.css"/>
            </head>
            <body>

                <div class="dashboard">
                <!-- 
                    <div class="profile">
                        <h2>Good Morning Champ!</h2>
                        <p>Time to seize the day 🌞</p>
                    </div> -->

                    <div class="schedule-table">
                        <h2>Стенды</h2>
                        <table>
                            <xsl:for-each select="$stands">
                                <xsl:variable name="pos" select="position()"/>
                                <tr>
                                    <td>📂 <xsl:value-of select="$stands[$pos]//@name"/></td>
                                    <td><xsl:value-of select="$stands[$pos]//@url"/></td>
                                    <td><xsl:value-of select="$stands[$pos]//@date"/></td>
                                </tr>
                            </xsl:for-each>
                        </table>
                    </div>

                    <!-- <div class="schedule-table">
                        <h2>Weekly Schedule</h2>
                        <table>
                            <tr>
                                <th>Day</th>
                                <th>Scheduled Exercise</th>
                                <th>Time</th>
                            </tr>
                            <tr>
                                <td>Monday</td>
                                <td>Running</td>
                                <td>6:00 AM</td>
                            </tr>
                            <tr>
                                <td>Tuesday</td>
                                <td>Swimming</td>
                                <td>7:00 AM</td>
                            </tr>
                            <tr>
                                <td>Wednesday</td>
                                <td>Cycling</td>
                                <td>6:30 AM</td>
                            </tr>
                            <tr>
                                <td>Thursday</td>
                                <td>Yoga</td>
                                <td>6:00 AM</td>
                            </tr>
                            <tr>
                                <td>Friday</td>
                                <td>Weight Training</td>
                                <td>8:00 AM</td>
                            </tr>
                        </table>
                    </div> -->

                    <!-- <div class="exercise-table">
                        <h2>Last 5 Exercises</h2>
                        <table>
                            <tr>
                                <th>Exercise</th>
                                <th>Duration</th>
                            </tr>
                            <tr>
                                <td>Running</td>
                                <td>30 min</td>
                            </tr>
                            <tr>
                                <td>Swimming</td>
                                <td>45 min</td>
                            </tr>
                            <tr>
                                <td>Cycling</td>
                                <td>60 min</td>
                            </tr>
                            <tr>
                                <td>Yoga</td>
                                <td>40 min</td>
                            </tr>
                            <tr>
                                <td>Weight Training</td>
                                <td>50 min</td>
                            </tr>
                        </table>
                    </div> -->

                    <!-- <div class="calories">
                        <h2>Active Calories</h2>
                        <div><strong>Today:</strong> 500</div>
                        <div><strong>This Week:</strong> 3500</div>
                        <div><strong>This Month:</strong> 14000</div>
                    </div>

                    <div class="personal-bests">
                        <h2>Personal Bests</h2>
                        <ul>
                            <li>Fastest 5K Run: 22 min</li>
                            <li>Heaviest Deadlift: 250 lbs</li>
                            <li>Longest Plank: 3 min</li>
                        </ul>
                    </div>

                    <div class="challenges">
                        <h2>Challenges</h2>
                        <ul>
                            <li>30-Day Running Streak</li>
                            <li>1000 Pushups in a Month</li>
                            <li>Swim 20km in a Month</li>

                        </ul>
                    </div>

                    <div class="activity-feed">
                        <h2>Friends Activity</h2>
                        <ul>
                            <li>Jane just set a new record in cycling: 30 miles!</li>
                            <li>Mike completed the 30-Day Running Streak Challenge!</li>
                            <li>Anna shared a new workout: 'Hill Sprints Interval'.</li>
                        </ul>
                    </div> -->

                </div>


                
            </body>
    </html>

    </xsl:template>

</xsl:stylesheet>
