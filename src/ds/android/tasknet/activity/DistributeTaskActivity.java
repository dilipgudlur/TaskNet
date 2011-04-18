package ds.android.tasknet.activity;

import ds.android.tasknet.R;

import ds.android.tasknet.config.Preferences;
import ds.android.tasknet.distributor.*;
import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DistributeTaskActivity extends Activity {

	static final int GET_AUDIO = 99;
	String host, configuration_file, clockType;
	TaskDistributor distributor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.distribute_task);

		Button distributeGlobalButton = (Button) findViewById(R.id.distributeGlobalButton);
		Button distributeLocalButton = (Button) findViewById(R.id.distributeLocalButton);
		
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
			EditText taskLoad = (EditText) findViewById(R.id.taskLoad);
			EditText methodName = (EditText) findViewById(R.id.methodName);

			public void onClick(View v) {
				Integer taskload = Integer.parseInt(taskLoad.getText()
						.toString());
				distributor.distribute(methodName.getText().toString(),
						taskload);

			}
		});

		distributeLocalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// System.out.println((new Integer((new
				// SampleApplicationLocal()).method1(10, 20))).toString());
			}
		});
    }
}
