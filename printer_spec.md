
# Printer Spec 

* Model: CTE-RP80 (80mm Printer)
* Version: 5.62
* Command mode: Print speed: ESC/POS 200m (max)
* Characters Per Line:       48
* Auto Cutter:               Yes
* Beeper:                    Yes
* Auto clear buffer:         No
* NV bit image:              No
* Serial:                    115200, None, 8, 1, DTR/DSR

## How was this information obtained

When using POSFactory app there is a print test section. 

<img src="https://github.com/mofosyne/Citaq-H10-3/assets/827793/141f3866-94f9-43d7-ac6f-25db855d4a68" height="300">

From that section, when you press `PRINT SELF TEST` it will print the self test.

This contains useful information on how to communicate to the serial printer that is connected internally via serial port.
	
```
CTE-RP80 
80mm Printer

Version:                   5.62
Command mode: Print speed: ESC/POS 200m (max)
Characters Per Line:       48
Auto Cutter:               Yes
Beeper:                    Yes
Auto clear buffer:         No
NV bit image:              No
Serial:                    115200, None, 8, 1, DTR/DSR

Resident Character: 
	Alphanumeric
	GBK 中文字特集
	BIG5體中文字符集
	KSC5601

Chinese character mode:      No
International character set: U.S.A. 
Default code page:           page0
Character code table:
	page 0 (PC437:Standard-Europe)
	page 1 (Katakana)
	page 2 (PC850:Multilingual)
	page 3 (PC860:Portuguese)
	page 4 (PC863:Canadian French) 
	page 5 (PC865:Nordic)
	page 6 (West-Europe)
	page 7 (Greek)
	page 13 (PC857 Turkish)
	page 14 (PC737:Greek)
	page 15 (PC928:Greek)
	page 16 (WPC1252)
	page 17 (PC866:Cyrillic#2)
	page 18 (PC852:Latin2) 
	page 19 (PC858:Euro)
	page 21 (PC874)
	page 33 (WPC775: Baltic Rim)
	page 34 (PC855:Cyrillic)
	page 36 (PC862:Hebrew)
	page 37 (PC864: Arabic)
	page 41 (PC1098:Farsi)
	page 46 (WPC1251:Cyrillic)
	page 47 (WPC1253:Greek)
	page 48 (WPC1254:Turkish)
	page 49 (WPC1255:Hebrew)
	page 50 (WPC1256:Arabic)
	page 51 (WPC1257: Baltic Rim)
	page 74 (Gujarati)
	page 254 (UTF-8)
```