package com.qunyi.commom.config.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 使用动态数据源的初衷，是能在应用层做到读写分离，即在程序代码中控制不同的查询方法去连接不同的库。除了这种方法以外，
 * 数据库中间件也是个不错的选择，它的优点是数据库集群对应用来说只暴露为单库，不需要切换数据源的代码逻辑。
 * 我们通过自定义注解 + AOP的方式实现数据源动态切换。
 * 首先定义一个ContextHolder, 用于保存当前线程使用的数据源名
 * @author liuqiuping
 * @date 2018-4-21
 */
public class DataSourceContextHolder {
    public static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);

    /**
     * 默认数据源
     */
    public static final String DEFAULT_DS = "Masterdb";

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    // 设置数据源名
    public static void setDB(String dbType) {
        log.info("切换到{}数据源", dbType);
        contextHolder.set(dbType);
    }

    // 获取数据源名
    public static String getDB() {
        return (contextHolder.get());
    }

    // 清除数据源名
    public static void clearDB() {
        contextHolder.remove();
    }
}
