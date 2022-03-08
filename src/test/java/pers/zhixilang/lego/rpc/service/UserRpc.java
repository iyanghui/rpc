package pers.zhixilang.lego.rpc.service;

import pers.zhixilang.lego.rpc.annotation.RpcClient;
import pers.zhixilang.lego.rpc.pojo.User;

/**
 *
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 13:50
 */
@RpcClient
public interface UserRpc {
    String insert(User user);

    User get(String id);
}
