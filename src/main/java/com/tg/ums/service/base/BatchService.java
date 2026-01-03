package com.tg.ums.service.base;

import com.tg.ums.entity.base.Batch;
import com.tg.ums.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    @Autowired
    private BatchRepository batchRepository;

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Optional<Batch> getBatchById(Integer batchId) {
        return batchRepository.findById(batchId);
    }

    public Batch saveBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    public void deleteBatch(Integer batchId) {
        batchRepository.deleteById(batchId);
    }
}
