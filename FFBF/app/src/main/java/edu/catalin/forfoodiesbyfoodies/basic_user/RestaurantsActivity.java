package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RestaurantsActivity extends AppCompatActivity {
    public static RestaurantsActivity instance = null;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private LinearLayout linearLayout;
    private CardView cardView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);



        toolbar = (Toolbar) findViewById(R.id.restaurantsToolBar);
        toolbar.inflateMenu(R.menu.menu_app);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_street) {
                    openActivityFromMenu(RestaurantsActivity.this, StreetFoodActivity.class, true);

                }
                if (item.getItemId() == R.id.menu_update) {
                    openActivityFromMenu(RestaurantsActivity.this, UpdateDataActivity.class, true);


                }
                if (item.getItemId() == R.id.menu_logout) {
                    if (RestaurantsActivity.instance != null) {
                        RestaurantsActivity.instance.finish();
                    }
                    if (UpdateDataActivity.instance != null) {
                        UpdateDataActivity.instance.finish();
                    }
                    if (StreetFoodActivity.instance != null) {
                        StreetFoodActivity.instance.finish();
                    }
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(RestaurantsActivity.this, MainActivity.class));

                }

                return false;
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.rlMainHome);
        cardView = (CardView) findViewById(R.id.cdHome);
        readRestaurantsForUser();
    }



    private void openActivityFromMenu(Context packageContext, Class<?> cls, boolean flagState) {
        Intent intent = new Intent(packageContext, cls);
        if (flagState) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        startActivity(intent);
    }

    private void readRestaurantsForUser() {

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


                    ImageView restaurantImage = new ImageView(RestaurantsActivity.this);


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
                            Toast.makeText(RestaurantsActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    restaurantImage.setAdjustViewBounds(true);
                    restaurantImage.setMaxWidth(350);
                    restaurantImage.setMaxHeight(350);
                    linearLayout.addView(restaurantImage);

                    TextView tvName = new TextView(RestaurantsActivity.this);
                    TextView tvLocation = new TextView(RestaurantsActivity.this);
                    TextView tvTimeTable = new TextView(RestaurantsActivity.this);


                    Button btnView = new Button(RestaurantsActivity.this);
                    btnView.setText("View");
                    btnView.setBackgroundResource(R.drawable.round_shape);
                    btnView.setId(i + (int)Math.round(Math.random()*100));
                    btnView.setTextColor(Color.WHITE);
                    int finalI = i;
                    btnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RestaurantsActivity.this, ViewRestaurantActivity.class);
                            intent.putExtra("name", restaurantsList.get(finalI).getName());
                            intent.putExtra("location", restaurantsList.get(finalI).getLocation());
                            intent.putExtra("timeTable",restaurantsList.get(finalI).getTimeTable());
                            intent.putExtra("reviews",restaurantsList.get(finalI).getReviews());
                            intent.putExtra("image", restaurantsList.get(finalI).getImageId());
                            startActivity(intent);
                            finish();
                        }
                    });

                    tvName.setText("\n" + restaurantsList.get(i).getName() + "\n");
                    linearLayout.addView(tvName);

                    tvLocation.setText(restaurantsList.get(i).getLocation() + "\n");
                    linearLayout.addView(tvLocation);

                    tvTimeTable.setText(restaurantsList.get(i).getTimeTable() + "\n");
                    linearLayout.addView(tvTimeTable);



                    TextView tvSpace = new TextView(RestaurantsActivity.this);
                    TextView tvSpace2 = new TextView(RestaurantsActivity.this);
                    tvSpace.setText("\n");
                    tvSpace2.setText("\n");
                    linearLayout.addView(btnView);
                    linearLayout.addView(tvSpace);
                    linearLayout.addView(tvSpace2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}