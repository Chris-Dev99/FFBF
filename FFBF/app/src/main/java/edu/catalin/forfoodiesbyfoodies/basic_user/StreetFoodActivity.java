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
import edu.catalin.forfoodiesbyfoodies.utils.StreetFood;

public class StreetFoodActivity extends AppCompatActivity {

    public static StreetFoodActivity instance = null;
    private Toolbar toolbar;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth fAuth;
    private FloatingActionButton btnAddStreetFood;
    private LinearLayout linearLayout;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_food);

        btnAddStreetFood = (FloatingActionButton) findViewById(R.id.btnAddStreetFood);
        fAuth = FirebaseAuth.getInstance();
        linearLayout = (LinearLayout) findViewById(R.id.rlMainStreetFood);
        cardView = (CardView) findViewById(R.id.cdHomeStreetFood);
        toolbar = (Toolbar) findViewById(R.id.streetFoodToolBar);
        toolbar.inflateMenu(R.menu.menu_app);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_restaurants) {
                    openActivityFromMenu(StreetFoodActivity.this, RestaurantsActivity.class, true);

                }
                if (item.getItemId() == R.id.menu_update) {
                    openActivityFromMenu(StreetFoodActivity.this, UpdateDataActivity.class, true);


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
                    startActivity(new Intent(StreetFoodActivity.this, MainActivity.class));

                }
                return false;
            }
        });

        readStreetFood();

        btnAddStreetFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StreetFoodActivity.this, AddStreetFoodActivity.class));
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

    private void readStreetFood() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = database.child("streetfood");
        ArrayList<StreetFood> streetFoodList = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    StreetFood streetFood = new StreetFood();
                    streetFood.setName(ds.child("name").getValue(String.class));
                    streetFood.setLocation(ds.child("location").getValue(String.class));
                    streetFood.setKind(ds.child("kind").getValue(String.class));
                    streetFood.setDescription(ds.child("description").getValue(String.class));
                    streetFood.setReview(ds.child("review").getValue(String.class));
                    streetFood.setImageId(ds.child("imageId").getValue(String.class));
                    streetFoodList.add(streetFood);

                }
                Collections.sort(streetFoodList, new Comparator<StreetFood>() {
                    @Override
                    public int compare(StreetFood o1, StreetFood o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                for (int i = 0; i < streetFoodList.size(); i++) {
                    ImageView stallImage = new ImageView(StreetFoodActivity.this);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(streetFoodList.get(i).getImageId());

                    final long ONE_MEGABYTE = 1024 * 1024;
                    storageReference.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    DisplayMetrics dm = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                                    stallImage.setImageBitmap(bm);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(StreetFoodActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    stallImage.setAdjustViewBounds(true);
                    stallImage.setMaxWidth(350);
                    stallImage.setMaxHeight(350);
                    linearLayout.addView(stallImage);
                    TextView tvName = new TextView(StreetFoodActivity.this);

                    tvName.setText("\n" + "Name: " + streetFoodList.get(i).getName() + "\n");
                    linearLayout.addView(tvName);

                    Button btnView = new Button(StreetFoodActivity.this);
                    btnView.setText("View");
                    btnView.setBackgroundResource(R.drawable.round_shape);
                    btnView.setId(i + (int) Math.round(Math.random() * 100) + 1);
                    btnView.setTextColor(Color.WHITE);
                    int finalI = i;
                    btnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(StreetFoodActivity.this, ViewStreetFoodActivity.class);
                            intent.putExtra("nameStreet", streetFoodList.get(finalI).getName());
                            intent.putExtra("locationStreet", streetFoodList.get(finalI).getLocation());
                            intent.putExtra("kindStreet", streetFoodList.get(finalI).getKind());
                            intent.putExtra("descriptionStreet", streetFoodList.get(finalI).getDescription());
                            intent.putExtra("reviewStreet", streetFoodList.get(finalI).getReview());
                            intent.putExtra("imageStreet", streetFoodList.get(finalI).getImageId());
                            startActivity(intent);
                            finish();
                        }
                    });
                    linearLayout.addView(btnView);
                    TextView tvSpace = new TextView(StreetFoodActivity.this);
                    tvSpace.setText("\n");
                    linearLayout.addView(tvSpace);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);

    }
}