package com.example.beginagain.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.beginagain.R;
import com.example.beginagain.Utils.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class ThongTinActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatButton btnVisit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin);
        initView();
        ActionToolbar();
        btnVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri webpage = Uri.parse(Utils.WEB_URL);
                intent.setData(webpage);
                startActivity(intent);
            }
        });
    }

    private void ActionToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);
        btnVisit = findViewById(R.id.buttonVisit);
    }
}