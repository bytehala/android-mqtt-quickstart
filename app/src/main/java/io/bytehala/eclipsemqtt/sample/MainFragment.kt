package io.bytehala.eclipsemqtt.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.old_activity_main.*
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttSecurityException
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

class MainFragment : Fragment() {

    lateinit var arrayAdapter: ArrayAdapter<Connection>

    val changeListener = ChangeListener()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.old_activity_main, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addConnectionFab.setOnClickListener {
            findNavController().navigate(R.id.action_new_connection)
        }

        arrayAdapter = ArrayAdapter<Connection>(
            requireContext(),
            R.layout.old_connection_text_view
        )

        list.adapter = arrayAdapter

        // get all available connections
        val connections =
            Connections.getInstance(requireContext())
                .connections


        if (connections != null) {
            val cons: List<String?> =
                ArrayList(connections.keys)
            Collections.reverse(cons)
            for (s in cons) {
                arrayAdapter.add(connections[s])
            }
        }

        val safeArgs: MainFragmentArgs by navArgs()
        val result = safeArgs.result

        Log.d("TEST", "Result " + result?.getString(ActivityConstants.server))

        result?.let {connectAction(result)}
    }

    private fun connectAction(data: Bundle) {
        val conOpt = MqttConnectOptions()

        /**
         * Mutal Auth connections could do something like this
         *
         *
         * SSLContext context = SSLContext.getDefault()
         * context.init({new CustomX509KeyManager()},null,null) //where CustomX509KeyManager proxies calls to keychain api
         * SSLSocketFactory factory = context.getSSLSocketFactory()
         *
         * MqttConnectOptions options = new MqttConnectOptions()
         * options.setSocketFactory(factory)
         *
         * client.connect(options)
         *
         */

        // The basic client information
        val server = data.getString(ActivityConstants.server)
        val clientId = data.getString(ActivityConstants.clientId)
        val portString = data.getString(ActivityConstants.port)
        val port = Integer.parseInt((if (portString.isNullOrEmpty()) "1883" else portString))
        val cleanSession = data.getBoolean(ActivityConstants.cleanSession)

        val ssl = data.getBoolean(ActivityConstants.ssl)
        val ssl_key = data.getString(ActivityConstants.ssl_key)

        val uri = if (ssl) {
            "ssl://$server:$port"
        } else {
            "tcp://$server:$port"
        }

        val client = Connections.getInstance(requireContext()).createClient(requireContext(), uri, clientId)

        if (ssl) {
            try {
                if(!ssl_key.isNullOrEmpty()) {
                    val key = FileInputStream(ssl_key)
                    conOpt.socketFactory = client.getSSLSocketFactory(key,
                        "mqtttest")
                }
            } catch (e: MqttSecurityException) {
                Log.e(javaClass.canonicalName,
                    "MqttException Occured: ", e)
            } catch (e: FileNotFoundException) {
                Log.e(javaClass.canonicalName,
                    "MqttException Occured: SSL Key file not found", e)
            }
        }

        // create a client handle
        val clientHandle = uri + clientId
        // last will message

        val message = data.getString(ActivityConstants.message)
        val topic = data.getString(ActivityConstants.topic)
        val qos = data.getInt(ActivityConstants.qos)
        val retained = data.getBoolean(ActivityConstants.retained)

        // connection options
        val username = data.getString(ActivityConstants.username)
        val password = data.getString(ActivityConstants.password)

        val timeout = data.getInt(ActivityConstants.timeout)
        val keepalive = data.getInt(ActivityConstants.keepalive)

        val connection = Connection(clientHandle, clientId, server, port,
            requireContext(), client, ssl)
        arrayAdapter.insert(connection, 0)

        connection.registerChangeListener(changeListener)

        // connect client
        val actionArgs = clientId!!
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING)

        conOpt.isCleanSession = cleanSession
        conOpt.connectionTimeout = timeout
        conOpt.keepAliveInterval = keepalive
        if (!username.equals(ActivityConstants.empty)) {
            conOpt.userName = username
        }
        if (!password.equals(ActivityConstants.empty)) {
            conOpt.password = password?.toCharArray()
        }

        val callback = ActionListener(requireContext(), ActionListener.Action.CONNECT, clientHandle, actionArgs)
        var doConnect = true

        if (message!!.isNotEmpty() || topic!!.isNotEmpty()) {
            try {
                conOpt.setWill(topic, message.toByteArray(), qos,
                    retained)
            } catch (e: Exception) {
                Log.e(javaClass.getCanonicalName(), "Exception Occured", e)
                doConnect = false
                callback.onFailure(null, e)
            }
        }

        client.setCallback(MqttCallbackHandler(requireContext(), clientHandle))

        //set traceCallback
        client.setTraceCallback(MqttTraceCallback())

        connection.addConnectionOptions(conOpt)
        Connections.getInstance(requireContext()).addConnection(connection)
        if (doConnect) {
            try {
                client.connect(conOpt, null, callback)
            } catch (e: MqttException) {
                Log.e(javaClass.getCanonicalName(),
                    "MqttException Occured", e)
            }
        }
    }

    /**
     * This class ensures that the user interface is updated as the Connection objects change their states
     */
    inner class ChangeListener : PropertyChangeListener {
        /**
         * @see java.beans.PropertyChangeListener.propertyChange
         */
        override fun propertyChange(event: PropertyChangeEvent) {
            if (event.propertyName != ActivityConstants.ConnectionStatusProperty) {
                return
            }
            requireActivity().runOnUiThread(Runnable { this@MainFragment.arrayAdapter.notifyDataSetChanged() })
        }
    }

}


fun Fragment.addBackPressedCallback(callback: () -> Unit): OnBackPressedCallback {
    val result = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(this, result)
    return result
}