package com.lab.app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemRepository repo;
    public ItemController(ItemRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Item> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Item> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Item create(@RequestBody Item in) throws Exception {
        in.setServedBy(InetAddress.getLocalHost().getHostName());
        return repo.save(in);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
