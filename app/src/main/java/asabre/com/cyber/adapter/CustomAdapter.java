package asabre.com.cyber.adapter;

//import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import asabre.com.cyber.R;
import asabre.com.cyber.model.DeviceModel;

public class CustomAdapter extends ArrayAdapter<DeviceModel> implements View.OnClickListener {

    private ArrayList<DeviceModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        ImageView deviceImage;
        TextView deviceName;
        TextView deviceAddress;
    }

    public CustomAdapter(@NonNull Context context,  ArrayList<DeviceModel> dataSet) {
        super(context, R.layout.browse_devices, dataSet);
        this.dataSet = dataSet;
        mContext = context;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        DeviceModel object = getItem(position);
        DeviceModel deviceModel = object;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // get the data item for this position
        DeviceModel deviceModel = getItem(position);

        ViewHolder viewHolder; // view lookup cache
        final View result;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.browse_devices, parent, false);
            viewHolder.deviceName = convertView.findViewById(R.id.device_name);
            viewHolder.deviceAddress = convertView.findViewById(R.id.device_address);
            viewHolder.deviceImage = convertView.findViewById(R.id.device_image);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.deviceName.setText(deviceModel.getName());
        viewHolder.deviceAddress.setText(deviceModel.getAddress());

        viewHolder.deviceAddress.setOnClickListener(this);
        viewHolder.deviceAddress.setTag(position);
//        return super.getView(position, convertView, parent);
        return convertView;
    }
}
