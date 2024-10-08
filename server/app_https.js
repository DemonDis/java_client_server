const express = require("express");
const https = require("https");
const fs = require("fs");
const path = require("path");

const app = express();
app.use(express.json());

// app.post('/', function (req, res) {
//   console.log(`Данные: ${JSON.stringify(req.body)}`)
//   res.json({requestBody: req.body})
//   // res.send('Сообщение => ' + req.body)
// })
app.post('/', (req, res) => {
  const content = req.body;
  res.send(`Получено: ${JSON.stringify(content)}`);
});


// Read SSL certificate and key files
const options = {
  key: fs.readFileSync(path.join(__dirname, "src/certificate/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "src/certificate/localhost.pem")),
};

// Create HTTPS server
const server = https.createServer(options, app);

server.listen(4500, () => {
  console.log(`App listening on https://localhost:4500`);

});
