package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.example.vivek.rentalmates.tasks.RegisterFlatAsyncTask;

import org.w3c.dom.Text;

public class RegisterFlatActivity extends ActionBarActivity {

    TextView textView1;
    TextView textView2;
    TextView textView3;
    EditText editText1;
    Button button1;
    Button registerButton;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_flat);
        textView1 = (TextView) findViewById(R.id.textView5);
        textView2 = (TextView) findViewById(R.id.textView6);
        textView3 = (TextView) findViewById(R.id.textView4);
        editText1 = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button2);
        registerButton = (Button) findViewById(R.id.button3);
        updateView(false);//need to be changed to true later
        prefs = this.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    void updateView(boolean isFlatAlreadyRegistered){
        if (isFlatAlreadyRegistered){
            textView1.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            editText1.setVisibility(View.GONE);
            button1.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            registerButton.setText("NEXT");
        }
        else {
            textView1.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            editText1.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }

    public void onRegisterFlatButtonClick(View view){
        FlatInfo flatInfo = new FlatInfo();
        flatInfo.setFlatName(editText1.getText().toString());
        flatInfo.setOwnerEmailId("vivekmsit@gmail.com");
        new RegisterFlatAsyncTask(this, flatInfo).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_flat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
