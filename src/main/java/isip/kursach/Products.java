package isip.kursach;

public class Products {
    private int id;
    private String name_products;
    private Double Price;
    private String Rating;
    private String Description;
    private int Category;
    private int Manufacturers;


    public Products(int id, String name_products, Double Price, String Rating, String Description, int Category, int Manufacturers) {
        this.id=id;
        this.name_products=name_products;
        this.Price=Price;
        this.Rating=Rating;
        this.Description=Description;
        this.Category=Category;
        this.Manufacturers=Manufacturers;
    }

    public int getId() {
        return id;
    }

    public String getName_products() {
        return name_products;
    }

    public Double getPrice() {
        return Price;
    }

    public String getRating() {
        return Rating;
    }
    public String getDescription() {
        return Description;
    }

    public int getCategory() {
        return Category;
    }

    public int getManufacturers() {
        return Manufacturers;

    }
}
