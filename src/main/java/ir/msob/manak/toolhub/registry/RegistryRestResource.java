package ir.msob.manak.toolhub.registry;

import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import ir.msob.jima.core.commons.operation.OperationsStatus;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.dto.ToolDto;
import ir.msob.manak.domain.model.toolhub.toolprovider.ToolProviderDto;
import ir.msob.manak.toolhub.gateway.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping(RegistryRestResource.BASE_URI)
@RequiredArgsConstructor
public class RegistryRestResource {
    public static final String BASE_URI = "/api/v1/registry";
    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);

    private final RegistryService service;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Mono<ToolProviderDto>> save(@RequestBody ToolProviderDto dto, Principal principal) {
        logger.info("REST request to create new provider, dto : {}", dto);
        User user = userService.getUser(principal);
        Mono<ToolProviderDto> res = service.save(dto, user);
        return ResponseEntity.status(OperationsStatus.SAVE).body(res);
    }

    @GetMapping
    public ResponseEntity<Flux<ToolDto>> getStream(Principal principal) {
        logger.info("REST request to get stream of provider");
        User user = userService.getUser(principal);
        Flux<ToolDto> res = service.getStream(user);
        return ResponseEntity.status(OperationsStatus.GET_STREAM).body(res);
    }
}
