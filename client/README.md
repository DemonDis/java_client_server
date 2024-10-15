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

## Run
```bash
# only lib
java -cp javax.json-api-1.1.4.jar MetricTest.java
# all libs
java -cp ".jar:lib/*" MetricTest.java
# or
javac -cp ".jar:lib/*" MetricTest.java
# run
java MetricTest
```
s
## Structure
```
📁 client/
├── 📁 credentials/
├── 📁 key/
|   └── 🔑 arm.p12
├── 📁 keystore/
├── 📁 lib/
|   ├── javax.json-1.1.4.jar
|   ├── javax.json-api-1.1.4.jar
|   ├── javax.websocket-client-api-1.1.jar
|   └── tyrus-standalone-client-1.9.jar
├── 📋 .gitignore
├── ☕ App.java
├── ☕ Client.java
├── ☕ Example.java
├── ☕ Example.java
├── ☕ HttpClientTest.java
├── ☕ HttpsClientTest.java
├── 📋 person.json
└── ...
```

## Generate keys
```bash
keytool -genkeypair -keystore arm.p12 -storetype PKCS12 -storepass MY_PASSWORD -alias KEYSTORE_ENTRY -keyalg RSA -keysize 2048 -validity 99999 -dname "CN=My SSL Certificate, OU=My Team, O=My Company, L=My City, ST=My State, C=SA" -ext san=dns:mydomain.com,dns:localhost,ip:127.0.0.1
```

### Libs
1. javax.json-api-1.1.4.jar
2. javax.json-1.1.4.jar
3. javax.websocket-client-api-1.1.jar
4. tyrus-standalone-client-1.9.jar

#### Info
1. [Disable Certificate Validation in Java SSL Connections](https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/)
2. [Interface JsonObject](https://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html)
3. [Java Classpath](https://howtodoinjava.com/java/basics/java-classpath/)