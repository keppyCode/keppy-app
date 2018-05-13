package com.qunyi.commom.config.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源配置,
 *自定义一个javax.sql.DataSource接口的实现，这里只需要继承Spring为我们预先实现好的父类AbstractRoutingDataSource
 * @author liuqiuping
 * @date  2018-4-21
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);


    @Override
    protected Object determineCurrentLookupKey(){
        log.info("动态调用的数据源为:{}", DataSourceContextHolder.getDB());

        return DataSourceContextHolder.getDB();
    }

}
