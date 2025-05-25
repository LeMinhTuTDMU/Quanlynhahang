package vn.edu.tdmu.projectsesan;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Truy vấn kiểm tra username và password, đồng thời lấy role
                Cursor cursor = dbHelper.getUserWithRole(username, password);
                if (cursor != null && cursor.moveToFirst()) {
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

                    Log.i("Login", "Đăng nhập thành công. Vai trò: " + role);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("role", role);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Login", "Lỗi hệ thống: ", e);
                Toast.makeText(this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
