package com.example.scan.atomic;

import com.example.scan.atomic.dto.LocalAtomicDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class LocalAtomicScanner {

    private static final String packageName = "com.example.scan.atomic.scan";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = scanClasses(packageName);
        System.out.println(classes);
    }


    public static List<LocalAtomicDTO> getLocalAtomic(String packageName) throws IOException, ClassNotFoundException {
        List<LocalAtomicDTO> localAtomicDTOList = new ArrayList<>();

        List<Class<?>> classList = scanClasses(packageName);
        if(classList == null || classList.size() == 0){
            return localAtomicDTOList;
        }

        for(Class<?> clazz : classList){
            String simpleName = clazz.getSimpleName();
            String classPackageName = clazz.getPackage().getName();
            Method[] methods = clazz.getMethods();
            boolean bean = AnnotationUtils.findAnnotation(clazz, Component.class) != null ? true : false;
            if(methods != null || methods.length != 0){
                for(Method method : methods){
                    LocalAtomic annotation = method.getAnnotation(LocalAtomic.class);
                    if(annotation != null){
                        localAtomicDTOList.add(LocalAtomicDTO.builder()
                                .bean(bean)
                                .beanName(bean ? StringUtils.uncapitalize(simpleName) : null)
                                .className(simpleName)
                                .packageName(classPackageName)
                                .method(method.getName())
                                .paramsTypeList(Arrays.stream(method.getParameters()).map(param -> param.getType().getName()).collect(Collectors.toList()))
                                .returnType(method.getReturnType().getName())
                                .build());
                    }
                }
            }
        }
        return localAtomicDTOList;
    }


    public static List<Class<?>> scanClasses(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace(".", "/");
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(path);
        while (urls != null && urls.hasMoreElements()){
            URL url = urls.nextElement();
            if("file".equals(url.getProtocol())){
                log.info("from file");
                String fileStr = URLDecoder.decode(url.getFile(), "UTF-8");
                File file = new File(fileStr);
                if(file.isDirectory()){
                    scanDirectory(file, packageName, classes);
                }else {

                }
            }else if("jar".equals(url.getProtocol())){
                log.info("from jar");
                scanJar(url, classes);
            }else {

            }
        }
        return classes;
    }

    private static void scanDirectory(File directory, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String newPackageName = packageName + "." + file.getName();
                scanDirectory(file, newPackageName, classes);
            } else if (file.getName().endsWith(".class")) {
                String newPackageName = packageName + ".";
                String className = newPackageName + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
    }

    private static void scanJar(URL url, List<Class<?>> classes) throws IOException, ClassNotFoundException {
        try (JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile()) {
            java.util.Enumeration<? extends JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().substring(0, entry.getName().length() - 6)
                            .replace('/', '.');
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
        }
    }

}
