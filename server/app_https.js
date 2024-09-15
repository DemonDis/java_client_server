const express = require("express");
const dotenv = require("dotenv").config();
const https = require("https");
const fs = require("fs");
const path = require("path");

const app = express();
const morgan = require("morgan");
const port = process.env.PORT || 4500;

// Middleware
app.use(morgan("dev"));

// Routes
app.get("/", (req, res) => {
  res.send("Server start");
});

// Read SSL certificate and key files
const options = {
  key: fs.readFileSync(path.join(__dirname, "src/certificate/localhost-key.pem")),
  cert: fs.readFileSync(path.join(__dirname, "src/certificate/localhost.pem")),
};

// Create HTTPS server
const server = https.createServer(options, app);

server.listen(port, () => {
  console.log(`App listening on https://localhost:${port}`);
});
