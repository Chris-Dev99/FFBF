package edu.catalin.forfoodiesbyfoodies.basic_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.catalin.forfoodiesbyfoodies.R;

public class ViewRestaurantActivity extends AppCompatActivity {

    private Button btnBook, btnViewReview;
    private TextView tvRestName, tvLocation, tvTimeTable, tvReviews;
    private ImageView ivRestImage;
    private  String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);
        Intent intent = getIntent();
        ivRestImage = (ImageView)findViewById(R.id.ivRestImage);
        tvRestName = (TextView)findViewById(R.id.tvRestName);
        tvTimeTable = (TextView)findViewById(R.id.tvTimeTable);
        tvLocation = (TextView)findViewById(R.id.tvLocation);
        tvReviews = (TextView)findViewById(R.id.tvReviews);
        btnBook = (Button)findViewById(R.id.btnBook);
        btnViewReview = (Button)findViewById(R.id.btnViewRestReview);

        tvRestName.setText(intent.getStringExtra("name"));
        tvLocation.setText(intent.getStringExtra("location"));
        tvTimeTable.setText(intent.getStringExtra("timeTable"));
        tvReviews.setText(intent.getStringExtra("reviews"));
        tvReviews.setVisibility(View.INVISIBLE);

        imageId = intent.getStringExtra("image");
        setImage();
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewRestaurantActivity.this, WebViewActivity.class));
            }
        });

        btnViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // view Review -> open new activity access data from db and display on the UI
                Intent intentRest = new Intent(ViewRestaurantActivity.this, CheckRestaurantReviewActivity.class);
                intentRest.putExtra("restaurantName",tvRestName.getText().toString());
                startActivity(intentRest);
                finish();
            }
        });

    }

    private void setImage(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Images").child(imageId);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        DisplayMetrics dm = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dm);

                        ivRestImage.setImageBitmap(bm);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewRestaurantActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ivRestImage.setAdjustViewBounds(true);
        ivRestImage.setMaxWidth(350);
        ivRestImage.setMaxHeight(350);

    }
}