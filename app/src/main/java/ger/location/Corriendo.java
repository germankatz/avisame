package ger.location;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * Created by Ger on 08/10/14.
 */
public class Corriendo extends Fragment {
    public static final String PREFS_NAME = "MyPrefsFile";
    Handler mHandler;
    MapView mapView;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View Corriendo = inflater.inflate(R.layout.corriendo, container, false);
        final TextView run = (TextView) Corriendo.findViewById(R.id.corr);
        final Button cancel = (Button) Corriendo.findViewById(R.id.cancelar);
        mHandler = new Handler();


        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                if (isMyServiceRunning(CheckPositionService.class)){
                                    run.setText("Hay una alarma corriendo \n Haga click abajo para cancelarla");
                                    cancel.setVisibility(View.VISIBLE);

                                }else {
                                    run.setText("No se encuentra ninguna alarma corriendo");
                                    cancel.setVisibility(View.GONE);
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), "Cancelar alarma", "SI");
        // Now we can any of the following methods.
        builder.content("¿Seguro desea cancelar la alarma?");
        builder.negativeText("NO");
        // Now we can build the dialog.
        final CustomDialog customDialog = builder.build();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog.
                customDialog.show();
            }
        });

        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                getActivity().stopService(new Intent(getActivity(), CheckPositionService.class));
            }

            @Override
            public void onCancelClick() {

            }
        });


        return Corriendo;
    }


    @Override
    public void onDestroy() {
        mHandler = null;
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}