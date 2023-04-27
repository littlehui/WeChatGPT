package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 18:13
 */
@Repository
public class UserConfigCacheRepository extends AbstractBaseRedisDAO<UserConfigCache> {

    public UserConfigCacheRepository() {
        this.zone = "User:Config:";
    }

    public void saveUserConfig(UserConfigCache userConfigCache) {
        this.save(userConfigCache.getUserCode(), userConfigCache);
    }

    public UserConfigCache getUserConfig(String userCode) {
        return get(userCode);
    }
}
