package com.expotrade.domain.port.in;

import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderType;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlaceOrderUseCaseTest {

    @Test
    void exposesCommandBasedPlaceOrderContract() throws NoSuchMethodException {
        Method placeOrder = PlaceOrderUseCase.class.getDeclaredMethod(
                "placeOrder",
                PlaceOrderUseCase.PlaceOrderCommand.class
        );

        assertThat(placeOrder.getReturnType()).isEqualTo(Mono.class);
    }

    @Test
    void exposesPlaceOrderCommandWithDomainAndSecurityFields() {
        assertThat(PlaceOrderUseCase.PlaceOrderCommand.class.getRecordComponents())
                .extracting(component -> component.getName() + ":" + component.getType().getSimpleName())
                .containsExactly(
                        "symbol:String",
                        "side:OrderSide",
                        "type:OrderType",
                        "quantity:BigDecimal",
                        "price:BigDecimal",
                        "stopLoss:BigDecimal",
                        "takeProfit:BigDecimal",
                        "brokerType:BrokerType",
                        "strategyId:String",
                        "userId:UUID"
                );
    }

    @Test
    void placeOrderCommandIsAValueCarrier() {
        assertThat(new PlaceOrderUseCase.PlaceOrderCommand(
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                BigDecimal.ONE,
                BigDecimal.TEN,
                null,
                null,
                BrokerType.IBKR,
                "strategy-1",
                UUID.randomUUID()
        )).isInstanceOf(Record.class);
    }
}
