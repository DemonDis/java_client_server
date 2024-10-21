# CLIENT (java)
```bash
java -version
# version
java version "17.0.12" 2024-07-16 LTS
```

## Echo
```bash
echo ${CLASSPATH}
export CLASSPATH=.:/javax.json-api-1.1.4.jar
```

## Run UNIX (:)
```bash
# only lib
java -cp javax.json-api-1.1.4.jar MetricsTest.java.java
# all libs
java -cp ".jar:libs/*" MetricsTest.java.java
# or
javac -cp ".jar:libs/*" MetricsTest.java.java
# run
java MetricsTest.java
```

## Run WINDOWS (;)
```bash
java -cp ".jar;libs/*" MetricsTest.java.java
```

## Structure
```
📁 client/
├── 📁 example/
|   └── ...
├── 📁 keystore/
|   ├── 🔑 arm.jks
|   └── 🔑 arm.p12
├── 📁 lib/
|   ├── ☕ javax.json-1.1.4.jar
|   ├── ☕ javax.json-api-1.1.4.jar
|   ├── ☕ javax.websocket-client-api-1.1.jar
|   ├── ☕ jetty-client-9.3.6.v20151106.jar
|   ├── ☕ jetty-io-9.3.6.v20151106.jar
|   ├── ☕ jetty-util-9.3.6.v20151106.jar
|   ├── ☕ tyrus-standalone-client-1.9.jar
|   ├── ☕ websocket-api-9.3.6.v20151106.jar
|   ├── ☕ websocket-client-9.3.6.v20151106.jar
|   ├── ☕ websocket-common-9.3.6.v20151106.jar
|   ├── ☕ websocket-server-9.3.6.v20151106.jar
|   └── ☕ websocket-servlet-9.3.6.v20151106.jar
├── 📋 .gitignore
├── ☕ HttpClientTest.java      # Запрос по http
├── ☕ HttpsClientTest.java     # Запрос по https (с отлюкчением  ssl)
├── ☕ WSClient.java            # Запрос по ws
├── ☕ WSSClientJetty.java      # Запрос по wss (с отлюкчением  ssl) JETTY (eclipse)
└── ☕ WSSClientTyrus.java      # Запрос по wss (в работе) TYRUS
```

## Generate keys
```bash
# p12
keytool -genkeypair -keystore arm.p12 -storetype PKCS12 -storepass MY_PASSWORD -alias KEYSTORE_ENTRY -keyalg RSA -keysize 2048 -validity 99999 -dname "CN=My SSL Certificate, OU=My Team, O=My Company, L=My City, ST=My State, C=SA" -ext san=dns:mydomain.com,dns:localhost,ip:127.0.0.1
# jks
keytool -importkeystore -srckeystore arm.p12 \
        -srcstoretype PKCS12 \
        -destkeystore arm.jks \
        -deststoretype JKS

# jks
keytool -genkey -keyalg RSA -validity 3650 -keystore "keystore.jks" -storepass "MY_PASSWORD" -keypass "keypassword" -alias "default" -dname "CN=127.0.0.1, OU=MyOrgUnit, O=MyOrg, L=MyCity, S=MyRegion, C=MyCountry"
```

### Libs
1. javax.json-api-1.1.4.jar
1. jetty-client-9.3.6.v20151106.jar
2. jetty-io-9.3.6.v20151106.jar
3. jetty-util-9.3.6.v20151106.jar
4. websocket-api-9.3.6.v20151106.jar
5. websocket-client-9.3.6.v20151106.jar
6. websocket-common-9.3.6.v20151106.jar
7. websocket-server-9.3.6.v20151106.jar
8. websocket-servlet-9.3.6.v20151106.jar

#### Info
1. [Disable Certificate Validation in Java SSL Connections](https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/)
2. [Interface JsonObject](https://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html)
3. [Java Classpath](https://howtodoinjava.com/java/basics/java-classpath/)
4. [Building a Samlple Java WebSocket Client](https://dzone.com/articles/sample-java-web-socket-client)
5. [WebSocket Client in Java](https://www.delftstack.com/howto/java/websocket-client-java/#then-we-need-to-create-a-clientmanager-and-ask-it-to-connect-to-the-annotated-endpoint-which-is-our-client-the-uri-will-specify-the-server)

#### Example
- [TLS Client-Server Example in Java](https://github.com/mortensen/tls-client-server-example)
- [Mutual TLS client and server in NodeJS](https://github.com/BenEdridge/mutual-tls)
- [Mineria](https://github.com/AlejandroCovarrubias/Mineria)
- [Java WebSockets](https://github.com/TooTallNate/Java-WebSocket)

#### Stack
- [javax.websocket client simple example](https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example)
- [tyrus websocket ssl handshake has failed](https://stackoverflow.com/questions/42051411/tyrus-websocket-ssl-handshake-has-failed)
- [how to add .crt file to keystore and trust store](https://stackoverflow.com/questions/57453154/how-to-add-crt-file-to-keystore-and-trust-store)
- [Can't find Jetty WebSocket classes after adding the libraries in classpath](https://stackoverflow.com/questions/17956357/cant-find-jetty-websocket-classes-after-adding-the-libraries-in-classpath)
- [Connecting to a secured websocket](https://stackoverflow.com/questions/29189197/connecting-to-a-secured-websocket)