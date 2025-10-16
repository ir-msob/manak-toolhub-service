package ir.msob.manak.toolhub.registry;

import ir.msob.jima.core.commons.filter.Filter;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.dto.ToolDto;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.toolhub.toolprovider.ToolProviderCacheService;
import ir.msob.manak.toolhub.toolprovider.ToolProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RegistryService {
    private final ToolProviderService toolProviderService;
    private final ToolProviderCacheService toolProviderCacheService;

    public Mono<ToolProviderDto> save(ToolProviderDto dto, User user) {
        ToolProviderCriteria toolProviderCriteria = ToolProviderCriteria.builder()
                .name(Filter.eq(dto.getName()))
                .build();
        return toolProviderService.delete(toolProviderCriteria, user)
                .then(toolProviderService.save(dto, user));
    }

    public Flux<ToolDto> getStream(User user) {
        return toolProviderCacheService.getToolProviders()
                .flatMapIterable(ToolProvider::getTools)
                .map(td -> ToolDto.builder()
                        .name(td.getName())
                        .description(td.getDescription())
                        .inputSchema(td.getInputSchema())
                        .outputSchema(td.getOutputSchema())
                        .version(td.getVersion())
                        .build());
    }
}
