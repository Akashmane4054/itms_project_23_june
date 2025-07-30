package com.itms.core.feignclient;

import org.springframework.stereotype.Component;

//@FeignClient(name = "user-service", url = "http://localhost:9002", fallbackFactory = userServiceFallbackFactory.class)
@Component
public interface userServiceFeignclient {

}
