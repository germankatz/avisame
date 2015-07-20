package ger.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class CheckPositionService extends Service implements LocationListener{
    private static final String TAG = "MyService";
    public static final String PREFS_NAME = "MyPrefsFile";

    LatLng coord,pos_actual;
    boolean sonar, vibrar,gps_is_on;
    int metros_f;
    Handler mHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Corriendo","Esta corriendo el service");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Corriendo",true);

        // Aca se crea el servicio, pasa una sola vez
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        coord = new LatLng(pref.getFloat("lat_f",0) , pref.getFloat("long_f",0) );
        metros_f = pref.getInt("metros_f",0);
        vibrar = pref.getBoolean("vibrar", true);
        sonar = pref.getBoolean("sonar", false);
    }

    @Override
    public void onDestroy() {
        // Aca se destruye el servicio, pasa una sola vez
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("Corriendo",false);
        mHandler = null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        // Esto vendria a ser lo que se ejecuta, yo le pongo un timer y cada tanto hago algo



        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

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
                                if (gps_is_on){
                                    Float metros = distFrom((float) pos_actual.latitude,(float) pos_actual.longitude,(float) coord.latitude,(float) coord.longitude);
                                    metros = metros *1000;
                                    if (metros <= metros_f ){
                                        Intent a = new Intent(CheckPositionService.this, Alarma.class);
                                        a.putExtra("sonar",sonar);
                                        a.putExtra("vibrar",vibrar);
                                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(a);
                                        stopService(new Intent(CheckPositionService.this, CheckPositionService.class));
                                        mHandler = null;
                                    }
                                }

                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();


    }
    @Override
    public void onLocationChanged(Location location) {
        gps_is_on = true;
        pos_actual = new LatLng(location.getLatitude(),location.getLongitude());
    }
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }


    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}