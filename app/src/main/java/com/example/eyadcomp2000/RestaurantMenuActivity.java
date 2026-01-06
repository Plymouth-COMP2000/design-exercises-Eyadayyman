package com.example.eyadcomp2000;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class RestaurantMenuActivity extends AppCompatActivity {

    private ListView guestListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Links to your layout containing guest_menu_list_view
        setContentView(R.layout.menutportal2);

        // Find the ListView defined in menutportal2.xml
        guestListView = findViewById(R.id.guest_menu_list_view);

        // Set Main Course as the default list when the page opens
        updateMenu(MenuData.mainCourseList);
    }

    /**
     * Helper method to refresh the ListView with a specific category list
     */
    private void updateMenu(List<String> selectedList) {
        // Use the custom white text layout so the items are visible
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.white_text_item, selectedList);
        guestListView.setAdapter(adapter);
    }

    // --- Category Button Methods (Linked via android:onClick in XML) ---

    public void showSoups(View view) {
        updateMenu(MenuData.soupList);
    }

    public void showSpecials(View view) {
        updateMenu(MenuData.specialsList);
    }

    public void showAppetizers(View view) {
        updateMenu(MenuData.appetizersList);
    }

    public void showMainCourse(View view) {
        updateMenu(MenuData.mainCourseList);
    }

    public void showDesserts(View view) {
        updateMenu(MenuData.dessertsList);
    }

    public void showDrinks(View view) {
        updateMenu(MenuData.drinksList);
    }

    // Triggered by the back button in menutportal2.xml
    public void goBackToGuest(View view) {
        finish();
    }
}