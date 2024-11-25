# Loader java
## Version tech stack 
```bash
# java -version
openjdk version "17.0.5" 2022-10-18
# mvn -version
Apache Maven 3.9.3 
```

## Запуск 
```bash
mvn exec:java
```

## Сборка
```bash
# Maven build
mvn package
```

## Контроль работы программы
```bash
jconsole
```

## Структура проекта
```
📂 src/main/java/loader/
├── 📂 logs/                        # Запись логов 🙈
|   |   └── ...
├── 📂 report/                      # Запись результатов 🙈
|   ├── 📙 _conf.json                       # Конфигурационный файл (все запросы и пользователя)
|   ├── 🧭 _index.xsl                       # Список всех xml users (создается при запуске)
|   ├── 📉 _merge_.xsl                      # Сборка отчета xslt
|   ├── 🧭 _request.xsl                     # Список всех xml requst (создается при запуске)
|   ├── 🧭 user_1_result.xml                # Результаты запуска (создается при запуске)
|   ├── 🧭 ... .xml                         # ...создается при запуске
|   └── 💄 styles.css                       # Стилизация отчета
├── 🙈 .gitignore
├── ☕ Metric.java                  # Main (запуск теста)
├── ☕ MetricLog.java               # Logs для отладки (запись в json)
├── ☕ MetricXml.java               # Создание xml users
├── ☕ MetricXmlMerge.java          # Создание xml merge
├── ☕ MetricXmlRequestList.java    # Создание xml request
├── ☕ HttpsRequest.java            # Запрос и сбор данных https
├── 📉 pom.xml                      # Для сборки maven
├── ☕ SocketRequest.java           # Запроc по socket
└── ☕ TransToHtml.java             # Генерация html
```

### Структура отчета в табличной форме (пример)
| Наименование запроса  | Запрос       | Количество пользователей  | Целевое время отклика (сек.)  | Максимальное время (сек.) | Среднее время отклика (сек.)  |
|:----------------------|:-------------|:--------------------------|:------------------------------|:--------------------------|:------------------------------|
| #1 Пользователи       | req:details  | 4                         | 1.5                           | 01                        | 0.5                           |

### Структура отчета xml (примеры)
#### Прогон по одному пользователю
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="stylesheet.xsl"?>
<metrics>
    <metric request="#1 Пользователи" requestName="req:details">
        <result_time max_time="5">01</result_time>
    </metric>
    <metric request="#2 Адресс" requestName="req:adress">
        <result_time max_time="1.5">01</result_time>
    </metric>
</metrics>
```
#### Merge всех пользователей
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<?xml-stylesheet type="text/xsl" href="_merge_.xsl"?>
<list date="13.11.2024, 08:17:19" url="10.1.23.44:8080">
    <entry name="user_1_result.xml"/>
    <entry name="user_2_result.xml"/>
    <entry name="user_3_result.xml"/>
    <entry name="user_4_result.xml"/>
</list>
```
#### Merge всех запросов
```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<list>
    <url name="req:detail"/>
    <url name="req:adress"/>
</list>
```

#### Примечание
- SSL disabled https
- SSL disabled socket

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