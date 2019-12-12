package com.autotradetech.alllibrary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.automated.imagepicker.Utils.ImageUtils;
import com.bumptech.glide.Glide;
import com.automated.imagepicker.ImagePickerActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ivLogo=findViewById(R.id.ivLogo);

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ImagePickerActivity.class);
                startActivityForResult(intent,100);
            }
        });

        Intent intent=new Intent(MainActivity.this, ImagePickerActivity.class);
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode==100){


                File file=(File)data.getSerializableExtra(ImagePickerActivity.IMAGE);

                Bitmap bitmap=ImageUtils.converfiletobitmap(file);
                String base64string=ImageUtils.convertbase64(bitmap);
                Glide.with(this).load(Base64.decode(base64string, Base64.DEFAULT)).into(ivLogo);

//                Glide.with(MainActivity.this).load(file).into(ivLogo);
            }
        }
    }
}
