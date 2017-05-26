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
 * modified and implemented by julian.bock.
 */

public class BluetoothService {
    private Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private CommandQueueThread commandQueueThread;

    private PriorityQueue<Instruction> commandQueue;
    private byte[] scanBuffer;

    public Object mainActivity;
    public Method scanCallback;
    public Method automaticSteeringCallback;

    private int state;

    public static final int IDLE                       = 0;
    public static final int AWAITING_STEERING_CALLBACK = 1;
    public static final int SCANNING                   = 2;

    private static final int AWAITING_SCAN_RESULTS     = 4;

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

    public int state() {
        return this.state;
    }

    public boolean busy() {
        return this.state != IDLE && connectedThread.sending;
    }

    public void send(Instruction ins) {
        this.send(ins, false);
    }

    public void send(Instruction ins, boolean queue) {
        if (queue) commandQueue.add(ins);
        else if (this.connectedThread != null) this.connectedThread.write(ins.getCmd());
    }

    public class CommandQueueThread extends Thread {
        private boolean interrupted = false;
        private ConnectedThread connection;

        public CommandQueueThread(ConnectedThread connection) {
            this.connection = connection;
        }

        public void run() {
            while (!interrupted) {
                if (!busy() && commandQueue.size() > 0) {
                    state = commandQueue.peek().getBtState();
                    connection.write(commandQueue.peek().getCmd());
                }
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            this.interrupted = true;
        }
    }

    public class ConnectThread extends Thread {
        private final BluetoothAdapter mBluetoothAdapter;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mBluetoothAdapter = adapter;

            try {
                tmp = (BluetoothSocket) device.getClass().getMethod(
                        "createRfcommSocket", new Class[] {int.class}).invoke(device, 1);
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
                    commandQueueThread = new CommandQueueThread(connectedThread);
                    commandQueueThread.start();
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
            mmBuffer = new byte[512];
            int numBytes;
            while (this.mmSocket.isConnected()) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    if (sending) {
                        if (lastMsg[lastMsg.length -1] != mmBuffer[numBytes -1]) {
                            write(lastMsg);
                        }
                        else {
                            if (commandQueue.size() > 0 && Arrays.equals(lastMsg, commandQueue.peek().getCmd())) {
                                commandQueue.poll();
                            }

                            lastMsg = null;
                            sending = false;
                        }
                    }
                    else {
                        if (state == SCANNING && mmBuffer[0] == (byte)0xFF) {
                            state = AWAITING_SCAN_RESULTS;
                            scanBuffer = new byte[numBytes];
                            System.arraycopy(mmBuffer, 0, scanBuffer, 0, numBytes);
                        }
                        else if (state == AWAITING_SCAN_RESULTS) {
                            byte[] tmp = new byte[scanBuffer.length +numBytes];
                            System.arraycopy(scanBuffer, 0, tmp, 0, scanBuffer.length);
                            System.arraycopy(mmBuffer, 0, tmp, scanBuffer.length, numBytes);
                            scanBuffer = tmp;
                            if ((scanBuffer[1] << 8) + scanBuffer[2] +4 == (byte)scanBuffer.length) {
                                ScanResult scanResult = new ScanResult(scanBuffer, 3, scanBuffer.length -4);

                                state = IDLE;
                                try { scanCallback.invoke(mainActivity, scanResult); }
                                catch (Exception e) { e.printStackTrace(); }
                            }
                        }

                        if (state == AWAITING_STEERING_CALLBACK && (
                                mmBuffer[0] == (byte)0x2F ||
                                mmBuffer[0] == (byte)0x3F ||
                                mmBuffer[0] == (byte)0x4F )) {
                                try {
                                    state = IDLE;
                                    automaticSteeringCallback.invoke(mainActivity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }

                catch (IOException e) {
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