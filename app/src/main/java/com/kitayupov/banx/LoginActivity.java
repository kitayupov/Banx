package com.kitayupov.banx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_login;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private ArrayList<String> list;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initControls();
        initUserMap();
        setButtonListener();
    }

    private void initUserMap() {
        list = new ArrayList<>();
        list.add(Constants.OPERATOR_ID, "user1");
        list.add(Constants.ADMINISTRATOR1_ID, "adm2");
        list.add(Constants.ADMINISTRATOR2_ID, "adm3");
        map = new HashMap<>();
        map.put(list.get(Constants.OPERATOR_ID), "111");
        map.put(list.get(Constants.ADMINISTRATOR1_ID), "222");
        map.put(list.get(Constants.ADMINISTRATOR2_ID), "333");
    }

    private void initControls() {
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        loginButton = (Button) findViewById(R.id.login_button);
    }

    private void setButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUserPass();
            }
        });
    }

    private void verifyUserPass() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean emptyField = false;

        if ("".equals(username)) {
            usernameEditText.setError(getString(R.string.alert_empty_field));
            emptyField = true;
        }

        if ("".equals(password)) {
            passwordEditText.setError(getString(R.string.alert_empty_field));
            emptyField = true;
        }

        if (!emptyField) {
            if (map.containsKey(username) && map.get(username).equals(password)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(Constants.USER, list.indexOf(username));
                startActivity(intent);
                finish();
            } else {
                usernameEditText.setText(null);
                passwordEditText.setText(null);
                Toast.makeText(this, R.string.alert_combination_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
