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

## Structure
```
📁 client
├── 📁 credentials/
|   ├── ...
|   └── ...
├── 📁 lib/
|   ├── javax.json-1.1.4.jar
|   └── javax.json-api-1.1.4.jar
└── ...
```

### Libs
1. javax.json-api-1.1.4.jar
2. javax.json-1.1.4.jar

#### Info
1. [Disable Certificate Validation in Java SSL Connections](https://nakov.com/blog/2009/07/16/disable-certificate-validation-in-java-ssl-connections/)
2. [Interface JsonObject](https://docs.oracle.com/javaee/7/api/javax/json/JsonObject.html)
3. [Java Classpath](https://howtodoinjava.com/java/basics/java-classpath/)