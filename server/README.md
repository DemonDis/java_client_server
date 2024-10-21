# Server (node)

## Server start
```bash
# install node_modules
npm i
# run server https
nodemon app_https.js
# run server websocket
nodemon app_socket.js
# run server http
nodemon app_http.js
# run server http
nodemon app_socket_tsl.js
```

## Generate keys
[Install openssl windows](https://ultahost.com/knowledge-base/install-openssl-on-windows/)
```bash
# private key
openssl genrsa -out key.pem
# create a CSR
openssl req -new -key key.pem -out csr.pem
# generate our SSL certificate
openssl x509 -req -days 365 -in csr.pem -signkey key.pem -out cert.pem
```

### Technology stack
1. NODE - **v21.7.1**

#### Info
1. [Building an Express App with an HTTPS Server ](https://dev.to/fredabod/building-an-express-app-with-an-https-server-2mbj)
2. [Node.js TLS plain TLS sockets](https://gist.github.com/pcan/e384fcad2a83e3ce20f9a4c33f4a13ae)
3. [How to Generate and Use an SSL Certificate in Node.js](https://dev.to/devland/how-to-generate-and-use-an-ssl-certificate-in-nodejs-2996)