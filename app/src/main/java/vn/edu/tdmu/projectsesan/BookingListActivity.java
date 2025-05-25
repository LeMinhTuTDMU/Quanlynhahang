package vn.edu.tdmu.projectsesan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class BookingListActivity extends AppCompatActivity {

    private ListView lvBookings;
    private DatabaseHelper dbHelper;
    private List<String> bookings;
    private int selectedBookingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        dbHelper = new DatabaseHelper(this);
        lvBookings = findViewById(R.id.lvBookings);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Load danh sách booking
        loadBookings();
        Button btnPayNow = findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(v -> {
            if (selectedBookingId != -1) {
                showPaymentDialog(selectedBookingId);
            } else {
                Toast.makeText(this, "Vui lòng chọn bàn muốn thanh toán trước!", Toast.LENGTH_SHORT).show();
            }
        });
        lvBookings.setOnItemClickListener((adapterView, view, position, id) -> {
            String selected = bookings.get(position);
            selectedBookingId = extractBookingId(selected); // Lưu lại booking đang chọn

            if (selectedBookingId != -1) {
                Toast.makeText(this, "Đã chọn bàn có mã: " + selectedBookingId, Toast.LENGTH_SHORT).show();
            }

        });


        // Nút xuất hóa đơn
        Button btnInvoice = findViewById(R.id.btnInvoice);
        btnInvoice.setOnClickListener(v -> {
            // Khi bấm nút xuất hóa đơn, mở InvoiceActivity và truyền bookingId vào
            if (selectedBookingId != -1) {
                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("BOOKING_ID", selectedBookingId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn bàn để xem hóa đơn!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void loadBookings() {
        bookings = dbHelper.getAllBookingInfo(); // Đảm bảo hàm này trả về danh sách bookings với thông tin đầy đủ
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookings);
        lvBookings.setAdapter(adapter);
    }

    private int extractBookingId(String bookingInfo) {
        try {
            String[] parts = bookingInfo.split(" - ");
            return Integer.parseInt(parts[0].trim()); // lấy booking_id
        } catch (Exception e) {
            return -1; // Trả về -1 nếu không trích xuất được booking_id
        }
    }

    private void showPaymentDialog(int bookingId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thanh toán");

        // Thiết lập layout cho Dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);

        EditText edtPaymentMethod = dialogView.findViewById(R.id.etPaymentMethod);

        builder.setPositiveButton("Thanh toán", (dialog, which) -> {
            String paymentMethod = edtPaymentMethod.getText().toString();

            if (paymentMethod.isEmpty()) {
                Toast.makeText(BookingListActivity.this, "Vui lòng nhập phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thực hiện thanh toán
            long paymentId = dbHelper.processPayment(bookingId, paymentMethod, 10, 5);

            if (paymentId != -1) {
                Toast.makeText(this, "Thanh toán thành công! Mã hóa đơn: " + paymentId, Toast.LENGTH_SHORT).show();
                loadBookings(); // Cập nhật lại danh sách
                showInvoiceDialog(bookingId, paymentId); // Hiển thị hóa đơn ngay sau khi thanh toán
            } else {
                Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }



    private void showInvoiceDialog(int bookingId, long paymentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hóa đơn thanh toán");

        // Lấy thông tin hóa đơn từ DB
        String invoiceInfo = dbHelper.getInvoiceDetails((int) bookingId);

        builder.setMessage(invoiceInfo);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}