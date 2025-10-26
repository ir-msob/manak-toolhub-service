package ir.msob.manak.toolhub.toolprovider;

import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ToolProviderCacheService {
    @Getter
    @Setter
    private Flux<ToolProviderDto> toolProviders;

    public synchronized Mono<ToolProviderDto> getByToolId(String toolId) {
        return getToolProviders()
                .filterWhen(provider ->
                        Flux.fromIterable(provider.getTools())
                                .any(tool -> tool.getName().equalsIgnoreCase(toolId))
                )
                .next();
    }


}
