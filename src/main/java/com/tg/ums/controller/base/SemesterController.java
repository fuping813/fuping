package com.tg.ums.controller.base;

import com.tg.ums.entity.base.Semester;
import com.tg.ums.service.base.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @GetMapping
    public List<Semester> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    @GetMapping("/{id}")
    public Semester getSemesterById(@PathVariable Integer id) {
        return semesterService.getSemesterById(id).orElseThrow();
    }

    @PostMapping
    public Semester createSemester(@RequestBody Semester semester) {
        return semesterService.saveSemester(semester);
    }

    @PutMapping("/{id}")
    public Semester updateSemester(@PathVariable Integer id, @RequestBody Semester semester) {
        semester.setSemesterId(id);
        return semesterService.saveSemester(semester);
    }

    @DeleteMapping("/{id}")
    public void deleteSemester(@PathVariable Integer id) {
        semesterService.deleteSemester(id);
    }
}
