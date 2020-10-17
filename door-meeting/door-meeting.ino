#include "Wire.h"
#include "LiquidCrystal_I2C.h"

// create an LCD object (Hex address, # characters, # rows)
// my LCD display in on Hex address 27 and is a 20x4 version
LiquidCrystal_I2C lcd(0x27, 20, 4); 

void setup()
{
  lcd.init();

  lcd.backlight();
  lcd.setCursor(1, 0);
  lcd.print("This is");
  lcd.setCursor(1, 1);
  lcd.print("DIYI0T.com");
}

void loop(){}
