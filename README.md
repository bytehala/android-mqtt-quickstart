> :warning: **Please feel free to create issues if it does not run for you.

# Android MQTT Application
you can publish or subscribe on any topic you want

## Send SMS functionality is added
If you want to send sms, you have to first subscribe to sms topic in application
To set your phone number in messages received via sms topic, The thirteen first characters of your message will be the phone number you want to send
example:
```message topic: sms
payload: +989375915077 Hello
```
Sending message was tested on samsung galaxy mini s5570 SDK version 18 ( Cyanogen mod )

It is highly recommended to change the default phone number in ```src\main\java\io\bytehala\eclipsemqtt\sample\MqttCallbackHandler.java``` line 141

## Environment Tested On
1. OpenJDK Java 11 by Amazon (sdkman 11.0.6-amzn)
2. Pixel XL API 29 emulator
3. Android Studio 3.6.2

I am maintaining this on my own, and as such am unable to test on multiple devices and environments.

You can support me buy contributing code, filing bugs, asking/answering questions, or buying me a coffee.

<a href='https://ko-fi.com/bytehala' target='_blank'>
  <img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' />
</a>

## Getting Started
1. Download the code `git clone http://www.github.com/bytehala/android-mqtt-quickstart`
2. Open the project in Android Studio
3. Build and run it.

## Using the Sample App
To test the app, you need an MQTT broker. Luckily, HiveMQ provides a free one which we can use for testing.
![HiveMQ broker](http://i.imgur.com/zStIVr4.png "MQTT Settings")

After successfully connecting, you can start subscribing and publishing to topics using the app.
![Subscribing](http://i.imgur.com/dPSryih.png "Subscribing")

![Success](http://i.imgur.com/gao1R0x.png "Success")

## More About MQTT
MQTT is a messaging protocol which has applications in the Internet of Things (IoT).
This sample project uses Eclipse's open-source implementation called the Paho Project.
If you go to the [original source](https://github.com/eclipse/paho.mqtt.java) where I lifted this project from, there are non-Android sample projects that use the Paho library.

This youtube video explains the MQTT for IoT at a very basic level.

[![](http://img.youtube.com/vi/1XzC3WqmiBs/0.jpg)](http://www.youtube.com/watch?v=1XzC3WqmiBs "Basics of MQTT IoT")

Learn more about the other MQTT options such as QOS, Last Will, etc from this really helpful 12 part series by HiveMQ
http://www.hivemq.com/blog/mqtt-essentials/

## Create Your Own App
All you need is an MQTT broker.  
This app is just piggybacking on HiveMQ's free broker.

Take note of the dependencies in this project.
`org.eclipse.paho.android.service` and `org.eclipse.paho.client.mqttv3` depend on the old android-support-v4, specifically the LocalBroadcastManager class.

Maybe we can migrate to mqttv5 using the plain Java library at https://github.com/eclipse/paho.mqtt.java

The eclipse sources can be found at:
https://github.com/eclipse/paho.mqtt.android

Honestly, when I made an MQTT app for a client, I just built on top of this sample project.

# Word of Warning
This app was made in 2015-2016, and is a demo of how to use the Eclipse MQTT Libraries, not how to code in Android.

Architecture components are a thing now, and I strongly advise the use of ViewModel and LifecycleHook.

# Work In Progress
Definitely look at the "jetpacknav" branch which aims to transform everything into a Single-Activity application, which has been the Android recommendation since 2018. It's currently a work in progress but a clear example of how to transform legacy apps from the old Android paradigm (multiple-Activity) to the newer ones (single-Activity Jetpack).

# MQTT V3 vs V5
This project uses MQTT v3 and I will be looking into using v3 and v5 in the near future.

### Note: The Repository is forked to add sms functionality to application