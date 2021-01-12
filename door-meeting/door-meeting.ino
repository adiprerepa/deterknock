#include <ArduinoJson.h>

#include "wifipass.h"
#include "Wire.h"
#include "LiquidCrystal_I2C.h"
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
//#include <ESP8266WebServer.h>
#include <Thread.h>
#include <ESP8266WiFi.h>
#include <ESPAsyncTCP.h>
#include <ESPAsyncWebServer.h>
// create an LCD object (Hex address, # characters, # rows)
// my LCD display in on Hex address 27 and is a 20x4 version
LiquidCrystal_I2C lcd(0x27, 20, 4);
// Multi-Network scenarios work well here, as clients in different
//  networks can change the state of a node.
ESP8266WiFiMulti wifiMulti;
const int serverPort = 8080;
const int greenPin = 12;
const int redPin = 13;
const int bluePin = 14;

// globally-held LCD message
String curStatStr;
// curStatStr.size()
int statSize;
boolean curBlinkState;
// LCD rotation for 16+ characters needs to be abstracted
//  to a separate thread, same goes for when the LED blinks on priority 1.
Thread lcdRotateThread = Thread();
Thread ledBlinkThread = Thread();
// AsyncWebServer so we can run MDNS.update on the "main" thread
AsyncWebServer server(8080);

void writeRgb(int red, int green, int blue) {
  analogWrite(redPin, red);
  analogWrite(bluePin, blue);
  analogWrite(greenPin, green);
}

void toggleRed() {
 curBlinkState = !curBlinkState;
 if (curBlinkState) {
  writeRgb(255, 0, 0);
 } else {
  writeRgb(0, 0, 0);
 }
}

void rotateLcd() {
  lcd.setCursor(0, 0);
  char curStat[statSize];
  curStatStr.toCharArray(curStat, statSize + 1);
  lcd.print(curStat);
  // rotate array
  char tmp = curStat[0];
  for (int i = 1; i < statSize; i++) {
    curStat[i - 1] = curStat[i];
  }
  curStat[statSize - 1] = tmp;
  curStatStr = String(curStat);
}

void setStatus(String lcdStat, String priority) {
  lcd.clear();
  // only start rotating if the length of the message
  // is more than 16 (LCD is 16*2)
  if (lcdStat.length() > 16) {
    curStatStr = lcdStat + " ";
    statSize = curStatStr.length();
    // enable rotating of the 16+ char string
    lcdRotateThread.enabled = true;
    lcd.setCursor(0, 1);
    lcd.print("Priority: " + priority);
  } else {
    // stop rotating
    lcdRotateThread.enabled = false;
    lcd.setCursor(0, 0);
    lcd.print(lcdStat);
    lcd.setCursor(0, 1);
    lcd.print("Priority: " + priority);
  }
  int p = priority.toInt();
  switch (p) {
    case 1:
      ledBlinkThread.enabled = true;
      return;
    case 2:
      writeRgb(255, 0, 0);
      break;
    case 3:
      writeRgb(255, 255, 0);
      break;
    case 4:
      writeRgb(0, 255, 0);
      break;
    default:
      Serial.println("Unknown priority, defaulting to 2..");
      writeRgb(255, 0, 0);
      break;
  }
//  if (p == 1) {
//    ledBlinkThread.enabled = true;
//    return;
//  } else if (p == 2) {
//    writeRgb(255, 0, 0);
//  } else if (p == 3) {
//    writeRgb(255, 255, 0);
//  } else if (p == 4) {
//    writeRgb(0, 255, 0);
//  }
  ledBlinkThread.enabled = false;

}

// post callback
void postStateChange(AsyncWebServerRequest *request) {
  if (!request->hasParam("lcd_msg") || !request->hasParam("priority")) {
    request->send(400, "text/plain", "Please include both lcd_msg and priority");
    return;
  }
  setStatus(request->getParam("lcd_msg")->value(), request->getParam("priority")->value());
  request->send(201, "text/plain", "Created.");
}

void setup() {
  Serial.begin(115200);
  lcd.init();
  lcd.backlight(); 
  wifiMulti.addAP(ssid, pass);
  while (wifiMulti.run() != WL_CONNECTED) {
    delay(1000);
    Serial.println("trying to connect...");
  }
  Serial.print("connected to ");
  Serial.println(WiFi.SSID());
  Serial.print("address:\t");
  Serial.println(WiFi.localIP());

  // change MDNS name here from aditya_door to anything
  if (!MDNS.begin(device_name, WiFi.localIP())) {
    Serial.println("Err setting up MDNS responder");
  }
  Serial.println("responder started");
  lcdRotateThread.enabled = false;
  lcdRotateThread.onRun(rotateLcd);
  lcdRotateThread.setInterval(1500);
  ledBlinkThread.enabled = false;
  ledBlinkThread.setInterval(750);
  ledBlinkThread.onRun(toggleRed);
  curBlinkState = false;
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *req) {
    req->send(200, "text/plain", "DeterKnock - Aditya Prerepa. This node's IP: " + WiFi.localIP().toString());
  });
  server.on("/stateChange", HTTP_POST, postStateChange);
  server.begin();
  Serial.print("Adding Service... : ");
  Serial.println(MDNS.addService("esp8266door", "tcp", 8080));
}

void loop() {
  // im not sure why we constantly have to advertise the service,
  //  but it doesn't work without. It just drops off after a while.
  MDNS.update();
  MDNS.addService("esp8266door", "tcp", 8080);
  if (lcdRotateThread.shouldRun()) {
    lcdRotateThread.run();
  }
  if (ledBlinkThread.shouldRun()) {
    ledBlinkThread.run();
  }
  // this delay needs to exist so we dont overload mDNS.
  delay(1000);
}
