package com.osio.apigatewayserver.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

//    private static final List<String> excludeUris = List.of(
//            "/api/user/signup", "/api/user/signin"
//    );

    @Data
    public static class Config {
        /*
        변수 설정은 application.yml 과 연동
        apply 메소드에서 미리 설정한 매개변수를 사용하고 싶다면 application.yml 에서 args 로 설정 후
        해당 static class 에 변수로 선언해서 사용할 수 있다.
         */
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage : {}", config.getBaseMessage());

            if (config.isPreLogger()) {
                log.info("Global Filter Start : request id -> {}", request.getId());
            }

            // 인증을 거치지 않을 ExcludeURL 에 포함되는지 판단
//            String path = request.getURI().getPath();
//            for (String excludeUri : excludeUris) {
//                if (excludeUri.equals(path)) {
//                    return chain.filter(exchange);
//                }
//            }

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter End : response code -> {}", response.getStatusCode());
                }
            }));
        };
    }

//    private Mono<Void> failAuthenticationResponse(ServerWebExchange exchange) {
//        final ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
}
