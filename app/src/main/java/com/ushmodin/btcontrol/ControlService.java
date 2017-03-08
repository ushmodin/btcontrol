package com.ushmodin.btcontrol;

import android.util.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;

/**
 * Created by nikolay on 04.03.17.
 */
// 3   3   8        8        10
// 000 000 00000000 00000000 0000000000
// XOR CMD DriveX   DriveY   EMPTY
public class ControlService {
    private static float lastX, lastY;
    private static Date lastCommandTime = null;


    public static void drive(float X, float Y) {
        if (lastCommandTime != null && new Date().getTime() - lastCommandTime.getTime() < 50 && Math.pow(lastX - X, 2) + Math.pow(lastY - Y, 2) < 0.05) {
            return;
        }

        float left = Y;
        float right = Y;
        if (X < -0.01) {
            right = right * (X + 1);
        } else if (X > 0.01) {
            left = left * (1 - X);
        }

        byte command = 1;
        byte [] data = new byte[] {
                command, (byte)(left * 127), (byte)(right * 127), 0
        };
        sign(data, checksum(data));
        Log.d("DATA", Arrays.toString(data));
        DeviceService.getINSTANCE().send(data);
        lastX = X;
        lastY = Y;
        lastCommandTime = new Date();
    }



    public static void stop() {
        byte [] data = new byte[] {
                2, 0, 0, 0
        };
        sign(data, checksum(data));
        DeviceService.getINSTANCE().send(data);
    }

    // 00000000 00000000 00000000 00000000
    private static int checksum(byte []data) {
        int v = 0;
        v ^= ((int)data[3] << 28) >>> 28;
        v ^= ((int)data[3] << 24) >>> 28;
        v ^= ((int)data[2] << 28) >>> 28;
        v ^= ((int)data[2] << 24) >>> 28;
        v ^= ((int)data[1] << 28) >>> 28;
        v ^= ((int)data[1] << 24) >>> 28;
        v ^= ((int)data[0] << 28) >>> 28;
        return v;
    }

    private static void sign(byte [] data, int s) {
        data[0] |= s << 4;
    }

    public static void main(String[] args) {

    }
}
