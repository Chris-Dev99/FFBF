package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.StreetFood;

public class CriticStreetFoodActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private CardView cardView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critic_street_food);

        fAuth = FirebaseAuth.getInstance();
        linearLayout = (LinearLayout) findViewById(R.id.rlCriticStreetFood);
        cardView = (CardView) findViewById(R.id.cdCriticStreetFood);

        readStreetFood();

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
                    ImageView stallImage = new ImageView(CriticStreetFoodActivity.this);
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
                            Toast.makeText(CriticStreetFoodActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    stallImage.setAdjustViewBounds(true);
                    stallImage.setMaxWidth(350);
                    stallImage.setMaxHeight(350);
                    linearLayout.addView(stallImage);
                    TextView tvName = new TextView(CriticStreetFoodActivity.this);

                    tvName.setText("\n" + "Name: " + streetFoodList.get(i).getName() + "\n");
                    linearLayout.addView(tvName);

                    Button btnView = new Button(CriticStreetFoodActivity.this);
                    btnView.setText("Review");
                    btnView.setBackgroundResource(R.drawable.round_shape);
                    btnView.setTextColor(Color.WHITE);
                    int finalI = i;
                    btnView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CriticStreetFoodActivity.this, ReviewStreetFoodActivity.class);
                            intent.putExtra("nameStreetCritic", streetFoodList.get(finalI).getName());
                            intent.putExtra("locationStreetCritic", streetFoodList.get(finalI).getLocation());
                            intent.putExtra("kindStreetCritic", streetFoodList.get(finalI).getKind());
                            intent.putExtra("descriptionStreetCritic", streetFoodList.get(finalI).getDescription());
                            intent.putExtra("imageStreetCritic", streetFoodList.get(finalI).getImageId());
                            startActivity(intent);
                            finish();
                        }
                    });
                    linearLayout.addView(btnView);
                    TextView tvSpace = new TextView(CriticStreetFoodActivity.this);
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