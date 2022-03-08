package pers.zhixilang.lego.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 方便扫描
 * add `@Component` => 注册到Spring ApplicationContext
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 13:48
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
}
