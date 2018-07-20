package com.voole.ad.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * Dao顶层基类
 * @author Administrator
 *
 */
public class BaseDao {

	@Resource(name="sqlSession")
	private SqlSessionTemplate  seqSessionTemplate;
	
	
	protected SqlSessionTemplate getSqlSession(){
		return this.seqSessionTemplate;
	}
}
