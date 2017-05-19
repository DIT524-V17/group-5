package se.gu.dit524.group5.bluetoothremote;

import android.support.annotation.NonNull;

/**
 * Created by julian.bock on 2017-03-10.
 */

public class Instruction implements Comparable {
    private byte[] cmd;
    private byte   crc;
    private long   priority;
    private int    btState;

    public Instruction(byte[] cmd) {
        this(cmd, 0, BluetoothService.IDLE);
    }

    public Instruction(byte[] cmd, int priority, int btState) {
        this.cmd = cmd;
        this.crc = calculateCRC8();
        this.priority = System.currentTimeMillis() -priority *1000;
        this.btState = btState;
    }

    public byte[] getCmd() {
        byte[] tmp = new byte[cmd.length +1];
        System.arraycopy(this.cmd, 0, tmp, 0, this.cmd.length);
        tmp[tmp.length -1] = crc;
        return tmp;
    }

    public long getPriority() {
        return priority;
    }

    public int getBtState() {
        return btState;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return (int)(this.priority -((Instruction) o).getPriority());
    }

    private byte calculateCRC8() {
        int polynomial  = 0xA7;
        int crc         = 0x63;
        for (int j = 0; j < this.cmd.length; j++) {
            for (int i = 0; i < 8; i++) {
                boolean b = ((this.cmd[j]   >> (7-i) & 1) == 1);
                boolean c7 = ((crc >> 7    & 1) == 1);
                crc <<= 1;
                if (c7 ^ b) crc ^= polynomial;
            }
        }
        return (byte)(crc &0xFF);
    }
}
