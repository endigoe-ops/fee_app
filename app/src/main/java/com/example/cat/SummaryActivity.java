package com.example.cat;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class SummaryActivity extends AppCompatActivity {

    ListView summaryListView;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary); // Make sure this XML exists

        summaryListView = findViewById(R.id.summary_list_view); // Check ID in XML
        dbHelper = new DBHelper(this);

        loadSummaryData();
    }

    private void loadSummaryData() {
        Cursor cursor = dbHelper.getStudentWithFees();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            HashMap<String, String> item = new HashMap<>();
            item.put("name", "Name: " + cursor.getString(1));
            item.put("regno", "Reg No: " + cursor.getString(2));
            item.put("total", "Total: " + cursor.getDouble(3));
            item.put("paid", "Paid: " + cursor.getDouble(4));
            item.put("balance", "Balance: " + cursor.getDouble(5));
            item.put("date", "Completion: " + cursor.getString(6));
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.fee_summary_item,
                new String[]{"name", "regno", "total", "paid", "balance", "date"},
                new int[]{R.id.name_text, R.id.regno_text, R.id.total_text, R.id.paid_text, R.id.balance_text, R.id.date_text}
        );

        summaryListView.setAdapter(adapter);
    }
}
