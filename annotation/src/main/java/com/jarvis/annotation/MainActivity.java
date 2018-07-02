package com.jarvis.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jarvis.item.annotation.ItemAnnotation;

@ItemAnnotation(
    name = "Jarvis", text = "Hello"
)
public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(MainActivityPoet.getAnnotationValue());
            }
        });
    }
}
