package com.example.cat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeesActivity extends AppCompatActivity {
    EditText regInput, totalInput, paidInput, balanceInput;
    Button saveFees, gotoSummary;
    DatePicker datePicker;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees);

        regInput = findViewById(R.id.reg_input_fees);
        totalInput = findViewById(R.id.total_fees_input);
        paidInput = findViewById(R.id.paid_input);
        balanceInput = findViewById(R.id.balance_output);
        datePicker = findViewById(R.id.date_picker);
        saveFees = findViewById(R.id.save_fees_button);
        gotoSummary = findViewById(R.id.goto_summary_button);
        db = new DBHelper(this);

        // Disallow past dates
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        // Automatically calculate balance when typing
        TextWatcher balanceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBalance();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        totalInput.addTextChangedListener(balanceWatcher);
        paidInput.addTextChangedListener(balanceWatcher);

        // Save fee info
        saveFees.setOnClickListener(v -> {
            String regno = regInput.getText().toString().trim();
            String totalStr = totalInput.getText().toString().trim();
            String paidStr = paidInput.getText().toString().trim();

            // Format date from DatePicker
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // months start from 0
            int year = datePicker.getYear();
            String date = day + "/" + month + "/" + year;

            if (regno.isEmpty() || totalStr.isEmpty() || paidStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int studentId = db.getStudentIdByRegNo(regno);
            if (studentId == -1) {
                Toast.makeText(this, "Student with this Reg No not found.", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                double total = Double.parseDouble(totalStr);
                double paid = Double.parseDouble(paidStr);
                double balance = total - paid;

                long result = db.insertFees(studentId, total, paid, balance, date);
                if (result != -1) {
                    Toast.makeText(this, "Fees saved successfully!", Toast.LENGTH_SHORT).show();
                    regInput.setText("");
                    totalInput.setText("");
                    paidInput.setText("");
                    balanceInput.setText("");
                } else {
                    Toast.makeText(this, "Failed to save fees.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid fee values", Toast.LENGTH_SHORT).show();
            }
        });

        // Go to Summary
        gotoSummary.setOnClickListener(v -> {
            startActivity(new Intent(this, SummaryActivity.class));
        });
    }

    private void calculateBalance() {
        String totalStr = totalInput.getText().toString().trim();
        String paidStr = paidInput.getText().toString().trim();

        if (!totalStr.isEmpty() && !paidStr.isEmpty()) {
            try {
                double total = Double.parseDouble(totalStr);
                double paid = Double.parseDouble(paidStr);
                double balance = total - paid;
                balanceInput.setText(String.valueOf(balance));
            } catch (NumberFormatException e) {
                balanceInput.setText("");
            }
        } else {
            balanceInput.setText("");
        }
    }
}
