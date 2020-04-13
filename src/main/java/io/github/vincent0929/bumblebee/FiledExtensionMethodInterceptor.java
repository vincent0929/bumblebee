package io.github.vincent0929.bumblebee;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings({"rawtypes"})
public class FiledExtensionMethodInterceptor implements MethodInterceptor {

    private Map<Class, Class> classMap;

    public FiledExtensionMethodInterceptor(Map<Class, Class> classMap) {
        this.classMap = classMap;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = proxy.invokeSuper(obj, args);
        if (result == null) {
            return null;
        }

        Class clazz = classMap.get(method.getReturnType());
        if (clazz == null) {
            return result;
        }

        BeanCopier beanCopier = BeanCopier.create(result.getClass(), clazz, false);
        Object newResult = clazz.newInstance();
        beanCopier.copy(result, newResult, null);
        return newResult;
    }
}
