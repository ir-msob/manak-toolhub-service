package ir.msob.manak.toolhub.gateway;

import ir.msob.jima.core.commons.logger.Logger;
import ir.msob.jima.core.commons.logger.LoggerFactory;
import ir.msob.jima.core.commons.operation.OperationsStatus;
import ir.msob.manak.core.model.jima.security.User;
import ir.msob.manak.core.service.jima.security.UserService;
import ir.msob.manak.domain.model.toolhub.dto.InvokeRequest;
import ir.msob.manak.domain.model.toolhub.dto.InvokeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping(GatewayRestResource.BASE_URI)
@RequiredArgsConstructor
public class GatewayRestResource {
    public static final String BASE_URI = "/api/v1/gateway";
    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);

    private final GatewayService service;
    private final UserService userService;

    @PostMapping("invoke")
    public ResponseEntity<Mono<InvokeResponse>> invoke(@RequestBody InvokeRequest dto, Principal principal) {
        logger.info("Invoke request received with tool {}", dto.getToolId());
        User user = userService.getUser(principal);
        Mono<InvokeResponse> res = service.invoke(dto, user);
        return ResponseEntity.status(OperationsStatus.SAVE).body(res);
    }
}
