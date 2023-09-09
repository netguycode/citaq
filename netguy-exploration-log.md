
# Exploration Log

## Rebooting into Recovery ROM 
So it seems citaq have included some sort of recovery ROM once you wipe the device. As you would expect, it includes an oudated copy of Soti MobiControl software, a factory app to test the device, standard android utilities and a simple APK management utility. The factory app had a few more features than I was expecting but hinted nothing towards an SDK (I was being very hopeful.) I will include a copy of the factory app in the "software" folder in this repo.

## Connectivity Issues

After factory resetting this device, I encountered significant connectivity issues with Bluetooth, WiFi, and Ethernet. As a part of troubleshooting, I examined the contents of the TF card attached to the device, where I discovered a copy of the MobiControl software and an unrelated APN package. Despite installing the APN package, the network and wireless problems persisted. I am currently actively seeking solutions and hardware drivers online (just need to crack open the device and get a few part numbers now).

## Manual Device Rooting

I am also exploring the possibility of manually rooting the device. The operating system is Android version 5.1.1, and it lacks the capability to receive automatic security updates—an issue I am keen to address.
(An issue im keen to address, also the possibilty in updating the OS to a newer version)

## ADB/USB Drivers

For ADB/USB drivers, I have downloaded the ADB "usb drivers" from an online source, and they have been verified as clean by my antivirus software. Furthermore, I have acquired a rockchip driver, as the webpage where I obtained them referred to these as chipset drivers, which may prove useful in the future. However, I am unsure if the rockchip drivers are the correct ones for my H10-3, a newer model of the device. You can find these drivers at https://gsmmobiledriver.com/citaq-h10.

## Ionic Framework Capacitor Plugin

[Capacitor](https://capacitorjs.com/) plugin for printing to the H10 thermal printer from inside Ionic Web App.

- https://www.npmjs.com/package/capacitor-plugin-serialprinter
    - Source: https://github.com/realashleybailey/capacitor-plugin-serialprinter
    - **Credit: realashleybailey**

## Additional advice: Removing Just Eat App
Advice found at: https://leeatljs.blogspot.com/2020/10/factory-reset-citaq-h10.html (Comments section)
1. Go To Just Eat WiFi Settings - Go To Top Right Add Network
2. It Will then try to load their Mobile Control App
3. Go To Bottom left the Return Curly Arrow
4. keep hitting it until iy takes you to the normal start page
5. Go to Settings > Apps
6. Uninstall all Just Eat Apps - there are about 4
7. Then Go to do reset

## Print Proxy

This app serves works great as an interface for connecting your device to the built-in serial (thermal) printer. 
It is priced at €14.99 and functions precisely as described on the [official website](https://citaq.co.uk/)

I've included the most up to date version avaliable. See "Software' folder for `printproxy3.apk`.
