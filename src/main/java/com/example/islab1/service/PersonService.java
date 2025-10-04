package com.example.islab1.service;

import com.example.islab1.model.*;
import com.example.islab1.repo.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository repo;
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    public PersonService(PersonRepository repo) {
        this.repo = repo;
    }

    public Page<Person> list(String filterField, String filterValue,
                             String sortField, String sortDir, int page, int size) {
        Sort sort = Sort.by((sortField == null || sortField.isBlank())
                ? "id"
                : sortField);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sort);

        if (filterField == null || filterField.isBlank() || filterValue == null || filterValue.isBlank()) {
            return repo.findAll(pageable);
        }

        switch (filterField) {
            case "name":
                return repo.findByName(filterValue, pageable);
            case "eyeColor":
                return repo.findByEyeColor(Color.valueOf(filterValue.toUpperCase()), pageable);
            case "hairColor":
                return repo.findByHairColor(Color.valueOf(filterValue.toUpperCase()), pageable);
            case "nationality":
                return repo.findByNationality(Country.valueOf(filterValue.toUpperCase()), pageable);
            default:
                return repo.findAll(pageable);
        }
    }

    public Optional<Person> get(Integer id) { return repo.findById(id); }

    public Person require(Integer id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Person not found"));
    }

    @Transactional
    public Person create(Person p) {
        if (p.getHeight() < 1) throw new IllegalArgumentException("height must be > 0");
        if (p.getCoordinates() == null || p.getCoordinates().getY() == null)
            throw new IllegalArgumentException("coordinates.y must be not null");
        Person saved = repo.save(p);
        log.info("Created person id={} name={} height={} nationality={}", saved.getId(), saved.getName(),
                saved.getHeight(), saved.getNationality());
        return saved;
    }

    @Transactional
    public Person update(Integer id, Person updated) {
        Person existing = require(id);
        existing.setName(updated.getName());
        existing.setEyeColor(updated.getEyeColor());
        existing.setHairColor(updated.getHairColor());
        existing.setHeight(updated.getHeight());
        existing.setBirthday(updated.getBirthday());
        existing.setNationality(updated.getNationality());

        if (updated.getCoordinates() == null) {
            throw new IllegalArgumentException("coordinates must be provided for update");
        }

        Coordinates existingCoordinates = existing.getCoordinates();
        if (existingCoordinates == null) {
            existingCoordinates = new Coordinates();
            existing.setCoordinates(existingCoordinates);
        }
        Coordinates incomingCoordinates = updated.getCoordinates();
        existingCoordinates.setX(incomingCoordinates.getX());
        existingCoordinates.setY(incomingCoordinates.getY());

        Location incomingLocation = updated.getLocation();
        if (incomingLocation == null) {
            existing.setLocation(null);
        } else {
            Location existingLocation = existing.getLocation();
            if (existingLocation == null) {
                existingLocation = new Location();
                existing.setLocation(existingLocation);
            }
            existingLocation.setX(incomingLocation.getX());
            existingLocation.setY(incomingLocation.getY());
            existingLocation.setZ(incomingLocation.getZ());
        }

        log.info("Updated person id={} name={} height={} nationality={}", existing.getId(), existing.getName(),
                existing.getHeight(), existing.getNationality());
        return existing;
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
        log.info("Deleted person id={}", id);
    }

    // Операции
    @Transactional
    public int deleteByNationality(Country nationality) {
        int deleted = repo.deleteAllByNationality(nationality);
        log.info("Bulk delete by nationality={} affected={} records", nationality, deleted);
        return deleted;
    }

    public List<Person> findHeightGreaterThan(int height) { return repo.findByHeightGreaterThan(height); }

    public List<Integer> uniqueHeights() {
        return repo.findAll().stream()
                .map(Person::getHeight).distinct().sorted().collect(Collectors.toList());
    }

    public double hairColorShare(Color hairColor) {
        long total = repo.count();
        if (total == 0) return 0.0;
        long count = repo.countByHairColor(hairColor);
        return (count * 100.0) / total;
    }

    public long countHairColorInLocation(Color hairColor, Long locationId) {
        if (locationId == null) return 0;
        return repo.countByHairColorAndLocation_Id(hairColor, locationId);
    }
}
