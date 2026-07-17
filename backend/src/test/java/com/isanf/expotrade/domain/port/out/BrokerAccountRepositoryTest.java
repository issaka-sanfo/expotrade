package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BrokerAccountRepositoryTest {

    @Test
    void exposesBrokerAccountPersistenceQueries() throws NoSuchMethodException {
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("save", BrokerAccount.class).getReturnType()).isEqualTo(BrokerAccount.class);
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("findById", UUID.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("findByUserId", UUID.class).getReturnType()).isEqualTo(List.class);
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("findByUserIdAndBrokerType", UUID.class, BrokerType.class).getReturnType()).isEqualTo(Optional.class);
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("findByUserIdAndBrokerTypeAndStatus", UUID.class, BrokerType.class, BrokerAccountStatus.class).getReturnType())
                .isEqualTo(Optional.class);
        assertThat(BrokerAccountRepository.class.getDeclaredMethod("deleteById", UUID.class).getReturnType()).isEqualTo(void.class);
    }
}
