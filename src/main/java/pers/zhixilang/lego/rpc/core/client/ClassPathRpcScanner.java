package pers.zhixilang.lego.rpc.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-04 16:49
 */
public class ClassPathRpcScanner extends ClassPathBeanDefinitionScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathRpcScanner.class);

    private RpcFactoryBean<?> rpcFactoryBean = new RpcFactoryBean<Object>();

    private Class<? extends Annotation> annotationClass;

    public ClassPathRpcScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.info("no rpc client found in 「{}」 ", Arrays.toString(basePackages));
        } else {
            processBeanDefinition(beanDefinitions);
        }
        return beanDefinitions;
    }

    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        }

        addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    private void processBeanDefinition(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition genericBeanDefinition;

        for (BeanDefinitionHolder definitionHolder: beanDefinitions) {
            genericBeanDefinition = (GenericBeanDefinition) definitionHolder.getBeanDefinition();
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(genericBeanDefinition.getBeanClassName());
            genericBeanDefinition.setBeanClass(this.rpcFactoryBean.getClass());

            genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }
}
