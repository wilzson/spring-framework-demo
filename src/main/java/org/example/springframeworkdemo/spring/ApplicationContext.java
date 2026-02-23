package org.example.springframeworkdemo.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;

/**
 * 模拟的spring容器
 * 用于存放bean元素
 */
public class ApplicationContext {

    private Class configClass;

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 1. 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            ComponentScan scanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);

            String path = scanAnnotation.value(); // 扫描路径 org.example.springframework

            path = path.replace(".", "/"); // 转换为org/example/springframework

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

                        className = className.replace("/", ".");

//                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);

                            if (clazz.isAnnotationPresent(Component.class)) {

                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
//            System.out.println(resource);

        }
    }

    public Object getBean(String beanName) {
        return "";
    }
}
