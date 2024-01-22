package com.mobanker.engine.design.hbase.impl;

import com.mobanker.engine.design.hbase.ScriptFunctionHbaseDao;
import com.mobanker.engine.design.pojo.hbase.ScriptFunctionEntity;
import com.mobanker.engine.framkwork.util.IdUtil;
import com.mobanker.framework.exception.HBaseORMException;
import com.mobanker.framework.hbase.dao.impl.BaseDaoImpl;
import com.mobanker.framework.hbase.dao.impl.value.ValueFactory;
import com.mobanker.framework.tracking.EETransaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScriptFunctionHbaseDaoImpl extends BaseDaoImpl<ScriptFunctionEntity> implements ScriptFunctionHbaseDao {

    @EETransaction(type = "SQL", name = "ScriptFunctionHbaseDaoImpl.save")
    @Override
    public void save(ScriptFunctionEntity entity) throws HBaseORMException {
        this.bulkUpdate(entity);
    }

    @EETransaction(type = "SQL", name = "ScriptFunctionHbaseDaoImpl.saveList")
    @Override
    public void saveList(List<ScriptFunctionEntity> entities) throws HBaseORMException {
        this.bulkUpdate(entities);
    }
    
    @EETransaction(type = "SQL", name = "ScriptFunctionHbaseDaoImpl.delete")
    @Override
    public void delete(String id) throws HBaseORMException {
        this.deleteById(ValueFactory.TypeCreate(id));
    }

    @Override
    public void bulkUpdate(ScriptFunctionEntity entity) {

    }

    @Override
    public void bulkUpdate(List<ScriptFunctionEntity> entities) {

    }

    @EETransaction(type = "SQL", name = "ScriptFunctionHbaseDaoImpl.get")
    @Override
    public ScriptFunctionEntity get(String id) throws HBaseORMException {
        return this.queryById(ValueFactory.TypeCreate(id));
    }

    
    @EETransaction(type = "SQL", name = "CommonFunctionHbaseDaoImpl.queryFunctionByType")
    @Override
    public List<ScriptFunctionEntity> queryFunctionByType(String type) throws HBaseORMException {
        FilterList filterList = new FilterList();
        List<ScriptFunctionEntity> page =null ;
        if (StringUtils.isNotBlank(type)) {
            String[] conditions = {type};
            RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(IdUtil.getRegex(conditions)));
            filterList.addFilter(rowFilter);
            page = queryWithFilter(filterList);
        }
       
        return page;
    }

}
