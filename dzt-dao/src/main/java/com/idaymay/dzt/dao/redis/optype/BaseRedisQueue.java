package com.idaymay.dzt.dao.redis.optype;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @ClassName BaseRedisQueue
 * @Author littlehui
 * @Date 2021/10/18 14:15
 * @Version 1.0
 **/
public class BaseRedisQueue<T> extends AbstractBaseRedisDAO<T> {

    public String adderQueue;

    public BaseRedisQueue() {
        this.zone = "queue:";
        adderQueue = "adder:";
    }

    public void pushItem(String key, T t) {
        this.leftPush(getKey(key), t);
    }

    public T popItem(String key) {
        return this.rightPop(getKey(key));
    }

    public List<T> rangeAll(String key) {
        return this.lRangeAll(getKey(key));
    }

    public void clean(String key) {
        this.delete(getKey(key));
    }

    public List<T> rangeBySize(String key, Integer size) {
        return this.lRangeSize(getKey(key), size);
    }

    public List<T> popSize(String key, Integer size) {
        List<T> popOrders = new ArrayList<>();
        for (int i=0; i<size; i++) {
            T s = this.popItem(key);
            if (s != null) {
                popOrders.add(s);
            } else {
                break;
            }
        }
        return popOrders;
    }

    private String getKey(String key) {
        return adderQueue + key;
    }
}
