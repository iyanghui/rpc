package pers.zhixilang.lego.rpc.core.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.zhixilang.lego.rpc.common.Request;
import pers.zhixilang.lego.rpc.common.Response;
import pers.zhixilang.lego.rpc.netty.client.NettyClient;
import pers.zhixilang.lego.rpc.utils.IdUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 动态代理
 * 调用netty客户端方法，完成服务调用
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:12
 */
public class RpcFactory implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcFactory.class);

    @Resource
    private NettyClient nettyClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setId(IdUtil.nextId());

        logger.debug("rpc请求「{}」", JSONObject.toJSONString(request));

        Response response = nettyClient.send(request);

        logger.debug("rpc响应「{}」", JSONObject.toJSONString(response));

        Class<?> returnType = method.getReturnType();

        if (null != response.getCode() && response.getCode() == 1) {
            throw new Exception(response.getMsg());
        }
        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)) {
            return response.getData();
        } else if (Collection.class.isAssignableFrom(returnType)) {
            return JSONArray.parseObject(response.getData().toString(), Object.class);
        } else if (Map.class.isAssignableFrom(returnType)) {
            return JSON.parseObject(response.getData().toString(), Object.class);
        } else {
            if (null != response.getData()) {
                return JSONObject.parseObject(response.getData().toString(), returnType);
            }
            return null;
        }
    }
}
