package ir.msob.manak.toolhub.toolprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.msob.jima.core.commons.client.BaseAsyncClient;
import ir.msob.jima.core.commons.operation.ConditionalOnOperation;
import ir.msob.jima.core.commons.resource.Resource;
import ir.msob.jima.core.commons.shared.ResourceType;
import ir.msob.jima.crud.api.kafka.client.ChannelUtil;
import ir.msob.manak.core.service.jima.crud.kafka.domain.service.DomainCrudKafkaListener;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderTypeReference;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import static ir.msob.jima.core.commons.operation.Operations.*;

@Component
@ConditionalOnOperation(operations = {SAVE, UPDATE_BY_ID, DELETE_BY_ID})
@Resource(value = ToolProvider.DOMAIN_NAME_WITH_HYPHEN, type = ResourceType.KAFKA)
public class ToolProviderKafkaListener
        extends DomainCrudKafkaListener<ToolProvider, ToolProviderDto, ToolProviderCriteria, ToolProviderRepository, ToolProviderService>
        implements ToolProviderTypeReference {
    public static final String BASE_URI = ChannelUtil.getBaseChannel(ToolProviderDto.class);

    protected ToolProviderKafkaListener(UserService userService, ToolProviderService service, ObjectMapper objectMapper, ConsumerFactory<String, String> consumerFactory, BaseAsyncClient asyncClient) {
        super(userService, service, objectMapper, consumerFactory, asyncClient);
    }

}
