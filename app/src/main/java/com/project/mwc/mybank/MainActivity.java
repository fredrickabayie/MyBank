package com.project.mwc.mybank;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn = (Button) findViewById(R.id.login_btn);
    }

    public void loginHandler (View view) {
        Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
        Intent mybank = new Intent(MainActivity.this, MyBank.class);
        startActivity(mybank);
        finish();
    }
}
