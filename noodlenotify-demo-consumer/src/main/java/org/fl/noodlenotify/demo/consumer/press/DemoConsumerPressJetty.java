package org.fl.noodlenotify.demo.consumer.press;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class DemoConsumerPressJetty {
	
	public static void main(String[] args) {
		
		final ClassPathXmlApplicationContext applicationContext
			= new ClassPathXmlApplicationContext(
				"classpath:org/fl/noodlenotify/demo/consumer/press/noodlenotify-demo-consumer-press-jetty.xml");
		
		Signal.handle(new Signal("INT"), new SignalHandler() {
			@Override
			public void handle(Signal signal) {
				System.out.println("Signal INT And Over");
				applicationContext.destroy();
				System.exit(0);
			}
	    });
	    Signal.handle(new Signal("TERM"), new SignalHandler() {
			@Override
			public void handle(Signal signal) {
				System.out.println("Signal TERM And Over");
				applicationContext.destroy();
				System.exit(0);
			}
	    });
		
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
		}
	}
}
