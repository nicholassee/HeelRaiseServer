package heelraiseserver.heelraiseserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //Adapter to interface with Bluetooth
    BluetoothAdapter BTAdapter;

    // For Bluetooth request
    public static int REQUEST_BLUETOOTH = 1;

    // To store context of the application
    Context context;

    // To store the list of Bluetooth devices
    ListView listView;

    SimpleAdapter mAdapter;

    // prepare the list of all records
    List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

    bluetoothReceiver bReciever;

    // for testing purposes
    BluetoothDevice testDevice;

    // Constant to store name of RPI
    private final static String SERVER_DEVICENAME = "Nexus 5";

    // For Spinner
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_server = (Button)findViewById(R.id.button_Server);
        context = getApplicationContext();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();

        // Onclick listener for button
//        btn_server.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // start server thread
//                Server mServer = new Server();
//                mServer.start();
//            }
//        });

//        System.out.println("Discovering");
//        bReciever = new bluetoothReceiver();
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(bReciever, filter);
//        fillMaps.clear();
//        mAdapter.notifyDataSetChanged();
//        BTAdapter.startDiscovery();

        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            CharSequence text = "Your mobile device does not support Bluetooth";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        else if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        // intent to make device discoverable for 300 seconds
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        Server mServer = new Server();
        mServer.start();
    }



    private class bluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            System.out.println("in broadcast intent");
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                System.out.println("Found action");
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("col_1", device.getName());
//                map.put("col_2", device.getAddress());
//                fillMaps.add(map);
//                mAdapter.notifyDataSetChanged();
//            }
        }
    }
}
