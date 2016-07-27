#! /bin/bash

sudo hciconfig hci0 piscan

while true ; do
    sudo python btReception.py
done


