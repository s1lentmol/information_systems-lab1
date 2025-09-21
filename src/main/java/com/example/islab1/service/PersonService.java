package com.example.islab1.service;

import com.example.islab1.model.Color;
import com.example.islab1.model.Country;
import com.example.islab1.model.Person;
import com.example.islab1.repo.PersonRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository repo;

    public PersonService(PersonRepository repo) {
        this.repo = repo;
    }

    public Page<Person> list(String filterField, String filterValue,
                             String sortField, String sortDir, int page, int size) {
        Sort sort = Sort.by((sortField == null || sortField.isBlank()) ? "id" : sortField);
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

    @Transactional
    public Person create(Person p) {
        if (p.getHeight() < 1) throw new IllegalArgumentException("height must be > 0");
        if (p.getCoordinates() == null || p.getCoordinates().getY() == null)
            throw new IllegalArgumentException("coordinates.y must be not null");
        return repo.save(p);
    }

    @Transactional
    public Person update(Integer id, Person updated) {
        Person existing = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Person not found"));
        existing.setName(updated.getName());
        existing.setEyeColor(updated.getEyeColor());
        existing.setHairColor(updated.getHairColor());
        existing.setHeight(updated.getHeight());
        existing.setBirthday(updated.getBirthday());
        existing.setNationality(updated.getNationality());
        existing.setCoordinates(updated.getCoordinates());
        existing.setLocation(updated.getLocation());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Integer id) { repo.deleteById(id); }

    // Операции
    @Transactional
    public int deleteByNationality(Country nationality) {
        return repo.deleteAllByNationality(nationality);
    }

    public List<Person> findHeightGreaterThan(int height) { return repo.findByHeightGreaterThan(height); }

    public List<Integer> uniqueHeights() {
        return repo.findAll().stream().map(Person::getHeight).distinct().sorted().collect(Collectors.toList());
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
