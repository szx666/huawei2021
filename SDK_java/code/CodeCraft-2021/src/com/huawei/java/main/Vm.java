package com.huawei.java.main;

/**
 * 虚拟机类
 */
public class Vm {
    //虚拟机型号作为map的key
//    private String vm_name;

    private int vm_cpu_core;

    private int vm_memory;

    private int double_node;

//    public String getVm_name() {
//        return vm_name;
//    }
//
//    public void setVm_name(String vm_name) {
//        this.vm_name = vm_name;
//    }

    public int getVm_cpu_core() {
        return vm_cpu_core;
    }

    public void setVm_cpu_core(int vm_cpu_core) {
        this.vm_cpu_core = vm_cpu_core;
    }

    public int getVm_memory() {
        return vm_memory;
    }

    public void setVm_memory(int vm_memory) {
        this.vm_memory = vm_memory;
    }

    public int getDouble_node() {
        return double_node;
    }

    public void setDouble_node(int double_node) {
        this.double_node = double_node;
    }
}
