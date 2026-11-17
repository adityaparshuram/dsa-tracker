package com.dsatracker.controller;

import com.dsatracker.model.*;
import com.dsatracker.security.CurrentUser;
import com.dsatracker.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ProblemController {
    private final ProblemService service;
    private final CurrentUser me;
    public ProblemController(ProblemService service, CurrentUser me) { this.service = service; this.me = me; }

    @GetMapping("/problems")
    public List<Problem> list(@RequestParam(required = false) String topic,
                              @RequestParam(required = false) Difficulty difficulty,
                              @RequestParam(required = false) Status status,
                              @RequestParam(required = false) String q) {
        return service.search(me.id(), topic, difficulty, status, q);
    }

    @PostMapping("/problems") @ResponseStatus(HttpStatus.CREATED)
    public Problem create(@Valid @RequestBody Problem p) { return service.create(me.id(), p); }

    @PutMapping("/problems/{id}")
    public Problem update(@PathVariable Long id, @Valid @RequestBody Problem p) { return service.update(me.id(), id, p); }

    @DeleteMapping("/problems/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(me.id(), id); }

    @GetMapping("/stats")
    public Map<String, Object> stats() { return service.stats(me.id()); }
}