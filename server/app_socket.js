const https = require('https');
const fs = require('fs');
const WebSocket  = require('ws');

const server = https.createServer({
  key: fs.readFileSync('src/certificate/key.pem'),
  cert: fs.readFileSync('src/certificate/cert.pem'),
});

const wss = new WebSocket.Server({ server });

wss.on('connection', function connection(ws) {
  ws.on('error', console.error);

  ws.on('message', function message(msg) {
    console.log(msg.toString());
  });
});

server.listen(8999, function listening() {
  const ws = new WebSocket(`wss://localhost:${server.address().port}/wss`, {
    rejectUnauthorized: false
  });
  ws.on('error', console.error);

  ws.on('open', function open() {
    ws.send('OPEN SOCKET!');
  });

  console.log('URL => ', ws._url);
});