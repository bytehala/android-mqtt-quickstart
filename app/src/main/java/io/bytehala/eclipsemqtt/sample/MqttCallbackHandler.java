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

import io.bytehala.eclipsemqtt.sample.R;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import io.bytehala.eclipsemqtt.sample.Connection.ConnectionStatus;

/**
 * Handles call backs from the MQTT Client
 *
 */
public class MqttCallbackHandler implements MqttCallback {
  private String TAG = "MQTT Message";

  /** {@link Context} for the application used to format and import external strings**/
  private Context context;
  /** Client handle to reference the connection that this handler is attached to**/
  private String clientHandle;

  /**
   * Creates an <code>MqttCallbackHandler</code> object
   * @param context The application's context
   * @param clientHandle The handle to a {@link Connection} object
   */
  public MqttCallbackHandler(Context context, String clientHandle)
  {
    this.context = context;
    this.clientHandle = clientHandle;
  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
   */
  @Override
  public void connectionLost(Throwable cause) {
//	  cause.printStackTrace();
    if (cause != null) {
      Connection c = Connections.getInstance(context).getConnection(clientHandle);
      c.addAction("Connection Lost");
      c.changeConnectionStatus(ConnectionStatus.DISCONNECTED);

      //format string to use a notification text
      Object[] args = new Object[2];
      args[0] = c.getId();
      args[1] = c.getHostName();

      String message = context.getString(R.string.connection_lost, args);

      //build intent
      Intent intent = new Intent();
      intent.setClassName(context, "io.bytehala.eclipsemqtt.sample.ConnectionDetailsActivity");
      intent.putExtra("handle", clientHandle);

      //notify the user
      Notify.notifcation(context, message, intent, R.string.notifyTitle_connectionLost);
    }
  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {

    //Get connection object associated with this object
    Connection c = Connections.getInstance(context).getConnection(clientHandle);

    //create arguments to format message arrived notifcation string
    String[] args = new String[2];
    args[0] = new String(message.getPayload());
    args[1] = topic+";qos:"+message.getQos()+";retained:"+message.isRetained();

    //get the string from strings.xml and format
    @SuppressLint("StringFormatMatches") String messageString = context.getString(R.string.messageRecieved, (Object[]) args);

    //create intent to start activity
    Intent intent = new Intent();
    intent.setClassName(context, "io.bytehala.eclipsemqtt.sample.ConnectionDetailsActivity");
    intent.putExtra("handle", clientHandle);

    //format string args
    Object[] notifyArgs = new String[3];
    notifyArgs[0] = c.getId();
    notifyArgs[1] = new String(message.getPayload());
    notifyArgs[2] = topic;


    String phone_number = ((String) notifyArgs[1]). substring(0,13);

    // if topic was sms send message to other number
    if (topic.equals("sms")){
      sendMessage((String) notifyArgs[1], phone_number);
      Log.d(TAG, "messageArrived: "+ topic + "\t" + notifyArgs[1] );
    }
    //notify the user 
    Notify.notifcation(context, context.getString(R.string.notification, notifyArgs), intent, R.string.notifyTitle);

    //update client history
    c.addAction(messageString);

  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Do nothing
  }
  private void sendMessage(String message, String phone_number){
    Intent intent = new Intent(context, getClass());
    PendingIntent pi = PendingIntent.getActivity(context, 0,intent , 0);

    // if phone number was incorrect
    if (!phone_number.matches("\\+?\\d+")) {
      Log.d(TAG, "messageArrived: Incorrect phone number ");
      // set a default phone number
      // NOTE: If you want to use this project for yourself change this phone number!!
      phone_number = "+989375915077";
    } else
      message = message.substring(13);

    // send sms to phone number
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phone_number, null, message, pi , null);
  }

}
