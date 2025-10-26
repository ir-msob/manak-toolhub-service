package ir.msob.manak.toolhub.toolprovider;

import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToolProviderCacheStartup {
    private final ToolProviderCacheService toolProviderCacheService;
    private final ToolProviderService toolProviderService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        toolProviderCacheService.setToolProviders(toolProviderService.getStream(new ToolProviderCriteria(), userService.getSystemUser()));
    }
}
