package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.Review;

public class CheckRestaurantReviewActivity extends AppCompatActivity {
    private Intent intent;
    private LinearLayout linearLayout;
    private CardView cardView;
    private TextView tvRestName;
    private String restName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_restaurant_review);

        intent = getIntent();
        restName = intent.getStringExtra("restaurantName");
        tvRestName = (TextView)findViewById(R.id.tvRestNameReview);
        linearLayout = (LinearLayout)findViewById(R.id.rlRestReviewCheck);
        cardView = (CardView)findViewById(R.id.cdRestHomeCheckReview);
        tvRestName.setText("Reviews for " + restName);
        
        readReviews();

    }

    private void readReviews() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = database.child("comments");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 1;
                for (DataSnapshot ds: snapshot.getChildren()) {

                    if(ds.child("restaurantName").getValue().equals(restName)) {
                        Review review = new Review();
                        review.setReview(ds.child("review").getValue(String.class));
                        TextView tvReview = new TextView(CheckRestaurantReviewActivity.this);
                        tvReview.setText(i + ") " + review.getReview() + "\n");
                        linearLayout.addView(tvReview);
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckRestaurantReviewActivity.this, RestaurantsActivity.class));
        finish();
    }
}