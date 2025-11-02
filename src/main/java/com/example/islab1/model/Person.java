package com.example.islab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "persons")
public class Person {
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");
    private static final int NAME_MAX_LENGTH = 255;
    private static final int HEIGHT_MAX_VALUE = 1_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // БД генерит id
    private Integer id;

    @NotBlank(message = "{person.name.notBlank}")
    @Size(max = NAME_MAX_LENGTH, message = "{person.name.size}")
    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location; // может быть null

    @Min(value = 1, message = "{person.height`.min}")
    @Max(value = HEIGHT_MAX_VALUE, message = "{person.height.max}")
    private int height;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date birthday; // может быть null

    @NotNull
    @Enumerated(EnumType.STRING)
    private Country nationality;

    public Person() {}

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public Color getEyeColor() { return eyeColor; }
    public void setEyeColor(Color eyeColor) { this.eyeColor = eyeColor; }
    public Color getHairColor() { return hairColor; }
    public void setHairColor(Color hairColor) { this.hairColor = hairColor; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public Country getNationality() { return nationality; }
    public void setNationality(Country nationality) { this.nationality = nationality; }

    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            creationDate = ZonedDateTime.now(MOSCOW_ZONE);
        } else {
            creationDate = creationDate.withZoneSameInstant(MOSCOW_ZONE);
        }
    }

    @PostLoad
    public void postLoad() {
        if (creationDate != null) {
            creationDate = creationDate.withZoneSameInstant(MOSCOW_ZONE);
        }
    }

    @Transient
    public ZonedDateTime getCreationDateMoscow() {
        return creationDate == null ? null : creationDate.withZoneSameInstant(MOSCOW_ZONE);
    }
}
