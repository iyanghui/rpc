package pers.zhixilang.lego.rpc.annotation;

import org.springframework.context.annotation.Import;
import pers.zhixilang.lego.rpc.RpcAutoConfiguration;
import pers.zhixilang.lego.rpc.core.client.RpcClientRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcAutoConfiguration.class, RpcClientRegistrar.class})
public @interface EnableRpc {

    /**
     * @see EnableRpc#basePackages()
     */
    String[] value() default {};

    /**
     * 指定扫描的package
     * @return packages
     */
    String[] basePackages() default {};
}
