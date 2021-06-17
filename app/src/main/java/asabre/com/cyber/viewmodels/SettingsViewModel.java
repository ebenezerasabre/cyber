package asabre.com.cyber.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    public static MutableLiveData<Integer> headLight = new MutableLiveData<>();
    public static MutableLiveData<Integer> tailLight = new MutableLiveData<>();
    public static MutableLiveData<Integer> interior = new MutableLiveData<>();

    public static MutableLiveData<String> headLightColorRGB = new MutableLiveData<>();
    public static MutableLiveData<String> tailLightColorRGB = new MutableLiveData<>();
    public static MutableLiveData<String> interiorLightColorRGB = new MutableLiveData<>();

    public static MutableLiveData<Boolean> suspensionFlow = new MutableLiveData<>();
    public static MutableLiveData<Boolean> touched = new MutableLiveData<>();

    public static void init(){
        headLight.postValue(0);
        tailLight.postValue(0);
        interior.postValue(0);

        headLightColorRGB.postValue("255255255");
        tailLightColorRGB.postValue("255255255");
        interiorLightColorRGB.postValue("255255255");

        touched.postValue(false);
    }

}
