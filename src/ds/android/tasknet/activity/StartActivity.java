package ds.android.tasknet.activity;

import ds.android.tasknet.R;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent i = new Intent(StartActivity.this, ConfigureNodeActivity.class);
        startActivity(i);
        finish();
    }
}