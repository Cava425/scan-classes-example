package com.example.scan.atomic.scan;

import com.example.scan.atomic.LocalAtomic;

public class B {

    @LocalAtomic(name = "multi", desc = "两个整数之积")
    public int multi(int a, int b){
        return a * b;
    }

}
