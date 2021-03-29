package com.huawei.java.main;

/**
 * 迁移排序类
 */
public class mig implements Comparable<mig>{

    private int server_id;

    private int vm_amount;

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public int getVm_amount() {
        return vm_amount;
    }

    public void setVm_amount(int vm_amount) {
        this.vm_amount = vm_amount;
    }

    @Override
    public int compareTo(mig mig) {           //重写Comparable接口的compareTo方法，
        return this.vm_amount - mig.getVm_amount();         //根据每台服务器剩余虚拟机数量升序排列，降序修改相减顺序即可
    }
}
