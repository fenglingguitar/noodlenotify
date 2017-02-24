package org.fl.noodlenotify.demo.producer.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.fl.noodlenotify.core.pclient.ProducerClient;

public class DemoProducerExample {
	
	public static void main(String[] args) throws Exception {
		
		ClassPathXmlApplicationContext applicationContext
				= new ClassPathXmlApplicationContext(
						"classpath:org/fl/noodlenotify/demo/producer/example/noodlenotify-demo-producer-example.xml");
		
		ProducerClient producerClient = (ProducerClient) applicationContext.getBean("producerClient");
		
		for (int i=0; i<Integer.MAX_VALUE; i++) {
    		System.out.print("Please Input Queue Name: ");
    		byte[] queueNameByteArray = new byte[1024];
    		int len = System.in.read(queueNameByteArray);
    		String queueName = "TestQueue1";
    		if (len > 2) {
    			queueName = new String(queueNameByteArray, 0, len-2);	
    		}
    		System.out.println("Input Queue Name: " + queueName);
    		
    		System.out.print("Please Input Message Content: ");
    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    		String content = bufferedReader.readLine();
    		if (content == null || content.equals("")) {
    			content = "Hello";
    		} 
    		System.out.println("Input Message Content: " + content);
    		
    		try {
    			producerClient.send(queueName, new String(content));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		//Thread.sleep(1000);
    	}
		
		applicationContext.destroy();
		applicationContext.close();
    }
}
