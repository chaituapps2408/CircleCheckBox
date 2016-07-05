package com.example.chaiy.circlecheckbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleCheckBox ccb = (CircleCheckBox) findViewById(R.id.checkBox);
        Button click = (Button) findViewById(R.id.click);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ccb.setChecked(!ccb.isChecked());
            }
        });

    }
}
