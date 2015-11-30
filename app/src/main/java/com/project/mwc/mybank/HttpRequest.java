package com.project.mwc.mybank;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fredrickabayie on 29/11/15.
 */
public class HttpRequest extends AsyncTask<String, Void, String> {

    String response = null;
    HttpURLConnection httpURLConnection = null;
    URL url = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        for (String url1 : params) {
            try {
                url = new URL(url1);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                System.out.println(url);
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String s = "";
                while ((s = bufferedReader.readLine()) != null) {
                    System.out.println(s);
                    response += s;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpURLConnection.disconnect();
            }
        }

        return response;
    }


    /**
     * @param values
     */
    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
    }


    /**
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
