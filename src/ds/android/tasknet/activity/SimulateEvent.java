package ds.android.tasknet.activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ds.android.tasknet.R;
import ds.android.tasknet.application.SampleApplication;
import ds.android.tasknet.config.Node;
import ds.android.tasknet.config.Preferences;
import ds.android.tasknet.msgpasser.Message;
import ds.android.tasknet.msgpasser.MessagePasser;
import ds.android.tasknet.msgpasser.MulticastMessage;
import ds.android.tasknet.task.DistributedTask;
import ds.android.tasknet.task.Task;
import ds.android.tasknet.task.TaskResult;
import ds.android.tasknet.clock.*;
import ds.android.tasknet.exceptions.InvalidMessageException;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SimulateEvent extends Activity {
	MessagePasser mp;
	String host_name;
	Integer host_current_load;
	Properties prop;
	public static int taskNum = 0;
	HashMap<String, ArrayList<Node>> taskGroup;
	HashMap<String, Task> taskDetails;
	EditText taMessages;
	Button taskButton;
	Handler guiRefresh;
	final int REFRESH = 1;
	final String TXTMSG = "txtmsg";
	boolean canSendMsg = true;
	long cpuTotal;
	long cpuIdle;
	float cpuLoad;
	int batteryLevel;
	int promisedLoad;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasknet);
		((Button) findViewById(R.id.btnExitTaskNet))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						System.exit(0);
					}
				});
		host_name = getIntent().getStringExtra("NodeName");
		taskGroup = new HashMap<String, ArrayList<Node>>();
		taskDetails = new HashMap<String, Task>();

		Preferences.setHostDetails(Preferences.conf_file, host_name);
		host_current_load = Preferences.host_initial_load;
		mp = new MessagePasser(Preferences.conf_file, host_name,
				ClockFactory.ClockType.VECTOR, Preferences.nodes.size());
		mp.start();

		taMessages = (EditText) findViewById(R.id.taMsgs);
		taskButton = (Button) findViewById(R.id.btnTaskAdv);
		promisedLoad = 0;

		taskButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String node = "";
				String kind = "";
				String msgid = "";
				taskNum++;
				String taskId = host_name + taskNum;
				Task newTask = new Task(new Integer(10), taskId, host_name);
				taMessages.append("Sent Task advertisement: " + taskId + "\n");
				synchronized(taskGroup){
					synchronized(taskDetails){
						taskGroup.put(taskId, new ArrayList<Node>());
						taskDetails.put(taskId, newTask);
					}
				}
				MulticastMessage mMsg = new MulticastMessage(node, kind, msgid,
						newTask, mp.getClock(), true,
						MulticastMessage.MessageType.TASK_ADV, host_name);
				Preferences.crashNode = "";
				try {
					mp.send(mMsg);
				} catch (InvalidMessageException e) {
					e.printStackTrace();
				}
			}
		});

		guiRefresh = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case REFRESH:
					/* Refresh UI */
					String m = msg.getData().getString(TXTMSG);
					taMessages.append(m);
					break;
				}
			}
		};

		listenForIncomingMessages();
//		keepSendingProfileUpdates();
	}

    private void listenForIncomingMessages() {
        /*
         * This thread keeps polling for any incoming messages and displays them
         * to user
         */

        (new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        Message msg = mp.receive();
                        if (msg != null) {
                            if (msg instanceof MulticastMessage) {
                                switch (((MulticastMessage) msg).getMessageType()) {
                                    case TASK_ADV:
                                    	appendToLogger("Received task advertisement\n");
                                        Task receivedTask = (Task) msg.getData();
                                        synchronized (Preferences.nodes) {
                                            Node host_node = Preferences.nodes.get(host_name);
                                            host_node.setTaskid(receivedTask.getTaskId());
                                            float remaining_load = Preferences.TOTAL_LOAD_AT_NODE
                                                    - (receivedTask.taskLoad + host_node.getProcessorLoad());
//                                            if (remaining_load > Preferences.host_reserved_load) {
                                            if(true){
                                                Message profileMsg = new Message(((MulticastMessage) msg).getSource(),
                                                        "", "", host_node);
                                                profileMsg.setNormalMsgType(Message.NormalMsgType.PROFILE_XCHG);
                                                try {
                                                    mp.send(profileMsg);
                                                } catch (InvalidMessageException ex) {
                                                    ex.printStackTrace();
                                                }
                                                appendToLogger("Sent message profile for task: "
                                                        + receivedTask.taskId + " " + remaining_load
                                                        + "\n");
                                            } else {
                                            	appendToLogger("Node Overloaded: " + receivedTask.taskId + " " + remaining_load + "\n");
                                            }
                                        }
                                        break;
                                }
                            } else {
                                switch (msg.getNormalMsgType()) {
                                    case NORMAL:
                                    	appendToLogger(msg.getData() + "\n");
                                        break;
                                    case PROFILE_XCHG:
                                        Node profileOfNode = (Node) msg.getData();
                                        appendToLogger(profileOfNode + "\n");
                                        // appendToLogger(profileOfNode + "\n");
                                        String taskId = profileOfNode.getTaskid();
                                        try {
                                        synchronized (taskGroup) {
                                            ArrayList<Node> taskNodes = taskGroup.get(taskId);
                                            if (taskNodes == null) {
                                                taskNodes = new ArrayList<Node>();
                                            }
                                            taskNodes.add(profileOfNode);
                                            taskGroup.put(taskId, taskNodes);
                                            distributeTask(profileOfNode.getTaskid());
                                        }
                                        } catch(Exception e) {
                                        	e.printStackTrace();
                                        }
                                        break;
                                    case DISTRIBUTED_TASK:
                                        DistributedTask distTask = (DistributedTask) msg.getData();
                                        TaskResult result = new TaskResult(distTask.getTaskLoad(),
                                                distTask.taskId, host_name, handleDistributedTask(distTask));
                                        appendToLogger(result.getTaskResult() + "\n");
                                        Message resultMsg = new Message(distTask.getSource(), "", "", result);
                                        resultMsg.setNormalMsgType(Message.NormalMsgType.TASK_RESULT);
                                        try {
                                            mp.send(resultMsg);
                                        } catch (InvalidMessageException ex) {
                                            ex.printStackTrace();
                                        }
                                        break;
                                    case TASK_RESULT:
                                        TaskResult taskResult = (TaskResult)msg.getData();
                                        appendToLogger("Result from: " + taskResult.getSource() + " ==> "
                                                + taskResult.getTaskResult());
                                        break;
                                }
                            }
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            private void appendToLogger(String msg) {
            	android.os.Message guiMessage = android.os.Message.obtain(guiRefresh, REFRESH);
            	Bundle msgBundle = new Bundle();
            	msgBundle.putString(TXTMSG, msg);
				guiMessage.setData(msgBundle);
				guiRefresh.sendMessage(guiMessage);
            }

            private Serializable handleDistributedTask(DistributedTask gotTask) {
                try {
                    Class cl = Class.forName(gotTask.getClassName());
                    HashMap<String, Class[]> mthdDef = new HashMap<String, Class[]>();
                    Method mthds[] = cl.getDeclaredMethods();
                    for (Method m : mthds) {
                        mthdDef.put(m.getName(), m.getParameterTypes());
                    }
                    if (gotTask.getParameters() != null && ((mthdDef.get(gotTask.getMethodName())).length != gotTask.getParameters().length)) {
                        System.out.println("Parameters don\'t match");
                    } else {
                        Class params[] = mthdDef.get(gotTask.getMethodName());
                        Object parameters[] = (Object[]) gotTask.getParameters();
                        try {
                            Method invokedMethod = cl.getMethod(gotTask.getMethodName(), params);
                            return (Serializable) invokedMethod.invoke(new SampleApplication(), parameters);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }

            private void distributeTask(String taskId) {
                ArrayList<Node> nodesAvailable = null;
                synchronized (taskGroup) {
                    nodesAvailable = taskGroup.get(taskId);
                }
                for (Node n : nodesAvailable) {
                    if (n != null && !n.hasBeenDistributed()) {
                        n.setDistributed(Boolean.TRUE);
                        Task taskToDistribute = taskDetails.get(taskId);
                        Serializable[] parameters = new Serializable[2];
                        parameters[0] = 10;
                        parameters[1] = 20;
                        DistributedTask dsTask = new DistributedTask(taskToDistribute.getTaskLoad(), taskId,
                                taskToDistribute.getSource(),
                                "ds.android.tasknet.application.SampleApplication", "method1", parameters);
                        Message distMsg = new Message(n.getName(), "", "", dsTask);
                        distMsg.setNormalMsgType(Message.NormalMsgType.DISTRIBUTED_TASK);
                        try {
                            mp.send(distMsg);
                        } catch (InvalidMessageException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

	private void keepSendingProfileUpdates() {
		(new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(Preferences.PROFILE_UPDATE_TIME_PERIOD);
						try {
							synchronized (Preferences.nodes) {
								Node updateNode = Preferences.nodes
										.get(host_name);
								updateNode.update(getCurrentRAM(),
										getCPUusage(), getBatteryLevel());
								Message profileUpdate = new Message(
										Preferences.COORDINATOR, "", "",
										updateNode);
								profileUpdate
										.setNormalMsgType(Message.NormalMsgType.PROFILE_UPDATE);
								mp.send(profileUpdate);
							}
						} catch (InvalidMessageException ex) {
							ex.printStackTrace();
						}
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}

	/************************* Claire's code **********************************/
	public int getBatteryLevel() {

		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						-1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				if (rawlevel >= 0 && scale > 0)
					batteryLevel = (rawlevel * 100) / scale;
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
		return batteryLevel;
	}

	private long getCurrentRAM() {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.availMem / 1048576L;
	}

	private float getCPUusage() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();

			String[] toks = load.split(" ");

			long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]);
			long currIdle = Long.parseLong(toks[5]);

			cpuLoad = (currTotal - cpuTotal) * 100.0f
					/ (currTotal - cpuTotal + currIdle - cpuIdle);
			cpuTotal = currTotal;
			cpuIdle = currIdle;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return cpuLoad;
	}

	/********************************************************************************/
}