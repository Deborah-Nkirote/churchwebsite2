package com.emt.dms1.Repository;



import com.emt.dms1.Models.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
@Repository
public interface EventRepository   extends JpaRepository<Events, Long> {
   // List<Events> findAllByDateAfter(LocalDate date);
}
