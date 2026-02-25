package org.example.springframeworkdemo.spring;

import lombok.Data;

/**
 * bean的定义
 * 简单理解为bean对象的代理类
 * 包含bean的定义信息，比如单例/多例，bean的类型
 */

public class BeanDefinition {

    private Class type;

    private String scope;

    // 比如懒加载的/非懒加载 ...


    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
