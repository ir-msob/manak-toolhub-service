package ir.msob.manak.toolhub.toolprovider;

import ir.msob.jima.core.commons.resource.BaseResource;
import ir.msob.jima.core.test.CoreTestData;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.test.jima.crud.restful.childdomain.BaseCharacteristicCrudRestResourceTest;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderCriteria;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderTypeReference;
import ir.msob.manak.toolhub.Application;
import ir.msob.manak.toolhub.ContainerConfiguration;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureWebTestClient
@SpringBootTest(classes = {Application.class, ContainerConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@CommonsLog
public class ToolProviderCharacteristicRestResourceIT
        extends BaseCharacteristicCrudRestResourceTest<ToolProvider, ToolProviderDto, ToolProviderCriteria, ToolProviderRepository, ToolProviderService, ToolProviderDataProvider, ToolProviderService, ToolProviderCharacteristicCrudDataProvider>
        implements ToolProviderTypeReference {

    @SneakyThrows
    @BeforeAll
    public static void beforeAll() {
        CoreTestData.init(new ObjectId(), new ObjectId());
    }

    @SneakyThrows
    @BeforeEach
    public void beforeEach() {
        getDataProvider().cleanups();
        ToolProviderDataProvider.createMandatoryNewDto();
        ToolProviderDataProvider.createNewDto();
        ToolProviderCharacteristicCrudDataProvider.createNewChild();
    }


    @Override
    public String getBaseUri() {
        return ToolProviderRestResource.BASE_URI;
    }

    @Override
    public Class<? extends BaseResource<String, User>> getResourceClass() {
        return ToolProviderCharacteristicRestResource.class;
    }
}
