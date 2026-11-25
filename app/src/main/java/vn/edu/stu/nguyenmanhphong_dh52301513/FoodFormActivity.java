package vn.edu.stu.nguyenmanhphong_dh52301513;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import vn.edu.stu.nguyenmanhphong_dh52301513.model.Food;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.FoodCategory;
import vn.edu.stu.nguyenmanhphong_dh52301513.util.ImageHelper;

public class FoodFormActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvFormTitle;
    ImageView ivFoodImage;
    Button btnChooseImage;
    EditText etFoodCode, etFoodName, etPrice, etDescription;
    Spinner spCategory;
    Button btnExit, btnSave;

    ArrayList<FoodCategory> categories;
    ArrayAdapter<String> categoryAdapter;
    DBHelper dbHelper;

    int editingCode = -1;
    String currentImagePath = "";
    String originalImagePath = "";

    ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        editingCode = getIntent().getIntExtra("foodCode", -1);
        if (savedInstanceState != null) {
            currentImagePath = savedInstanceState.getString("currentImagePath", "");
        }

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            String newPath = ImageHelper.copyImageToInternalStorage(
                                    FoodFormActivity.this, uri);
                            if (newPath.isEmpty()) {
                                Toast.makeText(
                                        FoodFormActivity.this,
                                        R.string.food_image_choose_failed,
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }
                            currentImagePath = newPath;
                            ImageHelper.loadImage(ivFoodImage, currentImagePath);
                        }
                    }
                });

        addControls();
        addEvents();
        loadFormData();
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        etFoodCode = findViewById(R.id.etFoodCode);
        etFoodName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);

        categories = dbHelper.getAllCategories();
        ArrayList<String> categoryNames = new ArrayList<>();
        for (FoodCategory category : categories) {
            categoryNames.add(category.getCategoryName());
        }
        categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
    }

    private void addEvents() {
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageLauncher.launch(
                        new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build()
                );
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSaveFood();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadFormData() {
        if (editingCode >= 0) {
            tvFormTitle.setText(R.string.food_form_edit);
            etFoodCode.setEnabled(false);

            Food food = dbHelper.getFoodByCode(editingCode);
            if (food == null) {
                Toast.makeText(this, R.string.food_not_found, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            etFoodCode.setText(String.valueOf(food.getFoodCode()));
            etFoodName.setText(food.getFoodName());
            etPrice.setText(formatPrice(food.getPrice()));
            etDescription.setText(food.getDescription());

            String storedImagePath = food.getImage() == null ? "" : food.getImage();
            originalImagePath = storedImagePath;
            if (currentImagePath.isEmpty()) {
                currentImagePath = storedImagePath;
            }
            ImageHelper.loadImage(ivFoodImage, currentImagePath);

            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategoryCode().equals(food.getCategory())) {
                    spCategory.setSelection(i);
                    break;
                }
            }
        } else {
            tvFormTitle.setText(R.string.food_form_add);
            etFoodCode.setText(String.valueOf(dbHelper.getNextFoodCode()));
            ImageHelper.loadImage(ivFoodImage, currentImagePath);

            String preselectedCategory = getIntent().getStringExtra("categoryCode");
            if (preselectedCategory != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getCategoryCode().equals(preselectedCategory)) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private String formatPrice(double price) {
        if (price == Math.floor(price)) {
            return String.valueOf((long) price);
        }
        return String.valueOf(price);
    }

    private void processSaveFood() {
        String codeText = etFoodCode.getText().toString().trim();
        if (codeText.isEmpty()) {
            Toast.makeText(this, R.string.food_code_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        int foodCode;
        try {
            foodCode = Integer.parseInt(codeText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.food_code_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (foodCode <= 0) {
            Toast.makeText(this, R.string.food_code_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingCode < 0 && dbHelper.isFoodCodeExists(foodCode)) {
            Toast.makeText(this, R.string.food_code_duplicate, Toast.LENGTH_SHORT).show();
            return;
        }

        String foodName = etFoodName.getText().toString().trim();
        if (foodName.isEmpty()) {
            Toast.makeText(this, R.string.food_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryPosition = spCategory.getSelectedItemPosition();
        if (categoryPosition < 0 || categoryPosition >= categories.size()) {
            Toast.makeText(this, R.string.food_no_category, Toast.LENGTH_SHORT).show();
            return;
        }
        FoodCategory category = categories.get(categoryPosition);

        double price;
        try {
            price = Double.parseDouble(etPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.food_price_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (price < 0) {
            Toast.makeText(this, R.string.food_price_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        String description = etDescription.getText().toString().trim();
        String image = currentImagePath == null ? "" : currentImagePath;

        Food food = new Food(
                foodCode,
                foodName,
                category.getCategoryCode(),
                image,
                price,
                description
        );

        boolean result;
        if (editingCode < 0) {
            result = dbHelper.insertFood(food);
        } else {
            result = dbHelper.updateFood(food);
        }

        if (result) {
            if (!originalImagePath.isEmpty()
                    && !originalImagePath.equals(currentImagePath)) {
                ImageHelper.deleteImage(originalImagePath);
            }
            Toast.makeText(this, R.string.food_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.food_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAbout) {
            Intent intent = new Intent(FoodFormActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnuExit) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentImagePath", currentImagePath);
    }
}
