package isip.kursach;

import java.time.LocalDate;
import java.util.Date;

public class Users {
    private int id;
    private String login;
    private String email;
    private String password;
    private String number_phone;
    private String address;
    private String full_name;
    private LocalDate birthday;

    public Users(int id, String login, String email, String password, String number_phone, String address, String full_name, LocalDate birthday){
        this.id=id;
        this.login=login;
        this.email=email;
        this.password=password;
        this.number_phone=number_phone;
        this.address=address;
        this.full_name=full_name;
        this.birthday=birthday;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber_phone() { return number_phone; }

    public String getAddress() { return address; }

    public String getFull_name() { return full_name; }

    public LocalDate getBirthday() { return birthday; }
}
