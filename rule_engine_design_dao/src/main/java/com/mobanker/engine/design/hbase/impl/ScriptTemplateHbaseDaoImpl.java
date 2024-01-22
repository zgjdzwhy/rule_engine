package com.mobanker.engine.design.hbase.impl;

import com.mobanker.engine.design.hbase.ScriptTemplateHbaseDao;
import com.mobanker.engine.design.pojo.hbase.ScriptTemplateEntity;
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
public class ScriptTemplateHbaseDaoImpl extends BaseDaoImpl<ScriptTemplateEntity> implements ScriptTemplateHbaseDao {

    @EETransaction(type = "SQL", name = "ScriptTemplateHbaseDaoImpl.save")
    @Override
    public void save(ScriptTemplateEntity entity) throws HBaseORMException {
       // this.bulkUpdate(entity);
    }

    @EETransaction(type = "SQL", name = "ScriptTemplateHbaseDaoImpl.saveList")
    @Override
    public void saveList(List<ScriptTemplateEntity> entities) throws HBaseORMException {
        this.bulkUpdate(entities);
    }

    public void bulkUpdate(List<ScriptTemplateEntity> entities) {
    }

    @EETransaction(type = "SQL", name = "ScriptTemplateHbaseDaoImpl.delete")
    @Override
    public void delete(String id) throws HBaseORMException {
        this.deleteById(ValueFactory.TypeCreate(id));
    }


    @EETransaction(type = "SQL", name = "ScriptTemplateHbaseDaoImpl.get")
    @Override
    public ScriptTemplateEntity get(String id) throws HBaseORMException {
        return this.queryById(ValueFactory.TypeCreate(id));
    }

    @EETransaction(type = "SQL", name = "ScriptTemplateHbaseDaoImpl.queryAllTemplate")
    @Override
    public List<ScriptTemplateEntity> queryAllTemplate(String type) throws HBaseORMException {
        FilterList filterList = new FilterList();
        List<ScriptTemplateEntity> page =null ;
        if (StringUtils.isNotBlank(type)) {
            String[] conditions = {type};
            RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(IdUtil.getRegex(conditions)));
            filterList.addFilter(rowFilter);
            page = queryWithFilter(filterList);
        }
       
        return page;
    }

}
