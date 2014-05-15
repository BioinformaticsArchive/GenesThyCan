package moara.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * <p>Getting windows environmental variable </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: IST, Drexel University</p>
 * @author http://www.rgagnon.com/javadetails/java-0150.html
 * @version 1.0
 */

public class EnvironmentVariable {
    
	private static String MOARA_HOME = null;
    
    public static Properties getEnv(){
        BufferedReader br;
        Process p;
        Properties envVars;
        Runtime r;
        String line, OS, key, value;
        int idx;

        try{
            r = Runtime.getRuntime();
            envVars = new Properties();
            OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("windows 9") > -1) {
                p = r.exec("command.com /c set");
            } else if ( (OS.indexOf("nt") > -1) || (OS.indexOf("windows") > -1)) {
                // thanks to JuanFran for the xp fix!
                p = r.exec("cmd.exe /c set");
            } else {
                // our last hope, we assume Unix (thanks to H. Ware for the fix)
                p = r.exec("env");
            }

            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ( (line = br.readLine()) != null) {
                idx = line.indexOf('=');
                key = line.substring(0, idx);
                value = line.substring(idx + 1);
                envVars.setProperty(key, value);
            }
            return envVars;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnv(String key){
        Properties env;

        env=getEnv();
        if(env!=null)
            return env.getProperty(key);
        else
            return null;
    }

    public static void setMoaraHome(String home){
    	MOARA_HOME = home;
    }

    public static String getMoaraHome(){
        if(MOARA_HOME!=null)
            return MOARA_HOME;
        else
            return getEnv("MOARA_HOME");
    }

}

