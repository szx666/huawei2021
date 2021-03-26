package com.huawei.java.main;

/**
 * 排序list的实体类
 */
public class Server1 implements Comparable<Server1>{
    //型号作为key存入map
    private String server_name;

    private int cpu_core;

    private int memory;

    private int cost_hardware;

    private int cost_energy;

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public int getCpu_core() {
        return cpu_core;
    }

    public void setCpu_core(int cpu_core) {
        this.cpu_core = cpu_core;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getCost_hardware() {
        return cost_hardware;
    }

    public void setCost_hardware(int cost_hardware) {
        this.cost_hardware = cost_hardware;
    }

    public int getCost_energy() {
        return cost_energy;
    }

    public void setCost_energy(int cost_energy) {
        this.cost_energy = cost_energy;
    }

    @Override
    public int compareTo(Server1 server1) {           //重写Comparable接口的compareTo方法，
        return this.cost_hardware - server1.getCost_hardware();         //根据生成随机数升序排列，降序修改相减顺序即可
    }
//    @Override
//    public int compareTo(Server1 server1) {           //重写Comparable接口的compareTo方法，
//        return this.cost_energy - server1.getCost_energy();         //根据生成随机数升序排列，降序修改相减顺序即可
//    }

}
