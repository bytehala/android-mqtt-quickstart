package io.bytehala.eclipsemqtt.sample.newconnection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.bytehala.eclipsemqtt.sample.ActivityConstants
import io.bytehala.eclipsemqtt.sample.Notify
import io.bytehala.eclipsemqtt.sample.R
import io.bytehala.eclipsemqtt.sample.addBackPressedCallback
import kotlinx.android.synthetic.main.old_activity_new_connection.*

class NewConnectionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = addBackPressedCallback {
            val actionConnect = NewConnectionFragmentDirections.actionConnect()
            findNavController().navigate(actionConnect)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.old_activity_new_connection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectButton.setOnClickListener {
//            //extract client information
//            Intent dataBundle = new Intent();
            val server = serverURI.text.toString()
            val port = port.text.toString()
            val clientId = clientId.text.toString()

            if ((server.isEmpty() || port.isEmpty() || clientId.isEmpty())) {
                val notificationText = getString(R.string.missingOptions)
                Notify.toast(requireContext(), notificationText, Toast.LENGTH_LONG)
                return@setOnClickListener
            }

            val cleanSession = cleanSessionCheckBox.isChecked



            val result = Bundle()
            
//            //put data into a bundle to be passed back to ClientConnections
            result.putString(ActivityConstants.server, server)
            result.putString(ActivityConstants.port, port)
            result.putString(ActivityConstants.clientId, clientId)
            result.putInt(ActivityConstants.action, ActivityConstants.connect)
            result.putBoolean(ActivityConstants.cleanSession, cleanSession)

            /**
             * Watch out, these settings are for when Advanced settings are empty
             */
            result.putString(ActivityConstants.message, ActivityConstants.empty)
            result.putString(ActivityConstants.message, ActivityConstants.empty)
            result.putString(ActivityConstants.topic, ActivityConstants.empty)
            result.putInt(ActivityConstants.qos, ActivityConstants.defaultQos)
            result.putBoolean(ActivityConstants.retained, ActivityConstants.defaultRetained)

            result.putString(ActivityConstants.username, ActivityConstants.empty)
            result.putString(ActivityConstants.password, ActivityConstants.empty)

            result.putInt(ActivityConstants.timeout, ActivityConstants.defaultTimeOut)
            result.putInt(ActivityConstants.keepalive, ActivityConstants.defaultKeepAlive)
            result.putBoolean(ActivityConstants.ssl, ActivityConstants.defaultSsl)

            val actionConnect = NewConnectionFragmentDirections.actionConnect(result)
            findNavController().navigate(actionConnect)
        }
    }

    /*
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
     */
}
