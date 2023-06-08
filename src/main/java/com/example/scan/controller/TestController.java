package com.example.scan.controller;

import com.example.scan.atomic.LocalAtomicScanner;
import com.example.scan.atomic.dto.LocalAtomicDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/scan")
    public List<LocalAtomicDTO> getClass(String packageName) throws IOException, ClassNotFoundException {
        return LocalAtomicScanner.getLocalAtomic(packageName);
    }


}
