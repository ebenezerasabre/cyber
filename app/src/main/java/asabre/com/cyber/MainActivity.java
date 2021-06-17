package asabre.com.cyber;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import asabre.com.cyber.viewmodels.HomeViewModel;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements BLEControllerListener {
    private static final String TAG = "MainActivity";

    private BLEController bleController;
    private RemoteControl remoteControl;
    private String deviceAddress;

    private boolean isLEDOn = false;

    private boolean isAlive = false;
    private Thread heartBeatThread = null;

    private boolean commandAlive = false;
    private Thread sendCommandThread = null;

    private ImageView bluetooth;
    private ImageView settings;


    private RadioButton buttonNormal;
    private RadioButton buttonLudacris;
    private TextView mode;
    private SeekBar leftSeekBar;
    private TextView seekBarLeftText;

    private RadioButton segment_r;
    private RadioButton segment_d;
    private RadioButton segment_p;
    private SeekBar rightSeekBar;
    private TextView seekBarRightText;

    private RadioButton ludacrisLaunch;
    private MaterialButton breakButton;
    private TextView tvConnected;

    public static boolean deviceIsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.content_main);

        this.bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(this.bleController);

        checkBLESupport();
        checkPermissions();


        init();
        callbacks();
        subscribeObservers();
        connectMsg();
    }




    private void init(){
        bluetooth = findViewById(R.id.bluetooth);
        settings = findViewById(R.id.settings);


        bluetooth.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

//        HomeViewModel.init();

        buttonNormal = findViewById(R.id.buttonNormal);
        buttonLudacris = findViewById(R.id.buttonLudacris);
        mode = findViewById(R.id.mode);
        leftSeekBar = findViewById(R.id.leftSeekBar);
        seekBarLeftText = findViewById(R.id.seekBarLeftText);

        segment_r = findViewById(R.id.segment_r);
        segment_d = findViewById(R.id.segment_d);
        segment_p = findViewById(R.id.segment_p);
        rightSeekBar = findViewById(R.id.rightSeekBar);
        seekBarRightText = findViewById(R.id.seekBarRightText);

        ludacrisLaunch = findViewById(R.id.ludacrisLaunch);
        breakButton = findViewById(R.id.breakButton);
        tvConnected = findViewById(R.id.tvConnected);



    }


    /**
     * Monsteer one
     * @param input
     */

    private void sendCommand(String input){
        if(deviceIsConnected){
            remoteControl.sendCommandStr(input);
            tvConnected.setText("connected");
        } else {
            Log.d(TAG, "device not connected: ");
            tvConnected.setText("disconnected");
        }
//        remoteControl.sendCommandStr(input);
//        HomeViewModel.connected.observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if(aBoolean){
//                    remoteControl.sendCommandStr(input);
//                    Log.d(TAG, "sendCommand: input -> "  + input);
//                } else {
//                    Log.d(TAG, "onChanged: Device not available or connected");
//                }
//            }
//        });

    }



    private void log(final String text) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                logView.setText(logView.getText() + "\n" + text);
//            }
//        });
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

        HomeViewModel.init();

//        HomeViewModel.connected.observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if(aBoolean){
//                    Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        this.deviceAddress = null;
//        this.bleController = BLEController.getInstance(this);
//        this.bleController.addBLEControllerListener(this);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            log("[BLE]\tSearching for CyberMINI...");
////            this.bleController.init();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.bleController.removeBLEControllerListener(this);

    }

    @Override
    public void BLEControllerConnected() {
        log("[BLE]\tConnected");
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                disconnectButton.setEnabled(true);
//                switchLEDButton.setEnabled(true);
//            }
//        });
//        startHeartBeat();
    }

    @Override
    public void BLEControllerDisconnected() {
        log("[BLE]\tDisconnected");
//        disableButtons();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                connectButton.setEnabled(true);
//            }
//        });
//        this.isLEDOn = false;
//        stopHeartBeat();
    }



    @Override
    public void BLEDeviceFound(String name, String address) {
        log("Device " + name + " found with address " + address);
        this.deviceAddress = address;
//        this.connectButton.setEnabled(true);
    }




    private void callbacks(){
        driverModeCallback();
        driverGearCallback();
        leftSeekBar();
        rightSeekBar();
        ludacrisLaunch();
        setBreak();

    }

    private void setBreak() {
        breakButton.setOnClickListener(view -> sendCommand("B1#"));
    }

    private void ludacrisLaunch() {
        ludacrisLaunch.setOnClickListener(view -> HomeViewModel.driveGear.postValue(255));
    }


    private void rightSeekBar() {
        rightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int currentValue = magRange(i, 0, 100, 0, 255);
                seekBarRightText.setText(String.valueOf(currentValue));
                HomeViewModel.driverProgress.postValue(currentValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rightSeekBar.setProgress(50);
            }
        });

    }

    private void leftSeekBar() {
        leftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int currentValue = magRange(i, 0, 100, 750, 2250);
                seekBarLeftText.setText(String.valueOf(currentValue));
                HomeViewModel.steerProgress.postValue(currentValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                leftSeekBar.setProgress(50);
            }
        });
    }

    private void driverGearCallback() {
        segment_r.setOnClickListener(view -> HomeViewModel.driveGear.postValue(0));
        segment_d.setOnClickListener(view -> HomeViewModel.driveGear.postValue(1));
        segment_p.setOnClickListener(view -> HomeViewModel.driveGear.postValue(2));
    }

    private void driverModeCallback() {
        buttonNormal.setOnClickListener(view -> {
            HomeViewModel.driveMode.postValue(0);
            rightSeekBar.setVisibility(View.VISIBLE);
            seekBarRightText.setVisibility(View.VISIBLE);
            ludacrisLaunch.setVisibility(View.GONE);
            mode.setText("0");
        });
        buttonLudacris.setOnClickListener(view -> {
            HomeViewModel.driveMode.postValue(1);
            rightSeekBar.setVisibility(View.GONE);
            seekBarRightText.setVisibility(View.GONE);
            ludacrisLaunch.setVisibility(View.VISIBLE);
            mode.setText("1");
        });
    }

    private int magRange(double num, int prevStart, int prevEnd, int newStart, int newEnd){
        double ratio = num / (prevEnd - prevStart);
        double anw = ratio * (newEnd - newStart) + newStart;
        return (int) anw;
    }

    private void subscribeObservers(){

       HomeViewModel.driveMode.observe(
               this,
               integer -> sendCommand(String.format(
                       "D%s%s225#",
                       HomeViewModel.driveGear.getValue(),
                       HomeViewModel.driveMode.getValue())));

        HomeViewModel.driveGear.observe(
                this,
                integer -> sendCommand(String.format(
                        "D%s%s225#",
                        HomeViewModel.driveGear.getValue(),
                        HomeViewModel.driveMode.getValue())));

        HomeViewModel.driverProgress.observe(
                this,
                integer -> sendCommand(String.format(
                        "D%s%s%s#",
                        HomeViewModel.driveGear.getValue(),
                        HomeViewModel.driveMode.getValue(),
                        HomeViewModel.driverProgress.getValue())));

        HomeViewModel.steerProgress.observe(
                this,
                integer -> sendCommand(String.format(
                        "S%s#",
                        HomeViewModel.steerProgress.getValue())));
    }

    private void connectMsg(){
////        sendCommand();
//        HomeViewModel.connected.observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if(aBoolean){
//                    tvConnected.setText("connected");
//                } else {
//                    tvConnected.setText("disconnected");
//                }
//            }
//        });
    }

}


