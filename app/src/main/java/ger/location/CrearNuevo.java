package ger.location;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ger on 08/10/14.
 */
public class CrearNuevo extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View Crear = inflater.inflate(R.layout.crear, container, false);
        final CheckBox vibrar = (CheckBox) Crear.findViewById(R.id.vibrar);
        final CheckBox sonar = (CheckBox) Crear.findViewById(R.id.sonar);
        final EditText metros = (EditText) Crear.findViewById(R.id.metros);
        Button confirmar = (Button) Crear.findViewById(R.id.crear_serv);

        RelativeLayout elegirCordenadas = (RelativeLayout) Crear.findViewById(R.id.elegir);
        elegirCordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ElegirCordenadas.class);
                startActivity(intent);
            }
        });

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isMyServiceRunning(CheckPositionService.class)){
                    Toast.makeText(getActivity(), "Ya hay una alarma corriendo ", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences pref = Crear.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    Float lat = pref.getFloat("lat",0);
                    Float lon = pref.getFloat("long",0);

                    if (lat != null && lon != null){
                        LatLng l = new LatLng(lat,lon);

                        if(!metros.getText().toString().isEmpty()){
                            int n_metros = Integer.valueOf(metros.getText().toString());
                            if (n_metros > 0){
                                if (vibrar.isChecked() || sonar.isChecked()){
                                    if(n_metros < 49 ){
                                        Toast.makeText(Crear.getContext(), "Debe ingresar un radio mayor a 50 mts", Toast.LENGTH_SHORT).show();
                                    }else{
                                       /* Todo salio bien  */
                                        Toast.makeText(Crear.getContext(), "Alarma creada", Toast.LENGTH_LONG).show();
                                        SharedPreferences settings = Crear.getContext().getSharedPreferences(PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putFloat("long_f", pref.getFloat("long",0));
                                        editor.putFloat("lat_f", pref.getFloat("lat",0));
                                        editor.putInt("metros_f", n_metros);
                                        editor.putBoolean("vibrar", vibrar.isChecked());
                                        editor.putBoolean("sonar", sonar.isChecked());
                                        editor.commit();
                                        getActivity().startService(new Intent(getActivity(), CheckPositionService.class)); // Iniciar
                                    }
                                }else{
                                    Toast.makeText(Crear.getContext(), "Debe seleccionar si vibrar o sonar", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(Crear.getContext(), "Debe ingresar un valor mayor a 0 en los metros", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(Crear.getContext(), "Debe ingresar un valor en los metros", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(Crear.getContext(), "Debe seleccionar una parte del mapa", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });



        return Crear;
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
