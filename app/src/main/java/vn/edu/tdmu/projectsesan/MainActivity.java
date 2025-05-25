package vn.edu.tdmu.projectsesan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
public class MainActivity extends AppCompatActivity {
    private ListView lvDishes;
    private Button btnAddDish;
    private DatabaseHelper dbHelper;
    private ListView lvDishSelection;
    private List<String> selectedDishes = new ArrayList<>();
    private List<String> tableList; // Fixed: Table list must be initialized
    private Button btnAddStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dbHelper = new DatabaseHelper(this);

        // Initialize table list to be used for booking
        tableList = dbHelper.getAllTables();

        lvDishes = findViewById(R.id.lvDishes);
        btnAddDish = findViewById(R.id.btnAddDish);
        Button btnAddStaff = findViewById(R.id.btnAddStaff);
        btnAddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStaffActivity.class);
                startActivity(intent);
            }
        });

        btnAddDish.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddDishActivity.class);
            startActivity(intent);
        });
        Button btnViewStaff = findViewById(R.id.btnViewStaff);
        btnViewStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaffListActivity.class);
                startActivity(intent);
            }
        });

        String role = getIntent().getStringExtra("role");
        if (role != null && role.equals("staff")) {
            btnAddStaff.setVisibility(View.GONE);  // Ẩn nếu là nhân viên
        } else {
            btnAddStaff.setVisibility(View.VISIBLE); // Hiện nếu là admin hoặc manager
        }
        Button btnViewAllBookings = findViewById(R.id.btnViewAllBookings);
        btnViewAllBookings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingListActivity.class);
            startActivity(intent);
        });

        setupTableBooking();

        Log.d("MainActivity", "Activity created successfully");

        loadDishes();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDishes();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // Gắn file menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_settings) {
            Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_profile) {
            Toast.makeText(this, "Thông tin cá nhân", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_logout) {
            Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    private void loadDishes() {
        Cursor cursor = dbHelper.getAllDishes();
        if (cursor == null) return;

        String[] from = new String[] {
                DatabaseHelper.COLUMN_DISH_NAME,
                DatabaseHelper.COLUMN_DISH_PRICE,
                DatabaseHelper.COLUMN_DISH_DESC
        };
        int[] to = new int[] {
                R.id.tvDishName,
                R.id.tvDishPrice,
                R.id.tvDishDescription
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_dish,
                cursor,
                from,
                to,
                0
        );

        lvDishes.setAdapter(adapter);
    }

    private void setupTableBooking() {
        Button btnViewTables = findViewById(R.id.btnViewTables);

        btnViewTables.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_table_booking, null);

            lvDishSelection = dialogView.findViewById(R.id.lvDishSelection);
            Cursor cursor = dbHelper.getAllDishes();
            List<String> dishList = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISH_NAME));
                    dishList.add(name);
                } while (cursor.moveToNext());
                cursor.close();
            }

            ArrayAdapter<String> dishAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_multiple_choice,
                    dishList
            );
            lvDishSelection.setAdapter(dishAdapter);
            lvDishSelection.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setNegativeButton("Hủy", null)
                    .create();

            Button btnBookTable = dialogView.findViewById(R.id.btnConfirmBooking);
            btnBookTable.setOnClickListener(view -> {
                bookTable(dialogView);
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    private void setupTableSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tableList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void bookTable(View dialogView) {
        EditText etGuestCount = dialogView.findViewById(R.id.etGuestCount);
        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etBookingTime = dialogView.findViewById(R.id.etBookingTime);
        EditText etFloor = dialogView.findViewById(R.id.etFloor);
        EditText etTableNumber = dialogView.findViewById(R.id.etTableNumber);

        String fullName = etFullName.getText().toString().trim();
        String bookingTime = etBookingTime.getText().toString().trim();
        String floor = etFloor.getText().toString().trim();
        String tableNumber = etTableNumber.getText().toString().trim();

        if (fullName.isEmpty() || bookingTime.isEmpty() || floor.isEmpty() || tableNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int guestCount = Integer.parseInt(etGuestCount.getText().toString().trim());
            int userId = getCurrentUserId();

            // Thêm đặt bàn => lấy bookingId vừa tạo
            long bookingId = dbHelper.addBooking(userId, fullName, bookingTime, floor, tableNumber, guestCount);

            selectedDishes.clear();
            for (int i = 0; i < lvDishSelection.getCount(); i++) {
                if (lvDishSelection.isItemChecked(i)) {
                    String dishName = (String) lvDishSelection.getItemAtPosition(i);
                    selectedDishes.add(dishName);

                    int dishId = dbHelper.getDishIdByName(dishName); // Cần tạo hàm này trong DatabaseHelper

                    if (dishId != -1) {
                        dbHelper.addOrderItem((int) bookingId, dishId, 1); // mặc định số lượng = 1
                    } else {
                        Log.w("BookTable", "Không tìm thấy dishId cho món: " + dishName);
                    }
                }
            }

            Log.d("SelectedDishes", "Món đã chọn: " + selectedDishes.toString());
            Toast.makeText(this, "Đặt bàn thành công cho " + guestCount + " khách", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số lượng khách hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean hasBookedTable() {
        int userId = getCurrentUserId();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_BOOKINGS,
                null,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        boolean hasBooking = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();

        return hasBooking;
    }

    private void showBookedTables() {
        int userId = getCurrentUserId();
        Cursor cursor = dbHelper.getUserBooking(userId);

        if (cursor != null && cursor.moveToFirst()) {
            String tableNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TABLE_NUMBER));
            String bookingTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_TIME));

            new AlertDialog.Builder(this)
                    .setTitle("Thông tin đặt bàn")
                    .setMessage("Bạn đã đặt " + tableNumber + "\nThời gian: " + bookingTime)
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đặt bàn", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) cursor.close();
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", 1);
    }

    private void updateBookingStatus() {
        if (hasBookedTable()) {
            showBookedTables();
        }
    }

    private void cancelBooking() {
        int userId = getCurrentUserId();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(
                DatabaseHelper.TABLE_BOOKINGS,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        db.close();

        if (deletedRows > 0) {
            Toast.makeText(this, "Đã hủy đặt bàn", Toast.LENGTH_SHORT).show();
            updateBookingStatus();
        }
    }

}
