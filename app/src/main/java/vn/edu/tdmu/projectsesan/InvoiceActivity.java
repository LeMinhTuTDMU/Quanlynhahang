package vn.edu.tdmu.projectsesan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InvoiceActivity extends AppCompatActivity {

    private EditText etBookingId;
    private Button btnSearchInvoice;
    private TextView tvInvoiceResult;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        etBookingId = findViewById(R.id.etBookingId);
        btnSearchInvoice = findViewById(R.id.btnSearchInvoice);
        tvInvoiceResult = findViewById(R.id.tvInvoiceResult);

        dbHelper = new DatabaseHelper(this);

        btnSearchInvoice.setOnClickListener(view -> {
            String bookingIdStr = etBookingId.getText().toString().trim();
            if (bookingIdStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập booking_id", Toast.LENGTH_SHORT).show();
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            String invoice = dbHelper.getInvoiceDetails(bookingId);

            if (invoice == null || invoice.isEmpty()) {
                tvInvoiceResult.setText("Không tìm thấy hóa đơn.");
            } else {
                tvInvoiceResult.setText(invoice);
            }
        });
    }
}
