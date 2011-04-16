package ds.android.tasknet.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ds.android.tasknet.R;

import ds.android.tasknet.application.SampleApplicationLocal;
import ds.android.tasknet.clock.ClockFactory;
import ds.android.tasknet.clock.ClockFactory.ClockType;
import ds.android.tasknet.clock.VectorClock;
import ds.android.tasknet.config.Preferences;
import ds.android.tasknet.exceptions.InvalidMessageException;
import ds.android.tasknet.infrastructure.TaskDistributor;
import ds.android.tasknet.msgpasser.Message;
import ds.android.tasknet.msgpasser.MessagePasser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class DistributeTaskActivity extends Activity {

	static final int GET_AUDIO = 99;
	private static final int BUSY_DIALOG = 100;
	private static final int SUCCESS_DIALOG = 101;
	private static final int FAILURE_DIALOG = 102;
	String host, configuration_file, clockType;
	Properties prop;
	TaskDistributor distributor;
				
	@Override	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribute_task);
        
        EditText methodName = (EditText) findViewById(R.id.methodName);
        //EditText taskLoad = (EditText) findViewById(R.id.taskLoad);
        Button distributeGlobalButton = (Button) findViewById(R.id.distributeGlobalButton);
        Button distributeLocalButton = (Button) findViewById(R.id.distributeLocalButton);
        
        prop = new Properties();
        try {
            prop.load(new FileInputStream(Preferences.conf_file));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        host = getIntent().getStringExtra("NodeName");
        configuration_file = Preferences.conf_file;
        clockType = Preferences.clockTypes[1];	//gives the Vector clock
                
        Preferences.setHostDetails(Preferences.conf_file, host);
                        
        distributor = new TaskDistributor(host, configuration_file, clockType);
        distributor.startListening();

        
        distributeGlobalButton.setOnClickListener(new OnClickListener(){
        EditText taskLoad = (EditText) findViewById(R.id.taskLoad);
        public void onClick(View v) {
			Integer taskload = Integer.parseInt(taskLoad.getText().toString());
			distributor.distributeTask(taskload);						
				
		}});
        
        distributeLocalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//System.out.println((new Integer((new SampleApplicationLocal()).method1(10, 20))).toString());
			}
		});
    }
}
