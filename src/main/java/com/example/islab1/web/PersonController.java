package com.example.islab1.web;

import com.example.islab1.model.*;
import com.example.islab1.ws.PersonChangePublisher;
import com.example.islab1.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService service;
    private final PersonChangePublisher publisher;

    public PersonController(PersonService service, PersonChangePublisher publisher) {
        this.service = service;
        this.publisher = publisher;
    }


    private void populateReferenceData(Model model) {
        model.addAttribute("colors", Color.values());
        model.addAttribute("countries", Country.values());
    }


    @GetMapping
    public String list(@RequestParam(required = false) String filterField,
                       @RequestParam(required = false) String filterValue,
                       @RequestParam(required = false) String sort,
                       @RequestParam(required = false) String dir,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Page<Person> persons;
        try {
            persons = service.list(filterField, filterValue, sort, dir, page, size);
        } catch (IllegalArgumentException ex) {
            persons = Page.empty(PageRequest.of(Math.max(page, 0), Math.max(size, 1)));
            model.addAttribute("filterError",
                    "Некорректное значение фильтра для поля \"" + filterField + "\".");
        }

        model.addAttribute("page", persons);
        model.addAttribute("filterField", filterField);
        model.addAttribute("filterValue", filterValue);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        populateReferenceData(model);
        return "persons/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        if (!model.containsAttribute("person")) {
            Person p = new Person();
            p.setCoordinates(new Coordinates());
            model.addAttribute("person", p);
        }
        populateReferenceData(model);
        return "persons/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("person") Person person,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            return "persons/form";
        }
        try {
            service.create(person);
            publisher.broadcastChange();

            redirectAttributes.addFlashAttribute("successMessage", "объект успешно сохранен");

            return "redirect:/persons";

        } catch (Exception ex) {
            bindingResult.reject("createError", ex.getMessage());
            populateReferenceData(model);
            return "persons/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Person p = service.require(id);
        model.addAttribute("person", p);
        return "persons/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Person p = service.require(id);
        if (p.getCoordinates() == null) {
            p.setCoordinates(new Coordinates());
        }
        model.addAttribute("person", p);
        populateReferenceData(model);
        return "persons/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("person") Person person,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateReferenceData(model);
            return "persons/form";
        }
        try {
            service.update(id, person);
            publisher.broadcastChange();

            redirectAttributes.addFlashAttribute("successMessage", "объект успешно сохранен");

            return "redirect:/persons";

        } catch (Exception ex) {
            bindingResult.reject("updateError", ex.getMessage());
            populateReferenceData(model);
            return "persons/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        publisher.broadcastChange();
        return "redirect:/persons";
    }
}
