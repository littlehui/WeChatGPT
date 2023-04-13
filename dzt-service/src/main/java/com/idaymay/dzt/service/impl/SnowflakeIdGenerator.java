package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.service.constant.ChatConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author longzhicheng
 */
@Component
@Slf4j
public class SnowflakeIdGenerator {
    private final long twepoch = 1663034660000L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    private final String WORKID = "workerId";

    @Setter
    private Long workerId;
    private long datacenterId = 0;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *  datacenterId和 workerId的组合
     *  1              0
     *  2              1
     *  3              4
     *  ...
     *  31             30
     *  1              31
     *  2              0
     *  3              1
     *  ...
     *  不重复次数 31 * 32 = 992，支持 992 个集群节点同时部署。
     * @description nextId
     * @param
     * @author littlehui
     * @date 2022/4/10 17:57
     * @return long
     */
    @PostConstruct
    public void initDataCenterIdAndWorkId() {
        if(workerId == null){
            long serialNumber = incr(WORKID, 1);
            //0-31
            workerId = serialNumber%32;
            //1-31
            datacenterId = serialNumber%31 + 1;
            log.info("初始化datacenterId：{}，workId：{}", datacenterId, workerId);
        }
    }

    public synchronized long nextId() {
        if(workerId == null){
            long serialNumber = incr(WORKID, 1);
            //0-31
            workerId = serialNumber%32;
            //1-31
            datacenterId = serialNumber%31 + 1;
        }
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }
 
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
 
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public long getSequence(long id) {
        return id & ~(-1L << sequenceBits);
    }

    public long getWorkerId(long id) {
        return id >> workerIdShift & ~(-1L << workerIdBits);
    }

    public long getDataCenterId(long id) {
        return id >> datacenterIdShift & ~(-1L << datacenterIdBits);
    }

    public long getGenerateDateTime(long id) {
        return (id >> timestampLeftShift & ~(-1L << 41L)) + twepoch;
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator();
        snowflakeIdGenerator.setWorkerId(32L);
        for (int i = 1; i < 1000; i++) {
            System.out.println(snowflakeIdGenerator.nextId());
        }
        //System.out.println(snowflakeIdGenerator.getWorkerId(451263489982394368L));
    }

    public String generaMessageId() {
        return ChatConstants.QUESTION_ID_PRE + nextId();
    }

    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }
}
