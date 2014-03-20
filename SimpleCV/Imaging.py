#!/usr/bin/python
# Importing the call function.
from subprocess import call
# Importing my Camera class.
from Camera import *
# Setting the number of pixels for the heigtht and width.
width = 640
height = 480
# Setting the RGB of our color.
ourColor = (54,255,0) # This is Green at a wavelength of 525 nm.
# Initializing camera.
initCam(width, height)
#Goes forever until power is lost or Ctrl^C is enter.
while True:
	#Captures the image.
	capture()
	#Removes our color.
	remove(ourColor)
	#Find if there are any coordinates.
	coordinates = findCoor()
	#If coordinates exist then sends true over the line.
	print coordinates
	if(coordinates):
		#Uses the call function to call to Gpio with sudo.
		call("sudo /home/pi/Desktop/SimpleCV/FINAL/GpioTrue.py", shell=True)
exit()
