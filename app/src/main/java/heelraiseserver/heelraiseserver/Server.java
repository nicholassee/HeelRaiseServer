package heelraiseserver.heelraiseserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Nicholas See on 05-Jun-16.
 */
public class Server extends Thread{
    private final BluetoothServerSocket mmServerSocket;

    // Get the default adapter
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private static final String TAG = "ServerThread";

    // Name for the SDP record when creating server socket
    private static final String NAME = "BluetoothChat";

    // Unique UUID for this application
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public Server() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        Log.i(TAG, "Server thread running");
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                Log.i(TAG, "Server thread listening to socket");
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                ManageConnection mConnection = new ManageConnection(socket, new ManageConnection.AsyncResponse() {
                    @Override
                    public void onTaskComplete(String leftReading, String rightReading) {
                        //do nothing
                    }
                });
                mConnection.start();
                //sample data to send
                // data will be following the protocol of leftreadings,rightreadings/
                Log.i(TAG, "Connected!");
                String[] mockData = {"11,25/" , "33,22/", "133,55/", "44,12/", "40,8/", "98,88/", "70,86/", "85,23/", "55,78/", "31,15/"};
                for(int j=0; j<10; j++) {
                    for (int i = 0; i < 10; i++) {
                        try {
                            // sleep to simulate 100 milliseconds sending from RPI
                            Thread.sleep(300);
                            mConnection.write(mockData[i].getBytes());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}

