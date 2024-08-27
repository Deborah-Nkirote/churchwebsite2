package com.emt.dms1.Repository;

import com.emt.dms1.Models.churchLeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChurchLeadersRepository extends JpaRepository<churchLeaders,Long> {
}
