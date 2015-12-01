package com.project.mwc.mybank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button login_btn;
    EditText username, password;
    Spinner spinner;

    private static final String TAG_RESULT = "result";
    private static final String TAG_USERNAME = "username";
//    private static final String TAG_USERDP = "dp";
//    private static final String TAG_ACNUMBER = "user_acnumber";
    private static final String TAG_ID = "user_id";

    SessionManager sessionManager;

    private ProgressDialog pDialog;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = (Spinner)findViewById(R.id.bank);
        spinner.setOnItemSelectedListener(this);
//        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
//        String[] items = new String[]{"Beige Capital", "Ecobank"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        spinner.setAdapter(adapter);

//        String selected = spinner.getOnItemSelectedListener().toString();

        sessionManager = new SessionManager(getApplicationContext());

        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
    }


    @Override
    public void onClick(View view) {
        if (view == login_btn) {
            readWebpage(view);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(spinner.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class DownloadWebPageTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            login_btn.setEnabled(false);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         *Functiont to open an http connection
         * @param urls The url to be sent
         * @return Returning the response
         */
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url1 : urls) {
                try {
                    URL url = new URL(url1);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    System.out.println(url);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(in));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        System.out.println(s);
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }

            return response;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            progressBar.setProgress(values[0]);
            System.out.println(values[0]);
        }


        /**
         *Function to get result from the http post
         * @param result Result from the post
         */
        @Override
        protected void onPostExecute(String result) {
            login_btn.setEnabled(true);
            try {
                JSONObject jsonObj = new JSONObject(result);

                String res = jsonObj.getString(TAG_RESULT);
                System.out.print(res + "\n");
                if(res.equals("1")) {

                    String user = jsonObj.getString(TAG_USERNAME);
                    String id = jsonObj.getString(TAG_ID);
//                    String pic = jsonObj.getString(TAG_USERDP);
//                    String balance = jsonObj.getString(TAG_BALANCE);
//                    System.out.println(pic);

                    sessionManager.createLoginSession(id, user);

                    if (pDialog.isShowing())
                        pDialog.dismiss();

                    Intent home = new Intent(MainActivity.this, MyBank.class);
                    startActivity(home);
                    finish();
                }
                else {
                    String msg = jsonObj.getString("message");
//                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, msg, Snackbar.LENGTH_SHORT);

                    // Changing message text color
//                    snackbar.setActionTextColor(Color.RED);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
//
                    snackbar.show();

                }

            } catch (JSONException jsonex) {
                jsonex.printStackTrace();
            }
        }
    }


    public void readWebpage(View view) {
        DownloadWebPageTask task = new DownloadWebPageTask();

        if(username.getText().toString().trim().length() > 0 && password.getText().toString().trim().length() > 0) {
            task.execute("http://cs.ashesi.edu.gh/~csashesi/class2016/fredrick-abayie/mobileweb/mybank/php/mybank.php?cmd=user_login&username="
                    +username.getText().toString().trim()+"&password="+password.getText().toString().trim());
        } else {
            username.setError("Please enter your username");
            password.setError("Please enter your password");
//            password.setError("");
        }
    }
}
