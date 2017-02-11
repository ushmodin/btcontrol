package com.ushmodin.btcontrol;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    public static int REQUEST_BLUETOOTH = 1;
    private final BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
    private final List<DeviceItem> items = new ArrayList<>();
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                items.add(new DeviceItem(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                adapter.notifyDataSetChanged();
                Log.d("DEVICELIST", "Found new device: " + bluetoothDevice.getName());
            }
        }
    };
    private DeviceListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (!ba.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        Set<BluetoothDevice> bondedDevices = ba.getBondedDevices();
        for (BluetoothDevice bondedDevice : bondedDevices) {
            items.add(new DeviceItem(bondedDevice.getName(), bondedDevice.getAddress()));
        }
        ToggleButton scanBtn = (ToggleButton) findViewById(R.id.scanButton);
        scanBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.clear();
                    SettingsActivity.this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                    ba.startDiscovery();
                    Log.d("DEVICELIST", "Scan start");
                } else {
                    SettingsActivity.this.unregisterReceiver(receiver);
                    ba.cancelDiscovery();
                    Log.d("DEVICELIST", "Scan stop");
                }
            }
        });
        adapter = new DeviceListAdapter(this, items);
        ListView devicesLV = (ListView) findViewById(R.id.devicesListView);
        devicesLV.setAdapter(adapter);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeviceItem item = (DeviceItem) view.getTag();
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Connect to " + item.getName())
                        .setMessage("Are you sure to connect to " + item.getName() + "?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("DEVICELIST", "Selected item " + item.getName());
                                ba.cancelDiscovery();
                                SettingsActivity.this.unregisterReceiver(receiver);
                                SettingsActivity.this.setResult(RESULT_OK);
                                SettingsActivity.this.finish();
                            }
                        })
                        .show();
            }
        });
    }
}
