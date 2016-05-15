package app.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class TabFragment1 extends Fragment {
    View mMain;
    CheckBox checkBox1 = null;
    EditText editText1 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_1, container, false);
        checkBox1 = (CheckBox) mMain.findViewById(R.id.checkBox1);
        editText1 = (EditText) mMain.findViewById(R.id.editText);

        checkBox1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(checkBox1.isChecked()){
                    enableEditText(editText1);
                }else{
                    disableEditText(editText1);
                }
            }
        });

        return mMain;
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    private void enableEditText(EditText editText) {
        editText.setClickable(true);
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        
    }
}
