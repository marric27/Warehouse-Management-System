package com.relatech.warehouse_management_system.goodsIn.states;

import com.relatech.warehouse_management_system.common.util.State;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GrnItemStateHandlerResolver {

    private final Map<State, GrnItemStateHandler> handlersByState;

    // Spring inietta qui tutte le classi che implementano IGrnItemStateHandler
    public GrnItemStateHandlerResolver(List<GrnItemStateHandler> handlers) {
        this.handlersByState = handlers.stream()
                .collect(Collectors.toMap(
                        GrnItemStateHandler::getState,
                        Function.identity()
                ));
    }

    public GrnItemStateHandler resolve(State state) {
        if (state == null) {
            state = State.OPEN;
        }
        return Optional.ofNullable(handlersByState.get(state))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No GRN item state handler registered for state: " + state
                ));
    }
}
