package com.emt.dms1.Repository;



import com.emt.dms1.Models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<UserModel, Long> {


    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String email);

}
