package com.project.ming.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    Button btn;
    String meal;
    int food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            RadioButton rbn1 = (RadioButton) findViewById(R.id.radio_rice);
            RadioButton rbn2 = (RadioButton) findViewById(R.id.radio_noodle);
            RadioButton rbn3 = (RadioButton) findViewById(R.id.radio_drink);

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_rice:
                        meal = "飯";
                        food=1;
                        rbn1.setChecked(true);
                        rbn2.setChecked(false);
                        rbn3.setChecked(false);
                        break;
                    case R.id.radio_noodle:
                        meal = "麵";
                        food=2;
                        rbn1.setChecked(false);
                        rbn2.setChecked(true);
                        rbn3.setChecked(false);
                        break;
                    case R.id.radio_drink:
                        meal = "飲料";
                        food=3;
                        rbn1.setChecked(false);
                        rbn2.setChecked(false);
                        rbn3.setChecked(true);
                        break;
                }
            }
        });

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Bundle b = new Bundle();

                b.putString("meal_ch", meal);
                b.putInt("meal_food",food);
                i.putExtras(b);
                i.setClass(MainActivity.this, SpinActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
