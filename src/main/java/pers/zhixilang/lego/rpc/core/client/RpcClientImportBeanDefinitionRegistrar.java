package pers.zhixilang.lego.rpc.core.client;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import pers.zhixilang.lego.rpc.annotation.EnableRpc;
import pers.zhixilang.lego.rpc.annotation.RpcClient;
import pers.zhixilang.lego.rpc.exception.RpcException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhixilang
 * @version 1.0.0
 * date 2022-03-08 20:17
 */
public class RpcClientImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        ClassPathRpcScanner classPathRpcScanner = new ClassPathRpcScanner(beanDefinitionRegistry);

        classPathRpcScanner.setAnnotationClass(RpcClient.class);
        classPathRpcScanner.registerFilters();
        classPathRpcScanner.scan(getBasePackages(annotationMetadata));
    }

    private String[] getBasePackages(AnnotationMetadata annotationMetadata) {
        Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(EnableRpc.class.getCanonicalName());
        if (null == attributes) {
            throw new RpcException();
        }

        Set<String> basePackages = new HashSet<>();
        basePackages.addAll(Arrays.asList((String[]) attributes.get("value")));
        basePackages.addAll(Arrays.asList((String[]) attributes.get("basePackages")));

        if (basePackages.size() == 0) {
            basePackages.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
        }
        return basePackages.toArray(new String[0]);

    }
}
