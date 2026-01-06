package com.example.eyadcomp2000;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MenuItemsListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView listView;
    private int selectedPosition = -1;
    private List<String> currentActiveList; // Tracks which list is currently on screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stafflayout4);

        listView = findViewById(R.id.staff_menu_list_view);

        // Start by viewing the Main Course list
        currentActiveList = MenuData.mainCourseList;
        refreshAdapter(currentActiveList);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            Toast.makeText(this, "Selected: " + currentActiveList.get(position), Toast.LENGTH_SHORT).show();
        });

        // Set up the buttons
        findViewById(R.id.edit_add_button).setOnClickListener(v -> showAddItemDialog());
        findViewById(R.id.delete_item_button).setOnClickListener(v -> deleteSelectedItem());
        findViewById(R.id.switch_category_button).setOnClickListener(v -> showSwitchCategoryDialog());
    }

    private void refreshAdapter(List<String> listToDisplay) {
        adapter = new ArrayAdapter<>(this, R.layout.white_text_item, listToDisplay);
        listView.setAdapter(adapter);
    }

    private void showSwitchCategoryDialog() {
        String[] categories = {"Soups", "Specials", "Appetizers", "Main Course", "Desserts", "Drinks"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("View Category");
        builder.setItems(categories, (dialog, which) -> {
            switch (which) {
                case 0: currentActiveList = MenuData.soupList; break;
                case 1: currentActiveList = MenuData.specialsList; break;
                case 2: currentActiveList = MenuData.appetizersList; break;
                case 3: currentActiveList = MenuData.mainCourseList; break;
                case 4: currentActiveList = MenuData.dessertsList; break;
                case 5: currentActiveList = MenuData.drinksList; break;
            }
            refreshAdapter(currentActiveList);
            Toast.makeText(this, "Now viewing: " + categories[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        final EditText input = new EditText(this);
        input.setHint("e.g. Chicken Dish - $12");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String item = input.getText().toString();
            if (!item.isEmpty()) {
                currentActiveList.add(item);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteSelectedItem() {
        if (selectedPosition != -1 && selectedPosition < currentActiveList.size()) {
            currentActiveList.remove(selectedPosition);
            adapter.notifyDataSetChanged();
            selectedPosition = -1;
            Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBackToMenu(View view) {
        finish();
    }
}