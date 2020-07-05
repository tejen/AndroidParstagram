package com.example.parstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.parstagram.fragments.CommentComposeFragment;
import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_MULTIPLE = 234;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private Bitmap resized;
    private ComposeFragment composeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_camera_foreground);
        getSupportActionBar().setTitle("Camera");
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new PostsFragment();
                        break;
                    case R.id.action_create:
                        fragment = new ComposeFragment();
                        composeFragment = (ComposeFragment) fragment;
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case android.R.id.home:
                composeFragment = new ComposeFragment();
                fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).commit();
                break;
            case R.id.miLogout:
                ParseUser.logOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Intent intent = new Intent(MainActivity.this,CustomPhotoGalleryActivity.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
            } else {
                // User refused to grant permission.
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    // lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (int i=0;i<imagesPath.length;i++){
                    imagesPathList.add(imagesPath[i]);
                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
                    composeFragment.ivPostImage.setImageBitmap(yourbitmap);
                    // lnrImages.addView(imageView);
                }
            }
        }

    }

    public void launchGallery() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MainActivity.this,CustomPhotoGalleryActivity.class);
            startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
}
