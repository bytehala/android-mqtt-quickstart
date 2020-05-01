package io.bytehala.eclipsemqtt.sample;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.bytehala.eclipsemqtt.sample.newconnection.NewConnectionActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * Token to pass to the MQTT Service
     */
    final static String TOKEN = "io.bytehala.eclipsemqtt.sample.ClientConnections";

    /**
     * ArrayAdapter to populate the list view
     */
    private ArrayAdapter<Connection> arrayAdapter = null;

    /**
     * {@link MainActivity.ChangeListener} for use with all {@link Connection} objects created by this instance of <code>ClientConnections</code>
     */
    private MainActivity.ChangeListener changeListener = new MainActivity.ChangeListener();

    /**
     * This instance of <code>ClientConnections</code> used to update the UI in {@link MainActivity.ChangeListener}
     */
    private MainActivity clientConnections = this;

    /**
     * Contextual action bar active or not
     */
    private boolean contextualActionBarActive = false;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_main);

        listView = findViewById(R.id.list);
        listView.setOnItemLongClickListener(new MainActivity.LongClickItemListener());
        listView.setTextFilterEnabled(true); // TODO: What's this for?

        listView.setOnItemClickListener((listView, view, position, id) -> {

            if (!contextualActionBarActive) {
                Connection c = arrayAdapter.getItem(position);

                // start the connectionDetails activity to display the details about the
                // selected connection
                Intent intent = new Intent();
                intent.setClassName(getApplicationContext().getPackageName(),
                        "io.bytehala.eclipsemqtt.sample.connectiondetails.ConnectionDetailsActivity");
                Log.d("TEST", "handle: " +  c.handle());
                intent.putExtra("handle", c.handle());
                startActivity(intent);
            }
        });


        FloatingActionButton fab = findViewById(R.id.addConnectionFab);

        fab.setOnClickListener(view -> createAndConnect());

        arrayAdapter = new ArrayAdapter<>(this, R.layout.old_connection_text_view);
        listView.setAdapter(arrayAdapter);

        // get all available connections
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();


        if (connections != null) {
            List<String> cons = new ArrayList<>(connections.keySet());
            Collections.reverse(cons);
            for (String s : cons) {
                arrayAdapter.add(connections.get(s));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem.OnMenuItemClickListener menuItemClickListener = new Listener(this);

        //load the correct menu depending on the status of logging
        if (Listener.logging) {
            getMenuInflater().inflate(R.menu.activity_connections_logging, menu);
            menu.findItem(R.id.endLogging).setOnMenuItemClickListener(menuItemClickListener);
        } else {
            getMenuInflater().inflate(R.menu.activity_connections, menu);
            menu.findItem(R.id.startLogging).setOnMenuItemClickListener(menuItemClickListener);
        }

        menu.findItem(R.id.newConnection).setOnMenuItemClickListener(
                menuItemClickListener);

        return true;
    }

    private void createAndConnect() {
        Intent createConnection;

        //start a new activity to gather information for a new connection
        createConnection = new Intent();
        createConnection.setClassName(
                clientConnections.getApplicationContext(),
                "io.bytehala.eclipsemqtt.sample.newconnection.NewConnectionActivity");

        clientConnections.startActivityForResult(createConnection,
                ActivityConstants.connect);
    }

    /**
     * @see ListActivity#onActivityResult(int, int, Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        Bundle dataBundle = data.getExtras();

        // perform connection create and connect
        connectAction(dataBundle);

    }

    @Override
    protected void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();

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

        Map<String, Connection> connections = Connections.getInstance(this).getConnections();

        for (Connection connection : connections.values()) {
            connection.registerChangeListener(changeListener);
            connection.getClient().unregisterResources();
        }
        super.onDestroy();
    }

    /**
     * Process data from the connect action
     *
     * @param data the {@link Bundle} returned by the {@link NewConnectionActivity} Acitivty
     */
    private void connectAction(Bundle data) {
        MqttConnectOptions conOpt = new MqttConnectOptions();
        /*
         * Mutal Auth connections could do something like this
         *
         *
         * SSLContext context = SSLContext.getDefault();
         * context.init({new CustomX509KeyManager()},null,null); //where CustomX509KeyManager proxies calls to keychain api
         * SSLSocketFactory factory = context.getSSLSocketFactory();
         *
         * MqttConnectOptions options = new MqttConnectOptions();
         * options.setSocketFactory(factory);
         *
         * client.connect(options);
         *
         */

        // The basic client information
        String server = (String) data.get(ActivityConstants.server);
        String clientId = (String) data.get(ActivityConstants.clientId);
        String portString = (String) data.get(ActivityConstants.port);
        int port = Integer.parseInt((portString != null && !"".equals(portString) ? portString : "1883"));
        boolean cleanSession = (Boolean) data.get(ActivityConstants.cleanSession);

        boolean ssl = (Boolean) data.get(ActivityConstants.ssl);
        String ssl_key = (String) data.get(ActivityConstants.ssl_key);
        String uri = null;
        if (ssl) {
            Log.e("SSLConnection", "Doing an SSL Connect");
            uri = "ssl://";

        } else {
            uri = "tcp://";
        }

        uri = uri + server + ":" + port;

        MqttAndroidClient client;
        client = Connections.getInstance(this).createClient(this, uri, clientId);

        if (ssl) {
            try {
                if (ssl_key != null && !ssl_key.equalsIgnoreCase("")) {
                    FileInputStream key = new FileInputStream(ssl_key);
                    conOpt.setSocketFactory(client.getSSLSocketFactory(key,
                            "mqtttest"));
                }

            } catch (MqttSecurityException e) {
                Log.e(this.getClass().getCanonicalName(),
                        "MqttException Occured: ", e);
            } catch (FileNotFoundException e) {
                Log.e(this.getClass().getCanonicalName(),
                        "MqttException Occured: SSL Key file not found", e);
            }
        }

        // create a client handle
        String clientHandle = uri + clientId;

        // last will message
        String message = (String) data.get(ActivityConstants.message);
        String topic = (String) data.get(ActivityConstants.topic);
        Integer qos = (Integer) data.get(ActivityConstants.qos);
        Boolean retained = (Boolean) data.get(ActivityConstants.retained);

        // connection options

        String username = (String) data.get(ActivityConstants.username);

        String password = (String) data.get(ActivityConstants.password);

        int timeout = (Integer) data.get(ActivityConstants.timeout);
        int keepalive = (Integer) data.get(ActivityConstants.keepalive);

        Connection connection = new Connection(clientHandle, clientId, server, port,
                this, client, ssl);
        arrayAdapter.insert(connection, 0);

        connection.registerChangeListener(changeListener);
        // connect client

        String[] actionArgs = new String[1];
        actionArgs[0] = clientId;
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

        conOpt.setCleanSession(cleanSession);
        conOpt.setConnectionTimeout(timeout);
        conOpt.setKeepAliveInterval(keepalive);
        if (!username.equals(ActivityConstants.empty)) {
            conOpt.setUserName(username);
        }
        if (!password.equals(ActivityConstants.empty)) {
            conOpt.setPassword(password.toCharArray());
        }

        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, clientHandle, actionArgs);

        boolean doConnect = true;

        if ((!message.equals(ActivityConstants.empty))
                || (!topic.equals(ActivityConstants.empty))) {
            // need to make a message since last will is set
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(),
                        retained.booleanValue());
            } catch (Exception e) {
                Log.e(this.getClass().getCanonicalName(), "Exception Occured", e);
                doConnect = false;
                callback.onFailure(null, e);
            }
        }
        client.setCallback(new MqttCallbackHandler(this, clientHandle));


        //set traceCallback
        client.setTraceCallback(new MqttTraceCallback());

        connection.addConnectionOptions(conOpt);
        Connections.getInstance(this).addConnection(connection);
        if (doConnect) {
            try {
                client.connect(conOpt, null, callback);
            } catch (MqttException e) {
                Log.e(this.getClass().getCanonicalName(),
                        "MqttException Occured", e);
            }
        }

    }

    /**
     * <code>LongClickItemListener</code> deals with enabling and disabling the contextual action bar and
     * processing the actions selected.
     */
    private class LongClickItemListener implements AdapterView.OnItemLongClickListener, ActionMode.Callback, DialogInterface.OnClickListener {

        /**
         * The index of the item selected, or -1 if an item is not selected
         **/
        private int selected = -1;
        /**
         * The view of the item selected
         **/
        private View selectedView = null;
        /**
         * The connection the view is representing
         **/
        private Connection connection = null;

        /* (non-Javadoc)
         * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            clientConnections.startActionMode(this);
            selected = position;
            selectedView = view;
            listView.setSelection(position);
            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            return true;
        }

        /* (non-Javadoc)
         * @see android.view.ActionMode.Callback#onActionItemClicked(android.view.ActionMode, android.view.MenuItem)
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            selectedView.setBackgroundColor(getResources().getColor(android.R.color.white));
            switch (item.getItemId()) {
                case R.id.delete:
                    delete();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        /* (non-Javadoc)
         * @see android.view.ActionMode.Callback#onCreateActionMode(android.view.ActionMode, android.view.Menu)
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.delete, menu);
            clientConnections.contextualActionBarActive = true;
            return true;
        }

        /* (non-Javadoc)
         * @see android.view.ActionMode.Callback#onDestroyActionMode(android.view.ActionMode)
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            selected = -1;
            selectedView = null;

        }

        /* (non-Javadoc)
         * @see android.view.ActionMode.Callback#onPrepareActionMode(android.view.ActionMode, android.view.Menu)
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * Deletes the connection, disconnecting if required.
         */
        private void delete() {
            connection = arrayAdapter.getItem(selected);
            if (connection.isConnectedOrConnecting()) {

                //display a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(clientConnections);
                builder.setTitle(R.string.disconnectClient)
                        .setMessage(getString(R.string.deleteDialog))
                        .setNegativeButton(R.string.cancelBtn, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //do nothing user cancelled action
                            }
                        })
                        .setPositiveButton(R.string.continueBtn, this)
                        .show();
            } else {
                arrayAdapter.remove(connection);
                Connections.getInstance(clientConnections).removeConnection(connection);
            }

        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            //user pressed continue disconnect client and delete
            try {
                connection.getClient().disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            arrayAdapter.remove(connection);
            Connections.getInstance(clientConnections).removeConnection(connection);

        }
    }


    /**
     * This class ensures that the user interface is updated as the Connection objects change their states
     */
    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty)) {
                return;
            }
            clientConnections.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    clientConnections.arrayAdapter.notifyDataSetChanged();
                }

            });

        }

    }

}
