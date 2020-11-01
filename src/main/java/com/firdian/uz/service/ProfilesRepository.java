package com.firdian.uz.service;

import com.firdian.uz.controllers.response.Profiles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilesRepository extends CrudRepository<Profiles, Integer> {

    Profiles findByName(String filename);
    
}
