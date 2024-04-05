package com.encore.event.coupon.adapter.out.redis;

import com.encore.event.config.RedisOperation;
import com.encore.event.coupon.application.port.in.ApplyForLimitedCouponIssueCommend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class LimitedCouponIssueOperation implements RedisOperation<ApplyForLimitedCouponIssueCommend> {

    @Override
    public Long count(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        //key 가져와서
        String key = commend.getKey();
        //key의 카운트를 셈
        Long size = operations.opsForSet().size(key);
        log.debug("[LimitedCouponIssueOperation] [count] key ::: {}, size ::: {}", key, size);
        return size;
    }

    @Override
    public Long add(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = commend.getKey();
        String value = this.generateValue(commend);
        Long result = operations.opsForSet().add(key, value);
        log.debug(
                "[LimitedCouponIssueOperation] [add] key ::: {}, value ::: {}, result ::: {}", key, value, result);
        return result;
    }

    @Override
    public Long remove(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = commend.getKey();
        String value = this.generateValue(commend);
        Long result = operations.opsForSet().remove(key, value);
        log.debug(
                "[LimitedCouponIssueOperation] [remove] key ::: {}, value ::: {}, result ::: {}",
                key,
                value,
                result);
        return result;
    }

    @Override
    public Boolean delete(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        String key = commend.getKey();
        Boolean result = operations.delete(key);
        log.debug("[LimitedCouponIssueOperation] [delete] key ::: {}, result ::: {}", key, result);
        return result;
    }

    @Override
    public Boolean expire(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend, Duration duration) {
        String key = commend.getKey();
        Boolean result = operations.expire(key, duration);
        log.debug(
                "[LimitedCouponIssueOperation] [expire] key ::: {}, expire ::: {}, result ::: {}",
                key,
                duration,
                result);
        return result;
    }

    @Override
    public String generateValue(ApplyForLimitedCouponIssueCommend commend) {
        return String.valueOf(commend.getUserId());
    }

    @Override
    public void execute(RedisOperations<String, Object> operations, ApplyForLimitedCouponIssueCommend commend) {
        this.count(operations, commend);
        this.add(operations, commend);
    }
}
