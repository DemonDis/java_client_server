const express = require("express");
const dotenv = require("dotenv").config();
const https = require("https");
const fs = require("fs");
const path = require("path");

const app = express();
const morgan = require("morgan");
const port = process.env.PORT || 4500;
const data = JSON.stringify({ key: 'value' });
app.use(express.json({
  inflate: true,
  limit: '100kb',
  reviver: null,
  strict: true,
  type: 'application/json',
  verify: undefined
}));
app.use(express.urlencoded({ extended: true }));
// Middleware
app.use(morgan("dev"));

// Routes
app.post("/", (req, res) => {
  // res.send("Server start");
  console.log(req.body)
  res.send(`Данные: ${JSON.stringify(req.body)}`);
});

// Read SSL certificate and key files
const options = {
  key: fs.readFileSync(path.join(__dirname, "src/certificate/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "src/certificate/localhost.pem")),
  hostname: 'localhost',
  port: 4500,
  path: '/',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length,
  }
};

// Create HTTPS server
const server = https.createServer(options, app);

server.listen(port, () => {
  console.log(`App listening on https://localhost:${port}`);

});
