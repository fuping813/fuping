package com.tg.ums.controller.base;

import com.tg.ums.entity.base.Teacher;
import com.tg.ums.service.base.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public Teacher getTeacherById(@PathVariable Integer id) {
        return teacherService.getTeacherById(id).orElseThrow();
    }

    @PostMapping
    public Teacher createTeacher(@RequestBody Teacher teacher) {
        return teacherService.saveTeacher(teacher);
    }

    @PutMapping("/{id}")
    public Teacher updateTeacher(@PathVariable Integer id, @RequestBody Teacher teacher) {
        teacher.setTeacherId(id);
        return teacherService.saveTeacher(teacher);
    }

    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
    }
}
