package com.example.deterknock;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StateChangeAsyncTask extends AsyncTask<String, Void, String> {

    private WeakReference<Activity> activityWeakReference;

    public StateChangeAsyncTask(Activity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
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
                return String.format("code %d. not successful.", statusCode);
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
