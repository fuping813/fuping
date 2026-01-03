package com.tg.ums.service.base;

import com.tg.ums.entity.base.Teacher;
import com.tg.ums.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Integer teacherId) {
        return teacherRepository.findById(teacherId);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Integer teacherId) {
        teacherRepository.deleteById(teacherId);
    }
}
