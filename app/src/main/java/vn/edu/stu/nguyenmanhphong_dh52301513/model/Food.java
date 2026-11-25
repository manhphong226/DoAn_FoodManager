package vn.edu.stu.nguyenmanhphong_dh52301513.model;

import androidx.annotation.NonNull;

public class Food {
    private int foodCode;
    private String foodName;
    private String category;
    private String image;
    private double price;
    private String description;

    // Chi dung de hien thi ten danh muc, khong phai cot trong SQLite.
    private String categoryName;

    public Food() {
    }

    public Food(int foodCode, String foodName, String category, String image, double price, String description) {
        this.foodCode = foodCode;
        this.foodName = foodName;
        this.category = category;
        this.image = image;
        this.price = price;
        this.description = description;
    }

    public Food(int foodCode, String foodName, String category, String image, double price, String description, String categoryName) {
        this.foodCode = foodCode;
        this.foodName = foodName;
        this.category = category;
        this.image = image;
        this.price = price;
        this.description = description;
        this.categoryName = categoryName;
    }

    public int getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(int foodCode) {
        this.foodCode = foodCode;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @NonNull
    @Override
    public String toString() {
        return foodCode + " - " + foodName + " - " + categoryName;
    }
}
