package com.encore.thecatch.receivecoupon.service;

import com.encore.thecatch.common.CatchException;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.coupon.domain.Coupon;
import com.encore.thecatch.coupon.domain.CouponStatus;
import com.encore.thecatch.coupon.repository.CouponRepository;
import com.encore.thecatch.receivecoupon.domain.ReceiveCoupon;
import com.encore.thecatch.receivecoupon.dto.KafkaLimitedCoupon;
import com.encore.thecatch.receivecoupon.repository.ReceiveCouponRepository;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReceiveCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ReceiveCouponRepository receiveCouponRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void limitedCouponReceive(String data) throws JsonProcessingException {

        KafkaLimitedCoupon limitedCoupon = objectMapper.readValue(data, KafkaLimitedCoupon.class);

        Optional<User> user = userRepository.findById(limitedCoupon.getUserId());
        if(user.isPresent()) {
            Optional<Coupon> coupon = couponRepository.findById(limitedCoupon.getCouponId());

            if(coupon.isPresent()) {
                List<ReceiveCoupon> receiveCouponList = receiveCouponRepository.findByCouponIdAndUserId(coupon.get().getId(),user.get().getId());

                if(receiveCouponList.isEmpty()) {
                    ReceiveCoupon receiveCoupon = ReceiveCoupon.builder().coupon(coupon.get()).user(user.get()).couponStatus(CouponStatus.ISSUANCE).build();
                    receiveCouponRepository.save(receiveCoupon);
                }
            }

        }

    }


}
