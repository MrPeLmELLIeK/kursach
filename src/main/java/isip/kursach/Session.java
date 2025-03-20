package isip.kursach;

public class Session {
    private static String login;

    public static void setLogin(String login) {
        Session.login = login;
    }

    public static String getLogin() {
        return login;
    }
}