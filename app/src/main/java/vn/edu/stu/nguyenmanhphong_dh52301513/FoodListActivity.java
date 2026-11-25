package vn.edu.stu.nguyenmanhphong_dh52301513;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import vn.edu.stu.nguyenmanhphong_dh52301513.adapter.FoodAdapter;
import vn.edu.stu.nguyenmanhphong_dh52301513.model.Food;
import vn.edu.stu.nguyenmanhphong_dh52301513.util.ImageHelper;

public class FoodListActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvHeader;
    RecyclerView recyclerView;
    FloatingActionButton btnAddFood;

    ArrayList<Food> data;
    FoodAdapter adapter;
    DBHelper dbHelper;
    String categoryCode = null;
    String categoryName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryCode = getIntent().getStringExtra("categoryCode");
        categoryName = getIntent().getStringExtra("categoryName");

        dbHelper = new DBHelper(this);
        addControls();
        addEvents();
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvHeader = findViewById(R.id.tvHeader);
        if (categoryCode == null || categoryCode.isEmpty()) {
            tvHeader.setText(R.string.food_list_title_all);
        } else {
            tvHeader.setText(getString(R.string.food_list_title_category, categoryName));
        }

        recyclerView = findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        adapter = new FoodAdapter(this, data, new FoodAdapter.OnFoodActionListener() {
            @Override
            public void onFoodClick(Food food) {
                openFoodForm(food.getFoodCode());
            }

            @Override
            public void onFoodLongClick(Food food) {
                showFoodOptions(food);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAddFood = findViewById(R.id.btnAddFood);
    }

    private void addEvents() {
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFoodForm(-1);
            }
        });
    }

    private void showFoodOptions(Food food) {
        String[] options = new String[]{
                getString(R.string.action_edit),
                getString(R.string.action_delete)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(food.getFoodName());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    openFoodForm(food.getFoodCode());
                } else {
                    processDeleteFood(food);
                }
            }
        });
        builder.show();
    }

    private void processDeleteFood(Food food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.food_delete_confirm_title);
        builder.setMessage(getString(R.string.food_delete_confirm_message, food.getFoodName()));
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.deleteFood(food.getFoodCode());
                ImageHelper.deleteImage(food.getImage());
                Toast.makeText(
                        FoodListActivity.this,
                        R.string.food_deleted,
                        Toast.LENGTH_SHORT
                ).show();
                loadFoods();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.show();
    }

    private void openFoodForm(int foodCode) {
        Intent intent = new Intent(FoodListActivity.this, FoodFormActivity.class);
        if (foodCode >= 0) {
            intent.putExtra("foodCode", foodCode);
        } else if (categoryCode != null && !categoryCode.isEmpty()) {
            intent.putExtra("categoryCode", categoryCode);
        }
        startActivity(intent);
    }

    private void loadFoods() {
        data.clear();
        if (categoryCode == null || categoryCode.isEmpty()) {
            data.addAll(dbHelper.getAllFoods());
        } else {
            data.addAll(dbHelper.getFoodsByCategory(categoryCode));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFoods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAbout) {
            Intent intent = new Intent(FoodListActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnuExit) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }
}
