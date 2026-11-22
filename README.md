# Citaq H10 Root & Knowledge Base

<img src="https://github.com/mofosyne/Citaq-H10-3/assets/827793/d53050c3-d200-4b8a-860e-46131f8c2ff4" height="300">

A comprehensive knowledge repository for research on the Citaq H10-3 Android All-in-one (Menulog Order Device/JustEat Order Device).

This repository consolidates technical documentation, hardware specifications, and research logs to help developers and enthusiasts understand and work with these devices.

## 🌟 Related Projects

**A huge shoutout to [@Crux4000](https://github.com/Crux4000) for the [citaq-h10-android10](https://github.com/Crux4000/citaq-h10-android10) project** — an impressive Android 10 port with PowerVR GPU acceleration for the H10-3! This project successfully:

- Upgrades the device from Android 5.1 (2016) to Android 10
- Enables GPU acceleration using the PowerVR G6110 (GLES 3.1)
- Maintains compatibility with the receipt printer, LEDs, touch, Wi-Fi, Bluetooth, and sound
- **Does NOT replace the bootloader or repartition** — the stock Android 5.1 remains as a fallback
- Boots in ~22 seconds and supports Home Assistant as a kiosk
- Provides comprehensive documentation and installation guides

If you're looking for a modern OS on your H10-3, this is **the reference implementation**.

---

If you find something missing or have additional information, please [open an issue](https://github.com/net-guy/citaq/issues).

---

## 📚 Documentation Index

* [Factory Reset Guide](./factory_reset_guide.md)
* [Printer Specification](./printer_spec.md)
* [Exploration Log - Net Guy](./netguy-exploration-log.md)
* [Exploration Log - Mofosyne](./mofosyne-exploration-log.md)
* [Exploration Log - Copilot ADB (H10-3 Survey 22 Nov 2026)](./copilot-adb-exploration-log.md)

---

# Useful Info

## Citaq H10-3 Spec (Menulog-AU Order Device)

**⚠️ Important Notes:**
- These are generic specifications transcribed from product images, **not guaranteed for every revision**
- A [comprehensive read-only ADB survey (22 Nov 2026)](./copilot-adb-exploration-log.md) of one H10-3 terminal reported:
  - **Processor**: 8× Cortex-A53 (not A9 as listed below)
  - **Hardware**: RK3368 (Rockchip), not generic "A9"
  - **RAM**: ~2 GB, **not 1 GB**
  - **OS**: Android 5.1.1 (not 4.2)
  - **Security Patch**: 2016-04-01 (severely outdated)
- **Always verify your device's actual properties** using `adb shell getprop` before making assumptions
- See [@Crux4000's detailed device list](https://github.com/Crux4000/citaq-h10-android10/blob/main/docs/DEVICES.md) for board variants

### Manufacturer Spec Sheet (May Not Match Your Device)
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

| Property | Value |
|----------|-------|
| Model | CTE-RP80 (80mm Printer) |
| Version | 5.62 |
| Command Mode | ESC/POS |
| Print Speed | 200 mm/s (max) |
| Characters Per Line | 48 |
| Auto Cutter | Yes (2 million cutting cycles rated) |
| Beeper | Yes (can be disabled) |
| Serial Port | `/dev/ttyS1` @ 115200 baud |
| Serial Settings | 8 data bits, 1 stop bit, no parity, DTR/DSR flow control |
| Auto Clear Buffer | No |
| NV Bit Image | No |

**See Also:**
- Crux4000's [Known Issues — Printer Beeps](https://github.com/Crux4000/citaq-h10-android10#known-issues) with fix command
- [Printer Self-Test Notes](./printer_spec.md) for detailed command sequences
- 2026 ADB Survey: [Printer Test Results](./copilot-adb-exploration-log.md#printer-test)

---

# Android Linux Hardware & Interfaces

## ✅ Verifying Your Actual Device Configuration

**Before modifying your device, determine its exact hardware. Use these commands:**

```bash
adb shell getprop ro.board.platform       # Board platform (e.g., rk3368)
adb shell getprop ro.product.device       # Product device string
adb shell getprop ro.build.type           # Build type (user vs userdebug)
adb shell getprop ro.debuggable           # ADB root access available?
adb shell cat /proc/cpuinfo               # CPU details
adb shell getprop ro.secure               # SELinux status
adb shell getenforce                      # SELinux enforcement level
```

**Device variants matter:** See [@Crux4000's device list](https://github.com/Crux4000/citaq-h10-android10/blob/main/docs/DEVICES.md) for all known H10-3 builds and their differences.

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
    - flow control: enabled (DTR/DSR)
    - Uses ESC/POS protocol

Shell example for controlling the printer on a CITAQ H10-3 (with RK3368)

**⚠️ Caution:** This historical example writes to the printer, feeds/cuts paper and changes buzzer state. It has **not been validated in the 2026 ADB survey**. Close other printer applications first. The optional cash-drawer command may open a connected drawer. Shell `echo -e` and NUL-byte handling are not portable, so use the SDK's byte-array interface for reliable binary output. The printer's current serial configuration has not been verified.

**For the latest, tested printer information:** See [@Crux4000's printer test results](https://github.com/Crux4000/citaq-h10-android10#printer-test) and [2026 ADB survey printer test](./copilot-adb-exploration-log.md#printer-test).

```bash
#[Set serial port speed]
#stty -F /dev/ttyS1 115200; # Missing from Android 6.0 toybox (Added on Android 9.0 (Pie) onward)

# ixon enables software XON/XOFF, not the DTR/DSR shown in the printer self-test.
# The current serial settings and native flow-control implementation need verification.

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

# Optional cash-drawer pulse: ESC p [0x1B, 0x70, 0x00, 0xC0, 0xC0] (SDK: OpenCash())
# echo -e '\x1B\x70\x00\xC0\xC0'>/dev/ttyS1

# Enable Buzzer [1F 1B 1F 50 40] (SDK: getBuzzer() )
echo -e '\x1F\x1B\x1F\x50\x40'>/dev/ttyS1

# Disable Buzzer [1F 1B 1F 50 42] (SDK: getBuzzer() )
# Use this command to disable the printer's alarm (usually means no paper or lid open)
echo -e '\x1F\x1B\x1F\x50\x42'>/dev/ttyS1
```

#### LED Status Bar

- **RK3188 or RK30BOARD boards:**
  - RED: `/sys/class/gpio/gpio190/value`
  - BLUE: `/sys/class/gpio/gpio172/value`
- **RK3368 boards:**
  - RED: `/sys/class/gpio/gpio124/value`
  - BLUE: `/sys/class/gpio/gpio106/value`

**See Also:** [Crux4000's verified LED test results (22 Nov 2026)](./copilot-adb-exploration-log.md#led-test)

Shell example for controlling LED on a CITAQ H10-3 (with RK3368)

**⚠️ Caution:** These commands change hardware state. GPIO assignments and permissions may vary between boards and firmware. Always verify your board type with `adb shell getprop ro.board.platform` before running LED commands.

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

## 🚀 Advanced: Android 10 Upgrade (Crux4000)

**Before diving into the SDK or modifying Android 5.1**, consider upgrading to Android 10 with the official Crux4000 port:

- ✅ **Modernization**: Upgrade from 2016's Android 5.1 to 2020's Android 10
- ✅ **GPU Acceleration**: Full PowerVR G6110 support (OpenGL ES 3.1) vs software rendering
- ✅ **Compatibility**: Modern browsers, Google Play, Home Assistant app all work
- ✅ **Non-destructive**: Stock bootloader unchanged, Android 5.1 remains as fallback
- 📖 **Well-documented**: Comprehensive installation and troubleshooting guides

**→ [Read Crux4000's Complete Guide](https://github.com/Crux4000/citaq-h10-android10)**

---

## SDK

**📝 Note:** This SDK is for the stock Android 5.1 environment. If you've upgraded to Android 10 via Crux4000's project, refer to that project's integration guides.

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

## 🙏 Credits & Contributors

This knowledge base exists because of the generosity of many people sharing their discoveries:

### Major Contributors

- **[@Crux4000](https://github.com/Crux4000)** — The [Android 10 port with GPU acceleration](https://github.com/Crux4000/citaq-h10-android10) is a monumental achievement. The 51 commits, comprehensive documentation, device tree work, and thermal fixes represent months of skilled engineering. This opened up these devices to modern use cases like Home Assistant.

- **[@mofosyne](https://github.com/mofosyne)** — Original consolidation of device research and [Exploration Log](./mofosyne-exploration-log.md) with detailed hardware findings.

- **[Net Guy](https://github.com/net-guy)** — Early [Exploration Log](./netguy-exploration-log.md) documenting the H10-3 baseline.

- **[@mildlycooldad](https://github.com/mildlycooldad)** — Beta tester for the Android 10 port who uncovered and helped fix critical bugs (eMMC numbering, ES8316 codec support, AP6255 Wi-Fi crashes, thermal issues). Acknowledged in Crux4000's credits.

### Community Resources

- The original Citaq SDK, shared through Reddit by a generous community member
- [Capacitor Serial Printer Plugin](https://www.npmjs.com/package/capacitor-plugin-serialprinter) by realashleybailey for Ionic apps
- Rockchip's public kernel sources and device tree documentation
- [Hal9k Firmware Library](https://hal9k.ru/?page_id=7) for preserving PX5 firmware
- Armbian's firmware repository for Bluetooth drivers

---

## Disclaimer
I am not affiliated in anyway with Citaq or Menulog(AU)/JustEat(Int). I do not endorse modifying your device as it may void any and all warranties that the device may have had prior. This may also be a breach in your Menulog(AU)/JustEat(Int) contract (if you are a resturant partner), so please proceed with caution.
