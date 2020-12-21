#include <ArduinoJson.h>

#include "Wire.h"
#include "LiquidCrystal_I2C.h"
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266mDNS.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <Thread.h>

// create an LCD object (Hex address, # characters, # rows)
// my LCD display in on Hex address 27 and is a 20x4 version
LiquidCrystal_I2C lcd(0x27, 20, 4); 
ESP8266WiFiMulti wifiMulti;
const char* ssid = "onhub";
const char* pass = "ekchotaghar";
const int serverPort = 8080;
const int greenLed = 12;
const int yellowLed = 13;
const int redLed = 14;

String curStatStr;
int statSize;
Thread lcdRotateThread = Thread();

ESP8266WebServer server(8080);

void rotateLcd() {
  lcd.setCursor(0, 0);
  char curStat[statSize];
  curStatStr.toCharArray(curStat, statSize + 1);
  lcd.print(curStat);
  // rotate array
  char tmp = curStat[0];
  for (int i = 1; i < statSize; i++) {
    curStat[i-1] = curStat[i];
  }
  curStat[statSize - 1] = tmp;
  curStatStr = String(curStat);
}

void setStatus(String lcdStat, String priority) {
  if (lcdStat.length() > 16) {
    curStatStr = lcdStat;
    lcdRotateThread.enabled = true;
    lcd.setCursor(1, 0);
    lcd.print(priority);
  } else {
    lcd.setCursor(0, 0);
    Serial.println(lcdStat);
    lcd.print(lcdStat);
    delay(250);
    lcd.setCursor(0, 1);
    lcd.print("Priority: " + String(priority));
  }
}

void postStateChange() {
  String body = server.arg("plain");
  Serial.println(body);

  DynamicJsonDocument doc(512);
  DeserializationError error = deserializeJson(doc, body);
  if (error) {
    Serial.print("error parsing json ");
    Serial.println(error.c_str());
    String msg = error.c_str();
    server.send(500, F("application/json"), "{\"error\": \"" + msg + "\"");
    return;
  }
  JsonObject obj = doc.as<JsonObject>();
  Serial.print(F("method: "));
  Serial.println(server.method());
  if (!server.method() == HTTP_POST) {
    server.send(501, F("application/json"), "{}");
    return;
  }
  if (!(obj.containsKey("lcd_msg") && obj.containsKey("priority"))) {
    doc["message"] = F("data fields need to include 'lcd_msg' and 'priority'");
    String buf;
    serializeJson(doc, buf);
    server.send(400, F("application/json"), buf);
    Serial.println("sent error - lcd_msg and priority need to exist");
    return;
  }
  Serial.println("starting state change...");
  String buf;
  serializeJson(doc, buf);
  setStatus(obj["lcd_msg"], obj["priority"]);
  server.send(201, F("application/json"), buf);
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
  
  if (!MDNS.begin("aditya_door")) {
    Serial.println("Err setting up MDNS responder");
  }
  Serial.println("responder started");
  lcdRotateThread.enabled = false;
  lcdRotateThread.onRun(rotateLcd);
  lcdRotateThread.setInterval(1500);
  server.on("/", HTTP_GET, []() {
        server.send(200, F("text/html"),
            F("Welcome to the REST Web Server"));
    });
  server.on(F("/stateChange"), HTTP_POST, postStateChange);
  server.begin();
}

/*
 * Flow:
 * save current state globally
 * mdns.update()
 * display current state on screen/leds
 * wait for state change
 * async check for 
 */

void loop(){
  if (lcdRotateThread.shouldRun()) {
    lcdRotateThread.run();
  }
  MDNS.update();
  server.handleClient();
}
