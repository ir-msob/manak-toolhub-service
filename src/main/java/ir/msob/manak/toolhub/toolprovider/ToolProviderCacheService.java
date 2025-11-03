package ir.msob.manak.toolhub.toolprovider;

import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.domain.model.toolhub.dto.ToolDto;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ToolProviderCacheService {
    @Getter
    private Flux<ToolProviderDto> toolProviders;

    public synchronized void setToolProviders(Flux<ToolProviderDto> toolProviders) {
        this.toolProviders = toolProviders;
    }

    public synchronized Mono<ToolProviderDto> getByToolId(String toolId) {
        return getToolProviders()
                .filterWhen(provider ->
                        Flux.fromIterable(provider.getTools())
                                .any(tool -> tool.getKey().equalsIgnoreCase(toolId))
                )
                .next();
    }

    public Flux<ToolDto> getStream(User user) {
        return getToolProviders()
                .flatMapIterable(ToolProvider::getTools)
                .map(td -> ToolDto.builder()
                        .toolId(td.getKey())
                        .description(td.getDescription())
                        .inputSchema(td.getInputSchema().getParams())
                        .outputSchema(td.getOutputSchema().getRes())
                        .errorSchema(td.getOutputSchema().getError())
                        .version(td.getVersion())
                        .build());
    }
}
