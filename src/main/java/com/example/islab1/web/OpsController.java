package com.example.islab1.web;

import com.example.islab1.model.Color;
import com.example.islab1.model.Country;
import com.example.islab1.model.Person;
import com.example.islab1.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ops")
public class OpsController {

    private final PersonService service;
    public OpsController(PersonService service) { this.service = service; }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("colors", Color.values());
        model.addAttribute("countries", Country.values());
        return "ops/index";
    }

    @PostMapping("/delete-by-nationality")
    public String deleteByNationality(@RequestParam Country nationality, Model model) {
        int deleted = service.deleteByNationality(nationality);
        model.addAttribute("message", "Удалено объектов: " + deleted);
        return index(model);
    }

    @GetMapping("/height-greater")
    public String heightGreater(@RequestParam int height, Model model) {
        List<Person> list = service.findHeightGreaterThan(height);
        model.addAttribute("results", list);
        return "ops/height-greater";
    }

    @GetMapping("/unique-heights")
    public String uniqueHeights(Model model) {
        model.addAttribute("heights", service.uniqueHeights());
        return "ops/unique-heights";
    }

    @GetMapping("/hair-share")
    public String hairShare(@RequestParam Color hairColor, Model model) {
        double share = service.hairColorShare(hairColor);
        model.addAttribute("hairColor", hairColor);
        model.addAttribute("share", share);
        return "ops/hair-share";
    }

    @GetMapping("/hair-count-in-location")
    public String hairCountInLocation(@RequestParam Color hairColor,
                                      @RequestParam(required = false) Long locationId,
                                      Model model) {
        long count = service.countHairColorInLocation(hairColor, locationId);
        model.addAttribute("hairColor", hairColor);
        model.addAttribute("locationId", locationId);
        model.addAttribute("count", count);
        return "ops/hair-count-in-location";
    }
}
