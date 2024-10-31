package com.emt.dms1.Repository;

import com.emt.dms1.Models.churchLeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChurchLeadersRepository extends JpaRepository<churchLeaders,Long> {

    @Query(value="Select * from leaders where name=: leadersName", nativeQuery = true)
    Optional<churchLeaders> findByName( @Param("leadersName")String name);
}
