package com.example.islab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float x;

    @NotNull
    private Float y;

    public Coordinates() {}
    public Coordinates(float x, Float y) { this.x = x; this.y = y; }

    public Long getId() { return id; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public Float getY() { return y; }
    public void setY(Float y) { this.y = y; }
}