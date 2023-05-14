package com.nowcoder.community.controller;

import com.nowcoder.community.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@RequestMapping("/admin")
@Controller
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getStatistics() {
        return "site/admin/data";
    }
    @RequestMapping(path = "/getUV", method = RequestMethod.POST)
    public String getUVCount(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                             Model model) {
        long uvCount = statisticsService.getUVCount(start, end);
        model.addAttribute("uvStart", start);
        model.addAttribute("uvEnd", end);
        model.addAttribute("uvCount", uvCount);
        return "forward:/admin/data";
    }
    @RequestMapping(path = "/getDAU", method = RequestMethod.POST)
    public String getDAUCount(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                              @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                              Model model) {
        long dauCount = statisticsService.getDAUCount(start, end);
        model.addAttribute("dauStart", start);
        model.addAttribute("dauEnd", end);
        model.addAttribute("dauCount", dauCount);
        return "forward:/admin/data";
    }
}
