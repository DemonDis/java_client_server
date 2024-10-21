const express = require('express');
const cors = require('cors');
const https = require('https');
const fs = require('fs');
const app = express();

app.use(cors({origin: '*'}));
app.use(express.json({
  inflate: true,
  limit: '50mb',
  reviver: null,
  strict: true,
  type: 'application/json',
  verify: undefined
}));
app.use(express.urlencoded({ extended: true }));

app.route('/')
  .post((req, res) => {
    console.log('Client прислал', req.query);
    res.send({'name': 'Hi!'});
  })


const httpsServer = https.createServer({
  key: fs.readFileSync('src/credentials/key.pem'),
  cert: fs.readFileSync('src/credentials/cert.pem'),
}, app);  

httpsServer.listen(4500, () => {
    console.log('HTTPS Server running on port 4500');
});
