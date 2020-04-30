/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package io.bytehala.eclipsemqtt.sample;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * This Class provides constants used for returning results from an activity 
 *
 */
public final class ActivityConstants {

  /** Application TAG for logs where class name is not used*/
  public static final String TAG = "MQTT Android";

  /*Default values **/

  /** Default QOS value*/
  public static final int defaultQos = 0;
  /** Default timeout*/
  public static final int defaultTimeOut = 1000;
  /** Default keep alive value*/
  public static final int defaultKeepAlive = 10;
  /** Default SSL enabled flag*/
  public static final boolean defaultSsl = false;
  /** Default message retained flag */
  public static final boolean defaultRetained = false;
  /** Default last will message*/
  public static final MqttMessage defaultLastWill = null;
  /** Default port*/
  public static final int defaultPort = 1883;

  /** Connect Request Code */
  public static final int connect = 0;
  /** AdvancedActivity Connect Request Code  **/
  public static final int advancedConnect = 1;
  /** Last will Request Code  **/
  public static final int lastWill = 2;
  /** Show History Request Code  **/
  public static final int showHistory = 3;

  /* Bundle Keys */

  /** Server Bundle Key **/
  public static final String server = "server";
  /** Port Bundle Key **/
  public static final String port = "port";
  /** ClientID Bundle Key **/
  public static final String clientId = "clientId";
  /** Topic Bundle Key **/
  public static final String topic = "topic";
  /** History Bundle Key **/
  public static final String history = "history";
  /** Message Bundle Key **/
  public static final String message = "message";
  /** Retained Flag Bundle Key **/
  public static final String retained = "retained";
  /** QOS Value Bundle Key **/
  public static final String qos = "qos";
  /** User name Bundle Key **/
  public static final String username = "username";
  /** Password Bundle Key **/
  public static final String password = "password";
  /** Keep Alive value Bundle Key **/
  public static final String keepalive = "keepalive";
  /** Timeout Bundle Key **/
  public static final String timeout = "timeout";
  /** SSL Enabled Flag Bundle Key **/
  public static final String ssl = "ssl";
  /** SSL Key File Bundle Key **/
  public static final String ssl_key = "ssl_key";
  /** Connections Bundle Key **/
  public static final String connections = "connections";
  /** Clean Session Flag Bundle Key **/
  public static final String cleanSession = "cleanSession";
  /** Action Bundle Key **/
  public static final String action = "action";

  /* Property names */

  /** Property name for the history field in {@link Connection} object for use with {@link java.beans.PropertyChangeEvent} **/
  public static final String historyProperty = "history";

  /** Property name for the connection status field in {@link Connection} object for use with {@link java.beans.PropertyChangeEvent} **/
  public static final String ConnectionStatusProperty = "connectionStatus";

  /* Useful constants*/

  /** Space String Literal **/
  public static final String space = " ";
  /** Empty String for comparisons **/
  public static final String empty = "";

}
