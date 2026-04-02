package com.epotrade.domain.port.out;

import com.epotrade.domain.model.Position;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRepository {
    Position save(Position position);
    Optional<Position> findById(UUID id);
    List<Position> findByUserId(UUID userId);
    Optional<Position> findByUserIdAndSymbol(UUID userId, String symbol);
    void deleteById(UUID id);
}
