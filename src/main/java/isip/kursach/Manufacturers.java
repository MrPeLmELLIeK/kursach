package isip.kursach;

public class Manufacturers {
    private int id;
    private String Name_manufacturers;
    private String Organization_name;


    public Manufacturers(int id, String Name_manufacturers, String Organization_name) {
        this.id=id;
        this.Name_manufacturers=Name_manufacturers;
        this.Organization_name=Organization_name;
    }

    public int getId() {
        return id;
    }

    public String getName_manufacturers() {
        return Name_manufacturers;
    }

    public String getOrganization_name() {
        return Organization_name;
    }
}