package com.trkaynak.mymap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override//Harita hazır olduğunda yapılacak işlemler
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Kullanıcının lokasyonu
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {

            @Override//kullanıcının yeri değiştiğinde yapılacaklar
            public void onLocationChanged(Location location) {

                mMap.clear();
                LatLng userLoation=new LatLng(location.getLatitude(),location.getLongitude());//kullanıcı yerini locationdan çekiyor ve userLocation değişkenine atıyor
                mMap.addMarker(new MarkerOptions().position(userLoation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoation,15));

                //Bir yere tıklayınca adres bilgilerinin gelmesi
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());//
                try {
                    List<Address> adressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);//bir tane adresi listeye al
                    if(adressList != null && adressList.size() > 0){
                        System.out.println("Adress info: "+adressList.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override//GPS sinyali kesilirse yapılacaklar
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mMap.setOnMapClickListener(this);

        //Kullanıcı izni alma
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) //eğer kullanıcı izin vermedi ise
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);//kullanıcıdan izin isteniyor.
        }
        else //izin verildi ise
            {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);//kullanıcı nerede, ne kadar sürede güncelleme yapılacak-0- her saniye,kaç metrede bir-0- her değişiklikte
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng userLAstLocation = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLAstLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLAstLocation,15));
        }




    }

    @Override//izinde gelen cevaba göre işlem yapma
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0){//büyük ise kullanıcı cevap vermemiş demektir.
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){//izin verdi ise
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
        String address="";
        try {
            List<Address> addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList != null && addressList.size() > 0){
                if(addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare();
                }
                if(addressList.get(0).getSubThoroughfare() != null){
                    address += addressList.get(0).getSubThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(address == ""){
            address = "No Address";
        }
        System.out.println("adress info: "+address);
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
    }
}
