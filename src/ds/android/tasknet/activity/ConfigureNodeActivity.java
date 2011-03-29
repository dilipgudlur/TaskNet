package ds.android.tasknet.activity;

import ds.android.tasknet.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConfigureNodeActivity extends Activity{
	
	EditText nodeName;
	Button btnConfig;
	String strNodeName;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_node);
        
        nodeName = (EditText)findViewById(R.id.txtNodeName);
        btnConfig = (Button)findViewById(R.id.btnConfigure);
        
        ((Button)findViewById(R.id.btnExit)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				System.exit(0);
			}
		});
        
        btnConfig.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strNodeName = nodeName.getText().toString();
				Intent i = new Intent(ConfigureNodeActivity.this, SimulateEvent.class);
				i.putExtra("NodeName", strNodeName);
		        startActivity(i);
		        finish();
			}
		});
	}

}
