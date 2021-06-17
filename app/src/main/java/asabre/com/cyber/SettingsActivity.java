package asabre.com.cyber;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import asabre.com.cyber.viewmodels.HomeViewModel;
import asabre.com.cyber.viewmodels.SettingsViewModel;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";


    private RemoteControl remoteControl;

    private RadioButton head1;
    private RadioButton head2;

    private RadioButton tail1;
    private RadioButton tail2;

    private RadioButton interior1;
    private RadioButton interior2;

    private ColorPickerView headCircle;
    private ColorPickerView tailCircle;
    private ColorPickerView interiorCircle;

    private ImageView fl;
    private ImageView fr;
    private ImageView rl;
    private ImageView rr;
    private ColorPickerView mColorPickerView;
    public static boolean deviceIsConnected = false;
    private ImageView backArrow;
    private TextView backText;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BLEController bleController = BLEController.getInstance(this);
        this.remoteControl = new RemoteControl(bleController);

        init();

        lightStateCallback();
        headCircleColorPreview();
        lightStateObservers();
        setSuspensions();



    }


    private void init(){
        head1 = findViewById(R.id.head1);
        head2 = findViewById(R.id.head2);
        tail1 = findViewById(R.id.tail1);
        tail2 = findViewById(R.id.tail2);
        interior1 = findViewById(R.id.interior1);
        interior2 = findViewById(R.id.interior2);

        headCircle = (ColorPickerView) findViewById(R.id.headCircle);
        tailCircle = (ColorPickerView) findViewById(R.id.tailCircle);
        interiorCircle = (ColorPickerView) findViewById(R.id.interiorCircle);

        fl = findViewById(R.id.fl);
        fr = findViewById(R.id.fr);
        rl = findViewById(R.id.rl);
        rr = findViewById(R.id.rr);

        mColorPickerView = findViewById(R.id.colorPickerView);
        backArrow = findViewById(R.id.backArrow);
        backText = findViewById(R.id.backText);

//        SettingsViewModel.init();

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    private void headCircleColorPreview(){
        headCircle.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            String rgb = RGBReturn(envelope);
            Log.d(TAG, "onColorSelected: rgb " + rgb);
            sendCommand(String.format("H%s%s#", SettingsViewModel.headLight.getValue(), rgb));
            SettingsViewModel.headLightColorRGB.postValue(rgb);
        });

        tailCircle.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            String rgb = RGBReturn(envelope);
            Log.d(TAG, "onColorSelected: rgb " + rgb);
            sendCommand(String.format("T%s%s#", SettingsViewModel.headLight.getValue(), rgb));
            SettingsViewModel.tailLightColorRGB.postValue(rgb);
        });

        interiorCircle.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            String rgb = RGBReturn(envelope);
            Log.d(TAG, "onColorSelected: rgb " + rgb);
            sendCommand(String.format("I%s%s#", SettingsViewModel.headLight.getValue(), rgb));
            SettingsViewModel.interiorLightColorRGB.postValue(rgb);
        });
    }

    private String RGBReturn(ColorEnvelope envelope){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(Arrays.toString(envelope.getArgb()));
        StringBuilder str = new StringBuilder();
        while(m.find()) {
//            System.out.println(m.group());
            int num = Integer.parseInt(m.group()) + 100;
            str.append(String.format(Locale.US, "%s", num));
        }
        return str.substring(3, 12);
    }


    private void lightStateCallback(){
        head1.setOnClickListener(view -> SettingsViewModel.headLight.postValue(1));
        head2.setOnClickListener(view -> SettingsViewModel.headLight.postValue(0));

        tail1.setOnClickListener(view -> SettingsViewModel.tailLight.postValue(1));
        tail2.setOnClickListener(view -> SettingsViewModel.tailLight.postValue(0));

        interior1.setOnClickListener(view ->SettingsViewModel.interior.postValue(1));
        interior2.setOnClickListener(view -> SettingsViewModel.interior.postValue(0));
    }



    private void lightStateObservers(){
        SettingsViewModel.headLight.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                sendCommand(String.format(Locale.US,
                        "H%s%s#",
                        SettingsViewModel.headLight.getValue(),
                        SettingsViewModel.headLightColorRGB.getValue()));
            }
        });

        SettingsViewModel.tailLight.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                sendCommand(String.format(Locale.US,
                        "T%s%s#",
                        SettingsViewModel.tailLight.getValue(),
                        SettingsViewModel.tailLightColorRGB.getValue()));
            }
        });

        SettingsViewModel.interior.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                sendCommand(String.format(Locale.US,
                        "I%s%s#",
                        SettingsViewModel.interior.getValue(),
                        SettingsViewModel.interiorLightColorRGB.getValue()));
            }
        });
    }



//    private void sendCommand(String input){
//////        this.remoteControl.sendCommandStr(input);
////        Log.d(TAG, "sendCommand: input -> "  + input);
//    }

    private void sendCommand(String input){
        if(deviceIsConnected){
            remoteControl.sendCommandStr(input);
        } else {
            Log.d(TAG, "device not connected: ");
        }
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


    private void setSuspensions() {
        flClick();
        frClick();
        rlClick();
        rrClick();

    }



    private void flClick(){
      fl.setOnTouchListener((view, motionEvent) -> {
          switch (view.getId()){
              case R.id.fl:
                      if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                          Log.d(TAG, "onTouchDown ");
                          sendCommand(String.format("F0#"));
                      }
                  }
                  return false;
          });
    }

    private void frClick(){
        fr.setOnTouchListener((view, motionEvent) -> {
            switch (view.getId()){
                case R.id.fr:
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        Log.d(TAG, "onTouchDown ");
                        sendCommand(String.format("F1#"));
                    }
            }
            return false;
        });
    }

    private void rlClick(){
        rl.setOnTouchListener((view, motionEvent) -> {
            switch (view.getId()){
                case R.id.rl:
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        Log.d(TAG, "onTouchDown ");
                        sendCommand(String.format("R0#"));
                    }
            }
            return false;
        });
    }

    private void rrClick(){
        rr.setOnTouchListener((view, motionEvent) -> {
            switch (view.getId()){
                case R.id.rr:
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        Log.d(TAG, "onTouchDown ");
                        sendCommand(String.format("R2#"));
                    }
            }
            return false;
        });
    }



}