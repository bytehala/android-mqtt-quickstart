package org.eclipse.paho.android.service.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * Token to pass to the MQTT Service
     */
    final static String TOKEN = "org.eclipse.paho.android.service.sample.ClientConnections";

    /**
     * ArrayAdapter to populate the list view
     */
    private ArrayAdapter<Connection> arrayAdapter = null;

    /**
     * {@link ClientConnections.ChangeListener} for use with all {@link Connection} objects created by this instance of <code>ClientConnections</code>
     */
//    private ClientConnections.ChangeListener changeListener = new ClientConnections.ChangeListener();

    /**
     * This instance of <code>ClientConnections</code> used to update the UI in {@link ClientConnections.ChangeListener}
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
        setContentView(R.layout.client_connections);

        listView = findViewById(R.id.list);

        arrayAdapter = new ArrayAdapter<Connection>(this, R.layout.connection_text_view);
        listView.setAdapter(arrayAdapter);

        // get all available connections
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();

        if (connections != null) {
            for (String s : connections.keySet())
            {
                arrayAdapter.add(connections.get(s));
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem.OnMenuItemClickListener menuItemClickListener = new Listener(this);

        //load the correct menu depending on the status of logging
        if (Listener.logging)
        {
            getMenuInflater().inflate(R.menu.activity_connections_logging, menu);
            menu.findItem(R.id.endLogging).setOnMenuItemClickListener(menuItemClickListener);
        }
        else {
            getMenuInflater().inflate(R.menu.activity_connections, menu);
            menu.findItem(R.id.startLogging).setOnMenuItemClickListener(menuItemClickListener);
        }

        menu.findItem(R.id.newConnection).setOnMenuItemClickListener(
                menuItemClickListener);

        return true;
    }

    /**
     * This class ensures that the user interface is updated as the Connection objects change their states
     *
     *
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
