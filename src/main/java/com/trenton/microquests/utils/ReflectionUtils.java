package com.trenton.microquests.utils;

import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static <T> List<T> initializeClasses(Plugin plugin, String packageName, Class<T> interfaceClass) {
        List<T> instances = new ArrayList<>();
        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(packageName)) {
                Class<?> clazz = info.load();
                if (interfaceClass.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isEnum()) {
                    try {
                        T instance = (T) clazz.getDeclaredConstructor().newInstance();
                        instances.add(instance);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to instantiate " + clazz.getSimpleName() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to scan classes: " + e.getMessage());
        }
        return instances;
    }
}