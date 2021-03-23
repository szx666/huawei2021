package com.huawei.java.main;

/**
 * 现有服务器信息
 */
public class ServerInfo {
    //服务器id作为key
//    private String server_id;

    private String server_name;

    private int A_cpu_core;

    private int A_memory;

    private int B_cpu_core;

    private int B_memory;

    private Boolean status;

//    public String getServer_id() {
//        return server_id;
//    }
//
//    public void setServer_id(String server_id) {
//        this.server_id = server_id;
//    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public int getA_cpu_core() {
        return A_cpu_core;
    }

    public void setA_cpu_core(int a_cpu_core) {
        A_cpu_core = a_cpu_core;
    }

    public int getA_memory() {
        return A_memory;
    }

    public void setA_memory(int a_memory) {
        A_memory = a_memory;
    }

    public int getB_cpu_core() {
        return B_cpu_core;
    }

    public void setB_cpu_core(int b_cpu_core) {
        B_cpu_core = b_cpu_core;
    }

    public int getB_memory() {
        return B_memory;
    }

    public void setB_memory(int b_memory) {
        B_memory = b_memory;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
