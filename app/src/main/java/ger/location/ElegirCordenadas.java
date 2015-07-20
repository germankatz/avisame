package ger.location;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ger on 08/10/14.
 */
public class ElegirCordenadas extends Activity{

    private GoogleMap myMap;
    Marker marker;
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        Toast.makeText(getApplicationContext(), "Manten apretado el lugar donde quieres ir", Toast.LENGTH_LONG).show();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        LatLng l = new LatLng(pref.getFloat("lat",0) , pref.getFloat("long",0) );

        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
        myMap = myMapFragment.getMap();
        myMap.setMyLocationEnabled(true);

        if (pref.getFloat("lat",666) != 666){
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        }

        myMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                marker = myMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Aqui quiero llegar")
                        .draggable(true).visible(true));
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("long", (float) latLng.longitude);
                editor.putFloat("lat", (float) latLng.latitude);
                editor.commit();
                Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                v.vibrate(50);
                Toast.makeText(ElegirCordenadas.this, "Posicion agregada correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        if (pref.getFloat("lat",666) != 666){
            if (marker != null)
                marker.remove();
            marker = myMap.addMarker(new MarkerOptions().position(l).title("Aqui quiero llegar").draggable(true).visible(true));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
