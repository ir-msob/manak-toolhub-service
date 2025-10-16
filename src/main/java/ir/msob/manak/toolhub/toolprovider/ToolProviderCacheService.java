package ir.msob.manak.toolhub.toolprovider;

import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ToolProviderCacheService {
    private final ToolProviderService toolProviderService;
    private final UserService userService;
    @Getter
    private Flux<ToolProviderDto> toolProviders;

    public synchronized void updateCache(User user) {
        toolProviders = null;
        toolProviders = toolProviderService.getStream(new ToolProviderCriteria(), user);
    }

    public synchronized Mono<ToolProviderDto> getByToolId(String toolId) {
        return getToolProviders()
                .filterWhen(provider ->
                        Flux.fromIterable(provider.getTools())
                                .any(tool -> tool.getName().equalsIgnoreCase(toolId))
                )
                .next();
    }

    @PostConstruct
    public void init() {
        this.updateCache(userService.getSystemUser());
    }
}
