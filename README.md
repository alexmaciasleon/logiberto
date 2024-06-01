# Logiberto

Logiberto syncs yours access logs to an SQL database.

![Alt Perfectly structured access data](https://github.com/alexmaciasleon/logiberto/blob/main/logiberto-screenshot.png?raw=true)

### Clone the repository

```bash
git clone https://github.com/alexmaciasleon/logiberto.git
cd logiberto

## Prerequisistes

[Java 1.7]
Java (for building and runnning)
openJDK version 17

- [Maven 3]
Apache Maven 3 (for building)

MariaDB 
You need a running instance of local instante of MariaDB as the SQL server.
mysql 15.1 Distrib 10.6.17-MariaDB

To deploy the structure of the database run:
mysql -u user1 -p logiberto < logiberto-ddl.sql

The porject uses a maven 3 as a buld tool, so it file directory structure is one of a maven project.

## Build the project

mvn clean package

## Run the logiberto daemon

cd target
java -jar Logiberto-0.0.1.jar


