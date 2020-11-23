package edu.catalin.forfoodiesbyfoodies.critic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.catalin.forfoodiesbyfoodies.R;
import edu.catalin.forfoodiesbyfoodies.utils.Review;

public class ReviewRestaurantsActivity extends AppCompatActivity {

    private Button btnReview;
    private TextView tvRestNameReview, tvLocationReview, tvTimeTableReview;
    private EditText etCommentReview;
    private ImageView ivReviewRestImage;
    private String imageId;
    private Review reviewObject = new Review();
    private FirebaseAuth fAuth;
    private DatabaseReference reference;
    private Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_restaurants);
        intent = getIntent();
        ivReviewRestImage = (ImageView)findViewById(R.id.ivReviewRestImage);
        tvRestNameReview = (TextView)findViewById(R.id.tvRestNameReview);
        tvTimeTableReview = (TextView)findViewById(R.id.tvTimeTableReview);
        tvLocationReview = (TextView)findViewById(R.id.tvLocationReview);
        etCommentReview = (EditText)findViewById(R.id.etCommentReview);
        btnReview = (Button)findViewById(R.id.btnReview);
        fAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("comments");

        tvRestNameReview.setText(intent.getStringExtra("nameReview"));
        tvLocationReview.setText(intent.getStringExtra("locationReview"));
        tvTimeTableReview.setText(intent.getStringExtra("timeTableReview"));

        imageId = intent.getStringExtra("imageReview");
        setImage();
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReview();
            }
        });
    }

    private void saveReview() {
        String review = etCommentReview.getText().toString().trim();
        if(review.isEmpty()){
            Toast.makeText(ReviewRestaurantsActivity.this, "Please add a review before saving!", Toast.LENGTH_SHORT).show();
        }else{
            reviewObject.setRestaurantName(intent.getStringExtra("nameReview"));
            reviewObject.setReview(review);

            reference.push().setValue(reviewObject)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ReviewRestaurantsActivity.this, "Review added successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ReviewRestaurantsActivity.this, CriticRestaurantsActivity.class));
                                finish();
                            }else {
                                Toast.makeText(ReviewRestaurantsActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void setImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(imageId);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        ivReviewRestImage.setImageBitmap(bm);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ReviewRestaurantsActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ivReviewRestImage.setAdjustViewBounds(true);
        ivReviewRestImage.setMaxWidth(350);
        ivReviewRestImage.setMaxHeight(350);
    }
}