package com.tg.ums.service.base;

import com.tg.ums.entity.base.Major;
import com.tg.ums.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MajorService {

    @Autowired
    private MajorRepository majorRepository;

    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }

    public Optional<Major> getMajorById(Integer majorId) {
        return majorRepository.findById(majorId);
    }

    public Major saveMajor(Major major) {
        return majorRepository.save(major);
    }

    public void deleteMajor(Integer majorId) {
        majorRepository.deleteById(majorId);
    }
}
