package com.example.librarybookrequisition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {
    FrameLayout layout;
    NavigationView navView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        layout = (FrameLayout) findViewById(R.id.AdminFragmentContainer);
        //navView = (NavigationView) findViewById(R.id.AdminNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.AdminFragmentContainer, new AdminProfileFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_navigation_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {

            //Shows the Appropriate Fragment by using id as address
            case R.id.RequestDashboardMenu:
                fragment = new AdminDashBoardFragment();
                break;
            case R.id.AddBookMenu:
                fragment = new AddBooksFragment();
                break;
            case R.id.LibraryBooksMenu:
                fragment=new AdminLibraryBooksFragment();
                break;
            case R.id.NewTitleRequestsMenu:
                fragment = new AdminNewTitleRequestsFragment();
                break;
            case R.id.AdminProfile:
                fragment = new AdminProfileFragment();
                break;

        }
        getSupportFragmentManager().beginTransaction().replace(R.id.AdminFragmentContainer, fragment).commit();
        return super.onOptionsItemSelected(item);
    }
}