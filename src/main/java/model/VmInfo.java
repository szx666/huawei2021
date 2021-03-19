package model;

/**
 * 现有虚拟机信息
 */
public class VmInfo {

    //新建虚拟机id作为key
//    private String vm_id;
    //所在服务器id
    private int server_id;
    //所在节点
    private String node;
    //虚拟机是否存在
    private Boolean status;
    //虚拟机型号
    private String vm_name;


    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getVm_name() {
        return vm_name;
    }

    public void setVm_name(String vm_name) {
        this.vm_name = vm_name;
    }
}
