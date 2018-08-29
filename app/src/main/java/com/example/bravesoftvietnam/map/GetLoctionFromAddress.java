package com.example.bravesoftvietnam.map;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GetLoctionFromAddress extends AsyncTask<Void, Void, JSONObject> {
    //https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
    GoogleMap googleMap;
    String address;
    public GetLoctionFromAddress(GoogleMap googleMap,String data) {
        this.googleMap = googleMap;
        address=data;
    }

    @Override
    protected JSONObject doInBackground(Void... data) {
        String resopone;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(" ", "%20"));
        HttpResponse responce = null;
        try {
            responce = httpClient.execute(httpPost);
            HttpEntity httpEntity = responce.getEntity();
            resopone = EntityUtils.toString(httpEntity);
            return new JSONObject(resopone);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (result != null) {
            try {
                JSONArray jsonArray = result.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String address = jsonObject.getString("formatted_address");
                    JSONObject geometry = jsonObject.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(address));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
