package isip.kursach;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProductsDetailsController {

    @FXML
    private Label idLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label manufacturerLabel;

    public void setProduct(Products product) {
        // Устанавливаем значения в Label
        idLabel.setText("ID: " + product.getId());
        nameLabel.setText("Name: " + product.getName_products());
        priceLabel.setText("Price: " + product.getPrice());
        ratingLabel.setText("Rating: " + product.getRating());
        descriptionLabel.setText("Description: " + product.getDescription());
        categoryLabel.setText("Category: " + product.getCategory());
        manufacturerLabel.setText("Manufacturer: " + product.getManufacturers());
    }
}