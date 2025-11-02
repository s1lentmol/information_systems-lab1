package com.example.islab1.repo;

import com.example.islab1.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    // Фильтрация
    Page<Person> findByName(String name, Pageable pageable);
    Page<Person> findByEyeColor(Color color, Pageable pageable);
    Page<Person> findByHairColor(Color color, Pageable pageable);
    Page<Person> findByNationality(Country country, Pageable pageable);

    // Спец-операции
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Person p WHERE p.nationality = :nat")
    int deleteAllByNationality(@Param("nat") Country nationality);

    List<Person> findByHeightGreaterThan(int height);
    long countByHairColor(Color hairColor);
    long countByHairColorAndLocation_Id(Color hairColor, Long locationId);
}
