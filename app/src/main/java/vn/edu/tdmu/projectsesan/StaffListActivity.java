package vn.edu.tdmu.projectsesan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StaffListActivity extends AppCompatActivity {
    ListView listView;
    DatabaseHelper dbHelper;
    List<Staff> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        listView = findViewById(R.id.listViewStaff);
        dbHelper = new DatabaseHelper(this);
        staffList = dbHelper.getAllStaff(); // Hàm này bạn cần viết trong DatabaseHelper

        StaffAdapter adapter = new StaffAdapter(this, staffList);
        listView.setAdapter(adapter);
    }
}

