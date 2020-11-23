package edu.catalin.forfoodiesbyfoodies.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.catalin.forfoodiesbyfoodies.MainActivity;
import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.Restaurant;

public class AdminRestaurantsActivity extends AppCompatActivity {
    public static AdminRestaurantsActivity instance = null;
    private FirebaseAuth fAuth;
    private Toolbar toolbar;
    private FloatingActionButton btnOpenAddRestaurants;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private LinearLayout linearLayout;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_restaurants);
        instance = this;

        btnOpenAddRestaurants = (FloatingActionButton) findViewById(R.id.btnOpenAddRestaurants);
        fAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.restaurantsAdminToolBar);
        toolbar.inflateMenu(R.menu.admin_menu);

        linearLayout = (LinearLayout) findViewById(R.id.rlMainAdminRest);
        cardView = (CardView) findViewById(R.id.cdRestHome);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_admin_update) {
                    openActivityFromMenu(AdminRestaurantsActivity.this, UpdateAdminActivity.class, true);
                }
                if (item.getItemId() == R.id.menu_critic) {
                    openActivityFromMenu(AdminRestaurantsActivity.this, RegisterCriticActivity.class, true);
                }
                if (item.getItemId() == R.id.menu_admin_logout) {
                    if (AdminRestaurantsActivity.instance != null) {
                        AdminRestaurantsActivity.instance.finish();
                    }
                    if (UpdateAdminActivity.instance != null) {
                        UpdateAdminActivity.instance.finish();
                    }
                    if (RegisterCriticActivity.instance != null) {
                        RegisterCriticActivity.instance.finish();
                    }
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(AdminRestaurantsActivity.this, MainActivity.class));

                }
                return false;
            }
        });

        readRestaurants();


        btnOpenAddRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminRestaurantsActivity.this, AddRestaurantActivity.class));
            }
        });

    }

    private void openActivityFromMenu(Context packageContext, Class<?> cls, boolean flagState) {
        Intent intent = new Intent(packageContext, cls);
        if (flagState) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(intent);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void readRestaurants() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = database.child("restaurants");
        ArrayList<Restaurant> restaurantsList = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Restaurant restaurant = new Restaurant();
                    restaurant.setName(ds.child("name").getValue(String.class));
                    restaurant.setImageId(ds.child("imageId").getValue(String.class));
                    restaurant.setTimeTable(ds.child("timeTable").getValue(String.class));
                    restaurant.setLocation(ds.child("location").getValue(String.class));
                    restaurant.setReviews(ds.child("reviews").getValue(String.class));
                    restaurantsList.add(restaurant);

                }
                Collections.sort(restaurantsList, new Comparator<Restaurant>() {
                    @Override
                    public int compare(Restaurant o1, Restaurant o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                for (int i = 0; i < restaurantsList.size(); i++) {


                    ImageView restaurantImage = new ImageView(AdminRestaurantsActivity.this);


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(restaurantsList.get(i).getImageId());


                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageReference.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    DisplayMetrics dm = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                                    restaurantImage.setImageBitmap(bm);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminRestaurantsActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    restaurantImage.setAdjustViewBounds(true);
                    restaurantImage.setMaxWidth(350);
                    restaurantImage.setMaxHeight(350);
                    linearLayout.addView(restaurantImage);

                    TextView tvName = new TextView(AdminRestaurantsActivity.this);
                    TextView tvLocation = new TextView(AdminRestaurantsActivity.this);
                    TextView tvTimeTable = new TextView(AdminRestaurantsActivity.this);

                    tvName.setText("\n" + restaurantsList.get(i).getName() + "\n");
                    linearLayout.addView(tvName);

                    tvLocation.setText(restaurantsList.get(i).getLocation() + "\n");
                    linearLayout.addView(tvLocation);

                    tvTimeTable.setText(restaurantsList.get(i).getTimeTable() + "\n");
                    linearLayout.addView(tvTimeTable);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);
    }
}