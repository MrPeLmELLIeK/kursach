package isip.kursach;

public class RecommendedProducts {
    private int id;
    private String name_products;
    private Double Price;
    private String Rating;
    private String Description;
    private int Category;

    public RecommendedProducts(int id, String name_products, String Description, String Rating, int Category,  Double Price){
        this.id=id;
        this.name_products=name_products;
        this.Description=Description;
        this.Rating=Rating;
        this.Category=Category;
        this.Price=Price;
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


}
