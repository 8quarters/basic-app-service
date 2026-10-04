package com.lab.app;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String servedBy;          // hostname/pod/instance that created it
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getServedBy() { return servedBy; }
    public void setServedBy(String servedBy) { this.servedBy = servedBy; }
    public Instant getCreatedAt() { return createdAt; }
}
