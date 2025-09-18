package com.example.islab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double x;

    private int y;
    private int z;

    public Location() {}
    public Location(Double x, int y, int z) { this.x = x; this.y = y; this.z = z; }

    public Long getId() { return id; }
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }
}
