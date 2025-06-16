package com.antonio.skybase.repositories;

import com.antonio.skybase.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    boolean existsByCode(String code);
    Country findByCode(String code);
}
