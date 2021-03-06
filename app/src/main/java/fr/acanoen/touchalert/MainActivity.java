package fr.acanoen.touchalert;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import fr.acanoen.touchalert.fragment.AlertFragment;
import fr.acanoen.touchalert.fragment.BoardFragment;
import fr.acanoen.touchalert.fragment.NotificationFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BoardFragment.OnFragmentInteractionListener, AlertFragment.OnFragmentInteractionListener, NotificationFragment.OnFragmentInteractionListener {

    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new BoardFragment());

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new BoardFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new AlertFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new NotificationFragment();
                    break;
            }
            return
            loadFragment(fragment);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onFragmentInteraction(Uri uri) {

    }


}

