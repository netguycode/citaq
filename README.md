# Citaq H10 Root & Knowledge Base

<img src="https://github.com/mofosyne/Citaq-H10-3/assets/827793/d53050c3-d200-4b8a-860e-46131f8c2ff4" height="300">

My knowledge repository for my research on Citaq H10-3 Android All-in-one (Menulog Order Device/JustEat Order Device).

This is an attempt to consolidate the information I've found on the internet regarding these devices in order to help others in the future.

If theres something missing which you'd like to add, raise an issue on the github repo https://github.com/net-guy/citaq/issues.

---

* [Factory Reset Guide](./factory_reset_guide.md)
* [Printer Specification](./printer_spec.md)
* [Exploration Log - Net Guy](./netguy-exploration-log.md)
* [Exploration Log - Mofosyne](./mofosyne-exploration-log.md)

---

# Useful Info

## Citaq H10-3 Spec (Menulog-AU Order Device)

* CPU : A9 Quad Core
* Freqency : 1.8GHZ
* Memory : 1GB
* Flash : 4GB Nano
* Storage : Maximum 32G
* OS : Android 4.2 (Later models also include Android 5.1.1)
* Display : 8"& 10"Vertical , 10"Horizontal resolution 1024*768
* Touch : Screen Capacitive multi-touch
* Speaker: Dual Track 2W
* Indicator Light  : Programmable orage and blue
* 3G : Supports either embedded or optional 3G
* Wifi : Embedded WIFI 802.11b/g/n
* Power Supply : Adapter 5V/24V dual voltage input
* Ports : 3 USB, 1 RS232, 1 LAN, 1 RJ11
* Printer : Integrated Seiko 80mm printer
* Bluetooth : 2.1 & 4.0 Supported
* Speed : 200mm/s
* Printing Span : 200KM
* Sensor : Out of paper, open the cover
* Cutting Times 2 million times
* Cutting way : Half cut / full cut

These spec was originally transcribed from `./Images/spec.png`

## Printer Spec

* Model: CTE-RP80 (80mm Printer)
* Version: 5.62
* Command mode: Print speed: ESC/POS 200m (max)
* Characters Per Line:       48
* Auto Cutter:               Yes
* Beeper:                    Yes
* Auto clear buffer:         No
* NV bit image:              No
* Serial:                    115200, None, 8, 1, DTR/DSR

---

# Android Linux

### Hardware CPU/Board Identification Method

We can check the hardware ID of a CITAQ device by checking the `Hardware` field in `/proc/cpuinfo`.

E.g. In a CITAQ H10-3, when `cat /proc/cpuinfo | grep 'Hardware'` is executed you get `Hardware	: rockchip,rk3368`.

The SDK typically checks if the board type matches via checking if the hardware ID string matches the output of getCpuHardware()
(e.g: `MainBoardUtil.getCpuHardware().contains('RK3368')`) (Refer to the SDK Board type table for all possible board types)

You can emulate this SDK function in android shell via this bash function for your own purpose (e.g. calling via javascript android frameworks)

```bash
function getCpuHardware { cat /proc/cpuinfo | grep 'Hardware' | cut -d':' -f2 | cut -d',' -f2 | tr -d '[:space:]' | tr '[:lower:]' '[:upper:]'; }
getCpuHardware # call get cpu hardware check
```


### Hardware GPIO and interfaces

#### CTE-RP80 Internal Printer Serial Port
    - path: `/dev/ttyS1`
    - baud: 115200
    - flag: none
    - flow control: enabled
    - Uses ESC/POS protocol

Shell example for controlling the printer on a CITAQ H10-3 (with RK3368)

```bash
#[Set serial port speed]
#stty -F /dev/ttyS1 115200; # Missing from Android 6.0 toybox (Added on Android 9.0 (Pie) onward)
#[Enable serial port flow control]
#stty -F /dev/ttyS1 ixon # Missing from Android 6.0 toybox (Added on Android 9.0 (Pie) onward)

#Note: Looks like /dev/ttyS1 is already configured by CITAQ android distro with the correct settings on boot.

# Gather information
hardwareID=$(cat /proc/cpuinfo | grep 'Hardware')
eth0_ip4=$(ifconfig eth0 | grep -o 'inet addr:[0-9.]*'| cut -d':' -f2)
wlan0_ip4=$(ifconfig wlan0 | grep -o 'inet addr:[0-9.]*'| cut -d':' -f2)
locdate=$(date)
#[Send print test to the printer]
#initialize printer
echo -e '\x1b\x40'>/dev/ttyS1
# send printer data
echo -e '\x1b\x45 CITAQ Hardware Test:\x1b\x46\xa'>/dev/ttyS1
echo -e $locdate'' >/dev/ttyS1
echo -e $hardwareID'' >/dev/ttyS1
echo -e 'Hostname: '$HOSTNAME'' >/dev/ttyS1
echo -e 'eth0 IP address: '$eth0_ip4''>/dev/ttyS1
echo -e 'wlan0 IP address: '$wlan0_ip4''>/dev/ttyS1

# Barcode
echo -e '\x1d\x6b\x04000\x00'>/dev/ttyS1 # Not sure on this format... but it works...

# Code 39 Barcode [0x1D, 'h', <height>, 0x1D, 'w', <width>, 0x1D, 'H', <textHeight>, 0x1D, 'k', 0x45, <payloadLen>, <payloadData>...] (SDK: PrintBarcode() )
#echo -e '\x1dh\x60''\x1dw\x02''\x1dH\x02''\x1dk\x45''\x0cHello World'>/dev/ttyS1 # Not yet working...

# QR Code [0x1B, 0x51, 0x52, 0x03 : Error Correction layer, String Length (High), String Length (Low), string data ... (SDK: getSendQRCmd() )
#echo -e '\x1b\x51\x52\x03\x00\x0bHello World'>/dev/ttyS1 # Not yet working...

# Send Cut Command ; GS V 66D 0D [0x1D, 0x56, 0x42, 0x00] (SDK: CutPaper() )
echo -e '\x1d\x56\x42\x00'>/dev/ttyS1

# Open Cash Register ; DLE DC4 n m t [0x1B, 0x70, 0x00, 0xC0, 0xC0] (SDK: OpenCash() )
echo -e '\x1B\x70\x00\xC0\xC0'>/dev/ttyS1

# Enable Buzzer [1F 1B 1F 50 40] (SDK: getBuzzer() )
echo -e '\x1F\x1B\x1F\x50\x40'>/dev/ttyS1

# Disable Buzzer [1F 1B 1F 50 42] (SDK: getBuzzer() )
echo -e '\x1F\x1B\x1F\x50\x42'>/dev/ttyS1 # Not sure on the point of this command... still beeps
```

#### LED Status Bar
    - MainBoardUtil.RK3188 or MainBoardUtil.RK30BOARD
        - RED: `/sys/class/gpio/gpio190/value`
        - BLUE: `/sys/class/gpio/gpio172/value`
    - MainBoardUtil.RK3368
        - RED: `/sys/class/gpio/gpio124/value`
        - BLUE: `/sys/class/gpio/gpio106/value`

Shell example for controlling LED on a CITAQ H10-3 (with RK3368)

```bash
# Turn on red
echo "1" > /sys/class/gpio/gpio124/value
# Turn off red
echo "0" > /sys/class/gpio/gpio124/value
# Turn on blue
echo "1" > /sys/class/gpio/gpio106/value
# Turn off blue
echo "0" > /sys/class/gpio/gpio106/value
```


---

## SDK

`./CitaqSDK/`

A kind user from reddit has contacted the device maker Citaq and has forwarded on the Software Development Kit to us.
You can find it in the CitaqSDK folder in this repo.

### SDK POSFactory App Sourcecode of interest:

 - Print to internal printer test page handler: `./CitaqSDK/src/com/citaq/citaqfactory/PrintActivity.java`
 - Set status LED bar handler: `./CitaqSDK/src/com/citaq/citaqfactory/LedActivity.java`

### SDK Board type table

| SDK Recognised Board Type | hardware ID string (/proc/cpuinfo) |
|---------------------------|------------------------------------|
| MainBoardUtil.SMDKV210    | `SMDKV210`                         |
| MainBoardUtil.RK3188      | `RK30BOARD`                        |
| MainBoardUtil.RK30BOARD   | `SUN50IW1P1`                       |
| MainBoardUtil.MSM8625Q    | `QRD MSM8625Q SKUD`                |
| MainBoardUtil.RK3368      | `RK3368`                           |

### Possible Serial Ports (As shown in SDK)

| CitaqApplication.java func() | Serial Port Devices |   Baud | Flow Control | What it was used for in SDK           |
|------------------------------|---------------------|--------|--------------|---------------------------------------|
| getPrintSerialPort()         |        '/dev/ttyS1` | 115200 |         true | Internal Thermal Printer              |
| getPrintSerialPortMT()       |       '/dev/ttyMT0` | 115200 |         true | Unknown Usage                         |
| getMSRSerialPort()           |        '/dev/ttyS2` |  19200 |        false | Magnetic Stripe Reader                |
| getCtmDisplaySerialPort()    |        '/dev/ttyS3` |   9600 |        false | FSK Caller ID, ESC/POS Printer Device |
| getMSRSerialPort_S4()        |        '/dev/ttyS4` |  19200 |        false | Magnetic Stripe Reader                |


---

# Software Folder

`./Software/`

Contains various useful software useful in the investigation / development / usage of this device.

## ADB/USB Drivers

`./Software/usb_driver_r13-windows`

 - Downloaded ADB "usb drivers" from an online source.
 - Verified the ADB drivers for cleanliness using antivirus software.
 - Uncertain if the rockchip drivers are suitable for H10-3, a newer device model.
 - Rockchip driver referred to as chipset drivers originally obtained from https://gsmmobiledriver.com/citaq-h10.

## Print Proxy

`./Software/printproxy`

 - The app serves as an interface for connecting devices to the built-in serial (thermal) printer.
 - Functions precisely as described on the official website.
 - Included the latest available version in the "Software" folder as printproxy3.apk.
   - Priced at €14.99 for full version from them
 - [official website](https://citaq.co.uk/)

---

# External Development Resources

## Ionic Framework Capacitor Plugin

Plugin available at https://www.npmjs.com/package/capacitor-plugin-serialprinter

 - Utilize [Capacitor](https://capacitorjs.com/) plugin for printing within Ionic Web App on the H10 thermal printer.
 - Source code: https://github.com/realashleybailey/capacitor-plugin-serialprinter
 - **Developed by realashleybailey**

---

## Disclaimer
I am not affiliated in anyway with Citaq or Menulog(AU)/JustEat(Int). I do not endorse modifying your device as it may void any and all warranties that the device may have had prior. This may also be a breach in your Menulog(AU)/JustEat(Int) contract (if you are a resturant partner), so please proceed with caution.
