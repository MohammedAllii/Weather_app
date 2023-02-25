package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String WWPREF_DATE = "wwpref_date";
    public static final String WWPREF_DESC = "wwpref_desc";
    public static final String WWPREF_LOCN = "wwpref_locn";
    public static final String WWPREF_CENT = "wwpref_cent";
    public static final String WWPREF_FAHR = "wwpref_fahr";
    public static final String WWPREF_WIND = "wwpref_wind";
    public static final String WWPREF_HUMD = "wwpref_humd";
    public static final String WWPREF_PRCP = "wwpref_prcp";
    public static final String WWPREF_TEMP = "wwpref_temp";

    public static final int CENTIGRADE = 1;
    public static final int FAHRENHEIT = 2;

    public static final int ADDRESSES = 10;
    public static final String ADDR_FORMAT = "%s, %s, %s";
    public static final String GOOGLE_URL = "https://www.google.com/search?hl=en&q=weather %s";
    public static final String WWPREF_COUNTRY = "WWPREF_COUNTRY";
    public static final String WWPREF_CITY = "WWPREF_CITY";
    public static final String WWPREF_ICON = "WWPREF_ICON";
    public static final String WWPREF_LAT = "WWPREF_LAT";
    public static final String WWPREF_LNG = "WWPREF_LNG";
    public static final String WWPREF_PRSS = "WWPREF_PRSS";


    String city = null;
    public LocationListener listener;

    public static final int REQUEST_PERMS = 1;


    EditText editText;
    Button button;
    ImageView imageView;
    TextView temptv, time, longitude, latitude, humidity, pressure, wind, country, city_nam;
    private ViewGroup dayGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        temptv = findViewById(R.id.textView3);
        time = findViewById(R.id.textView2);

        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        country = findViewById(R.id.country);
        city_nam = findViewById(R.id.city_nam);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), city,Toast.LENGTH_LONG).show();
                if (TextUtils.isEmpty(editText.getText())) {

                    editText.setError("champ vide");

                } else {
                    city = editText.getText().toString();
                    FindWeather(city);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPref();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getPref();
    }

    public void FindWeather(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=e53301e27efa0b66d05045d91b2742d3&units=metric";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //temperature
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject object = jsonObject.getJSONObject("main");
                            double temp = object.getDouble("temp");
                            temptv.setText("Temperature\n" + temp + "°C");

                            //country
                            JSONObject object8 = jsonObject.getJSONObject("sys");
                            String count = object8.getString("country");
                            country.setText(count + "  :");

                            //city
                            String city = jsonObject.getString("name");
                            city_nam.setText(city);

                            //icon
                            JSONArray jsonArray = jsonObject.getJSONArray("weather");
                            JSONObject obj = jsonArray.getJSONObject(0);
                            String icon = obj.getString("icon");
                            Picasso.get().load("http://openweathermap.org/img/wn/" + icon + "@2x.png").into(imageView);

                            //date

                            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm  \nE, MMM dd yyyy");
                            Date dateCurrent = new Date();
                            String date = formatter.format(dateCurrent);
                            time.setText(date);

                            //latitude
                            JSONObject object2 = jsonObject.getJSONObject("coord");
                            double lat_find = object2.getDouble("lat");
                            latitude.setText(lat_find + "°  N");

                            //longitude
                            JSONObject object3 = jsonObject.getJSONObject("coord");
                            double long_find = object3.getDouble("lon");
                            longitude.setText(long_find + "°  E");

                            //humidity
                            JSONObject object4 = jsonObject.getJSONObject("main");
                            int humidity_find = object4.getInt("humidity");
                            humidity.setText(humidity_find + "  %");

                            //pressure
                            JSONObject object7 = jsonObject.getJSONObject("main");
                            String pressure_find = object7.getString("pressure");
                            pressure.setText(pressure_find + "  hPa");

                            //wind speed
                            JSONObject object9 = jsonObject.getJSONObject("wind");
                            String wind_find = object9.getString("speed");
                            wind.setText(wind_find + "  km/h");

                            //description
                            JSONArray jsonArray1 = jsonObject.getJSONArray("weather");
                            JSONObject obj1 = jsonArray1.getJSONObject(0);
                            String desc = obj1.getString("description");
                            savePref(String.valueOf(temp), count, city, icon, date, String.valueOf(lat_find), String.valueOf(long_find), wind_find, humidity_find,
                                    pressure_find, desc);

                            //mettre à jour le widget
                            updateWidget(count,city,desc,date,temp,wind_find,pressure_find,humidity_find,icon);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            //si ville inconnu
            @Override
            public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), "Ville inconnu :)", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }
    private void located(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (!Geocoder.isPresent()) {
            Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_LONG).show();
            return;
        }
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, ADDRESSES);
            if (addressList == null) {
                Toast.makeText(getApplicationContext(), "invalide", Toast.LENGTH_LONG).show();
                return;
            }
            city = addressList.get(0).getAdminArea();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public void savePref(String temp, String country, String cityName, String icon, String date,
                         String lat_find, String long_find, String wind_find, int humidity_find,
                         String pressure_find,
                          String desc) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(WWPREF_TEMP, temp);
        editor.putString(WWPREF_COUNTRY, country);
        editor.putString(WWPREF_CITY, cityName);
        editor.putString(WWPREF_ICON, icon);
        editor.putString(WWPREF_DATE, date);
        editor.putString(WWPREF_LAT, lat_find);
        editor.putString(WWPREF_LNG, long_find);
        editor.putString(WWPREF_HUMD, String.valueOf(humidity_find));
        editor.putString(WWPREF_PRSS, pressure_find);
        editor.putString(WWPREF_WIND, wind_find);


        editor.putString(WWPREF_DESC, desc);


        editor.apply();
    }

    public void getPref() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (preferences.contains(MainActivity.WWPREF_TEMP)) {

            temptv.setText("Temperature\n" + preferences.getString(MainActivity.WWPREF_TEMP, "") + "°C");
            country.setText(preferences.getString(MainActivity.WWPREF_COUNTRY, "") + "  :");
            city_nam.setText(preferences.getString(MainActivity.WWPREF_CITY, ""));
            Picasso.get().load("http://openweathermap.org/img/wn/" + preferences.getString(MainActivity.WWPREF_ICON, "") + "@2x.png").into(imageView);
            time.setText(preferences.getString(MainActivity.WWPREF_DATE, ""));
            latitude.setText(preferences.getString(MainActivity.WWPREF_LAT, "") + "°  N");
            Log.d("MainAWWPREF_LAT:", "             " + preferences.getString(WWPREF_LAT, ""));
            longitude.setText(preferences.getString(MainActivity.WWPREF_LNG, "") + "°  E");
            humidity.setText(preferences.getString(MainActivity.WWPREF_HUMD, "") + "  %");
            pressure.setText(preferences.getString(MainActivity.WWPREF_PRSS, "") + "  hPa");
            wind.setText(preferences.getString(MainActivity.WWPREF_WIND, "") + "  km/h");



        }
    }
    public void updateWidget(String count, String city,String date, String desc, Double temp, String wind_find, String pressure_find, int humidity_find, String icon){
        // Créer une instance de AppWidgetManager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        // Récupérer l'ID widget
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), WeatherWidget.class));
        // Créer une instance de RemoteViews avec les mises à jour que vous souhaitez appliquer au widget
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.location,count+", "+city);
        views.setTextViewText(R.id.date, date);
        views.setTextViewText(R.id.description,desc);
        views.setTextViewText(R.id.centigrade,String.valueOf(temp)+" °C");
        views.setTextViewText(R.id.wind,wind_find+" km/h");
        views.setTextViewText(R.id.pressure,"Pressure :"+pressure_find+" Pa");
        views.setTextViewText(R.id.humidity, "Humidity :"+humidity_find+" %");
        ViewsTarget target = new ViewsTarget(getApplicationContext(), views, R.id.weather, appWidgetIds);
        Picasso.get().load("http://openweathermap.org/img/wn/" + icon + "@2x.png").into(target);

    }


}