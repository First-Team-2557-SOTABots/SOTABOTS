#!/usr/bin/python
# Importing utilties
import wiringpi2 as wipi
from time import sleep
# Setting up Gpio
wipi.wiringPiSetupGpio()
# Setting pin mode
wipi.pinMode(11, 1)
# Sending true over pin 11
wipi.digitalWrite(11, 1)
# Waiting for a little bit to make sure there is a signal
sleep(.25)
# Resetting Pin to input.
wipi.pinMode(11, 0)
