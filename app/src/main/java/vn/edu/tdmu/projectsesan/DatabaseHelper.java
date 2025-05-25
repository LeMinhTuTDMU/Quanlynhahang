package vn.edu.tdmu.projectsesan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    // ✅ Khai báo các biến CONSTANT (viết hoa, public nếu cần dùng ở class khác)
    public static final String DATABASE_NAME = "Sesan.db";
    public static final int DATABASE_VERSION = 18 ;

    // Tên bảng và các cột
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    public static final String TABLE_DISHES = "dishes";
    public static final String COLUMN_DISH_ID = "dish_id";
    public static final String COLUMN_DISH_NAME = "dish_name";
    public static final String COLUMN_DISH_PRICE = "price";
    public static final String COLUMN_DISH_DESC = "description";

    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKING_ID = "booking_id";
    public static final String COLUMN_TABLE_NUMBER = "table_number";
    public static final String COLUMN_BOOKING_TIME = "booking_time";
    public static final String COLUMN_BOOKING_USER_ID = "user_id";
    public static final String COLUMN_GUEST_COUNT = "guest_count";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_BOOKING_STATUS = "booking_status"; // Đổi tên thành booking_status

    public static final String TABLE_TABLES = "tables";
    public static final String COLUMN_TABLE_ID = "table_id";
    public static final String COLUMN_TABLE_NAME = "table_name";
    public static final String COLUMN_TABLE_FLOOR = "floor";
    public static final String COLUMN_TABLE_CAPACITY = "capacity";

    // Bảng payments
    public static final String TABLE_PAYMENTS = "payments";

    public static final String COLUMN_PAYMENT_ID = "payment_id";
    public static final String COLUMN_PAYMENT_BOOKING_ID = "booking_id";
    public static final String COLUMN_PAYMENT_AMOUNT = "amount";
    public static final String COLUMN_PAYMENT_TIME = "payment_time";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_PAYMENT_TAX = "tax";
    public static final String COLUMN_PAYMENT_DISCOUNT = "discount";

    // Bảng order_items
        public static final String TABLE_ORDER_ITEMS = "order_items";
        public static final String COLUMN_ORDER_ID = "id";
        public static final String COLUMN_ORDER_BOOKING_ID = "booking_id";
        public static final String COLUMN_ORDER_DISH_NAME = "dish_name";
        public static final String COLUMN_ORDER_QUANTITY = "quantity";
        public static final String COLUMN_ORDER_DISH_ID = "dish_id";

    public static final String TABLE_STAFF = "staff";
    public static final String STAFF_ID = "id";
    public static final String STAFF_FULL_NAME = "full_name";
    public static final String STAFF_PHONE = "phone";
    public static final String STAFF_POSITION = "position";
    public static final String STAFF_SCHEDULE = "schedule";
    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getAllDishes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_DISHES,
                new String[]{COLUMN_DISH_ID + " AS _id", COLUMN_DISH_NAME, COLUMN_DISH_PRICE, COLUMN_DISH_DESC},
                null, null, null, null, null
        );

        // Log kiểm tra các cột có trong cursor
        Log.d("DATABASE", "Columns: " + Arrays.toString(cursor.getColumnNames()));
        return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users



        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "role TEXT" +        // "admin", "manager", "staff"
                ");";
        db.execSQL(CREATE_USERS_TABLE);

        String INSERT_DEFAULT_ADMIN = "INSERT INTO users (username, password, role) " +
                "VALUES ('admin', 'admin123', 'admin')";
        db.execSQL(INSERT_DEFAULT_ADMIN);
        // Tạo bảng dishes
        String CREATE_DISHES_TABLE = "CREATE TABLE " + TABLE_DISHES + "("
                + COLUMN_DISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DISH_NAME + " TEXT,"
                + COLUMN_DISH_PRICE + " REAL,"
                + COLUMN_DISH_DESC + " TEXT)";
        db.execSQL(CREATE_DISHES_TABLE);

        // Tạo bảng đặt bàn
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TABLE_NUMBER + " TEXT NOT NULL,"
                + COLUMN_BOOKING_TIME + " TEXT NOT NULL,"
                + COLUMN_BOOKING_USER_ID + " INTEGER,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_FLOOR + " TEXT,"
                + COLUMN_GUEST_COUNT + " INTEGER DEFAULT 1,"
                + COLUMN_BOOKING_STATUS + " TEXT DEFAULT 'Chưa thanh toán',"
                + "FOREIGN KEY(" + COLUMN_BOOKING_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        // Tạo bảng tables
        String CREATE_TABLES_TABLE = "CREATE TABLE " + TABLE_TABLES + "("
                + COLUMN_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TABLE_NAME + " TEXT,"
                + COLUMN_TABLE_FLOOR + " TEXT,"
                + COLUMN_TABLE_CAPACITY + " INTEGER)";
        db.execSQL(CREATE_TABLES_TABLE);

        // Tạo bảng payments
        String CREATE_PAYMENTS_TABLE = "CREATE TABLE IF NOT EXISTS payments (" +
                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "booking_id INTEGER NOT NULL," +
                "amount REAL NOT NULL," +
                "payment_time TEXT DEFAULT CURRENT_TIMESTAMP," +
                "payment_method TEXT NOT NULL," +
                "tax REAL DEFAULT 0," +
                "discount REAL DEFAULT 0," +
                "FOREIGN KEY(booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE)";
        db.execSQL(CREATE_PAYMENTS_TABLE);

        String CREATE_STAFF_TABLE = "CREATE TABLE staff (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "full_name TEXT, " +           // Chuyển từ full_name thành name
                "phone TEXT, " +
                "position TEXT, " +         // chức vụ: nhân viên, quản lý
                "schedule TEXT, " +         // lịch làm việc (dạng text hoặc json)
                "FOREIGN KEY(user_id) REFERENCES users(user_id))";
        db.execSQL(CREATE_STAFF_TABLE);

        String CREATE_ORDER_ITEMS_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_BOOKING_ID + " INTEGER, " +
                COLUMN_ORDER_DISH_ID + " INTEGER, " +  // dùng hằng số mới
                COLUMN_ORDER_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ORDER_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(booking_id), " +
                "FOREIGN KEY(" + COLUMN_ORDER_DISH_ID + ") REFERENCES " + TABLE_DISHES + "(dish_id)" +
                ")";
        db.execSQL(CREATE_ORDER_ITEMS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu nâng cấp phiên bản

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TABLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS payments");
        onCreate(db);





    }
    public boolean AddStaff(Staff staff) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STAFF_FULL_NAME, staff.getName());
        values.put(STAFF_PHONE, staff.getPhone());
        values.put(STAFF_POSITION, staff.getPosition());
        values.put(STAFF_SCHEDULE, staff.getSchedule());

        long result = db.insert(TABLE_STAFF, null, values);
        return result != -1;
    }

    // Phương thức thêm user
    public long insertUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);

        // Kiểm tra username đã tồn tại chưa
        Cursor cursor = db.query("users", null, "username = ?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return -1; // tồn tại rồi
        }

        long id = db.insert("users", null, values);
        db.close();
        return id;
    }

    public boolean insertStaff(int userId, String fullName, String phone, String position) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("full_name", fullName);
        values.put("phone", phone);
        values.put("position", position);
        values.put("schedule", ""); // mặc định rỗng

        long result = db.insert("staff", null, values);
        db.close();
        return result != -1;
    }


    // Phương thức kiểm tra đăng nhập

    public boolean addDish(String name, double price, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DISH_NAME, name);
        values.put(COLUMN_DISH_PRICE, price);
        values.put(COLUMN_DISH_DESC, description);

        long result = db.insert(TABLE_DISHES, null, values);
        return result != -1; // Trả về true nếu thêm thành công
    }

    public long addBooking(int userId, String fullName, String bookingTime, String floor, String tableNumber, int guestCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_BOOKING_TIME, bookingTime);
        values.put(COLUMN_FLOOR, floor);
        values.put(COLUMN_TABLE_NUMBER, tableNumber);
        values.put(COLUMN_GUEST_COUNT, guestCount);

        long bookingId = db.insert(TABLE_BOOKINGS, null, values);
        db.close();
        return bookingId;
    }

    public Cursor getUserBooking(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_BOOKINGS,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null);
    }
    public List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TABLES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TABLE_NAME));
                String floor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TABLE_FLOOR));
                tables.add(name + " - Tầng " + floor);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tables;
    }
    public void debugBookings() {
        List<String> bookings = getAllBookingInfo();
        for (String info : bookings) {
            Log.d("Bookings", info);
        }
    }
    public List<String> getAllBookingInfo() {
        List<String> bookingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy các booking chưa thanh toán
        Cursor cursor = db.rawQuery(
                "SELECT b.booking_id, b.table_number, b.full_name, b.guest_count, b.floor, b.booking_time, b.booking_status " +
                        "FROM bookings b " +
                        "LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                        "WHERE p.booking_id IS NULL", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String table = cursor.getString(1);
                String name = cursor.getString(2);
                int guests = cursor.getInt(3);
                String floor = cursor.getString(4);
                String time = cursor.getString(5);
                String status = cursor.getString(6);

                String info = id + " - Bàn " + table + " - " + name + " - " + guests + " khách - Tầng " + floor + " - Lúc " + time + " - " + status;
                bookingList.add(info);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookingList;
    }

    public void addOrderItem(int bookingId, int dishId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_BOOKING_ID, bookingId);
        values.put("dish_id", dishId);
        values.put(COLUMN_ORDER_QUANTITY, quantity);
        db.insert(TABLE_ORDER_ITEMS, null, values);
        db.close();
    }



    public long processPayment(int bookingId, String paymentMethod, double taxPercent, double discountPercent) {
        SQLiteDatabase db = this.getWritableDatabase();
        long paymentId = -1;
        db.beginTransaction();

        try {
            double totalAmount = 0;

            // ✅ JOIN order_items với dishes để lấy quantity và price
            Cursor cursor = db.rawQuery(
                    "SELECT oi.quantity, d.price FROM order_items oi " +
                            "JOIN dishes d ON oi.dish_id = d.dish_id " +
                            "WHERE oi.booking_id = ?",
                    new String[]{String.valueOf(bookingId)}
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                    totalAmount += price * quantity;
                } while (cursor.moveToNext());
                cursor.close();
            }

            if (totalAmount <= 0) {
                Log.e("PAYMENT_ERROR", "Tổng tiền = 0 hoặc không có món ăn.");
                return -1;
            }

            double tax = totalAmount * (taxPercent / 100);
            double discount = totalAmount * (discountPercent / 100);
            double finalAmount = totalAmount + tax - discount;

            ContentValues values = new ContentValues();
            values.put("booking_id", bookingId);
            values.put("amount", finalAmount);
            values.put("payment_time", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
            values.put("payment_method", paymentMethod);
            values.put("tax", tax);
            values.put("discount", discount);

            paymentId = db.insert("payments", null, values);

            if (paymentId == -1) {
                Log.e("PAYMENT_ERROR", "Insert thất bại: " + values.toString());
            } else {
                ContentValues update = new ContentValues();
                update.put("booking_status", "đã thanh toán");
                db.update("bookings", update, "booking_id = ?", new String[]{String.valueOf(bookingId)});

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return paymentId;
    }




    public String getInvoiceDetails(int bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder invoice = new StringBuilder();

        Cursor bookingCursor = db.rawQuery(
                "SELECT b.booking_id, b.full_name, b.booking_time, p.amount, p.payment_method, p.tax, p.discount, p.payment_time " +
                        "FROM bookings b LEFT JOIN payments p ON b.booking_id = p.booking_id WHERE b.booking_id = ?",
                new String[]{String.valueOf(bookingId)}
        );

        if (bookingCursor.moveToFirst()) {
            invoice.append("HÓA ĐƠN THANH TOÁN\n");
            invoice.append("Booking ID: ").append(bookingCursor.getInt(0)).append("\n");
            invoice.append("Khách: ").append(bookingCursor.getString(1)).append("\n");
            invoice.append("Thời gian đặt: ").append(bookingCursor.getString(2)).append("\n");
            invoice.append("Thời gian thanh toán: ").append(bookingCursor.getString(7)).append("\n");
            invoice.append("Phương thức: ").append(bookingCursor.getString(4)).append("\n");
            invoice.append("Tổng tiền: ").append(bookingCursor.getDouble(3)).append(" VND\n");
            invoice.append("Thuế: ").append(bookingCursor.getDouble(5)).append(" VND\n");
            invoice.append("Giảm giá: ").append(bookingCursor.getDouble(6)).append(" VND\n");
        }else {
            invoice.append("Không tìm thấy hóa đơn.");
        }

        bookingCursor.close();

        // Thêm chi tiết món ăn
        invoice.append("\nChi tiết món ăn:\n");

        Cursor itemsCursor = db.rawQuery(
                "SELECT dish_name, quantity FROM order_items WHERE booking_id = ?",
                new String[]{String.valueOf(bookingId)}
        );

        if (itemsCursor.moveToFirst()) {
            do {
                String dish = itemsCursor.getString(0);
                int quantity = itemsCursor.getInt(1);
                invoice.append("- ").append(dish).append(" x ").append(quantity).append("\n");
            } while (itemsCursor.moveToNext());
        }

        itemsCursor.close();
        db.close();

        return invoice.toString();
    }


    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STAFF, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(STAFF_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(STAFF_FULL_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(STAFF_PHONE));
                String position = cursor.getString(cursor.getColumnIndexOrThrow(STAFF_POSITION));
                String schedule = cursor.getString(cursor.getColumnIndexOrThrow(STAFF_SCHEDULE));
                staffList.add(new Staff(id, name, phone, position, schedule));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return staffList;
    }

    public Cursor getUserWithRole(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT user_id , role FROM users WHERE username = ? AND password = ?";
        return db.rawQuery(query, new String[]{username, password});
    }

    public int getDishIdByName(String dishName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DISHES,
                new String[]{COLUMN_DISH_ID},
                COLUMN_DISH_NAME + " = ?",
                new String[]{dishName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISH_ID));
            cursor.close();
            return id;
        }

        if (cursor != null) cursor.close();
        return -1; // Không tìm thấy
    }

}
