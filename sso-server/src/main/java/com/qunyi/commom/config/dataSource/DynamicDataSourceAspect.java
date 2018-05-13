package com.qunyi.commom.config.dataSource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 编写AOP切面，实现切换逻辑：
 * @author liuqiuping
 * @date 2018-4-21
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(com.qunyi.commom.config.dataSource.DS)")
    public void beforeSwitchDS(JoinPoint point){
        System.out.println("执行切面处理++++++++++++++++++++++++++++");
        //获得当前访问的class
        Class<?> className = point.getTarget().getClass();

        //获得访问的方法名
        String methodName = point.getSignature().getName();
        //得到方法的参数的类型
        Class[] argClass = ((MethodSignature)point.getSignature()).getParameterTypes();
        String dataSource = DataSourceContextHolder.DEFAULT_DS;
        try {
            // 得到访问的方法对象
            Method method = className.getMethod(methodName, argClass);

            // 判断是否存在@DS注解
            if (method.isAnnotationPresent(DS.class)) {
                DS annotation = method.getAnnotation(DS.class);
                // 取出注解中的数据源名
                dataSource = annotation.value();
                System.out.println("注解动态获取的数据源名为==============："+dataSource);

            }
            System.out.println("是否执行注解DS==============："+dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 切换数据源
        DataSourceContextHolder.setDB(dataSource);

    }


    @After("@annotation(com.qunyi.commom.config.dataSource.DS)")
    public void afterSwitchDS(JoinPoint point){

        DataSourceContextHolder.clearDB();

    }
}