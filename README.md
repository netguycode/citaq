# Citaq H10 Root & Knowledge Base

<img src="https://github.com/mofosyne/Citaq-H10-3/assets/827793/d53050c3-d200-4b8a-860e-46131f8c2ff4" height="300">

My knowledge repository for my research on Citaq H10-3 Android All-in-one (Menulog Order Device/JustEat Order Device).

This is an attempt to consolidate the information I've found on the internet regarding these devices in order to help others in the future.

If theres something missing which you'd like to add, raise an issue on the github repo https://github.com/orbin/citaq/issues.

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

### Hardware CPU/Board types

| getCpuHardware() Recognised Board Type | hardware ID string (/proc/cpuinfo) |
|----------------------------------------|------------------------------------|
| SMDKV210                               | `SMDKV210`                         |
| RK3188                                 | `RK30BOARD`                        |
| RK30BOARD                              | `SUN50IW1P1`                       |
| MSM8625Q                               | `QRD MSM8625Q SKUD`                |
| RK3368                                 | `RK3368`                           |

### Hardware GPIO and interfaces

 - CTE-RP80 Internal Printer Serial Port
    - path: `/dev/ttyS1`
    - baud: 115200
    - flag: none
    - flow control: enabled
    - Uses ESC/POS protocol

 - LED Status Bar
    - MainBoardUtil.RK3188 or MainBoardUtil.RK30BOARD
        - RED: `/sys/class/gpio/gpio190/value`
        - BLUE: `/sys/class/gpio/gpio172/value`
    - MainBoardUtil.RK3368
        - RED: `/sys/class/gpio/gpio124/value`
        - BLUE: `/sys/class/gpio/gpio106/value`

### Possible Serial Ports (As shown in SDK)

| CitaqApplication.java func() | Serial Port Devices |   Baud | Flow Control | What it was used for in SDK           |
|------------------------------|---------------------|--------|--------------|---------------------------------------|
| getPrintSerialPort()         |        '/dev/ttyS1` | 115200 |         true | Internal Thermal Printer              |
| getPrintSerialPortMT()       |       '/dev/ttyMT0` | 115200 |         true | Unknown Usage                         |
| getMSRSerialPort()           |        '/dev/ttyS2` |  19200 |        false | Magnetic Stripe Reader                |
| getCtmDisplaySerialPort()    |        '/dev/ttyS3` |   9600 |        false | FSK Caller ID, ESC/POS Printer Device |
| getMSRSerialPort_S4()        |        '/dev/ttyS4` |  19200 |        false | Magnetic Stripe Reader                |


---

## SDK

`./CitaqSDK/`

A kind user from reddit has contacted the device maker Citaq and has forwarded on the Software Development Kit to us.
You can find it in the CitaqSDK folder in this repo.

### SDK POSFactory App Sourcecode of interest:

 - Print to internal printer test page handler: `./CitaqSDK/src/com/citaq/citaqfactory/PrintActivity.java`
 - Set status LED bar handler: `./CitaqSDK/src/com/citaq/citaqfactory/LedActivity.java`

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
