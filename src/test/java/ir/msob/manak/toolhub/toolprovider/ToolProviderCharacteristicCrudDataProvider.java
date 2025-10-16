package ir.msob.manak.toolhub.toolprovider;

import ir.msob.manak.core.test.jima.crud.base.childdomain.characteristic.BaseCharacteristicCrudDataProvider;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import org.springframework.stereotype.Component;

@Component
public class ToolProviderCharacteristicCrudDataProvider extends BaseCharacteristicCrudDataProvider<ToolProviderDto, ToolProviderService> {
    public ToolProviderCharacteristicCrudDataProvider(ToolProviderService childService) {
        super(childService);
    }
}
