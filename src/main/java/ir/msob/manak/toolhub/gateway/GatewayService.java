package ir.msob.manak.toolhub.gateway;

import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import ir.msob.jima.core.commons.methodstats.MethodStats;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.domain.model.toolhub.dto.InvokeRequest;
import ir.msob.manak.domain.model.toolhub.dto.InvokeResponse;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.domain.service.toolhub.util.ToolExecutorUtil;
import ir.msob.manak.toolhub.toolprovider.ToolProviderCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);

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

        logger.info("Request dispatch → tool [{}], provider [{}]", toolId, provider.getName());

        return webClient.post()
                .uri(uriBuilder -> uriBuilder(uriBuilder, provider))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InvokeResponse.class)
                .onErrorResume(e -> buildError(toolId, e));
    }

    // =========================
    // 🔧 Helper Methods
    // =========================

    private URI uriBuilder(org.springframework.web.util.UriBuilder builder, ToolProviderDto provider) {
        return builder.host(provider.getServiceName()).path(provider.getEndpoint()).build();
    }

    private Mono<InvokeResponse> buildError(String toolId, Throwable e) {
        String errorMsg = ToolExecutorUtil.resolveErrorMessage(e);
        logger.error(e, "Error invoking tool [{}]: {}", toolId, errorMsg);
        return buildError(toolId, errorMsg);
    }

    private Mono<InvokeResponse> buildError(String toolId, String message) {
        String formattedMessage = ToolExecutorUtil.buildErrorResponse(toolId, message);
        logger.warn("Returning error for tool [{}]: {}", toolId, formattedMessage);
        return Mono.just(InvokeResponse.builder()
                .toolId(toolId)
                .error(formattedMessage)
                .build());
    }

    private void logResult(String toolId, InvokeResponse response) {
        if (response.getError() == null)
            logger.debug("Invocation completed successfully for tool [{}]", toolId);
        else
            logger.warn("Invocation completed with error for tool [{}]: {}", toolId, response.getError());
    }
}
