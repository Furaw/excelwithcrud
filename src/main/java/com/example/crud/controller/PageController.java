package com.example.crud.controller;

import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@Controller
public class PageController {
    @Autowired
    StudentRepository studentRepository;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students);

        return "greeting";
    }
}
