> :warning: **Please feel free to create issues if it does not run for you.

# android-mqtt-quickstart [![Build Status](https://travis-ci.org/bytehala/android-mqtt-quickstart.svg?branch=master)](https://travis-ci.org/bytehala/android-mqtt-quickstart)
Android Studio port of the Eclipse paho MQTT sample project.

### Environment Tested On
1. OpenJDK Java 11 by Amazon (sdkman 11.0.6-amzn)
2. Pixel XL API 29 emulator
3. Android Studio 3.6.2

I am maintaining this on my own, and as such am unable to test on multiple devices and environments.

You can support me buy contributing code, filing bugs, asking/answering questions, or buying me a coffee.

<a href='https://ko-fi.com/bytehala' target='_blank'>
  <img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' />
</a>

### Getting Started
1. Download the code `git clone http://www.github.com/bytehala/android-mqtt-quickstart`
2. Open the project in Android Studio
3. Build and run it.

### Using the Sample App
To test the app, you need an MQTT broker. Luckily, HiveMQ provides a free one which we can use for testing.
![HiveMQ broker](http://i.imgur.com/zStIVr4.png "MQTT Settings")

After successfully connecting, you can start subscribing and publishing to topics using the app.
![Subscribing](http://i.imgur.com/dPSryih.png "Subscribing")

![Success](http://i.imgur.com/gao1R0x.png "Success")

### More About MQTT
MQTT is a messaging protocol which has applications in the Internet of Things (IoT).
This sample project uses Eclipse's open-source implementation called the Paho Project.
If you go to the [original source](https://github.com/eclipse/paho.mqtt.java) where I lifted this project from, there are non-Android sample projects that use the Paho library.

This video explains the MQTT for IoT at a very basic level.
[![](http://img.youtube.com/vi/1XzC3WqmiBs/0.jpg)](http://www.youtube.com/watch?v=1XzC3WqmiBs "Basics of MQTT IoT")

Learn more about the other MQTT options such as QOS, Last Will, etc from this really helpful 12 part series by HiveMQ
http://www.hivemq.com/blog/mqtt-essentials/

### Create Your Own App
All you need is an MQTT broker.  
This app is just piggybacking on HiveMQ's free broker.

Take note of the dependencies in this project.
`org.eclipse.paho.android.service` and `org.eclipse.paho.client.mqttv3` depend on the old android-support-v4, specifically the Fragment support class.
Eclipse might update that someday, but as of now, we are unable to use the AndroidX version.

The eclipse sources can be found at:
https://github.com/eclipse/paho.mqtt.java

(Honestly, when I made an MQTT app for a client, I just built on top of this sample project.)
