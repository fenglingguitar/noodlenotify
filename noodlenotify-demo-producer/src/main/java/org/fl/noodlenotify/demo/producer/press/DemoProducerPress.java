package org.fl.noodlenotify.demo.producer.press;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.fl.noodlenotify.core.pclient.ProducerClient;

public class DemoProducerPress {
	
	public static void main(String[] args) throws Exception {
		
		ClassPathXmlApplicationContext applicationContext
				= new ClassPathXmlApplicationContext(
						"classpath:org/fl/noodlenotify/demo/producer/press/noodlenotify-demo-producer-press.xml");
		
		ProducerClient producerClient = (ProducerClient) applicationContext.getBean("producerClientProxy");
		
		final byte[] content = new byte[512];
		for (int i=0; i<512; i++) {
			content[i] = 'A';
		}
		
		producerClient.send("TestQueue1", new String(content));

		applicationContext.destroy();
    }
}
