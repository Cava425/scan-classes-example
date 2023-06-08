package com.example.scan.atomic.scan.inner;

import com.example.scan.atomic.LocalAtomic;
import org.springframework.stereotype.Service;

@Service
public class C {

    @LocalAtomic(name = "add", desc = "两个整数之和")
    public Integer add(Integer a, int b){
        return a + b;
    }

}
