package org.example.springframeworkdemo.spring;

import org.springframework.context.annotation.Bean;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟的spring容器
 * 用于存放bean元素
 */
public class ApplicationContext {

    private static final String SINGLETON = "singleton";
    private static final String PROTOTYPE = "prototype";

    private Class configClass;

    // beanDefinition缓存，用于bean对象初始化前缓存bean的定义对象
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 1. 扫描---> beanDefinition---> beanDefinitionMap
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            ComponentScan scanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            String path = scanAnnotation.value(); // 扫描路径 org.example.springframework
            path = path.replace(".", "/"); // 转换为org\example\springframework

            ClassLoader classLoader = ApplicationContext.class.getClassLoader();

            // 通过相对路径获取
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    // 递归获取.class后缀文件
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith(".class")) {
                        // 通过反射来获取对象
                        String className = fileName.substring(fileName.indexOf("org"), fileName.indexOf(".class"));

                        // mac为 /
//                        className = className.replace("/", ".");

                        // windows
                        className = className.replace("\\", ".");

//                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);

                            if (clazz.isAnnotationPresent(Component.class)) {

                                // 获取beanName
                                Component component = clazz.getAnnotation(Component.class);

                                // 如果前两位大写，则直接返回，反之则首字母小写..
                                // clazz.getSimpleName
                                String beanName = component.value().equals("") ? Introspector.decapitalize(clazz.getSimpleName()) : component.value();

                                // BeanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    // 如果存在scope注解则为多例
                                    Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                    String value = scopeAnnotation.value();
                                    beanDefinition.setScope(value);
                                } else {
                                    beanDefinition.setScope(SINGLETON);
                                }

                                beanDefinitionMap.put(beanName, beanDefinition);


                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        // 2. 实例化单例Bean，根据beanDefinition 来创建单例池
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (SINGLETON.equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);

            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();

        try {
            // 无参构造实例化
            Object instance = clazz.getConstructor().newInstance();

            // 依赖注入
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Autowired.class)) {
                    // 访问权限，改了这个才能赋值
                    f.setAccessible(true);
                    // 要么单例池找，自动生成
                    f.set(instance, getBean(f.getName()));
                }
            }

            // 初始化
            return instance;

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public Object getBean(String beanName) {


        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (SINGLETON.equals(scope)) {
                // fixme 多线程情况下，需要双检锁来保证bean唯一
                // 单例
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    Object bean1 = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, bean1);
                    return bean1;
                }
                return bean;
            } else {
                return createBean(beanName, beanDefinition);
            }
        }
    }
}
