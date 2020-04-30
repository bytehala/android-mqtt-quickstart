package io.bytehala.eclipsemqtt.sample.newconnection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.bytehala.eclipsemqtt.sample.ActivityConstants
import io.bytehala.eclipsemqtt.sample.R
import kotlinx.android.synthetic.main.old_activity_new_connection.*

class NewConnectionFragment : Fragment() {

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

            val result = Bundle()
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
}