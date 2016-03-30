# android-mqtt-quickstart
Android Studio port of the Eclipse paho MQTT sample project.

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

Learn more about the other MQTT options such as QOS, Last Will, etc from this really helpful 12 part series by HiveMQ
http://www.hivemq.com/blog/mqtt-essentials/

### Create Your Own App
(All you need is an MQTT broker)

What you want from this project are the two .jar files inside the `libs/` directory.
If you want to generate your own version of these .jar files, they can be built from source using maven:
https://github.com/eclipse/paho.mqtt.java

(Honestly, when I made an MQTT app for a client, I just built on top of this sample project.)

### Using Eclipse
There's an Eclipse version available here: https://github.com/eclipse/paho.mqtt.java
