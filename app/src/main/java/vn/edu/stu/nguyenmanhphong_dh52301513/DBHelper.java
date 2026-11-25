package vn.edu.stu.nguyenmanhphong_dh52301513;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import vn.edu.stu.nguyenmanhphong_dh52301513.model.Food;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.FoodCategory;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "dbquanlymonan.sqlite";
    public static final int DB_VERSION = 1;

    public static final String TABLE_CATEGORY = "FoodCategory";
    public static final String TABLE_FOOD = "Food";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + " (" +
                "categoryCode TEXT PRIMARY KEY, " +
                "categoryName TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_FOOD + " (" +
                "foodCode INTEGER PRIMARY KEY, " +
                "foodName TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "image TEXT, " +
                "price REAL, " +
                "description TEXT, " +
                "FOREIGN KEY (category) REFERENCES " + TABLE_CATEGORY + "(categoryCode))");

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        insertCategory(db, "TYPE01", "Menu Thái");
        insertCategory(db, "TYPE02", "Menu Việt");
        insertCategory(db, "TYPE03", "Menu Nhật");

        insertFood(db, new Food(1, "Pad Thai", "TYPE01", "", 85000,
                "Mì xào Thái với tôm, đậu phộng và nước sốt me"));
        insertFood(db, new Food(2, "Tom Yum", "TYPE01", "", 95000,
                "Súp chua cay Thái Lan với tôm và nấm"));
        insertFood(db, new Food(3, "Phở bò", "TYPE02", "", 65000,
                "Phở bò truyền thống với nước dùng hầm xương"));
        insertFood(db, new Food(4, "Bánh mì", "TYPE02", "", 35000,
                "Bánh mì Việt Nam với thịt, pate và rau thơm"));
        insertFood(db, new Food(5, "Sushi", "TYPE03", "", 120000,
                "Cơm cuộn Nhật Bản với cá hồi tươi"));
        insertFood(db, new Food(6, "Ramen", "TYPE03", "", 110000,
                "Mì nước Nhật Bản với nước dùng xương heo"));
    }

    // ==================== FoodCategory ====================

    public ArrayList<FoodCategory> getAllCategories() {
        ArrayList<FoodCategory> data = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT categoryCode, categoryName FROM " + TABLE_CATEGORY +
                        " ORDER BY categoryCode", null);
        while (cursor.moveToNext()) {
            data.add(new FoodCategory(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        database.close();
        return data;
    }

    public FoodCategory getCategory(String categoryCode) {
        FoodCategory category = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT categoryCode, categoryName FROM " + TABLE_CATEGORY +
                        " WHERE categoryCode = ?", new String[]{categoryCode});
        if (cursor.moveToNext()) {
            category = new FoodCategory(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        database.close();
        return category;
    }

    public boolean insertCategory(String categoryCode, String categoryName) {
        SQLiteDatabase database = getWritableDatabase();
        boolean result = insertCategory(database, categoryCode, categoryName);
        database.close();
        return result;
    }

    private boolean insertCategory(SQLiteDatabase database, String categoryCode, String categoryName) {
        ContentValues values = new ContentValues();
        values.put("categoryCode", categoryCode);
        values.put("categoryName", categoryName);
        long id = database.insert(TABLE_CATEGORY, null, values);
        return id != -1;
    }

    public boolean updateCategory(String categoryCode, String categoryName) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("categoryName", categoryName);
        int rows = database.update(TABLE_CATEGORY, values,
                "categoryCode = ?", new String[]{categoryCode});
        database.close();
        return rows > 0;
    }

    public boolean deleteCategory(String categoryCode) {
        SQLiteDatabase database = getWritableDatabase();
        int rows = database.delete(TABLE_CATEGORY,
                "categoryCode = ?", new String[]{categoryCode});
        database.close();
        return rows > 0;
    }

    public int countFoodsByCategory(String categoryCode) {
        int count = 0;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_FOOD + " WHERE category = ?",
                new String[]{categoryCode});
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return count;
    }

    // ==================== Food ====================

    public ArrayList<Food> getAllFoods() {
        return queryFoods(null);
    }

    public ArrayList<Food> getFoodsByCategory(String categoryCode) {
        return queryFoods(categoryCode);
    }

    private ArrayList<Food> queryFoods(String categoryCode) {
        ArrayList<Food> data = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT f.foodCode, f.foodName, f.category, f.image, f.price, f.description, " +
                "c.categoryName FROM " + TABLE_FOOD + " f " +
                "LEFT JOIN " + TABLE_CATEGORY + " c ON f.category = c.categoryCode ";
        Cursor cursor;
        if (categoryCode == null) {
            cursor = database.rawQuery(sql + "ORDER BY f.foodCode", null);
        } else {
            cursor = database.rawQuery(sql + "WHERE f.category = ? ORDER BY f.foodCode",
                    new String[]{categoryCode});
        }
        while (cursor.moveToNext()) {
            data.add(new Food(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getString(5),
                    cursor.getString(6)
            ));
        }
        cursor.close();
        database.close();
        return data;
    }

    public Food getFoodByCode(int foodCode) {
        Food food = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT foodCode, foodName, category, image, price, description FROM " +
                        TABLE_FOOD + " WHERE foodCode = ?",
                new String[]{String.valueOf(foodCode)});
        if (cursor.moveToNext()) {
            food = new Food(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getString(5)
            );
        }
        cursor.close();
        database.close();
        return food;
    }

    public boolean isFoodCodeExists(int foodCode) {
        boolean exists = false;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT foodCode FROM " + TABLE_FOOD + " WHERE foodCode = ?",
                new String[]{String.valueOf(foodCode)});
        exists = cursor.moveToNext();
        cursor.close();
        database.close();
        return exists;
    }

    public int getNextFoodCode() {
        int nextCode = 1;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MAX(foodCode) FROM " + TABLE_FOOD, null);
        if (cursor.moveToNext() && !cursor.isNull(0)) {
            nextCode = cursor.getInt(0) + 1;
        }
        cursor.close();
        database.close();
        return nextCode;
    }

    public boolean insertFood(Food food) {
        SQLiteDatabase database = getWritableDatabase();
        boolean result = insertFood(database, food);
        database.close();
        return result;
    }

    private boolean insertFood(SQLiteDatabase database, Food food) {
        ContentValues values = new ContentValues();
        values.put("foodCode", food.getFoodCode());
        values.put("foodName", food.getFoodName());
        values.put("category", food.getCategory());
        values.put("image", food.getImage());
        values.put("price", food.getPrice());
        values.put("description", food.getDescription());
        long id = database.insert(TABLE_FOOD, null, values);
        return id != -1;
    }

    public boolean updateFood(Food food) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("foodName", food.getFoodName());
        values.put("category", food.getCategory());
        values.put("image", food.getImage());
        values.put("price", food.getPrice());
        values.put("description", food.getDescription());
        int rows = database.update(TABLE_FOOD, values,
                "foodCode = ?", new String[]{String.valueOf(food.getFoodCode())});
        database.close();
        return rows > 0;
    }

    public boolean deleteFood(int foodCode) {
        SQLiteDatabase database = getWritableDatabase();
        int rows = database.delete(TABLE_FOOD,
                "foodCode = ?", new String[]{String.valueOf(foodCode)});
        database.close();
        return rows > 0;
    }
}
