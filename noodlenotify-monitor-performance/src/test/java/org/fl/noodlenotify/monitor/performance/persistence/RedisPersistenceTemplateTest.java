package org.fl.noodlenotify.monitor.performance.persistence;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.monitor.performance.persistence.vo.BaseVo;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/performance/persistence/noodlenotify-monitor-performance-persistence.xml"
})

public class RedisPersistenceTemplateTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(RedisPersistenceTemplateTest.class);
	
	@Autowired
	private RedisPersistenceTemplate redisPersistenceTemplate;

	@Test
	public void testInsert() throws Exception {
		String keyName = "keyName";
		Date date = new Date();
		TestVo testVo = new TestVo();
		testVo.setCreateDate(date);
		double score = date.getTime();
		redisPersistenceTemplate.insert(keyName, score, testVo);
	}

	@Test
	public void testDelete() throws Exception {
		String keyName = "keyName";
		Date date = new Date();
		TestVo testVo = new TestVo();
		testVo.setCreateDate(date);
		double score = date.getTime();
		redisPersistenceTemplate.insert(keyName, score, testVo);
		redisPersistenceTemplate.delete(keyName, testVo);
	}

	@Test
	public void testDeletes() throws Exception {
		long startTime = new Date().getTime();
		String keyName = "keyName";
		TestVo testVo1 = new TestVo("v1", new Date());
		redisPersistenceTemplate.insert(keyName, testVo1.getCreateDate().getTime(), testVo1);
		TestVo testVo2 = new TestVo("v2", new Date());
		redisPersistenceTemplate.insert(keyName, testVo2.getCreateDate().getTime(), testVo2);
		TestVo testVo3 = new TestVo("v3", new Date());
		redisPersistenceTemplate.insert(keyName, testVo3.getCreateDate().getTime(), testVo3);
		TestVo testVo4 = new TestVo("v4", new Date());
		redisPersistenceTemplate.insert(keyName, testVo4.getCreateDate().getTime(), testVo4);
		redisPersistenceTemplate.deletes(keyName, startTime, new Date().getTime());
	}

	@Test
	public void testQueryList() throws Exception {
		long startTime = new Date().getTime();
		String keyName = "keyName";
		TestVo testVo1 = new TestVo("v1", new Date());
		redisPersistenceTemplate.insert(keyName, testVo1.getCreateDate().getTime(), testVo1);
		TestVo testVo2 = new TestVo("v2", new Date());
		redisPersistenceTemplate.insert(keyName, testVo2.getCreateDate().getTime(), testVo2);
		TestVo testVo3 = new TestVo("v3", new Date());
		redisPersistenceTemplate.insert(keyName, testVo3.getCreateDate().getTime(), testVo3);
		TestVo testVo4 = new TestVo("v4", new Date());
		redisPersistenceTemplate.insert(keyName, testVo4.getCreateDate().getTime(), testVo4);
		List<TestVo> queryList = redisPersistenceTemplate.queryList(keyName, startTime, new Date().getTime(), TestVo.class);
		for(TestVo testVo : queryList) {
			logger.info("Name: " + testVo.getName());
		}
	}

	@Test
	public void testQueryPage() throws Exception {
		long startTime = new Date().getTime();
		String keyName = "keyName";
		TestVo testVo1 = new TestVo("v1", new Date());
		redisPersistenceTemplate.insert(keyName, testVo1.getCreateDate().getTime(), testVo1);
		TestVo testVo2 = new TestVo("v2", new Date());
		redisPersistenceTemplate.insert(keyName, testVo2.getCreateDate().getTime(), testVo2);
		TestVo testVo3 = new TestVo("v3", new Date());
		redisPersistenceTemplate.insert(keyName, testVo3.getCreateDate().getTime(), testVo3);
		TestVo testVo4 = new TestVo("v4", new Date());
		redisPersistenceTemplate.insert(keyName, testVo4.getCreateDate().getTime(), testVo4);
		PageVo<TestVo> queuePageVo = redisPersistenceTemplate.queryPage(keyName, startTime, new Date().getTime(), 1, 2, TestVo.class);
		logger.info("TotalCount: " + queuePageVo.getTotalCount() 
				+ ", TotalPageCount: " + queuePageVo.getTotalPageCount() 
				+ ", Start: " + queuePageVo.getStart()
				+ ", PageSize: " + queuePageVo.getPageSize()
				+ ", CurrentPageNo: " + queuePageVo.getCurrentPageNo()
				);
		if (queuePageVo.getTotalCount() > 0) {
			for (TestVo testVo : queuePageVo.getData()) {
				logger.info("Name: " + testVo.getName());
			}
		}
	}
	
	@Test
	public void testGetKeys() throws Exception {
		Set<String> keysSet = redisPersistenceTemplate.getKeys();
		for (String key : keysSet) {
			logger.info("Key: " + key);
		}
	}
	
	static class TestVo extends BaseVo {
		
		private String name;
		private Date createDate;

		public TestVo() {
		}

		public TestVo(String name, Date createDate) {
			this.name = name;
			this.createDate = createDate;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}
	}
}
