package isip.kursach;

public class Admins {
    private int id;
    private String login;
    private int code;
    private String password;

    public Admins(int id, String login, int code, String password){
        this.id=id;
        this.login=login;
        this.code=code;
        this.password=password;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public int getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }
}