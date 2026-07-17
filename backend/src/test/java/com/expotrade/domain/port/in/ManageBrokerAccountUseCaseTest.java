package com.expotrade.domain.port.in;

import com.expotrade.domain.model.BrokerAccount;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ManageBrokerAccountUseCaseTest {

    @Test
    void exposesLinkBrokerAccountCommandWithSecurityContext() {
        assertThat(ManageBrokerAccountUseCase.LinkBrokerAccountCommand.class.getRecordComponents())
                .extracting(component -> component.getName() + ":" + component.getType().getSimpleName())
                .containsExactly(
                        "userId:UUID",
                        "brokerType:BrokerType",
                        "accountId:String",
                        "apiKey:String",
                        "apiSecret:String",
                        "accessToken:String"
                );
    }

    @Test
    void exposesUpdateBrokerAccountCommandWithSecurityContext() {
        assertThat(ManageBrokerAccountUseCase.UpdateBrokerAccountCommand.class.getRecordComponents())
                .extracting(component -> component.getName() + ":" + component.getType().getSimpleName())
                .containsExactly(
                        "accountId:UUID",
                        "userId:UUID",
                        "apiKey:String",
                        "apiSecret:String",
                        "accessToken:String"
                );
    }

    @Test
    void exposesBrokerAccountLifecycleOperations() throws NoSuchMethodException {
        assertThat(ManageBrokerAccountUseCase.class.getDeclaredMethod("linkAccount", ManageBrokerAccountUseCase.LinkBrokerAccountCommand.class).getReturnType())
                .isEqualTo(BrokerAccount.class);
        assertThat(ManageBrokerAccountUseCase.class.getDeclaredMethod("updateAccount", ManageBrokerAccountUseCase.UpdateBrokerAccountCommand.class).getReturnType())
                .isEqualTo(BrokerAccount.class);
        assertThat(ManageBrokerAccountUseCase.class.getDeclaredMethod("unlinkAccount", UUID.class, UUID.class).getReturnType())
                .isEqualTo(void.class);
        assertThat(ManageBrokerAccountUseCase.class.getDeclaredMethod("getAccounts", UUID.class).getReturnType())
                .isEqualTo(List.class);
        assertThat(ManageBrokerAccountUseCase.class.getDeclaredMethod("verifyAccount", UUID.class, UUID.class).getReturnType())
                .isEqualTo(BrokerAccount.class);
    }
}
