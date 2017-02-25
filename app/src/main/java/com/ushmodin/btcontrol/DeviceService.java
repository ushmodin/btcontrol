package com.ushmodin.btcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nikolay on 12.02.17.
 */

public class DeviceService {
    private final static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private final static UUID DEFAULT_UUID = UUID.randomUUID();
    private final static DeviceService INSTANCE = new DeviceService();
    public static final String LOG_TAG = "BLUETOOTH";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private BluetoothSocket bluetoothSocket;


    private DeviceService() {
    }


    public static DeviceService getINSTANCE() {
        return INSTANCE;
    }

    public void connectTo(String mac) throws IOException {
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(mac);
        bluetoothSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(DEFAULT_UUID);
        bluetoothSocket.connect();
    }

    public void send(final byte[] data) {
        if (bluetoothSocket != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        bluetoothSocket.getOutputStream().write(data);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Can't write", e);
                    }
                }
            });
        }
    }

    public void disconnect() {
        try {
            bluetoothSocket.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't close", e);
        }
    }
}
