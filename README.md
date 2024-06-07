# Logiberto

Logiberto monitors, parses and syncs your access log files into a relational database so you can query your log data with SQL.

![Alt Perfectly structured access data](https://github.com/alexmaciasleon/logiberto/blob/main/logiberto-screenshot.png?raw=true)

### Clone the repository

```bash
git clone https://github.com/alexmaciasleon/logiberto.git
cd logiberto
<<<<<<< Updated upstream
```
=======
`
>>>>>>> Stashed changes
## Prerequisistes

- [Java] (for building and runnning)
recommended version: openJDK version 17

- [Maven]
Apache Maven 3 (for building)

- [MariaDB]
By default logiberto works with MariaDB but can be easily replaced by any other relational database that has JDBC support.

To deploy the structure of the database run:
```bash
mysql -u user1 -p logiberto < logiberto-ddl.sql
```

The project uses Maven 3 as a build tool, so it file directory structure is one of a maven project.

## Build the project
```bash
mvn clean package
```
## Run the logiberto daemon

```bash
cd target
java -jar Logiberto-0.0.1.jar
```

## Startup bash script

startLogibertod.sh file provided.

Edit the file and set your particular file paths.


## Add Logiberto daemon to systemd (linux only)


We have provided a systemd, just adapt the path to your particular case


set your path in logiberto.service then copy:
```bash
cp logibertod.service /etc/systemd/system/
```
## Enable the logiberto daemon with systemd (linux only)
```bash
sudo systemctl enable logibertod
```
## Start logiberto daemon (linux only)
```bash
sudo systemctl start logibertod
```
