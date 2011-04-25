package ds.android.tasknet.activity;

import ds.android.tasknet.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConfigureNodeActivity extends Activity{
	
	EditText nodeName;
	Button btnConfig;
	String strNodeName;
	PowerManager.WakeLock wl;
	
	
	/*public void onPause()
	{
		super.onStart();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		wl.acquire();
	}
	
	public void onResume()
	{
		super.onResume();
		wl.release();		
	}*/
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_node);
        
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		//wl.acquire();
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
				//wl.release();
				strNodeName = nodeName.getText().toString();
				Intent i = new Intent(ConfigureNodeActivity.this, DistributeTaskActivity.class);
				i.putExtra("NodeName", strNodeName);
		        startActivity(i);
		        finish();
		        
			}
		});
	}

}
