package at.fhtw.usageservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsageDataRepository extends JpaRepository<UsageData, Long> {
    Optional<UsageData> findByHour(LocalDateTime hour);
}
