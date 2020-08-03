package com.xcl.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class IpMonitorFilter implements GlobalFilter, Ordered {

    /**
     * 总计需要记录近一分钟的数据
     */
    private static final List<RequestStrategy> STRATEGIES = new ArrayList<>();
    private static final Map<String, MethodIpPercent> collections = new ConcurrentHashMap<>();

    static {
//      STRATEGIES.add(new RequestStrategy(30, 20));
        STRATEGIES.add(new RequestStrategy(10, 2));

        STRATEGIES.forEach(requestStrategy -> {
            collections.put(requestStrategy.getKey(), MethodIpPercent.create(requestStrategy.getSecond(), requestStrategy.getTimes()));
            System.out.println("init requestStrategy:" + requestStrategy);
        });
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        // 思路：获取客户端ip，判断是否在黑名单中，在的话就拒绝访问，不在的话就放行
        // 从上下文中取出request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 从request对象中获取客户端ip
        String clientIp = request.getRemoteAddress().getHostString();
        String requestPath = request.getPath().toString();

        if(requestPath.startsWith("/api/user/register/")) {
            String methodName = generateOperateName(clientIp, requestPath);

            AtomicBoolean result = new AtomicBoolean(false);

            // 对于防刷策略去收集数据
            collections.forEach((key, methodIpPercent) -> {
                boolean limited = methodIpPercent.isLimited(methodName);
                if (limited) {
                    result.set(true);
                    return;
                }
                methodIpPercent.increment(methodName, System.currentTimeMillis() - startTime);
            });
            if (result.get()) {
                String data = "访问过于频繁，请稍后再试";
                DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
                return response.writeWith(Mono.just(wrap));
            }
        }

        // 合法请求，放行，执行后续的过滤器
        return chain.filter(exchange);
    }

    /**
     * 生成操作名称
     *
     * @return
     */
    private String generateOperateName(String clientIp, String requestPath) {
        return clientIp + "_" + requestPath;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static class RequestStrategy {
        private Integer second;
        private Integer times;

        public Integer getSecond() {
            return second;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }

        public Integer getTimes() {
            return times;
        }

        public void setTimes(Integer times) {
            this.times = times;
        }

        public RequestStrategy(Integer second, Integer times) {
            this.second = second;
            this.times = times;
        }

        public String getKey() {
            return second + "_" + times;
        }
    }
}
