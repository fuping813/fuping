package com.tg.ums.service.base;

import com.tg.ums.entity.base.Semester;
import com.tg.ums.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    public Optional<Semester> getSemesterById(Integer semesterId) {
        return semesterRepository.findById(semesterId);
    }

    public Semester saveSemester(Semester semester) {
        return semesterRepository.save(semester);
    }

    public void deleteSemester(Integer semesterId) {
        semesterRepository.deleteById(semesterId);
    }
}
