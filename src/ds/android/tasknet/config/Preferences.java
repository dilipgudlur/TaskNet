package ds.android.tasknet.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @authors
 * Divya Vavili - dvavili@andrew.cmu.edu
 * Yash Pathak - ypathak@andrew.cmu.edu
 *
 */
/**
 * Constants/Preferences used in the project
 */
public class Preferences {

	public static final String seperator = System.getProperty("file.separator");
    public static final String conf_file = seperator + "mnt" + seperator + "sdcard" 
    										+ seperator + "ppfile.ini";
    public static final int num_kind = 6;
    public static final int num_msgid = 6;
    public static final int delayedEnoughThreshold = 1000;
    public static final int receiverDelayedEnoughThreshold = 1000;
    public static final int numClockTypes = 3;
    public static final String[] clockTypes = {"LOGICAL", "VECTOR", "DEFAULT"};
    public static int host_index;
    public static String logger_name;
    public static final String MULTICAST_MESSAGE = "multicast";
    public static final String ENTER_CRITICAL_SECTION = "Enter Critical Section";
    public static final String LEAVE_CRITICAL_SECTION = "Leave Critical Section";
    public static HashMap<String, InetAddress> node_addresses;
    public static HashMap<String, Integer> nodes;
    public static HashMap<Integer, String> node_names;
    public static boolean logDrop,logDelay,logDuplicate,logEvent;
    public static String crashNode;

    public static void setHostDetails(String configuration_filename, String local_host) {
        nodes = new HashMap<String, Integer>();
        node_names = new HashMap<Integer, String>();
        node_addresses = new HashMap<String, InetAddress>();
        crashNode = "";
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(configuration_filename));
            logger_name = prop.getProperty("LOGGER");
            StringTokenizer node_string = new StringTokenizer(prop.getProperty("NAMES"), ",");
            int num_nodes = node_string.countTokens();
            for (int i = 0; i < num_nodes; i++) {
                String node_name = node_string.nextToken();
                if (node_name.equalsIgnoreCase(local_host)) {
                    host_index = i;
                }
                if (!node_name.equalsIgnoreCase(logger_name)) {
                    nodes.put(node_name, i);
                    node_names.put(i, node_name);
                    node_addresses.put(node_name, InetAddress.getByName(prop.getProperty("node." + node_name + ".ip")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}