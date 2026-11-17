package com.dsatracker.service;

import com.dsatracker.exception.NotFoundException;
import com.dsatracker.model.*;
import com.dsatracker.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemService {
    private final ProblemRepository repo;
    public ProblemService(ProblemRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Problem> search(Long uid, String topic, Difficulty d, Status s, String q) {
        return repo.search(uid, blank(topic), d, s, blank(q));
    }

    public Problem create(Long uid, Problem p) { p.setId(null); p.setUserId(uid); return repo.save(normalize(p)); }

    public Problem update(Long uid, Long id, Problem in) {
        Problem p = repo.findByIdAndUserId(id, uid).orElseThrow(() -> new NotFoundException("Problem " + id + " not found"));
        p.setTitle(in.getTitle()); p.setTopic(in.getTopic()); p.setDifficulty(in.getDifficulty());
        p.setStatus(in.getStatus()); p.setPlatform(in.getPlatform()); p.setLink(in.getLink());
        p.setNotes(in.getNotes()); p.setSolvedDate(in.getSolvedDate());
        return repo.save(normalize(p));
    }

    public void delete(Long uid, Long id) {
        repo.delete(repo.findByIdAndUserId(id, uid).orElseThrow(() -> new NotFoundException("Problem " + id + " not found")));
    }

    /** Dashboard analytics: totals, breakdowns, current streak and a 12-week activity map. */
    @Transactional(readOnly = true)
    public Map<String, Object> stats(Long uid) {
        List<Problem> all = repo.findByUserId(uid);
        List<Problem> solved = all.stream().filter(p -> p.getStatus() == Status.SOLVED).toList();

        Map<String, Long> byDifficulty = new LinkedHashMap<>();
        for (Difficulty d : Difficulty.values())
            byDifficulty.put(d.name(), solved.stream().filter(p -> p.getDifficulty() == d).count());

        Map<String, Long> byTopic = solved.stream().collect(Collectors.groupingBy(Problem::getTopic, Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        Map<LocalDate, Long> perDay = solved.stream().filter(p -> p.getSolvedDate() != null)
                .collect(Collectors.groupingBy(Problem::getSolvedDate, Collectors.counting()));

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate d = perDay.containsKey(today) ? today : today.minusDays(1);
        while (perDay.containsKey(d)) { streak++; d = d.minusDays(1); }

        Map<String, Long> activity = new LinkedHashMap<>();
        for (int i = 83; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            activity.put(day.toString(), perDay.getOrDefault(day, 0L));
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("total", all.size());
        out.put("solved", solved.size());
        out.put("revisit", all.stream().filter(p -> p.getStatus() == Status.REVISIT).count());
        out.put("streak", streak);
        out.put("byDifficulty", byDifficulty);
        out.put("byTopic", byTopic);
        out.put("activity", activity);
        return out;
    }

    private Problem normalize(Problem p) {
        if (p.getStatus() == Status.SOLVED && p.getSolvedDate() == null) p.setSolvedDate(LocalDate.now());
        if (p.getStatus() != Status.SOLVED) p.setSolvedDate(null);
        return p;
    }
    private String blank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
}