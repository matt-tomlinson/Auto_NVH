package app.myapplication;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by marlonvilorio on 5/5/16.
 */
public class BluetoothTabPaired extends Fragment {


    private View view = null;
    private ArrayAdapter<String> listAdapter;
    ListView pairedDevicesList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //assign neccesarry
        view = inflater.inflate(R.layout.bluetooth_tab_paired, container, false);
        pairedDevicesList = (ListView) view.findViewById(R.id.listView);

        //Check to see if user has Bluetooth, if bluetooth is off turn it on
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(),"Device does not support Bluetooth",Toast.LENGTH_LONG).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,1);
        }


        //We need these for our devices
        final ArrayList pDevices = new ArrayList();
        final ArrayList devices = new ArrayList();


        //Wait till bluetooth is on
        while (true) {
            if (mBluetoothAdapter.isEnabled()){
                break;
            }
        }

        //Get paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) { //for (int i = 0; i < pairedDevices.size(); i++)
                // Add the name and address to an array adapter to show in a ListView
                pDevices.add(device.getName() + "\n" + device.getAddress()); //pairedDevices[i].add
                devices.add(device.getAddress());
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_singlechoice,
                pDevices.toArray(new String[pDevices.size()]));
        pairedDevicesList.setAdapter(adapter);


        return view;

    }
}
