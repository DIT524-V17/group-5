package se.gu.dit524.group5.bluetoothremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * As published on https://developer.android.com/guide/topics/connectivity/bluetooth.html,
 * modified by julian.bock.
 */

public class BluetoothService {

    public Handler handler;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice bluetoothDevice;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private PriorityQueue<Instruction> commandQueue;

    private byte[] scanBuffer;
    protected Object mainActivity;
    protected Method scanCallback;
    protected Method automaticSteeringCallback;
    protected boolean awaitingSteeringCallback;
    private boolean awaitingScanResults;

    public BluetoothService() {
        this.commandQueue = new PriorityQueue<>();
        this.handler = new Handler();
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                if (deviceName.contains("Group5")) {
                    this.bluetoothDevice = device;
                    this.connectThread = new ConnectThread(this.bluetoothAdapter, this.bluetoothDevice);
                    this.connectThread.start();
                }
            }
        }
    }

    public void send(Instruction ins) {
        this.send(ins, false);
    }


    public void send(Instruction ins, boolean queue) {
        if (queue) commandQueue.add(ins);
        if (this.connectedThread != null) this.connectedThread.write(ins.getCmd());
    }

    public class ConnectThread extends Thread {
        private final BluetoothAdapter mBluetoothAdapter;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mBluetoothAdapter = adapter;

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

            while (true) {
                try {
                    Thread.sleep(1000);
                    mmSocket.connect();
                    connectedThread = new ConnectedThread(this.mmSocket);
                    connectedThread.start();
                    return;

                } catch (Exception connectException) {
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        System.out.println("Could not close the client socket:");
                        closeException.printStackTrace();
                    }
                    return;
                }
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
            mmBuffer = new byte[4096];
            int numBytes;
            while (this.mmSocket.isConnected()) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    if (sending) {
                        if (lastMsg[lastMsg.length -1] != mmBuffer[numBytes -1]) write(lastMsg);
                        else {
                            if (commandQueue.size() > 0
                                    && Arrays.equals(lastMsg, commandQueue.peek().getCmd())) {
                                commandQueue.poll();
                            }
                            lastMsg = null;
                            sending = false;

                            if (commandQueue.size() > 0
                                    && commandQueue.peek().getPriority() < System.currentTimeMillis()) {
                                this.write(commandQueue.peek().getCmd());
                            }
                        }
                    }
                    else {
                        if (!awaitingScanResults && mmBuffer[0] == (byte)0xFF) {
                            awaitingScanResults = true;
                            scanBuffer = new byte[numBytes];
                            System.arraycopy(mmBuffer, 0, scanBuffer, 0, numBytes);
                        }
                        else if (awaitingScanResults) {
                            byte[] tmp = new byte[scanBuffer.length +numBytes];
                            System.arraycopy(scanBuffer, 0, tmp, 0, scanBuffer.length);
                            System.arraycopy(mmBuffer, 0, tmp, scanBuffer.length, numBytes);
                            scanBuffer = tmp;
                            if ((scanBuffer[1] << 8) + scanBuffer[2] +4 == (byte)scanBuffer.length) {
                                ScanResult scanResult = new ScanResult(scanBuffer, 3, scanBuffer.length -4);

                                awaitingScanResults = false;
                                try { scanCallback.invoke(mainActivity, scanResult); }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                        }
                        if (awaitingSteeringCallback && mmBuffer[0] == (byte)0x2F) {
                            awaitingSteeringCallback = false;
                            try { automaticSteeringCallback.invoke(mainActivity); }
                            catch (Exception e) { e.printStackTrace(); }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred when receiving data:");
                    e.printStackTrace();
                    try { mmSocket.close(); } catch (IOException ex) { ex.printStackTrace(); }
                }
            }
        }

        public void write(byte[] bytes) {
            if (this.sending) return;
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
}