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
package io.bytehala.eclipsemqtt.sample.connectiondetails;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import io.bytehala.eclipsemqtt.sample.ActionListener;
import io.bytehala.eclipsemqtt.sample.ActivityConstants;
import io.bytehala.eclipsemqtt.sample.Connection;
import io.bytehala.eclipsemqtt.sample.Connections;
import io.bytehala.eclipsemqtt.sample.Listener;
import io.bytehala.eclipsemqtt.sample.MqttCallbackHandler;
import io.bytehala.eclipsemqtt.sample.R;

/**
 * The connection details activity operates the fragments that make up the
 * connection details screen.
 * <p>
 * The fragments which this FragmentActivity uses are
 * <ul>
 * <li>{@link HistoryFragment}
 * <li>{@link PublishFragment}
 * <li>{@link SubscribeFragment}
 * </ul>
 * 
 */
public class ConnectionDetailsActivity extends AppCompatActivity implements
    ActionBar.TabListener {

  /**
   * {@link SectionsPagerAdapter} that is used to get pages to display
   */
  SectionsPagerAdapter sectionsPagerAdapter;
  /**
   * {@link ViewPager} object allows pages to be flipped left and right
   */
  ViewPager viewPager;

  /** The currently selected tab **/
  private int selected = 0;

  /**
   * The handle to the {@link Connection} which holds the data for the client
   * selected
   **/
  private String clientHandle = null;

  /** This instance of <code>ConnectionDetailsActivity</code> **/
  private final ConnectionDetailsActivity connectionDetails = this;

  /**
   * The instance of {@link Connection} that the <code>clientHandle</code>
   * represents
   **/
  private Connection connection = null;

  /**
   * The {@link ChangeListener} this object is using for the connection
   * updates
   **/
  private ChangeListener changeListener = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    clientHandle = getIntent().getStringExtra("handle");

    //clientHandle: tcp://broker.hivemq.com:1883random111
//    clientHandle: tcp://broker.hivemq.com:1883random111
    Log.d("TEST", "clientHandle: " + clientHandle);

    setContentView(R.layout.old_activity_connection_details);
    // Create the adapter that will return a fragment for each of the pages
    sectionsPagerAdapter = new SectionsPagerAdapter(
        getSupportFragmentManager());

    // Set up the action bar for tab navigation
    final ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    // add the sectionsPagerAdapter
    viewPager = (ViewPager) findViewById(R.id.pager);
    viewPager.setAdapter(sectionsPagerAdapter);

    viewPager
        .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

          @Override
          public void onPageSelected(int position) {
            // select the tab that represents the current page
            actionBar.setSelectedNavigationItem(position);

          }
        });

    // Create the tabs for the screen
    for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
      ActionBar.Tab tab = actionBar.newTab();
      tab.setText(sectionsPagerAdapter.getPageTitle(i));
      tab.setTabListener(this);
      actionBar.addTab(tab);
    }

    connection = Connections.getInstance(this).getConnection(clientHandle);
    changeListener = new ChangeListener();
    connection.registerChangeListener(changeListener);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //Recover connections.
    Map<String, Connection> connections = Connections.getInstance(this).getConnections();

    //Register receivers again
    for (Connection connection : connections.values()) {
      connection.getClient().registerResources(this);
      connection.getClient().setCallback(new MqttCallbackHandler(this, connection.getClient().getServerURI() + connection.getClient().getClientId()));
    }
  }

  @Override
  protected void onDestroy() {
    connection.removeChangeListener(null);

    Map<String, Connection> connections = Connections.getInstance(this).getConnections();

    for (Connection connection : connections.values()) {
      connection.registerChangeListener(changeListener);
      connection.getClient().unregisterResources();
    }
    super.onDestroy();
  }

  /**
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    int menuID;
    Integer button = null;
    boolean connected = Connections.getInstance(this)
        .getConnection(clientHandle).isConnected();

    // Select the correct action bar menu to display based on the
    // connectionStatus and which tab is selected
    if (connected) {

      switch (selected) {
        case 0 : // history view
          menuID = R.menu.activity_connection_details;
          break;
        case 1 : // subscribe view
          menuID = R.menu.activity_subscribe;
          button = R.id.subscribe;
          break;
        case 2 : // publish view
          menuID = R.menu.activity_publish;
          button = R.id.publish;
          break;
        default :
          menuID = R.menu.activity_connection_details;
          break;
      }
    }
    else {
      switch (selected) {
        case 0 : // history view
          menuID = R.menu.activity_connection_details_disconnected;
          break;
        case 1 : // subscribe view
          menuID = R.menu.activity_subscribe_disconnected;
          button = R.id.subscribe;
          break;
        case 2 : // publish view
          menuID = R.menu.activity_publish_disconnected;
          button = R.id.publish;
          break;
        default :
          menuID = R.menu.activity_connection_details_disconnected;
          break;
      }
    }
    // inflate the menu selected
    getMenuInflater().inflate(menuID, menu);
    Listener listener = new Listener(this, clientHandle);
    // add listeners
    if (button != null) {
      // add listeners
      menu.findItem(button).setOnMenuItemClickListener(listener);
      if (!Connections.getInstance(this).getConnection(clientHandle)
          .isConnected()) {
        menu.findItem(button).setEnabled(false);
      }
    }
    // add the listener to the disconnect or connect menu option
    if (connected) {
      menu.findItem(R.id.disconnect).setOnMenuItemClickListener(listener);
    }
    else {
      menu.findItem(R.id.connectMenuOption).setOnMenuItemClickListener(
              item -> {
                reconnect();
                return true;
              });
    }

    return true;
  }

  /**
   * @see android.app.ActionBar.TabListener#onTabUnselected(android.app.ActionBar.Tab,
   *      android.app.FragmentTransaction)
   */
  @Override
  public void onTabUnselected(ActionBar.Tab tab,
      FragmentTransaction fragmentTransaction) {
    // Don't need to do anything when a tab is unselected
  }

  /**
   * @see android.app.ActionBar.TabListener#onTabSelected(android.app.ActionBar.Tab,
   *      android.app.FragmentTransaction)
   */
  @Override
  public void onTabSelected(ActionBar.Tab tab,
      FragmentTransaction fragmentTransaction) {
    // When the given tab is selected, switch to the corresponding page in
    // the ViewPager.
    viewPager.setCurrentItem(tab.getPosition());
    selected = tab.getPosition();
    // invalidate the options menu so it can be updated
    invalidateOptionsMenu();

    updateButtons();


    // history fragment is at position zero so get this then refresh its
    // view
    ((HistoryFragment) sectionsPagerAdapter.getItem(0)).refresh();
  }

  /**
   * @see android.app.ActionBar.TabListener#onTabReselected(android.app.ActionBar.Tab,
   *      android.app.FragmentTransaction)
   */
  @Override
  public void onTabReselected(ActionBar.Tab tab,
      FragmentTransaction fragmentTransaction) {
    // Don't need to do anything when the tab is reselected
  }

  /**
   * Provides the Activity with the pages to display for each tab
   * 
   */
  public class SectionsPagerAdapter extends FragmentPagerAdapter {

    // Stores the instances of the pages
    private ArrayList<Fragment> fragments = null;

    /**
     * Only Constructor, requires a the activity's fragment managers
     * 
     * @param fragmentManager
     */
    public SectionsPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
      fragments = new ArrayList<Fragment>();
      // create the history view, passes the client handle as an argument
      // through a bundle
      Fragment fragment = new HistoryFragment();
      Bundle args = new Bundle();
      args.putString("handle", getIntent().getStringExtra("handle"));
      fragment.setArguments(args);
      // add all the fragments for the display to the fragments list
      fragments.add(fragment);
      fragments.add(new SubscribeFragment());
      fragments.add(new PublishFragment());

    }

    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    /**
     * 
     * @see FragmentPagerAdapter#getPageTitle(int)
     */
    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0 :
          return getString(R.string.history).toUpperCase();
        case 1 :
          return getString(R.string.subscribe).toUpperCase();
        case 2 :
          return getString(R.string.publish).toUpperCase();
      }
      // return null if there is no title matching the position
      return null;
    }

  }

  /**
   * <code>ChangeListener</code> updates the UI when the {@link Connection}
   * object it is associated with updates
   * 
   */
  private class ChangeListener implements PropertyChangeListener {

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      // connection object has change refresh the UI

      connectionDetails.runOnUiThread(new Runnable() {

        @Override
        public void run() {
          connectionDetails.invalidateOptionsMenu();
          ((HistoryFragment) connectionDetails.sectionsPagerAdapter
              .getItem(0)).refresh();

          updateButtons();

        }
      });

    }
  }

  private void updateButtons() {
    boolean connected = Connections.getInstance(connectionDetails)
            .getConnection(clientHandle).isConnected();

    if(selected == 2) {
      connectionDetails.findViewById(R.id.publishButton).setEnabled(connected);
      connectionDetails.findViewById(R.id.publishButton).setOnClickListener(
              view -> publish()
      );
    }
    if(selected == 1) {
      connectionDetails.findViewById(R.id.subscribeButton).setEnabled(connected);
      connectionDetails.findViewById(R.id.subscribeButton).setOnClickListener(
              view -> subscribe()
      );
    }
  }

  /**
   * Subscribe to a topic that the user has specified
   */
  private void subscribe()
  {
    String topic = ((EditText) connectionDetails.findViewById(R.id.topic)).getText().toString();
    ((EditText) connectionDetails.findViewById(R.id.topic)).getText().clear();

    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosSubRadio);
    int checked = radio.getCheckedRadioButtonId();
    int qos = ActivityConstants.defaultQos;

    switch (checked) {
      case R.id.qos0 :
        qos = 0;
        break;
      case R.id.qos1 :
        qos = 1;
        break;
      case R.id.qos2 :
        qos = 2;
        break;
    }

    try {
      String[] topics = new String[1];
      topics[0] = topic;
      Log.d("TEST", "Subscribing to topic " + topics[0]);
      Connections.getInstance(this).getConnection(clientHandle).getClient()
              .subscribe(topic, qos, null, new ActionListener(this, ActionListener.Action.SUBSCRIBE, clientHandle, topics));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
  }

  /**
   * Publish the message the user has specified
   */
  private void publish()
  {
    String topic = ((EditText) connectionDetails.findViewById(R.id.lastWillTopic))
            .getText().toString();

    ((EditText) connectionDetails.findViewById(R.id.lastWillTopic)).getText().clear();

    String message = ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText()
            .toString();

    ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText().clear();

    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosRadio);
    int checked = radio.getCheckedRadioButtonId();
    int qos = ActivityConstants.defaultQos;

    switch (checked) {
      case R.id.qos0 :
        qos = 0;
        break;
      case R.id.qos1 :
        qos = 1;
        break;
      case R.id.qos2 :
        qos = 2;
        break;
    }

    boolean retained = ((CheckBox) connectionDetails.findViewById(R.id.retained))
            .isChecked();

    String[] args = new String[2];
    args[0] = message;
    args[1] = topic+";qos:"+qos+";retained:"+retained;

    try {
      Connections.getInstance(this).getConnection(clientHandle).getClient()
              .publish(topic, message.getBytes(), qos, retained, null, new ActionListener(this, ActionListener.Action.PUBLISH, clientHandle, args));
    } catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }

  }

  private void reconnect() {

    Log.d("TEST", "Reconnecting");

    Connections.getInstance(this).getConnection(clientHandle).changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

    Connection c = Connections.getInstance(this).getConnection(clientHandle);
    try {
      c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(getApplicationContext(), ActionListener.Action.CONNECT, clientHandle));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }

  }

}
