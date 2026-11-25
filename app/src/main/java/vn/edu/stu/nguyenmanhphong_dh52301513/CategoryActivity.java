package vn.edu.stu.nguyenmanhphong_dh52301513;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import vn.edu.stu.nguyenmanhphong_dh52301513.adapter.CategoryAdapter;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.FoodCategory;

public class CategoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvFormTitle;
    EditText etCategoryCode, etCategoryName;
    Button btnClear, btnSave;
    RecyclerView recyclerView;

    ArrayList<FoodCategory> data;
    CategoryAdapter adapter;
    DBHelper dbHelper;
    String editingCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        addControls();
        addEvents();

        if (savedInstanceState != null) {
            editingCode = savedInstanceState.getString("editingCode");
            if (editingCode != null) {
                etCategoryCode.setEnabled(false);
                tvFormTitle.setText(R.string.category_form_edit);
            }
        }
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvFormTitle = findViewById(R.id.tvFormTitle);
        etCategoryCode = findViewById(R.id.etCategoryCode);
        etCategoryName = findViewById(R.id.etCategoryName);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);

        recyclerView = findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new CategoryAdapter(this, data,
                new CategoryAdapter.OnCategoryActionListener() {
                    @Override
                    public void onCategoryClick(FoodCategory category) {
                        openFoodList(category);
                    }

                    @Override
                    public void onCategoryLongClick(FoodCategory category) {
                        showCategoryOptions(category);
                    }
                });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processSaveCategory();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearForm();
            }
        });
    }

    private void processSaveCategory() {
        String categoryCode = etCategoryCode.getText().toString().trim().toUpperCase(Locale.ROOT);
        String categoryName = etCategoryName.getText().toString().trim();

        if (categoryCode.isEmpty()) {
            Toast.makeText(this, R.string.category_empty_code, Toast.LENGTH_SHORT).show();
            return;
        }
        if (categoryName.isEmpty()) {
            Toast.makeText(this, R.string.category_empty_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingCode == null) {
            boolean inserted = dbHelper.insertCategory(categoryCode, categoryName);
            if (!inserted) {
                Toast.makeText(this, R.string.category_duplicate, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateCategory(editingCode, categoryName);
            Toast.makeText(this, R.string.category_updated, Toast.LENGTH_SHORT).show();
        }

        clearForm();
        loadCategories();
    }

    private void clearForm() {
        editingCode = null;
        etCategoryCode.setText("");
        etCategoryName.setText("");
        etCategoryCode.setEnabled(true);
        tvFormTitle.setText(R.string.category_form_add);
    }

    private void processEditCategory(FoodCategory category) {
        editingCode = category.getCategoryCode();
        etCategoryCode.setText(category.getCategoryCode());
        etCategoryName.setText(category.getCategoryName());
        etCategoryCode.setEnabled(false);
        tvFormTitle.setText(R.string.category_form_edit);
    }

    private void showCategoryOptions(FoodCategory category) {
        String[] options = new String[]{
                getString(R.string.action_edit),
                getString(R.string.action_delete)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(category.getCategoryName());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    processEditCategory(category);
                } else {
                    processDeleteCategory(category);
                }
            }
        });
        builder.show();
    }

    private void processDeleteCategory(FoodCategory category) {
        int foodCount = dbHelper.countFoodsByCategory(category.getCategoryCode());
        if (foodCount > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.category_delete_blocked_title);
            builder.setMessage(getString(
                    R.string.category_delete_blocked_message,
                    category.getCategoryName(),
                    foodCount
            ));
            builder.setPositiveButton(R.string.button_ok, null);
            builder.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.category_delete_confirm_title);
        builder.setMessage(getString(
                R.string.category_delete_confirm_message,
                category.getCategoryName()
        ));
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.deleteCategory(category.getCategoryCode());
                if (editingCode != null && editingCode.equals(category.getCategoryCode())) {
                    clearForm();
                }
                Toast.makeText(
                        CategoryActivity.this,
                        R.string.category_deleted,
                        Toast.LENGTH_SHORT
                ).show();
                loadCategories();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.show();
    }

    private void openFoodList(FoodCategory category) {
        Intent intent = new Intent(CategoryActivity.this, FoodListActivity.class);
        intent.putExtra("categoryCode", category.getCategoryCode());
        intent.putExtra("categoryName", category.getCategoryName());
        startActivity(intent);
    }

    private void loadCategories() {
        data.clear();
        data.addAll(dbHelper.getAllCategories());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("editingCode", editingCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAllFoods) {
            Intent intent = new Intent(CategoryActivity.this, FoodListActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnuAbout) {
            Intent intent = new Intent(CategoryActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnuExit) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}
