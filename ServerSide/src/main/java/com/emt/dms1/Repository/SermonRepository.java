package com.emt.dms1.Repository;


import com.emt.dms1.Models.Sermons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SermonRepository  extends JpaRepository<Sermons, Long> {
    //void updateLiveStreamUrl(String url, String sermonId);
}
