package com.hungrydroid.firebasestorage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewActivity extends AppCompatActivity {

    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        iv = (ImageView)findViewById(R.id.iv2);
        System.out.println(getIntent().getStringExtra("image"));
        Picasso.with(ViewActivity.this).load(getIntent().getStringExtra("image")).into(iv);
        //Toast.makeText(ViewActivity.this, "Loading Image...", Toast.LENGTH_SHORT).show();
    }


}
