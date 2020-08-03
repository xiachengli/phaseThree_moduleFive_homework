package com.xcl.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 统一登录认证
 */
@Component //注册到容器中
public class TokenFilter implements GlobalFilter , Ordered {
    /**
     * 过滤的业务逻辑
     * @param exchange 封装了request和response
     * @param chain 网关过滤器链（包括全局过滤器和单路由过滤器）
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //登录/注册/获取验证码接口放行
        if (path.startsWith("/api/user/login") || path.startsWith("/api/user/register") || path.startsWith("/api/code")) {
            return chain.filter(exchange);
        }

        //拿到cookie信息
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
        if (cookie == null) {
            //请先登录
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            String data = "please login";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            return response.writeWith(Mono.just(wrap));
        }
        //放行
        return chain.filter(exchange);
    }

    /**
     * 返回值代表当前过滤器的顺序，值越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
