const express = require('express');
const cors = require('cors');
const https = require('https');
const fs = require('fs');

const app = express();
app.use(cors({origin: '*'}));
app.use(express.json({ limit: '50mb' }));

app.post('/', (req, res) => {
  console.log('[HTTPS]:', req.body, '\n');
  res.json(`SEND JS => JAVA`);
});

const httpsServer = https.createServer({
  key: fs.readFileSync('src/certificate/key.pem'),
  cert: fs.readFileSync('src/certificate/cert.pem'),
}, app);

httpsServer.listen(4500, () => {
    console.log('HTTPS Server running on port 4500');
});
