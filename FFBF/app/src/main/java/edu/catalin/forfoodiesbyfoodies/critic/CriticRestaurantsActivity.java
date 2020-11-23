package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.Restaurant;

public class CriticRestaurantsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private LinearLayout linearLayout;
    private CardView cardView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critic_restaurants);
        
        linearLayout = (LinearLayout)findViewById(R.id.rlCriticMainHome);
        cardView = (CardView)findViewById(R.id.cdCriticHome);
        
        readRestaurantsForCritic();
    }

    private void readRestaurantsForCritic() {
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


                    ImageView restaurantImage = new ImageView(CriticRestaurantsActivity.this);


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
                            Toast.makeText(CriticRestaurantsActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    restaurantImage.setAdjustViewBounds(true);
                    restaurantImage.setMaxWidth(350);
                    restaurantImage.setMaxHeight(350);
                    linearLayout.addView(restaurantImage);

                    TextView tvName = new TextView(CriticRestaurantsActivity.this);



                    Button btnView = new Button(CriticRestaurantsActivity.this);
                    btnView.setText("Review");
                    btnView.setBackgroundResource(R.drawable.round_shape);
                    btnView.setId(i + (int)Math.round(Math.random()*100));
                    btnView.setTextColor(Color.WHITE);
                    int finalI = i;
                    btnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CriticRestaurantsActivity.this, ReviewRestaurantsActivity.class);
                            intent.putExtra("nameReview", restaurantsList.get(finalI).getName());
                            intent.putExtra("locationReview", restaurantsList.get(finalI).getLocation());
                            intent.putExtra("timeTableReview",restaurantsList.get(finalI).getTimeTable());
                            intent.putExtra("reviews",restaurantsList.get(finalI).getReviews());
                            intent.putExtra("imageReview", restaurantsList.get(finalI).getImageId());
                            startActivity(intent);
                            finish();
                        }
                    });

                    tvName.setText("\nRestaurant Name: " + restaurantsList.get(i).getName() + "\n");
                    linearLayout.addView(tvName);
                    TextView tvSpace = new TextView(CriticRestaurantsActivity.this);
                    tvSpace.setText("\n");
                    linearLayout.addView(btnView);
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