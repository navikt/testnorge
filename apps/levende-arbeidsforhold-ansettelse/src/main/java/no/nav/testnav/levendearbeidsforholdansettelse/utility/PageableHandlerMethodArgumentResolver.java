package no.nav.testnav.levendearbeidsforholdansettelse.utility;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;

public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final Integer MAX_SIZE = 50;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter methodParameter,
                                        BindingContext bindingContext,
                                        ServerWebExchange serverWebExchange) {

        var pageValues = serverWebExchange.getRequest()
                .getQueryParams()
                .getOrDefault("page", List.of(DEFAULT_PAGE));
        var sizeValues = serverWebExchange.getRequest()
                .getQueryParams()
                .getOrDefault("size", List.of(DEFAULT_SIZE));

        var page = pageValues.getFirst();

        var sortParam = serverWebExchange.getRequest()
                .getQueryParams().getFirst("sort");

        var sort = Sort.unsorted();

        if (nonNull(sortParam)) {
            var parts = sortParam.split(",");
            if (parts.length == 2) {
                var property = parts[0];
                var direction = Sort.Direction.fromString(parts[1]);
                sort = Sort.by(direction, property);
            }
        }

        return Mono.just(
                PageRequest.of(
                        Integer.parseInt(page),
                        Math.min(Integer.parseInt(sizeValues.getFirst()),
                                MAX_SIZE), sort
                )
        );
    }
}