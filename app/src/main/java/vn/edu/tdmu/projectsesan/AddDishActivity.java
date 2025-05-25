package vn.edu.tdmu.projectsesan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddDishActivity extends AppCompatActivity {
    private EditText etDishName, etDishPrice, etDishDescription;
    private Button btnSaveDish;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        dbHelper = new DatabaseHelper(this);

        etDishName = findViewById(R.id.etDishName);
        etDishPrice = findViewById(R.id.etDishPrice);
        etDishDescription = findViewById(R.id.etDishDescription);
        btnSaveDish = findViewById(R.id.btnSaveDish);

        btnSaveDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etDishName.getText().toString().trim();
                String priceStr = etDishPrice.getText().toString().trim();
                String description = etDishDescription.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(AddDishActivity.this, "Vui lòng điền tên và giá", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        double price = Double.parseDouble(priceStr);
                        boolean isAdded = dbHelper.addDish(name, price, description);
                        if (isAdded) {
                            Toast.makeText(AddDishActivity.this, "Đã thêm món ăn", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddDishActivity.this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddDishActivity.this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}