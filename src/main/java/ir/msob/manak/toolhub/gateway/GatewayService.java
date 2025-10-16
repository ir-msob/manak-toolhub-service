package ir.msob.manak.toolhub.gateway;

import ir.msob.jima.core.beans.properties.JimaProperties;
import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import ir.msob.jima.core.commons.methodstats.MethodStats;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.domain.model.toolhub.dto.InvokeRequest;
import ir.msob.manak.domain.model.toolhub.dto.InvokeResponse;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.toolhub.toolprovider.ToolProviderCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);
    private final JimaProperties jimaProperties;

    private final ToolProviderCacheService toolProviderCacheService;
    private final WebClient webClient;

    /**
     * Main entry point — always returns InvokeResponse (never throws).
     */
    public Mono<InvokeResponse> invoke(InvokeRequest dto, User user) {
        String toolId = dto.getToolId();

        logger.info("Invoke started → tool [{}]", toolId);

        return toolProviderCacheService.getByToolId(toolId)
                .flatMap(provider -> request(provider, dto))
                .switchIfEmpty(buildError(toolId, "Tool not found: " + toolId))
                .onErrorResume(e -> buildError(toolId, e))
                .doOnSuccess(resp -> logResult(toolId, resp));
    }

    /**
     * Sends a reactive HTTP request to the tool provider.
     */
    @MethodStats
    private Mono<InvokeResponse> request(ToolProviderDto provider, InvokeRequest request) {
        String toolId = request.getToolId();
        String url = normalizeUrl(provider.getBaseUrl(), provider.getEndpoint());

        logger.info("Request dispatch → tool [{}], provider [{}], url [{}]", toolId, provider.getName(), url);

        return webClient.post()
                .uri(url)
                .bodyValue(request.getParams())
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(jimaProperties.getClient().getRequestTimeout())
                .map(result -> buildSuccess(toolId, result))
                .onErrorResume(e -> buildError(toolId, e));
    }

    // =========================
    // 🔧 Helper Methods
    // =========================

    private String normalizeUrl(String base, String endpoint) {
        return base.endsWith("/") ?
                base + endpoint.replaceFirst("^/", "") :
                base + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
    }

    private InvokeResponse buildSuccess(String toolId, Object result) {
        return InvokeResponse.builder()
                .toolId(toolId)
                .result(result)
                .build();
    }

    private Mono<InvokeResponse> buildError(String toolId, Throwable e) {
        String msg = resolveErrorMessage(e);
        logger.error(e, "Error invoking tool [{}]: {}", toolId, msg);
        return buildError(toolId, msg);
    }

    private Mono<InvokeResponse> buildError(String toolId, String message) {
        logger.warn("Returning error for tool [{}]: {}", toolId, message);
        return Mono.just(InvokeResponse.builder()
                .toolId(toolId)
                .error(message)
                .build());
    }

    private void logResult(String toolId, InvokeResponse response) {
        if (response.getError() == null)
            logger.debug("Invocation completed successfully for tool [{}]", toolId);
        else
            logger.warn("Invocation completed with error for tool [{}]: {}", toolId, response.getError());
    }

    private String resolveErrorMessage(Throwable e) {
        return switch (e) {
            case WebClientResponseException wcre ->
                    String.format("HTTP %s: %s", wcre.getStatusCode(), wcre.getResponseBodyAsString());
            case TimeoutException ignored ->
                    "Request timed out after " + jimaProperties.getClient().getRequestTimeout().toSeconds() + " seconds";
            case IllegalArgumentException iae -> "Invalid request: " + iae.getMessage();
            default -> e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        };
    }
}
