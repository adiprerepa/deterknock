package com.example.deterknock;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class StateChangeAsyncTask extends AsyncTask<String, Void, String> {

    private WeakReference<Activity> activityWeakReference;
    public static String TAG = "StateChangeAsyncTask";

    public StateChangeAsyncTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... params) {
        try {
            String u = params[0] + String.format("?lcd_msg=%s&priority=%s", params[1], params[2]);
            Log.d(TAG, "doInBackground: " + u);
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            JSONObject jsonReq = new JSONObject();
            jsonReq.put("lcd_msg", params[1]);
            jsonReq.put("priority", params[2]);
            // write request to esp8266
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(jsonReq.toString());
            int statusCode = urlConnection.getResponseCode();

            if (statusCode >= 200 && statusCode < 300) {
                return "Successful state Change!";
            } else {
                Log.d(TAG, "doInBackground: " + String.format("code %d. not successful.", statusCode));
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                Log.d(TAG, "doInBackground error: " + br.lines().collect(Collectors.joining()));
                return "code " + statusCode + " Not successful.";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return String.format("%s is not a valid url", params[0]);
            // todo split up exception handling to provide error-specific toast messages
        } catch (IOException e) {
            e.printStackTrace();
            return "probably cannot find esp8266. Check logs for details.";
        } catch (Exception e) {
            e.printStackTrace();
            return "something went wrong";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(activityWeakReference.get().getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
