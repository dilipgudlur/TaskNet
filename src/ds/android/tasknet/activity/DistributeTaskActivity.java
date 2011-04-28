package ds.android.tasknet.activity;

import java.io.Serializable;
import java.util.ArrayList;

import ds.android.tasknet.R;

import ds.android.tasknet.config.Preferences;
import ds.android.tasknet.application.*;
import ds.android.tasknet.distributor.*;
import ds.android.tasknet.logger.*;
import ds.android.tasknet.task.TaskLookup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DistributeTaskActivity extends Activity {

	static final int GET_AUDIO = 99;
	String host, configuration_file, clockType,methodname;
	TaskDistributor distributor;
	EditText taskLoad,methodName;
	int taskload;
	PowerManager.WakeLock wl;
	Serializable[] paramsToSend;
	
	/*public void onPause()
	{
		super.onPause();
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
		setContentView(R.layout.distribute_task);
		
		//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    //wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "TAG");
	    
		Button distributeGlobalButton = (Button) findViewById(R.id.distributeGlobalButton);
		Button distributeLocalButton = (Button) findViewById(R.id.distributeLocalButton);
		taskLoad = (EditText) findViewById(R.id.taskLoad);
		methodName = (EditText) findViewById(R.id.methodName);
		
		((Button)findViewById(R.id.exitButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
			}
		});

		host = getIntent().getStringExtra("NodeName");
		configuration_file = Preferences.conf_file;
		Preferences.setHostDetails(Preferences.conf_file, host);

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
		distributor = new TaskDistributor(host, configuration_file, ip);
		
		distributeGlobalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				//long startTime = System.currentTimeMillis();
//				paramsToSend = castParams();
				paramsToSend = new Serializable[2];
				paramsToSend[0]= (Serializable)10;
				paramsToSend[1]= (Serializable)20;
				taskload = Integer.parseInt(taskLoad.getText().toString());
				methodname = methodName.getText().toString();
				distributor.distribute("ds.android.tasknet.application.SampleApplicationLocal",
						methodname,paramsToSend,taskload);
				//long elapsedTime = System.currentTimeMillis() - startTime;
				//distributor.logMessage("Elapsed Time:"+ Long.toString(elapsedTime));
			}
		});

		distributeLocalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {			
				//long startTime = System.currentTimeMillis();
				taskload = Integer.parseInt(taskLoad.getText().toString());
				methodname = methodName.getText().toString();
				paramsToSend = new Serializable[2];
				paramsToSend[0]= (Serializable)10;
				paramsToSend[1]= (Serializable)20;
				distributor.executeTaskLocally("ds.android.tasknet.application.SampleApplicationLocal",
						methodname,paramsToSend,taskload);//defined in TaskDistributor.java				
				//long elapsedTime = System.currentTimeMillis() - startTime;
				//distributor.logMessage("Elapsed Time:"+ Long.toString(elapsedTime));
				
			}
		});
    }
	
//	private Serializable[] castParams() {
//        Serializable[] castedParams = new Serializable[params.length];
//        String type;
//
//        for (int i = 0; i < this.params.length; i++) {
//            type = this.params[i].getSimpleName();
//            if(i == 0){
//            	
//            	castedParams[i] = param1.getText().toString();
//            }else if(i == 1){
//            	castedParams[i] = param2.getText().toString();
//            }
//            
//            if (type.equals("int") || type.equals("Integer")) {
//                try {
//                    castedParams[i] = Integer.parseInt(castedParams[i].toString());
//                } catch (java.lang.NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//
//            } else if (type.equals("char") || type.equals("Character")) {
//                castedParams[i] = castedParams[i].toString().toCharArray();
//                if (castedParams[i].toString().length() > 1) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//
//            } else if (type.equals("float") || type.equals("Float")) {
//                try {
//                    castedParams[i] = Float.parseFloat(castedParams[i].toString());
//                } catch (NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//
//            } else if (type.equals("double") || type.equals("Double")) {
//                try {
//                    castedParams[i] = Float.parseFloat(castedParams[i].toString());
//                } catch (NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//            } else if (type.equals("byte")) {
//                try {
//                    castedParams[i] = Byte.parseByte(castedParams[i].toString());
//                } catch (NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//            } else if (type.equals("boolean")) {
//                castedParams[i] = new Boolean(castedParams[i].toString());
//                if (!(castedParams[i].equals("true") && castedParams[i].equals("false"))) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//
//            } else if (type.equals("long")) {
//                try {
//                    castedParams[i] = Long.parseLong(castedParams[i].toString());
//                } catch (NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//            } else if (type.equals("short")) {
//                try {
//                    castedParams[i] = Short.parseShort(castedParams[i].toString());
//                } catch (NumberFormatException e) {
//                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            		builder.setMessage("Parameter type don't match")
//            	       		.setCancelable(false)
//            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//            	       	public void onClick(DialogInterface dialog, int id) { 
//            	           }
//            	       });
//            	AlertDialog alertbox = builder.create();
//            	alertbox.show();
//                    return null;
//                }
//
//            }
//        }
//
//        return castedParams;
//    }
	
	
}
