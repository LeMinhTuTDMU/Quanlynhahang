package vn.edu.tdmu.projectsesan;

public class Staff {
    private int id;
    private String Name;
    private String phone;
    private String position;
    private String schedule;

    // Constructor
    public Staff(int id, String Name, String phone, String position, String schedule) {
        this.id = id;
        this.Name = Name;
        this.phone = phone;
        this.position = position;
        this.schedule = schedule;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "Staff [id=" + id + ", Name=" + Name + ", phone=" + phone + ", position=" + position + ", schedule=" + schedule + "]";
    }
}
