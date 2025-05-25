package vn.edu.tdmu.projectsesan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class StaffAdapter extends ArrayAdapter<Staff> {
    public StaffAdapter(Context context, List<Staff> staffList) {
        super(context, 0, staffList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Staff staff = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_staff, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);
        TextView tvPosition = convertView.findViewById(R.id.tvPosition);
        TextView tvSchedule = convertView.findViewById(R.id.tvSchedule);

        tvName.setText("Tên: " + staff.getName());
        tvPhone.setText("SĐT: " + staff.getPhone());
        tvPosition.setText("Chức vụ: " + staff.getPosition());
        tvSchedule.setText("Lịch làm: " + staff.getSchedule());

        return convertView;
    }
}
