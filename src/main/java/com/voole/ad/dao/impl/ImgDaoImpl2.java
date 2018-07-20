package com.voole.ad.dao.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.voole.ad.dao.BaseDao;
import com.voole.ad.dao.IImgDao2;
import com.voole.ad.utils.datasources.DataSourceType;
@Repository
public class ImgDaoImpl2 extends BaseDao implements IImgDao2{
	@DataSourceType(type="t2")
	@Override
	public List<Map> loadImgData(Map<String, Object> map) {
		return  this.getSqlSession().selectList("img.loadImgData",map);
	}


}
