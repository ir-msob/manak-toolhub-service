package ir.msob.manak.toolhub.toolprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.filter.Filter;
import ir.msob.jima.core.commons.id.BaseIdService;
import ir.msob.jima.core.commons.operation.BaseBeforeAfterDomainOperation;
import ir.msob.jima.crud.service.domain.BeforeAfterComponent;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.crud.base.childdomain.ChildDomainCrudService;
import ir.msob.manak.core.service.jima.crud.base.domain.DomainCrudService;
import ir.msob.manak.core.service.jima.service.IdService;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;

@Service
public class ToolProviderService extends DomainCrudService<ToolProvider, ToolProviderDto, ToolProviderCriteria, ToolProviderRepository>
        implements ChildDomainCrudService<ToolProviderDto> {

    private final ModelMapper modelMapper;
    private final IdService idService;
    private final ToolProviderCacheService toolProviderCacheService;

    protected ToolProviderService(BeforeAfterComponent beforeAfterComponent, ObjectMapper objectMapper, ToolProviderRepository repository, ModelMapper modelMapper, IdService idService, ToolProviderCacheService toolProviderCacheService) {
        super(beforeAfterComponent, objectMapper, repository);
        this.modelMapper = modelMapper;
        this.idService = idService;
        this.toolProviderCacheService = toolProviderCacheService;
    }

    @Override
    public ToolProviderDto toDto(ToolProvider domain, User user) {
        return modelMapper.map(domain, ToolProviderDto.class);
    }

    @Override
    public ToolProvider toDomain(ToolProviderDto dto, User user) {
        return dto;
    }

    @Override
    public Collection<BaseBeforeAfterDomainOperation<String, User, ToolProviderDto, ToolProviderCriteria>> getBeforeAfterDomainOperations() {
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public Mono<ToolProviderDto> getDto(String id, User user) {
        return super.getOne(id, user);
    }

    @Transactional
    @Override
    public Mono<ToolProviderDto> updateDto(String id, @Valid ToolProviderDto dto, User user) {
        return super.update(id, dto, user);
    }

    @Override
    public BaseIdService getIdService() {
        return idService;
    }

    @Override
    public Mono<Void> postSave(ToolProviderDto dto, ToolProvider savedDomain, User user) {
        toolProviderCacheService.setToolProviders(getStream(new ToolProviderCriteria(), user));
        return super.postSave(dto, savedDomain, user);
    }

    @Override
    public Mono<Void> postUpdate(ToolProviderDto dto, ToolProvider updatedDomain, User user) {
        toolProviderCacheService.setToolProviders(getStream(new ToolProviderCriteria(), user));
        return super.postUpdate(dto, updatedDomain, user);
    }

    @Override
    public Mono<Void> postDelete(ToolProviderDto dto, ToolProviderCriteria criteria, User user) {
        toolProviderCacheService.setToolProviders(getStream(new ToolProviderCriteria(), user));
        return super.postDelete(dto, criteria, user);
    }

    @Override
    public Mono<Void> preSave(ToolProviderDto dto, User user) {
        return deleteIfExists(dto, user)
                .then(super.preSave(dto, user));
    }

    private Mono<Void> deleteIfExists(ToolProviderDto dto, User user) {
        ToolProviderCriteria criteria = ToolProviderCriteria.builder()
                .name(Filter.eq(dto.getName()))
                .build();
        return this.deleteMany(criteria, user)
                .then();
    }
}
