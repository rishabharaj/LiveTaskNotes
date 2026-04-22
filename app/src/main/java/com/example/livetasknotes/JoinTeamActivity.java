package com.example.livetasknotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class JoinTeamActivity extends AppCompatActivity {

    private EditText etUserName, etTeamCode;
    private Button btnJoin, btnCreateTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("TaskAppPrefs", MODE_PRIVATE);
        if (prefs.contains("teamCode") && prefs.contains("userName")) {
            // Already logged in
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_join_team);

        etUserName = findViewById(R.id.etUserName);
        etTeamCode = findViewById(R.id.etTeamCode);
        btnJoin = findViewById(R.id.btnJoin);
        btnCreateTeam = findViewById(R.id.btnCreateTeam);

        btnJoin.setOnClickListener(v -> {
            String name = etUserName.getText().toString().trim();
            String code = etTeamCode.getText().toString().trim();

            if (name.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "Please enter Name and Team Code", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAndProceed(name, code);
        });

        btnCreateTeam.setOnClickListener(v -> {
            String name = etUserName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your Name first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate 6 digit random team code
            int randomCode = 100000 + new Random().nextInt(900000);
            String code = String.valueOf(randomCode);

            // Copy to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Team Code", code);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Team Created! Code: " + code + " (Copied to Clipboard)", Toast.LENGTH_LONG).show();
            saveAndProceed(name, code);
        });
    }

    private void saveAndProceed(String name, String code) {
        SharedPreferences prefs = getSharedPreferences("TaskAppPrefs", MODE_PRIVATE);
        prefs.edit().putString("userName", name).putString("teamCode", code).apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
