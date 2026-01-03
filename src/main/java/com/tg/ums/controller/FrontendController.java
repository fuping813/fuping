package com.tg.ums.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前端控制器，用于处理SPA路由
 */
@Controller
@RequestMapping("/")
public class FrontendController {
    
    /**
     * 将所有非API请求转发到index.html，由Vue Router处理
     */
    @GetMapping("")
    public String root() {
        return "forward:/index.html";
    }
    
    /**
     * 处理所有其他非API路径，确保SPA路由正常工作
     */
    @GetMapping({"/teaching-plan", "/course-catalog", "/chapter-quiz", "/score-statistics", "/data-import-export"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
