package com.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.annotation.OperationLog;
import com.blog.common.Result;
import com.blog.entity.Moment;
import com.blog.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台说说管理（需登录）：CRUD 说说。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/moments")
public class MomentAdminController {

    private final MomentService momentService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Moment> pageResult = momentService.list(page, size);
        Map<String, Object> map = new HashMap<>();
        map.put("records", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return Result.ok(map);
    }

    @OperationLog(value = "发布说说", type = "CREATE")
    @PostMapping
    public Result<Moment> create(@Valid @RequestBody Moment moment) {
        return Result.ok(momentService.create(moment));
    }

    @OperationLog(value = "更新说说", type = "UPDATE")
    @PutMapping("/{id}")
    public Result<Moment> update(@PathVariable Long id, @Valid @RequestBody Moment moment) {
        return Result.ok(momentService.update(id, moment));
    }

    @OperationLog(value = "删除说说", type = "DELETE")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        momentService.delete(id);
        return Result.ok();
    }
}
