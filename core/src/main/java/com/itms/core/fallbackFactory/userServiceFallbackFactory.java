//package com.itms.core.fallbackFactory;
//
//import org.springframework.stereotype.Component;
//
//import com.itms.core.fallback.userServiceFallback;
//import com.itms.core.feignclient.userServiceFeignclient;
//
//@Component
//public class userServiceFallbackFactory implements FallbackFactory<userServiceFeignclient> {
//
//	@Override
//	public userServiceFeignclient create(Throwable cause) {
//		return new userServiceFallback(cause);
//	}
//
//}
