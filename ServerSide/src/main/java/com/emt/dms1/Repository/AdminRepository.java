package com.emt.dms1.Repository;


import com.emt.dms1.Models.AdminModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<AdminModel, Long> {


    @Query("SELECT a.welcomeMessage FROM AdminModel a")
    String findSingleWelcomeMessage();




}
