package asabre.com.cyber;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import asabre.com.cyber.adapter.CustomAdapter;
import asabre.com.cyber.model.DeviceModel;
import asabre.com.cyber.viewmodels.HomeViewModel;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;

public class ScanActivity extends AppCompatActivity implements BLEControllerListener {
    private static final String TAG = "ScanActivity";

    private TextView logView;
    private Button connectButton;
    private Button disconnectButton;
    private Button switchLEDButton;

    private BLEController bleController;
    private RemoteControl remoteControl;
    private String deviceAddress;

    private boolean isLEDOn = false;

    private boolean isAlive = false;
    private Thread heartBeatThread = null;

//    private ListView mListView;
    private List<BluetoothDevice> listItem = null;


//    ArrayList<DeviceModel> mDeviceModels;
//    private CustomAdapter adapter;



    private void init(){
        /**
         * I have not instantiated listview
         */
//        mListView = findViewById(R.id.listViewHolder);

//        mDeviceModels = new ArrayList<>();
//        mDeviceModels.add(new DeviceModel("Apple Pie", "Android 1.0"));
//        mDeviceModels.add(new DeviceModel("Banana Bread", "Android 1.1"));
//        mDeviceModels.add(new DeviceModel("Cupcake", "Android 1.5"));
//
//        adapter = new CustomAdapter(getApplicationContext(), mDeviceModels);
//        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                DeviceModel deviceModel = mDeviceModels.get(i);
//                Log.d(TAG, "device clicked " + deviceModel);
//            }
//        });

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_activitty);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);




        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        this.logView = findViewById(R.id.logView);
        this.logView.setMovementMethod(new ScrollingMovementMethod());

        initConnectButton();
        initDisconnectButton();
        initSwitchLEDButton();

        checkBLESupport();
        checkPermissions();

        disableButtons();


//        HomeViewModel.init();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        init();

    }



    public void startHeartBeat() {
        this.isAlive = true;
        this.heartBeatThread = createHeartBeatThread();
        this.heartBeatThread.start();
    }

    public void stopHeartBeat() {
        if(this.isAlive) {
            this.isAlive = false;
            this.heartBeatThread.interrupt();
        }
    }

    private Thread createHeartBeatThread() {
        return new Thread() {
            @Override
            public void run() {
                while(ScanActivity.this.isAlive) {
                    heartBeat();
                    try {
                        Thread.sleep(1000l);
                    }catch(InterruptedException ie) { return; }
                }
            }
        };
    }

    private void heartBeat() {
        this.remoteControl.heartbeat();
    }

    private void initConnectButton() {
        this.connectButton = findViewById(R.id.connectButton);
        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButton.setEnabled(false);
                log("Connecting...");
                Log.d(TAG, "onClick: connecting....");
                bleController.connectToDevice(deviceAddress);
            }
        });
    }

    private void initDisconnectButton() {
        this.disconnectButton = findViewById(R.id.disconnectButton);
        this.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectButton.setEnabled(false);
                log("Disconnecting...");
                Log.d(TAG, "onClick: disconnecting.....");
                bleController.disconnect();
            }
        });
    }

    private void initSwitchLEDButton() {
        this.switchLEDButton = findViewById(R.id.switchButton);
        this.switchLEDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLEDOn = !isLEDOn;
                remoteControl.switchLED(isLEDOn);
                log("LED switched " + (isLEDOn?"On":"Off"));
            }
        });
    }

    private void disableButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(false);
                switchLEDButton.setEnabled(false);
            }
        });
    }

    private void log(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.setText(logView.getText() + "\n" + text);
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            log("\"Access Fine Location\" permission not granted yet!");
            log("Without this permission Blutooth devices cannot be searched!");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
    }

    private void checkBLESupport() {
        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }

//        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.deviceAddress = null;
        this.bleController = BLEController.getInstance(this);
        this.bleController.addBLEControllerListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            log("[BLE]\tSearching for CyberMINI...");
            this.bleController.init();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.bleController.removeBLEControllerListener(this);
//        stopHeartBeat();
    }

    @Override
    public void BLEControllerConnected() {
        log("[BLE]\tConnected");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnectButton.setEnabled(true);
                switchLEDButton.setEnabled(true);
            }
        });
//        startHeartBeat();
    }

    @Override
    public void BLEControllerDisconnected() {
        log("[BLE]\tDisconnected");
        disableButtons();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectButton.setEnabled(true);
            }
        });
        this.isLEDOn = false;
//        stopHeartBeat();
    }

    @Override
    public void BLEDeviceFound(String name, String address) {
        log("Device " + name + " found with address " + address);
        this.deviceAddress = address;
        this.connectButton.setEnabled(true);
    }
}