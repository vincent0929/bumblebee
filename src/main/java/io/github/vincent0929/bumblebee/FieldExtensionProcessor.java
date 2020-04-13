package io.github.vincent0929.bumblebee;

import io.github.vincent0929.bumblebee.annotaions.EnableFiledExtension;
import io.github.vincent0929.bumblebee.annotaions.FieldExtension;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@SuppressWarnings({"rawtypes"})
public class FieldExtensionProcessor implements ClassProcessor, BeanPostProcessor {

    private static final Map<Class, Class> fieldExtensionMap = new ConcurrentHashMap<>();

    @Override
    public boolean isSupport(Class clazz) {
        boolean isGenericClass = isGenericClass(clazz);
        if (isGenericClass) {
            return false;
        }
        return true;
    }

    @Override
    public void process(Class clazz) {
        List<Field> annotationFields = getAnnotationFields(clazz);
        if (annotationFields.isEmpty()) {
            return;
        }

        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(clazz);
        annotationFields.forEach(field -> {
            FieldExtension annotation = field.getDeclaredAnnotation(FieldExtension.class);
            generator.addProperty(field.getName() + annotation.suffix(), annotation.type());
        });
        Class newClazz = (Class) generator.createClass();

        fieldExtensionMap.put(clazz, newClazz);
    }

    private List<Field> getAnnotationFields(Class clazz) {
        if (clazz == null || Object.class.equals(clazz)) {
            return new ArrayList<>();
        }
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(
                field -> field.getDeclaredAnnotation(FieldExtension.class) != null).collect(Collectors.toList());
        fields.addAll(getAnnotationFields(clazz.getSuperclass()));
        return fields;
    }

    private boolean isGenericClass(Class clazz) {
        return clazz == null || Boolean.class.equals(clazz) || Character.class.equals(clazz) || Integer.class.equals(clazz) ||
                Byte.class.equals(clazz) || Short.class.equals(clazz) || Long.class.equals(clazz) || Float.class.equals(clazz) ||
                Double.class.equals(clazz) || String.class.equals(clazz) || Object.class.equals(clazz);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        EnableFiledExtension declaredAnnotation = bean.getClass().getDeclaredAnnotation(EnableFiledExtension.class);
        if (declaredAnnotation == null) {
            return bean;
        }

        Method[] methods = bean.getClass().getDeclaredMethods();
        Set<Class> returnClassSet = Arrays.stream(methods).map(Method::getReturnType).collect(Collectors.toSet());
        if (returnClassSet.isEmpty()) {
            return bean;
        }

        Map<Class, Class> classMap = returnClassSet.stream().filter(fieldExtensionMap::containsKey)
                .collect(Collectors.toMap(returnType -> returnType, fieldExtensionMap::get));
        if (classMap.isEmpty()) {
            return bean;
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        enhancer.setCallback(new FiledExtensionMethodInterceptor(classMap));
        return enhancer.create();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
