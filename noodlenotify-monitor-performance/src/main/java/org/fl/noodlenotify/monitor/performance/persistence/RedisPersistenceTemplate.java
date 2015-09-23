package org.fl.noodlenotify.monitor.performance.persistence;

import java.util.List;
import java.util.Set;

import org.fl.noodle.common.mvc.vo.PageVo;

public interface RedisPersistenceTemplate {

	/*
	 * 分页查询
	 */
	public <T> PageVo<T> queryPage(String keyName, double min, double max, int pageIndex, int pageSize, Class<T> clazz) throws Exception;

	/*
	 * LIST查询
	 */
	public <T> List<T> queryList(String keyName, double min, double max, Class<T> clazz) throws Exception;

	/*
	 * 插入
	 */
	public <T> void insert(String keyName, double score, Object vo) throws Exception;

	/*
	 * 删除
	 */
	public <T> void delete(String keyName, Object vo) throws Exception;

	/*
	 * 区域删除
	 */
	public <T> void deletes(String keyName, double min, double max) throws Exception;
	
	/*
	 * 得到全部Key
	 */
	public Set<String> getKeys() throws Exception;
}
