package com.example.cat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText nameInput, regInput;
    Button saveStudent, gotoFees;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInput = findViewById(R.id.name_input);
        regInput = findViewById(R.id.reg_input);
        saveStudent = findViewById(R.id.save_button);
        gotoFees = findViewById(R.id.goto_fees_button);
        db = new DBHelper(this);

        saveStudent.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String regno = regInput.getText().toString().trim();

            if (name.isEmpty() || regno.isEmpty()) {
                Toast.makeText(this, "Please enter both name and reg no.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if regno already exists
            if (db.getStudentIdByRegNo(regno) != -1) {
                Toast.makeText(this, "This Reg No is already registered.", Toast.LENGTH_LONG).show();
            } else {
                boolean inserted = db.insertStudent(name, regno);
                if (inserted) {
                    Toast.makeText(this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                    nameInput.setText("");
                    regInput.setText("");
                } else {
                    Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        gotoFees.setOnClickListener(v -> {
            startActivity(new Intent(this, FeesActivity.class));
        });
    }
}
