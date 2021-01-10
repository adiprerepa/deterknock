#include <ArduinoJson.h>

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
ESP8266WiFiMulti wifiMulti;
const char* ssid = "onhub";
const char* pass = "ekchotaghar";
const int serverPort = 8080;
const int greenPin = 12;
const int redPin = 13;
const int bluePin = 14;

String curStatStr;
int statSize;
boolean curBlinkState;
Thread lcdRotateThread = Thread();
Thread ledBlinkThread = Thread();
// red -> 255, 0, 0
// green -> 0, 255, 0
// blue -> 0, 0, 255
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
  if (lcdStat.length() > 16) {
    curStatStr = lcdStat + " ";
    statSize = curStatStr.length();
    lcdRotateThread.enabled = true;
    Serial.println("enabled lcd thread");
    lcd.setCursor(0, 1);
    lcd.print("Priority: " + priority);
  } else {
    lcdRotateThread.enabled = false;
    lcd.setCursor(0, 0);
    Serial.println(lcdStat);
    lcd.print(lcdStat);
    delay(250);
    lcd.setCursor(0, 1);
    lcd.print("Priority: " + String(priority));
  }
  int p = priority.toInt();
  if (p == 1) {
    ledBlinkThread.enabled = true;
    return;
  } else if (p == 2) {
    writeRgb(255, 0, 0);
  } else if (p == 3) {
    writeRgb(255, 255, 0);
  } else if (p == 4) {
    writeRgb(0, 255, 0);
  }
  ledBlinkThread.enabled = false;

}

void postStateChange(AsyncWebServerRequest *request) {
  if (!request->hasParam("lcd_msg") || !request->hasParam("priority")) {
    request->send(400, "text/plain", "Please include both lcd_msg and priority");
    return;
  }
  setStatus(request->getParam("lcd_msg")->value(), request->getParam("priority")->value());
  request->send(201, "text/plain", "Created.");
}

void setup() {
  lcd.init();
  lcd.backlight();
  Serial.begin(115200);
  // we can eventually do multi network
  wifiMulti.addAP(ssid, pass);
  while (wifiMulti.run() != WL_CONNECTED) {
    delay(1000);
    Serial.println("trying to connect...");
  }
  Serial.print("connected to ");
  Serial.println(WiFi.SSID());
  Serial.print("address:\t");
  Serial.println(WiFi.localIP());

  if (!MDNS.begin("aditya_door", WiFi.localIP())) {
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
    req->send(200, "text/plain", "lol stop knocking pls ty. IP: " + WiFi.localIP());
  });
  server.on("/stateChange", HTTP_POST, postStateChange);
  server.begin();
  Serial.print("Adding Service... : ");
  Serial.println(MDNS.addService("esp8266door", "tcp", 8080));
}

/*
   Flow:
   save current state globally
   mdns.update()
   display current state on screen/leds
   wait for state change
   async check for
*/

void loop() {
  Serial.print("MDNS Update Status: ");
  Serial.println(MDNS.update());
  if (lcdRotateThread.shouldRun()) {
    lcdRotateThread.run();
  }
  if (ledBlinkThread.shouldRun()) {
    ledBlinkThread.run();
  }
}
