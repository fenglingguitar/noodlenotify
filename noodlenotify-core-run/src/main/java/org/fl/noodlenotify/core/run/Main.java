package org.fl.noodlenotify.core.run;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class Main {
	
    public static void main(String[] args) throws Exception {
    	
    	if (args.length == 0) {
    		System.out.println("Place input param");
    		System.out.println("  -e  Start a exchange");
			System.out.println("  -d  Start a distribute");
			System.out.println("  -f [ConfigPath]  Set config path");
			System.out.println("  -l [ConfigPath]  Set logback path");
			return;
    	}
    	
    	String configFile = null;
    	String logbackFile = "../conf/logback.common.xml";
    	
    	boolean isE = false;
    	boolean isD = false;
    	boolean isF = false;
    	boolean isL = false;
    	
    	boolean isRight = true;
    	
    	for (int i=0; i<args.length; i++) {
    		if (args[i].equals("-e")) {
    			isE = true;
    			configFile = "../conf/noodlenotify-core-exchange-netty.xml";
    		} else if (args[i].equals("-d")) {
    			isD = true;
    			configFile = "../conf/noodlenotify-core-distribute.xml";
    		} else if (args[i].equals("-f")) {
    			if (i + 1 < args.length) {
    				isF = true;
    				configFile = args[i + 1];
    				i += 1;
    			} else {
    				isRight = false;
    			}
    		} else if (args[i].equals("-l")) {
    			if (i + 1 < args.length) {
    				isL = true;
    				logbackFile = args[i + 1];
    				i += 1;
    			} else {
    				isRight = false;
    			}
    		} else {
    			isRight = false;
    		}
    	}
    	
    	if (!isRight) {
    		System.out.println("Place input right param: ");
			System.out.println("  -e  Start a exchange");
			System.out.println("  -d  Start a distribute");
			System.out.println("  -f [ConfigPath]  Set config path");
			System.out.println("  -l [ConfigPath]  Set logback path");
			return;
    	}
    	
    	if (isE && isD) {
    		System.out.println("Place input -p or -d");
    		System.out.println("  -e  Start a exchange");
			System.out.println("  -d  Start a distribute");
			System.out.println("  -f [ConfigPath]  Set config path");
			System.out.println("  -l [ConfigPath]  Set logback path");
    		return;
    	}
    	
    	if (isE) {
    		System.out.println("Start a exchange...");
    	} else {
    		System.out.println("Start a distribute...");
    	}
    	
    	if (isF) {
    		System.out.println("Config file path: " + configFile);
    	}
    	
    	if (isL) {
    		System.out.println("Logback file path: " + logbackFile);
    	}
    	
    	LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    	JoranConfigurator configurator = new JoranConfigurator();  
        configurator.setContext(lc);  
        lc.reset();  
        configurator.doConfigure(logbackFile);  
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);  
    	
    	final FileSystemXmlApplicationContext applicationContext = 
    			new FileSystemXmlApplicationContext(configFile);
    	
    	Signal.handle(new Signal("INT"), new SignalHandler() {
			@Override
			public void handle(Signal signal) {
				System.out.println("Signal INT And Over");
				applicationContext.destroy();
				applicationContext.close();
				System.exit(0);
			}
        });
        Signal.handle(new Signal("TERM"), new SignalHandler() {
			@Override
			public void handle(Signal signal) {
				System.out.println("Signal TERM And Over");
				applicationContext.destroy();
				applicationContext.close();
				System.exit(0);
			}
        });
        
        try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}