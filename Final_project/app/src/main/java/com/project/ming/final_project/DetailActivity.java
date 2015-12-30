package com.project.ming.final_project;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DetailActivity extends AppCompatActivity {

    TextView t_sr;
    TextView t_de;
    int num;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getExtras();

        t_sr = (TextView) findViewById(R.id.textView5);
        t_sr.setText(b.getString("store"));
        num = b.getInt("store_num");
        t_de = (TextView) findViewById(R.id.textView7);
        AssetManager assetManager = getAssets();

        try{
            InputStream is = assetManager.open("test"+num+".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str1 = br.readLine();
            t_de.setText(str1);
            br.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            t_de.setText("尚無資料");
        }
        catch (IOException e) {
            e.printStackTrace();
            t_de.setText("尚無資料");
        }

        btn = (Button) findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Bundle b = new Bundle();

                b.putInt("num", num);
                i.putExtras(b);
                i.setClass(DetailActivity.this, MapActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
