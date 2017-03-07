package se.gu.dit524.group5.bluetoothremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * As published on https://developer.android.com/guide/topics/connectivity/bluetooth.html,
 * modified by julian.bock.
 */

public class BluetoothService {

    public Handler handler;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice bluetoothDevice;
    public ConnectThread connectThread;
    public ConnectedThread connectedThread;

    public BluetoothService() {
        this.handler = new Handler();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void prepareConnection() {
        if (this.connectThread == null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    System.out.println(deviceName + " / " + deviceHardwareAddress);
                    if (deviceName.contains("Group5")) {
                        this.bluetoothDevice = device;
                        this.connectThread = new ConnectThread(this.bluetoothAdapter, this.bluetoothDevice);
                        this.connectThread.start();
                    }
                }
            }
        }
        else if (this.connectThread.isConnected && this.connectedThread == null) {
            this.connectedThread = new ConnectedThread(this.connectThread.mmSocket);
            this.connectedThread.start();
        }
    }

    public boolean connectionEstablished() {
        return connectThread != null && connectThread.isConnected && connectedThread != null;
    }

    public class ConnectThread extends Thread {
        private final BluetoothAdapter mBluetoothAdapter;
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public boolean isConnected;

        public ConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mBluetoothAdapter = adapter;
            mmDevice = device;

            try {
                tmp = (BluetoothSocket) device.getClass().getMethod(
                        "createRfcommSocket", new Class[] {int.class}).invoke(device,1);
            }
            catch (Exception e) {
                System.out.println("Socket's create() method failed:");
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                isConnected = true;

            } catch (IOException connectException) {
                try { mmSocket.close(); }
                catch (IOException closeException) {
                    System.out.println("Could not close the client socket:");
                    closeException.printStackTrace();
                }
                return;
            }
        }

        public void cancel() {
            try { mmSocket.close(); }
            catch (IOException e) {
                System.out.println("Could not close the client socket:");
                e.printStackTrace();
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private boolean sending = false;
        private byte[] lastMsg;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try { tmpIn = socket.getInputStream(); }
            catch (IOException e) {
                System.out.println("An error occurred when creating input stream:");
                e.printStackTrace();
            }
            try { tmpOut = socket.getOutputStream(); }
            catch (IOException e) {
                System.out.println("An error occurred when creating output stream:");
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    if (sending) {
                        if (lastMsg[lastMsg.length -1] != mmBuffer[numBytes -1]) write(lastMsg);
                        else { lastMsg = null; sending = false; }
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred when receiving data:");
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            if (this.sending) return;
            byte[] tmp = new byte[bytes.length +1];
            System.arraycopy(bytes, 0, tmp, 0, bytes.length);
            tmp[tmp.length -1] = crc8(bytes);
            bytes = tmp;
            lastMsg = bytes;
            this.sending = true;

            try { mmOutStream.write(bytes); }
            catch (IOException e) {
                System.out.println("An error occurred when sending data:");
                e.printStackTrace();
            }
        }

        public void cancel() {
            try { mmSocket.close(); }
            catch (IOException e) {
                System.out.println("An error occurred when closing the connection:");
                e.printStackTrace();
            }
        }
    }

    byte crc8(byte[] data) {
        int polynomial  = 0xA7;
        int crc         = 0x63;
        for (int j = 0; j < data.length; j++) {
            for (int i = 0; i < 8; i++) {
                boolean b = ((data[j]   >> (7-i) & 1) == 1);
                boolean c7 = ((crc >> 7    & 1) == 1);
                crc <<= 1;
                if (c7 ^ b) crc ^= polynomial;
            }
        }
        return (byte)(crc &0xFF);
    }
}