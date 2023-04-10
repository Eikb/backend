package de.vikz.wumtbackend.modul;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modul")
public class ModulController {
    private final ModulRepository modulRepository;

    public ModulController(ModulRepository modulRepository) {
        this.modulRepository = modulRepository;
    }

    @GetMapping
    public ResponseEntity<List<Modul>> getAllModuls() {
        return ResponseEntity.ok(modulRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<String> createModul(@RequestBody Modul modul) {
        modulRepository.save(modul);
        return ResponseEntity.ok("Das Modul mit dem Namen: " + modul.getName() + " wurde erstellt.");
    }

    @PutMapping
    public ResponseEntity<String> updateModul(@RequestBody Modul modul) {
        modulRepository.updateNameBy(modul.getName(), modul.getId());
        return ResponseEntity.ok("Das Modul mit der ID: " + modul.getId() + " wurde bearbeitet.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModul(@PathVariable("id") Integer modulId) {
        modulRepository.deleteById(modulId);
        return ResponseEntity.ok("Das Modul mit der Id: " + modulId + " wurde gelöscht.");
    }
}
