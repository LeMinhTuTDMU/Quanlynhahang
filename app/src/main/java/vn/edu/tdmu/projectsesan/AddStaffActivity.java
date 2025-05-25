package vn.edu.tdmu.projectsesan;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddStaffActivity extends AppCompatActivity {
    private EditText etFullName, etPhone, etPosition, etUsername, etPassword;
    private Spinner spinnerRole;
    private Button btnSaveStaff;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etPosition = findViewById(R.id.etPosition);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSaveStaff = findViewById(R.id.btnSaveStaff);

        // Thiết lập danh sách quyền
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"staff", "manager", "admin"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSaveStaff.setOnClickListener(v -> saveStaff());
    }

    private void saveStaff() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String position = etPosition.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (fullName.isEmpty() || phone.isEmpty() || position.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm tài khoản user
        long userId = dbHelper.insertUser(username, password, role);
        if (userId == -1) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thêm vào bảng staff
        boolean success = dbHelper.insertStaff((int) userId, fullName, phone, position);
        if (success) {
            Toast.makeText(this, "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi thêm nhân viên", Toast.LENGTH_SHORT).show();
        }
    }
}
