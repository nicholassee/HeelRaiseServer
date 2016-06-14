package heelraiseserver.heelraiseserver;

import android.bluetooth.BluetoothSocket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nicholas See on 05-Jun-16.
 */
public class ManageConnection extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BufferedInputStream mmBufInStream;
    private final static String TAG = "ManageConnectionThread";
    private String leftPressureReadings;
    private String rightPressureReadings;
    //to indicate if current stream is for left or right readings.
    // false indicate left, true indicate right
    private boolean leftRightFlag;

    /**
     * Interface Method for asyncTask to return result when
     * onPostExecute() is completed. Required due to lifecycle of asynctask
     * unable to bind currentLocationService in doInBackground() stage
     * @return N.A.
     */
    public interface AsyncResponse{
        void onTaskComplete(String leftReadings, String rightReadings);
    }

    public AsyncResponse delegate = null;

    public ManageConnection(BluetoothSocket socket, AsyncResponse delegate) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mmBufInStream = null;
        this.delegate = delegate;
        leftRightFlag = false;
        leftPressureReadings = "";
        rightPressureReadings = "";

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                // data will be following the protocol of leftreadings,rightreadings/
                mmBufInStream = new BufferedInputStream(mmInStream);
                while (mmBufInStream.available()>0)
                {
                    char c = (char)mmBufInStream.read();
                    // switch case to determine leftpressure readings, rightpressure readings
                    // or new set of readings
                    switch(c){
                        // if ',' set flag to indicate rightpressure readings will be received next
                        case ',':
                            leftRightFlag = true;
                            break;
                        // if '/', set flag to indicate leftpressure readings will be received next,
                        // new set of data and propagate data back to main UI.
                        case '/':
                            leftRightFlag = false;
                            delegate.onTaskComplete(leftPressureReadings, rightPressureReadings);
                            rightPressureReadings = "";
                            leftPressureReadings = "";
                            break;
                        // by default, depending on flag, concatenate values to their respective variables.
                        default:
                            if(leftRightFlag){
                                rightPressureReadings = rightPressureReadings + c;
                            }else{
                                leftPressureReadings = leftPressureReadings + c;
                            }
                            break;
                    }

                }
            } catch (IOException e) {
                cancel();
                e.printStackTrace();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }

    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
