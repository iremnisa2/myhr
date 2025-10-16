package com.myhr.myhr.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {


    @GetMapping("/admin/dashboard")
    public String admin() { return "admin-dashboard"; }

    @GetMapping("/company/home")
    public String company() { return "company-home"; }
}
