package com.common.home.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDAOImpl implements CommonDAO{
	
	@Autowired
	private SqlSession sqlSession;	
	private static final String Namespace = "com.common.home.mapper.CommonMapper";
	
	@Override
	public String getTimestamp() {
		String methodName = Namespace + "." + new Object(){}.getClass().getEnclosingMethod().getName();
		return sqlSession.selectOne(methodName); 
	}

}
