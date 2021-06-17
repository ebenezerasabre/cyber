package asabre.com.cyber.viewmodels;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    public static MutableLiveData<Integer> steerProgress = new MutableLiveData<>();
    public static MutableLiveData<Integer> driverProgress = new MutableLiveData<>();
    public static MutableLiveData<Integer> driveMode = new MutableLiveData<>();
    public static MutableLiveData<Integer> driveGear = new MutableLiveData<>();

    public static MutableLiveData<Integer> breakVariable = new MutableLiveData<>();
    public static MutableLiveData<Boolean> luda = new MutableLiveData<>();
    public static MutableLiveData<String> batteryPercentage = new MutableLiveData<>();
    public static MutableLiveData<Boolean> suspensionFlow = new MutableLiveData<>();

    public static   MutableLiveData<List<BluetoothDevice>> devices = new MutableLiveData<>();
    public static MutableLiveData<Boolean> connected = new MutableLiveData<>();


    public static void init(){
        steerProgress.postValue(90);
        driverProgress.postValue(90);
        driveMode.postValue(0);
        driveGear.postValue(0);

        luda.postValue(false);
        breakVariable.postValue(0);
        batteryPercentage.postValue("50");
        suspensionFlow.postValue(false);

        devices.postValue(new ArrayList<>());
        connected.postValue(true);
    }



}
