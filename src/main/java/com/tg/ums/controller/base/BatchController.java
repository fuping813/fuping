package com.tg.ums.controller.base;

import com.tg.ums.entity.base.Batch;
import com.tg.ums.service.base.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @GetMapping
    public List<Batch> getAllBatches() {
        return batchService.getAllBatches();
    }

    @GetMapping("/{id}")
    public Batch getBatchById(@PathVariable Integer id) {
        return batchService.getBatchById(id).orElseThrow();
    }

    @PostMapping
    public Batch createBatch(@RequestBody Batch batch) {
        return batchService.saveBatch(batch);
    }

    @PutMapping("/{id}")
    public Batch updateBatch(@PathVariable Integer id, @RequestBody Batch batch) {
        batch.setBatchId(id);
        return batchService.saveBatch(batch);
    }

    @DeleteMapping("/{id}")
    public void deleteBatch(@PathVariable Integer id) {
        batchService.deleteBatch(id);
    }
}
