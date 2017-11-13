package com.github.hesimin.rpc.provider.scan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hesimin 2017-11-13
 */
public class ClassScan {
    private static          Map<String, List<Object>> classCache   = new ConcurrentHashMap<>();
    private static volatile boolean                   SCAN_OK      = false;
    private static final    String                    PACKAGE_NAME = "com.github.hesimin.api";

    private ClassScan() {
    }

    public static List<Object> getBean(String className) throws ClassNotFoundException {
        if (!SCAN_OK) {
            try {
                synchronized (ClassScan.class) {
                    new ClassScan().scan(PACKAGE_NAME);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return classCache.get(className);
    }

    private void scan(String packageName) throws IOException, ClassNotFoundException {
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("[.]", File.separator));
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        for (File directory : dirs) {
            scanClass(directory, packageName);
        }
        SCAN_OK = true;
    }

    private void scanClass(File directory, String packageName) throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                scanClass(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                Class clazz = Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6));
                if (clazz.isInterface()) {
                    continue;
                }
                Object instance = null;
                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
                Class[] clazzInterfaces = clazz.getInterfaces();
                for (Class clazzInterface : clazzInterfaces) {
                    List<Object> classes = classCache.get(clazzInterface.getName());
                    if (classes == null) {
                        classes = new ArrayList<>();
                        classCache.put(clazzInterface.getName(), classes);
                    }
                    classes.add(instance);
                }
            }
        }
    }
}
