package com.project.ming.final_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Handler;

public class MapActivity extends AppCompatActivity {

    private GoogleMap map;
    private String provider;    //選到的位置提供器
    private LocationManager locationMgr;    //用來取得位置提供器的類別
    private boolean isRunning = true;   //執行緒的迴圈判斷
    int nm;     //用來接收前一畫面的參數
    Button btn1, btn2, btn3;
    Marker mk;      //
    LatLng origin, dest;     //定義原點、終點
    Thread thread;      //定義執行緒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);      //Normal形式地圖

        Bundle b = getIntent().getExtras();
        nm = b.getInt("num");

        switch (nm) {
            case 0:
                dest = new LatLng(25.0435874, 121.5292888);      //設終點位置為咖食堂
                MarkerOptions m0 = new MarkerOptions();
                m0.position(dest);
                m0.title("咖食堂");
                m0.draggable(true);
                map.addMarker(m0);
                moveMap(dest.latitude, dest.longitude);
                break;
            case 1:
                dest = new LatLng(25.0438375, 121.5338748);      //設終點位置為自助餐(六教)
                MarkerOptions m1 = new MarkerOptions();
                m1.position(dest);
                m1.title("自助餐(六教)");
                m1.draggable(true);
                map.addMarker(m1);
                moveMap(dest.latitude, dest.longitude);
                break;
            case 2:
                dest = new LatLng(25.0440581, 121.5362271);      //終點位置為炸醬麵大王
                MarkerOptions m2 = new MarkerOptions();
                m2.position(dest);
                m2.title("炸醬麵大王");
                m2.draggable(true);
                map.addMarker(m2);
                moveMap(dest.latitude, dest.longitude);
                break;
            case 3:
                dest = new LatLng(25.0438189, 121.5337731);      //設終點為金盃美而美
                MarkerOptions m3 = new MarkerOptions();
                m3.position(dest);
                m3.title("金盃美而美");
                m3.draggable(true);
                map.addMarker(m3);
                moveMap(dest.latitude, dest.longitude);
                break;
            case 4:
                dest = new LatLng(25.0440454, 121.5362271);      //設終點為搖立得
                MarkerOptions m4 = new MarkerOptions();
                m4.position(dest);
                m4.title("搖立得");
                m4.draggable(true);
                map.addMarker(m4);
                moveMap(dest.latitude, dest.longitude);
                break;
        }
        //路徑規劃的按鈕
        btn1 = (Button) findViewById(R.id.button4);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setEnabled(false);     //按鈕的致能
                thread = new Thread(new Runnable() {    //建立新的thread
                    @Override
                    public void run() {
                        while (isRunning) {     //一開始isRunning = true
                            try {
                                Log.d("thread", "How are you?");    //用來debug
                                myHandle.sendMessage(myHandle.obtainMessage());     //發射訊息
                                Thread.sleep(2000);     //延遲兩秒
                            } catch (InterruptedException e) {      //當某個執行緒中斷時，而另一個執行緒試圖使用「interrupt()」方法來中斷已停止執行的執行緒
                                Thread.currentThread().interrupt();     //中斷目前的thread
                            }
                        }
                    }
                });
                thread.start();
            }
        });
        //返回主選單的按鈕
        btn2 = (Button) findViewById(R.id.button5);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;      //中斷thread
                thread.interrupt();     //呼叫該執行緒的interrupt()方法，讓該執行緒丟出InterruptedException好脫離原本的執行流程
                //切換回主畫面
                Intent i = new Intent();
                i.setClass(MapActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        //離開的按鈕
        btn3 = (Button) findViewById(R.id.button6);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    Handler myHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {    // handler接收到消息後就會執行裡面的程式
            Log.d("thread", "What the ...?");       //用來debug
            if (initLocationProvider()) {   //取得位置提供器
                whereAmI();     //定位出目前位置
                String url = getDirectionsUrl(origin, dest);    //組合成URL
                DownloadTask downloadTask = new DownloadTask();     //從URL下載JSON(JavaScript Object Notation)資料的方法
                downloadTask.execute(url);      //該方法通過攜帶經緯度的url請求得到json數據
            } else {
                Toast.makeText(MapActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();   //無法取得位置提供器
            }
        }
    };

    private boolean initLocationProvider() {    //取得可用的位置提供器
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {      //選擇使用網路提供器
            provider = LocationManager.NETWORK_PROVIDER;    //現在是由網路提供位置
            return true;
        }
        return false;
    }

    private void whereAmI() {   //定位目前位置
        LocationListener locationListener = new LocationListener() {       //位置監聽器
            @Override
            public void onLocationChanged(Location location) {
                updateWithNewLocation(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateWithNewLocation(null);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("1", "Status Changed: Out of Service");
                        Toast.makeText(MapActivity.this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("2", "Status Changed: Temporarily Unavailable");
                        Toast.makeText(MapActivity.this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("3", "Status Changed: Available");
                        Toast.makeText(MapActivity.this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = locationMgr.getLastKnownLocation(provider); //由網路提供時間
        updateWithNewLocation(location);    //定位目前位置及改變地圖焦距
        origin = new LatLng(location.getLatitude(),location.getLongitude());    //回傳值
        locationMgr.requestLocationUpdates(provider, 5000, 5, locationListener);    //更新時間為5秒，且距離超過5公尺

    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double lat = location.getLatitude();            //緯度
            double lng = location.getLongitude();           //經度
            showMarkerMe(lat, lng);     //建立我的標記
            moveMap(lat, lng);      //移動地圖及改變焦距
        }
    }

    private void showMarkerMe(double lat, double lng) {     //建立我的標記
        if (mk != null) {
            mk.remove();
        }
        MarkerOptions me = new MarkerOptions();
        me.position(new LatLng(lat, lng));
        me.title("我在這裡");
        me.draggable(true);
        mk = map.addMarker(me);
    }

    private void moveMap(double lat, double lng) {      //移動地圖及改變焦距
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(18)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    //組合成url
    private String getDirectionsUrl(LatLng origin, LatLng dest) {   //組合成URL
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;   //路線的起點
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;    //路線的終點
        String sensor = "sensor=false";        // Sensor的致能
        String parameters = str_origin + "&" + str_dest + "&" + sensor; //建立web service的參數
        String output = "json"; //輸出格式
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;    //建立web service的url
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);  //建立http連結溝通的網址
            urlConnection = (HttpURLConnection) url.openConnection();   //連接url
            urlConnection.connect();    //從url讀資料
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {        //從URL下載JSON(JavaScript Object Notation)資料的方法
        // 下載數據
        @Override
        protected String doInBackground(String... url) {
            String data = "";   //從網路服務中儲存的資料
            try {
                data = downloadUrl(url[0]); //取出網路服務中的資料
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    //解析JSON格式
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // 解析資料
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject); //開始解析資料
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                // 取出i個路徑
                List<HashMap<String, String>> path = result.get(i);
                // 取出i個路徑上的所有點
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // 在路徑上畫上所有點
                lineOptions.addAll(points);
                lineOptions.width(5);  //導航路徑寬度
                lineOptions.color(Color.BLUE); //導航路徑顏色

            }
            map.addPolyline(lineOptions);
        }
    }

    public class DirectionsJSONParser {
        // 接收一個JSONObject並返回一個列表的列表，包含經緯度
        public List<List<HashMap<String,String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {
                jRoutes = jObject.getJSONArray("routes");

                for(int i=0;i<jRoutes.length();i++) {
                    jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    for(int j=0;j<jLegs.length();j++) {
                        jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                        for(int k=0;k<jSteps.length();k++) {
                            String polyline = "";
                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for(int l=0;l<list.size();l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
            }
            return routes;
        }
        //解碼折線點的方法
        private  List<LatLng> decodePoly(String encoded) {

                List<LatLng> poly = new ArrayList<LatLng>();
                int index = 0,len = encoded.length();
                int lat = 0, lng = 0;

                while(index < len) {
                    int b, shift = 0, result = 0;
                    do{
                        b = encoded.charAt(index++) - 63;
                        result |= (b &  0x1f ) << shift;
                        shift += 5;
                    }while (b >=  0x20 );
                    int dlat = ((result &  1 ) !=  0  ? ~(result >>  1 ) : (result >>  1 ));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do{
                        b = encoded.charAt(index++) - 63;
                        result |= (b &  0x1f) << shift;
                        shift += 5;
                    }  while  (b >=  0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p =  new  LatLng((((double) lat / 1E5)),
                            (((double) lng / 1E5)));
                    poly.add(p);
                }
                return poly;
            }
        }
    }




