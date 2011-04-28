package ds.android.tasknet.activity;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DistributeTaskActivity extends Activity {

	static final int GET_AUDIO = 99;
	String host, configuration_file, clockType,methodname;
	TaskDistributor distributor;
	EditText taskLoad,methodName;
	TextView hostName;
	int taskload;
	PowerManager.WakeLock wl;
	private Class[] params;
	Serializable[] paramsToSend ={12,22};
	public boolean checkFlag = false;
   
    //EditText param1;
    //EditText param2;
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distribute_task);
		
		
		Button distributeGlobalButton = (Button) findViewById(R.id.distributeGlobalButton);
		Button distributeLocalButton = (Button) findViewById(R.id.distributeLocalButton);
		//Button checkMethodButton = (Button) findViewById(R.id.checkMethodButton);
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
		
		hostName =(TextView) findViewById(R.id.hostName);
		//Log.d("check", hostName.getText().toString());
		
		
		hostName.setText(host.toString());

		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
		distributor = new TaskDistributor(host, configuration_file, ip);
		
		//final Context c = this.getApplicationContext();
		
		distributeGlobalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
					if(checkMethod()){
						//paramsToSend = castParams();
						taskload = Integer.parseInt(taskLoad.getText().toString());
						methodname = methodName.getText().toString();
						distributor.distribute("ds.android.tasknet.application.SampleApplicationLocal",methodname,paramsToSend,taskload);
					}
					/*
					taskload = Integer.parseInt(taskLoad.getText().toString());
					methodname = methodName.getText().toString();
					distributor.distribute("ds.android.tasknet.application.SampleApplicationLocal",methodname,paramsToSend,taskload);
					*/
				}
		});

		distributeLocalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
					if(checkMethod()){
					//paramsToSend = castParams();
					taskload = Integer.parseInt(taskLoad.getText().toString());
					methodname = methodName.getText().toString();				 
					distributor.executeTaskLocally("ds.android.tasknet.application.SampleApplicationLocal",methodname,paramsToSend,taskload);//defined in TaskDistributor.java				
					}			
			}
		});
		/*
		checkMethodButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				
					checkFlag = checkMethod();
				
			}
		});*/
    }
	
	private boolean checkMethod(){
		Log.d("check", "method = "+methodName.getText().toString());
		Log.d("check", "load = "+ Integer.toString(taskLoad.getText().toString().length()));
		
		
		if(methodName.getText() == null|| taskLoad.getText() == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Please input both method and taskLoad")
    	       		.setCancelable(false)
    	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	       	public void onClick(DialogInterface dialog, int id) {
    	           }
    	       });
    	AlertDialog alertbox = builder.create();
    	alertbox.show();
    	return false;
		}
		
		if(methodName.getText().toString().length() == 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Please input method")
    	       		.setCancelable(false)
    	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	       	public void onClick(DialogInterface dialog, int id) {
    	           }
    	       });
    	AlertDialog alertbox = builder.create();
    	alertbox.show();
    	return false;
		}
		
		if(taskLoad.getText().toString().length()== 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Please input taskLoad")
    	       		.setCancelable(false)
    	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	       	public void onClick(DialogInterface dialog, int id) {
    	           }
    	       });
    	AlertDialog alertbox = builder.create();
    	alertbox.show();
    	return false;
		}
		
		
		
		
		
		try {
            boolean found = false;
            Method method = null;
            Serializable[] paramsToSend;
            //String className = strClassName.substring(0, strClassName.indexOf(".class"));
            //Class aClass = Class.forName("ds.android.tasknet.application.SampleApplicationLocal");
            Class aClass = SampleApplicationLocal.class;
            Method[] methods = aClass.getMethods();
           
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName.getText().toString())) {
                	//System.out.println("method input = "+methods[i].getName());
                    method = methods[i];
                    found = true;
                    break;
                }
            }
            if (found == true) {
            	/*Button checkMethodButton = (Button) findViewById(R.id.checkMethodButton);
            	checkMethodButton.setText("Check param");
            	ListView lv1=(ListView)findViewById(R.id.ListView);
            	
            	if(methodName.getText().toString().equals("method1")){
            	
            		param1=(EditText)findViewById(R.id.param1);
            		param1.setVisibility(param1.VISIBLE);
            		param2=(EditText)findViewById(R.id.param2);
            		param2.setVisibility(param2.VISIBLE);
            	}
            	
                Class[] paras = method.getParameterTypes();
                String lv_arr[] = new String[paras.length];
              
                for (int i = 0; i < paras.length; i++) {
                    lv_arr[i] = paras[i].getSimpleName();
                }
                //lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lv_arr));
                
                this.params = paras;
                Log.d("cast", Integer.toString(params.length));*/
                return true;
            }
            
            if (found == false) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    		builder.setMessage("Method doesn't exist")
	    	       		.setCancelable(false)
	    	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
	    	       	public void onClick(DialogInterface dialog, int id) {
	    	           }
	    	       });
	    	AlertDialog alertbox = builder.create();
	    	alertbox.show();	
            }
            return false;
        } catch (Exception ex) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Class doesn't exist")
    	       		.setCancelable(false)
    	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	       	public void onClick(DialogInterface dialog, int id) { 
    	           }
    	       });
    	AlertDialog alertbox = builder.create();
    	alertbox.show();
    	return false;
        }
	}
	/*
	private Serializable[] castParams() {
        Serializable[] castedParams = new Serializable[params.length];
        String type;
        
       // if(param1.get)

        for (int i = 0; i < this.params.length; i++) {
            type = this.params[i].getSimpleName();
            if(i == 0){
            	
            	castedParams[i] = param1.getText().toString();
            }else if(i == 1){
            	castedParams[i] = param2.getText().toString();
            }
            
            if (type.equals("int") || type.equals("Integer")) {
                try {
                    castedParams[i] = Integer.parseInt(castedParams[i].toString());
                } catch (java.lang.NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }

            } else if (type.equals("char") || type.equals("Character")) {
                castedParams[i] = castedParams[i].toString().toCharArray();
                if (castedParams[i].toString().length() > 1) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }

            } else if (type.equals("float") || type.equals("Float")) {
                try {
                    castedParams[i] = Float.parseFloat(castedParams[i].toString());
                } catch (NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }

            } else if (type.equals("double") || type.equals("Double")) {
                try {
                    castedParams[i] = Float.parseFloat(castedParams[i].toString());
                } catch (NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }
            } else if (type.equals("byte")) {
                try {
                    castedParams[i] = Byte.parseByte(castedParams[i].toString());
                } catch (NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }
            } else if (type.equals("boolean")) {
                castedParams[i] = new Boolean(castedParams[i].toString());
                if (!(castedParams[i].equals("true") && castedParams[i].equals("false"))) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }

            } else if (type.equals("long")) {
                try {
                    castedParams[i] = Long.parseLong(castedParams[i].toString());
                } catch (NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }
            } else if (type.equals("short")) {
                try {
                    castedParams[i] = Short.parseShort(castedParams[i].toString());
                } catch (NumberFormatException e) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            		builder.setMessage("Parameter type don't match")
            	       		.setCancelable(false)
            	       		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	       	public void onClick(DialogInterface dialog, int id) { 
            	           }
            	       });
            	AlertDialog alertbox = builder.create();
            	alertbox.show();
                    return null;
                }

            }
        }

        return castedParams;
    }*/
}
