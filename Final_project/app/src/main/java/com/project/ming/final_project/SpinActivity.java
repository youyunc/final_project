package com.project.ming.final_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SpinActivity extends AppCompatActivity {

    TextView t_ch;
    Button btn;
    Spinner sp1;
    CharSequence[] store1={"咖食堂","自助餐（六教)","炸醬麵大王"};
    CharSequence[] store2={"炸醬麵大王","金盃美而美"};
    CharSequence[] store3={"金盃美而美","搖立得"};
    CharSequence store_opt;
    int di;
    int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        Bundle b=getIntent().getExtras();
        if(b!=null){
            t_ch=(TextView)findViewById(R.id.textView3);
            t_ch.setText(b.getString("meal_ch"));
            j=b.getInt("meal_food");
        }

        sp1 =(Spinner)findViewById(R.id.spinner);
        switch(j) {
            case 1:
                ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, store1);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp1.setAdapter(adapter);
                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        store_opt = store1[position];
                        di=position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.e("no_choose", "請選擇一家廠商");
                    }
                });
                break;
            case 2:
                ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, store2);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp1.setAdapter(adapter2);
                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        store_opt = store2[position];
                        di=position+2;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.e("no_choose", "請選擇一家廠商");
                    }
                });
                break;
            case 3:
                ArrayAdapter<CharSequence> adapter3 = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, store3);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp1.setAdapter(adapter3);
                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        store_opt = store3[position];
                        di=position+3;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.e("no_choose", "請選擇一家廠商");
                    }
                });
                break;
        }

        btn=(Button)findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent();
                Bundle b =new Bundle();

                b.putCharSequence("store",store_opt);
                b.putInt("store_num",di);
                i.putExtras(b);
                i.setClass(SpinActivity.this, DetailActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
