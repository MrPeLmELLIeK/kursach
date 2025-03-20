package isip.kursach;

public class Category {
    private int id;
    private String name_category;

    public Category(int id, String name_category){
        this.id = id;
        this.name_category = name_category;
    }

    public int getId(){
        return id;
    }

    public String getName_category() {
        return name_category;
    }
}
