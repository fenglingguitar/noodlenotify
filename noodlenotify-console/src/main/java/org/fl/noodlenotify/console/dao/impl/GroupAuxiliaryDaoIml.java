package org.fl.noodlenotify.console.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.GroupAuxiliaryDao;
import org.fl.noodlenotify.console.domain.GroupAuxiliaryMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.GroupAuxiliaryVo;

@Repository("groupAuxiliaryDao")
public class GroupAuxiliaryDaoIml implements GroupAuxiliaryDao {
	
	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;
	
	@Override
	public List<GroupAuxiliaryVo> queryGroupAuxiliaryList(GroupAuxiliaryVo vo) throws Exception {
		String strSql = "SELECT group_Num FROM CSL_AXL_GROUP_NUM";
		return dynamicSqlTemplate.queryListSql(strSql, null, GroupAuxiliaryVo.class);
	}

	@Override
	public void insertGroupAuxiliary(GroupAuxiliaryVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, GroupAuxiliaryMd.class);
	}
}
