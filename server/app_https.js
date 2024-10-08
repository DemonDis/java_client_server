const express = require('express');
const cors = require('cors');
const https = require('https');
const fs = require('fs');
const app = express();

app.use(cors({origin: '*'}));
app.use(express.json({
  inflate: true,
  limit: '100kb',
  reviver: null,
  strict: true,
  type: 'application/json',
  verify: undefined
}));
app.use(express.urlencoded({ extended: true }));

app.route('/article')
.post((req, res) => {
    res.send(`Server получил и вернул то же: ${JSON.stringify(req.body)}`);
    console.log('Client прислал', req.body);
})

const httpsServer = https.createServer({
  key: fs.readFileSync('src/certificate/key.pem'),
  cert: fs.readFileSync('src/certificate/cert.pem'),
}, app);

httpsServer.listen(4500, () => {
    console.log('HTTPS Server running on port 4500');
});
