package com.example.scan.atomic.scan;

import com.example.scan.atomic.LocalAtomic;
import org.springframework.stereotype.Component;

@Component
public class Aaa {

    @LocalAtomic(name = "add", desc = "两个整数之和")
    public int add(int a, int b){
        return a + b;
    }

}
