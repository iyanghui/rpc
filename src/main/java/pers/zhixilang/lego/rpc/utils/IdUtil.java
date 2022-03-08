package pers.zhixilang.lego.rpc.utils;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:04
 */
public class IdUtil {
    private static SnowFlakeWorker snowFlakeWorker = new SnowFlakeWorker(3, 3);

    public static String nextId() {
        return String.valueOf(snowFlakeWorker.nextId());
    }
}
