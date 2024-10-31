package com.emt.dms1.Repository;



import com.emt.dms1.Models.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    @Query(value="Select * from Event where eventName =: eventName", nativeQuery = true)
    Optional<Events> findByName(@Param("eventName") String eventName);
}
