const tls = require('tls');
const fs = require('fs');
const path=require('path');

const options = {
    key:fs.readFileSync(path.join(__dirname,'./src/keys_server/key.pem')),
    cert:fs.readFileSync(path.join(__dirname,'./src/keys_server/cert.pem')),
    ca:fs.readFileSync(path.join(__dirname,'./src/keys_server/csr.pem')),
    requestCert: true, 
    rejectUnauthorized: true
}; 

const server = tls.createServer(options, (socket) => {
    console.log('server connected', 
        socket.authorized ? 'authorized' : 'unauthorized');
    
    socket.on('error', (error) => {
        console.log(error);
    });
    
    socket.write('welcome!\n');
    socket.setEncoding('utf8');
    socket.pipe(process.stdout);
    socket.pipe(socket);
});

server.listen(8000, () => {
    console.log('server bound');
});