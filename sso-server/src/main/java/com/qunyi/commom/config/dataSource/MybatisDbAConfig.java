package com.qunyi.commom.config.dataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建自定义sqlSessionFactory,实现多个数据源动态注入sqlSessionFactory
 * @author liuqiuping
 * @date 2018-4-21
 */
@Configuration
@MapperScan(basePackages = {"com.qunyi.modules.dao"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisDbAConfig {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    @Bean(name = "Masterdb")
    @ConfigurationProperties(prefix = "spring.datasource.master") // application.properteis中对应属性的前缀
    public DataSource MasterDataSource() {
        logger.info("注入 druid MasterDataSource 主数据源！！！");
//        DruidDataSource datasource = new DruidDataSource();
////        datasource.setUrl(propertyResolver.getProperty("url"));
////        datasource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
////        datasource.setUsername(propertyResolver.getProperty("username"));
////        datasource.setPassword(propertyResolver.getProperty("password"));
////        datasource.setInitialSize(Integer.valueOf(propertyResolver.getProperty("initialSize")));
////        datasource.setMinIdle(Integer.valueOf(propertyResolver.getProperty("minIdle")));
////        datasource.setMaxWait(Long.valueOf(propertyResolver.getProperty("maxWait")));
////        datasource.setMaxActive(Integer.valueOf(propertyResolver.getProperty("maxActive")));
////        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(propertyResolver.getProperty("minEvictableIdleTimeMillis")));
////        return datasource;

        return DataSourceBuilder.create().build();
    }

    @Bean(name = "Clusterdb")
    @ConfigurationProperties(prefix = "spring.datasource.cluster") // application.properteis中对应属性的前缀
    public DataSource clusterDataSource() {
        logger.info("注入 druid clusterDataSource 备用数据源！！！");
//        DruidDataSource datasource = new DruidDataSource();
//        datasource.setUrl(propertyResolver.getProperty("url"));
//        datasource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
//        datasource.setUsername(propertyResolver.getProperty("username"));
//        datasource.setPassword(propertyResolver.getProperty("password"));
//        datasource.setInitialSize(Integer.valueOf(propertyResolver.getProperty("initialSize")));
//        datasource.setMinIdle(Integer.valueOf(propertyResolver.getProperty("minIdle")));
//        datasource.setMaxWait(Long.valueOf(propertyResolver.getProperty("maxWait")));
//        datasource.setMaxActive(Integer.valueOf(propertyResolver.getProperty("maxActive")));
//        datasource.setMinEvictableIdleTimeMillis(Long.valueOf(propertyResolver.getProperty("minEvictableIdleTimeMillis")));
//        return datasource;
        return DataSourceBuilder.create().build();
    }

    @Bean(name="dynamicDataSource")
    @Primary    //优先使用，多数据源
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        DataSource master = MasterDataSource();
        DataSource slave = clusterDataSource();
        //设置默认数据源
        dynamicDataSource.setDefaultTargetDataSource(master);
        //配置多数据源
        Map<Object,Object> map = new HashMap<>();
        map.put("Masterdb", master);   //key需要跟ThreadLocal中的值对应
        map.put("Clusterdb", slave);
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }


}