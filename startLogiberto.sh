#!/bin/bash
set -e
# Let's wait for 30 seconds in case other processes need to come up first.
sleep 4
echo "Starting logiberto daemon..."
/usr/bin/java -jar /home/alex/logiberto/target/Logiberto-0.0.1.jar &> /home/alex/logiberto.log &
echo "Logiberto daemon started!"
