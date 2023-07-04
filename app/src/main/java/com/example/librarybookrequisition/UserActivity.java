package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class UserActivity extends AppCompatActivity {
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        frameLayout = (FrameLayout) findViewById(R.id.UserFragmentContainer);
        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, new UserProfileFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {

            //Shows the Appropriate Fragment by using id as address
            case R.id.DashBoardMenu:
                fragment = new UserHomeFragment();
                break;
//
            case R.id.NotificationsMenu:
                fragment = new UserNotificationsFragment();
                break;


            case R.id.ProfileMenu:
                fragment = new UserProfileFragment();
                break;

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
        return super.onOptionsItemSelected(item);
    }
}