package com.tracker.virustracker.controller;

import com.tracker.virustracker.models.Location;
import com.tracker.virustracker.services.VirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    VirusDataService virusDataService;

    @GetMapping("/")
    public String home(Model model) {
        List<Location> allStats = virusDataService.getAllStats();
        int totalCasesWorldWide = allStats.stream().mapToInt(Location::getLatestTotalCases).sum();
        int totalNewCases = allStats.stream().mapToInt(Location::getDelta).sum();
        model.addAttribute("locationStatistics", allStats);
        model.addAttribute("totalCasesWorldWide", totalCasesWorldWide);
        model.addAttribute("delta", totalNewCases);
        return "home";
    }
}
