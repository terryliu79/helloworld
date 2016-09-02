package com.test.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothReceiver receiver;
    private Button On,Off,list;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
//    private Bluetooth client;
    private List<String> devices;
    private List<BluetoothDevice> deviceList;
    private final String lockName = "BOLUTEK";
    private final UUID MY_UUID=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        On = (Button)findViewById(R.id.bturnon);
        Off = (Button)findViewById(R.id.bturnoff);
        list = (Button)findViewById(R.id.blist);
//
        lv = (ListView)findViewById(R.id.listView);
        deviceList = new ArrayList<BluetoothDevice>();
        devices = new ArrayList<String>();
        BA = BluetoothAdapter.getDefaultAdapter();
    }
    public void turnOnBluetooth(View view){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn,0);
            Toast.makeText(getApplicationContext(),"Turned on"
                    ,Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Already on",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void turnoffBluetooth(View view){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,

                Toast.LENGTH_LONG).show();
    }
    public void list(View view){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());

        Toast.makeText(getApplicationContext(),"Showing Paired Devices",
                Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

    }
    public void visible(View view){
        Intent getVisible = new Intent(BluetoothAdapter.
                ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);//设置持续时
        startActivityForResult(getVisible, 0);

    }
    public void search(View view){
       BA.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver = new BluetoothReceiver();
        registerReceiver(receiver, filter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                setContentView(R.layout.connect_layout);
                BluetoothDevice device = deviceList.get(position);
                Toast.makeText(getApplicationContext(),"connecting to" +device.getName(),
                        Toast.LENGTH_LONG).show();
                ConnectThread thread =new ConnectThread(device );
                thread.start();

//                client = new Bluetooth(device, handler);
//                try {
//                    client.connect(message);
//                } catch (Exception e) {
//                    Log.e("TAG", e.toString());
//                }
            }
        });

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
@Override
protected void onDestroy() {
    unregisterReceiver(receiver);
    super.onDestroy();
}
//    private final Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Bluetooth.CONNECT_FAILED:
//                    Toast.makeText(ComminuteActivity.this, "连接失败", Toast.LENGTH_LONG).show();
//                    try {
//                        client.connect(message);
//                    } catch (Exception e) {
//                        Log.e("TAG", e.toString());
//                    }
//                    break;
//                case Bluetooth.CONNECT_SUCCESS:
//                    Toast.makeText(ComminuteActivity.this, "连接成功", Toast.LENGTH_LONG).show();
//                    break;
//                case Bluetooth.READ_FAILED:
//                    Toast.makeText(ComminuteActivity.this, "读取失败", Toast.LENGTH_LONG).show();
//                    break;
//                case Bluetooth.WRITE_FAILED:
//                    Toast.makeText(ComminuteActivity.this, "写入失败", Toast.LENGTH_LONG).show();
//                    break;
//                case Bluetooth.DATA:
//                    Toast.makeText(ComminuteActivity.this, msg.arg1 + "", Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };
private class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (isLock(device)) {
                devices.add(device.getName());
//            }
            deviceList.add(device);
        }
        showDevices();
    }
}
    private boolean isLock(BluetoothDevice device) {
        boolean isLockName = (device.getName()).equals(lockName);
        boolean isSingleDevice = devices.indexOf(device.getName()) == -1;
        return isLockName && isSingleDevice;
    }

    private void showDevices() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                devices);
        lv.setAdapter(adapter);
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
//            try {
//                // MY_UUID is the app's UUID string, also used by the server code
//                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//            } catch (IOException e) { }

            Method method;
            try {
                method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) method.invoke(device, 1);
            } catch (Exception e) {
//                setState(CONNECT_FAILED);
                Log.e("TAG", e.toString());
            }
            mmSocket = tmp;
        }
        public void run() {
            // Cancel discovery because it will slow down the connection
            BA.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();


            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out

                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            // Do work to manage the connection (in a separate thread)
//            manageConnectedSocket(mmSocket);
//            Toast.makeText(getApplicationContext(),"sending",
//                    Toast.LENGTH_LONG).show();
            try {
                OutputStream outStream = mmSocket.getOutputStream();
                outStream.write("hello".getBytes());
            } catch (IOException e) {
//                setState(WRITE_FAILED);
                Log.e("TAG", e.toString());
            }
            try {
                InputStream inputStream = mmSocket.getInputStream();
                int data;
                while (true) {
                    try {
                        data = inputStream.read();
//                        Toast.makeText(getApplicationContext(),data,
//                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("TAG", e.toString());
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e("TAG", e.toString());
            }
        }
        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
