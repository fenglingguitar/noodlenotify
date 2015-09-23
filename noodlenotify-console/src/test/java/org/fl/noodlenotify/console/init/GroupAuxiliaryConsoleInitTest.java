package org.fl.noodlenotify.console.init;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:org/fl/noodlenotify/console/bean/noodlenotify-console-bean.xml"
})
public class GroupAuxiliaryConsoleInitTest extends AbstractJUnit4SpringContextTests {
	
	@Test
	public void test() {
	}
}
