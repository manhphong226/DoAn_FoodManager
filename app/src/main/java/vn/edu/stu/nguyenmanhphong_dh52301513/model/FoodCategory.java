package vn.edu.stu.nguyenmanhphong_dh52301513.model;

import androidx.annotation.NonNull;

public class FoodCategory {
    private String categoryCode;
    private String categoryName;

    public FoodCategory() {
    }

    public FoodCategory(String categoryCode, String categoryName) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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
        return categoryCode + " - " + categoryName;
    }
}
