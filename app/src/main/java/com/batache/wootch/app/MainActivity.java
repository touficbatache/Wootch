package com.batache.wootch.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.batache.wootch.BuildConfig;
import com.batache.wootch.R;
import com.batache.wootch.app.base.BaseActivity;
import com.batache.wootch.app.search.SearchActivity;
import com.batache.wootch.util.DimensionUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

  private static final int PERMISSIONS_REQUEST = 123;

  @BindView(R.id.app_bar)
  AppBarLayout appBar;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  private NavController navController;
  private AppBarConfiguration appBarConfiguration;

  @BindView(R.id.navigation)
  BottomNavigationView bottomNavigationView;

  @Override
  public Context getContext() {
    return MainActivity.this;
  }

  @Override
  public int getLayout() {
    return R.layout.activity_main;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setSupportActionBar(toolbar);

    setupNavigation();

    findViewById(R.id.btn_sign_out).setOnClickListener(view -> {
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestEmail()
          .build();
      GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
      googleSignInClient.signOut();

      FirebaseAuth.getInstance().signOut();
    });

    if (!isFullAccessStorageAllowed()) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }

  }

  private void setupNavigation() {
    navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    Set<Integer> topLevelDestinations = new HashSet<>();
    topLevelDestinations.add(R.id.homeFragment);
    topLevelDestinations.add(R.id.libraryFragment);
    appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//      if (destination.getId() == R.id.exploreFragment || destination.getId() == R.id.myMusicFragment) {
//        appBar.setElevation(0);
//        floatingSearchBar.setVisibility(View.VISIBLE);
//        toolbar.setVisibility(View.GONE);
//      } else {
//        appBar.setElevation(10);
//        toolbar.setVisibility(View.VISIBLE);
//        floatingSearchBar.setVisibility(View.GONE);
//      }
//    });

    navController.addOnDestinationChangedListener((controller, destination, arguments) ->
        findViewById(R.id.bottom_navigation_container).setVisibility(destination.getId() == R.id.homeMoreFragment ? View.GONE : View.VISIBLE)
    );

//    bottomNavigationView.post(this::onApplyAllWindowInsets);
    NavigationUI.setupWithNavController(bottomNavigationView, navController);

//    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//      NavigationUI.onNavDestinationSelected(item, navController);
//      if (isSearchOpen) {
//        closeResetSearch();
//      }
//      return true;
//    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.activity_overview, menu);

    MenuItem playerTestMenuItem = menu.findItem(R.id.action_playertests);
    playerTestMenuItem.setVisible(BuildConfig.DEBUG);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        /* Override default {@link pct.droid.activities.BaseActivity } behaviour */
        return false;
      case R.id.action_playertests:
//        openPlayerTestDialog();
        break;
      case R.id.action_search:
        //start the search activity
        SearchActivity.startActivity(this);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private boolean isFullAccessStorageAllowed() {
    int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSIONS_REQUEST: {
        if (grantResults.length < 1 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
          Toast.makeText(this, "Permissions granted, now you can read and write the storage", Toast.LENGTH_LONG).show();
          finish();
        }
      }
    }
  }

  @Override
  public void onApplyAllWindowInsets() {
    appBar.setPadding(0, DimensionUtils.TOP_INSET, 0, 0);
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfiguration);
  }
}
