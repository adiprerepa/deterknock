# DeterKnock


Stop knocking on my door when i'm doing something important!

The goal is to convey to people coming up to my door that I am in a meeting or something, so they don't start banging away. If you live with people who like to bother you
(aHEM mom), this will be of use to you.

The apparatus is an ESP8266 connected to a 16 by 2 LCD with an RGB LED controlled by an android app to convey the status of my being. You will be able to set a message in the android app, which is diplayed
on the LCDas well as set a priority which is displayed on the second line of the LCD as well as in an RGB LED.

## How It Works

When you upload the code to the esp8266, it will begin an mDNS responder for a hard-coded domain (which you can change in a header file). This domain needs to be unique within the network. It then 
advertises a TCP service on the `esp8266door` channel, which is how the android client 'finds' the esp8266's address.

The Android App shows a list of local services, and you are able to select any one of them to set the status of the LCD.


