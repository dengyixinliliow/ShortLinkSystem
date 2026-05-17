package com.example.project.controller;

import com.example.project.common.convention.result.Result;
import com.example.project.common.convention.result.Results;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {

    public Result<Void> placeholder() {
        return Results.success();
    }
}
