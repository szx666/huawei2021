package model;


import com.sun.xml.internal.ws.util.StringUtils;

import java.io.IOException;
import java.util.*;

import static model.Read.*;

public class Write {
    //设置全局Map
    public static Map<Integer, ServerInfo> map_ServerInfo = new HashMap<>();
    public static Map<Integer, VmInfo> map_vmInfo = new HashMap<>();

    public static Integer server_id = 0;
    //public static Integer vm_id = 0;

    /**
     * @Description: 根据每天增删虚拟机操作购买服务器
     * @Date 2021年3月13日 10:54:48
     * @Version V1.0
     */
    public static void OperationByDay() throws IOException {

        //成本计算
        int cost_hardware = 0;
        int cost_energy_all = 0;
        int cost_energy_day = 0;
        int cost_all = 0;

        //每天操作计算
        int server_num = 0;
        int vm_num = 0;

        int i, j, num;


        Map<String,Vm> map_vm = Read.ReadVm();
        List<List<Operation>> list_operation = ReadOperation();
        //储存了800天的操作，每天的操作数如何获取

        //用来储存每一天操作的list
        List<Operation> list_by_day = new ArrayList<Operation>();

        //储存每一台vm对应信息list
        List<vm_server> vm_server = new ArrayList<>();
//        Map<Integer,VmInfo> map_VmInfo = new HashMap<>();
//        Map<Integer,ServerInfo> map_ServerInfo = new HashMap<>();

//        Integer server_id = 0;

        for (i = 0; i < list_operation.size(); i++) {
            list_by_day = list_operation.get(i);
            System.out.println("第"+i+"天");


            //i代表第几天 j代表第几个操作
            for (j = 0; j < list_by_day.size(); j++) {

                VmInfo vmInfo = new VmInfo();
                //判断是add还是del
                //add的话对照虚拟机vm型号判断是单节点部署还是双节点部署
                //判断当前有没有可供部署的服务器，如果没有的话就要购买服务器进行增加，同时还要记录是第i天增加的
                if (list_by_day.get(j).getOperation_name().equals("add")) {
//                    String b = list_by_day.get(0).getVm_name();
//                    Vm c = map_vm.get("vmPCXA5");
//                    Vm a = map_vm.get(b);
                    //判断是否双节点 1为双节点
                    //3月15日问题
                    if (map_vm.get(list_by_day.get(j).getVm_name()).getDouble_node() == 1) {
                        //双节点部署需要将虚拟机两种资源减半
                        Integer cpu_core = map_vm.get(list_by_day.get(j).getVm_name()).getVm_cpu_core() / 2;
                        Integer memory = map_vm.get(list_by_day.get(j).getVm_name()).getVm_memory() / 2;


                        if (map_ServerInfo.isEmpty()) {
                            BuyServer();
                            vmInfo.setServer_id(0);
                        } else {//如果map_ServerInfo中有已购买的服务器，则看服务器剩余的CPU核数和内存是否能存入虚拟机
                            //挨个遍历已有的服务器，先比较AB节点剩余内核和内存与当前虚拟机，若有余量则加入，若没有则购买
                            for (Map.Entry<Integer, ServerInfo> entry : map_ServerInfo.entrySet()) {
                                //已购服务器的内核数和内存大小均大于等于map_vm中对应虚拟机的大小，则在entry中减去相应的大小
                                //同时将存储进的服务器信息写入
                                if (entry.getValue().getA_cpu_core() >= cpu_core && entry.getValue().getA_memory() >= memory && entry.getValue().getB_cpu_core() >= cpu_core && entry.getValue().getB_memory() >= memory) {
                                    entry.getValue().setA_cpu_core(entry.getValue().getA_cpu_core() - cpu_core);
                                    entry.getValue().setB_cpu_core(entry.getValue().getB_cpu_core() - cpu_core);
                                    entry.getValue().setA_memory(entry.getValue().getA_memory() - memory);
                                    entry.getValue().setB_memory(entry.getValue().getB_memory() - memory);
                                    vmInfo.setServer_id(entry.getKey());
                                    break;//若满足条件跳出for循环
                                }
                                //若不满足条件跳出本次循环进入下一个已购买服务器
                            }
                            if(vmInfo.getServer_id() == 0){
                                //如果没有服务器满足条件则购买
                                BuyServer();
                                map_ServerInfo.get(server_id - 1).setA_cpu_core(map_ServerInfo.get(server_id - 1).getA_cpu_core() - cpu_core);
                                map_ServerInfo.get(server_id - 1).setB_cpu_core(map_ServerInfo.get(server_id - 1).getB_cpu_core() - cpu_core);
                                map_ServerInfo.get(server_id - 1).setA_memory(map_ServerInfo.get(server_id - 1).getA_memory() - memory);
                                map_ServerInfo.get(server_id - 1).setB_memory(map_ServerInfo.get(server_id - 1).getB_memory() - memory);
                            }
                            /**
                             * 有问题，每次都要重新买服务器
                             */

                        }
                        //双节点
                        vmInfo.setNode(null);
                    } else {//若为单节点
                        Integer cpu_core = map_vm.get(list_by_day.get(j).getVm_name()).getVm_cpu_core();
                        Integer memory = map_vm.get(list_by_day.get(j).getVm_name()).getVm_memory();

                        if (map_ServerInfo.isEmpty()) {
                            BuyServer();
                            vmInfo.setServer_id(0);
                        } else {//如果map_ServerInfo中有已购买的服务器，则看服务器剩余的CPU核数和内存是否能存入虚拟机
                            //挨个遍历已有的服务器，先比较AB节点剩余内核和内存与当前虚拟机，若有余量则加入，若没有则购买
                            for (Map.Entry<Integer, ServerInfo> entry : map_ServerInfo.entrySet()) {
                                //已购服务器的内核数和内存大小均大于等于map_vm中对应虚拟机的大小，则在entry中减去相应的大小
                                //同时将存储进的服务器信息写入
                                if (entry.getValue().getA_cpu_core() >= cpu_core && entry.getValue().getA_memory() >= memory) {
                                    entry.getValue().setA_cpu_core(entry.getValue().getA_cpu_core() - cpu_core);
                                    entry.getValue().setA_memory(entry.getValue().getA_memory() - memory);
                                    vmInfo.setNode("A");
                                    vmInfo.setServer_id(entry.getKey());
                                    break;//若满足条件跳出for循环
                                } else if (entry.getValue().getB_cpu_core() >= cpu_core && entry.getValue().getB_memory() >= memory) {
                                    entry.getValue().setB_cpu_core(entry.getValue().getB_cpu_core() - cpu_core);
                                    entry.getValue().setB_memory(entry.getValue().getB_memory() - memory);
                                    vmInfo.setNode("B");
                                    vmInfo.setServer_id(entry.getKey());
                                    break;//若满足条件跳出for循环
                                }
                                //若不满足条件跳出本次循环进入下一个已购买服务器
                            }
                            String a = String.valueOf(vmInfo.getServer_id());
                            if(vmInfo.getServer_id() == 0){
                                //如果没有服务器满足条件则购买
                                BuyServer();
                                //若新买服务器默认放A节点
                                map_ServerInfo.get(server_id - 1).setA_cpu_core(map_ServerInfo.get(server_id - 1).getA_cpu_core() - cpu_core);
                                map_ServerInfo.get(server_id - 1).setB_cpu_core(map_ServerInfo.get(server_id - 1).getB_cpu_core() - cpu_core);
                                vmInfo.setNode("A");
                                vmInfo.setServer_id(server_id - 1);
                            }
                        }

                    }
                    //在确认了存入服务器后再将虚拟机相关信息存入map_vmInfo中
                    /**
                     * 有问题，应该存入选择的服务器id
                     */
                    vmInfo.setStatus(true);
                    vmInfo.setVm_name(list_by_day.get(j).getVm_name());

                    map_vmInfo.put(list_by_day.get(j).getVm_id(), vmInfo);

                    vm_server vs = new vm_server();
                    vs.setServer_id(vmInfo.getServer_id());
                    vs.setNode(vmInfo.getNode());
                    vm_server.add(vs);
                    /**
                     * list存储vm_id对应的server_id和node
                     */
                } else {
                    //del虚拟机操作
                    map_vmInfo.get(list_by_day.get(j).getVm_id()).setStatus(false);
                    //判断虚拟机所在的服务器 此服务器id需要从map_vmInfo中读取
                    Integer serverId = map_vmInfo.get(list_by_day.get(j).getVm_id()).getServer_id();
//                    String a = map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getNode();
                    //判断是否双节点 null为双节点
                    if (map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getNode() == null) {
                        //双节点部署需要将虚拟机两种资源减半

                        Integer cpu_core = map_vm.get(map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getVm_name()).getVm_cpu_core() / 2;
                        Integer memory = map_vm.get(map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getVm_name()).getVm_memory() / 2;


                        //Integer a = map_ServerInfo.get(serverId).getA_cpu_core();
                        map_ServerInfo.get(serverId).setA_cpu_core(map_ServerInfo.get(serverId).getA_cpu_core() + cpu_core);
                        map_ServerInfo.get(serverId).setB_cpu_core(map_ServerInfo.get(serverId).getB_cpu_core() + cpu_core);
                        map_ServerInfo.get(serverId).setA_memory(map_ServerInfo.get(serverId).getA_memory() + memory);
                        map_ServerInfo.get(serverId).setB_memory(map_ServerInfo.get(serverId).getB_memory() + memory);

                    } else {//若为单节点
                        Integer cpu_core = map_vm.get(map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getVm_name()).getVm_cpu_core();
                        Integer memory = map_vm.get(map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getVm_name()).getVm_memory();

                        if (map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getNode().equals("A")) {
                            map_ServerInfo.get(serverId).setA_cpu_core(map_ServerInfo.get(serverId).getA_cpu_core() + cpu_core);
                            map_ServerInfo.get(serverId).setA_memory(map_ServerInfo.get(serverId).getA_memory() + memory);
                        } else {
                            map_ServerInfo.get(serverId).setB_cpu_core(map_ServerInfo.get(serverId).getB_cpu_core() + cpu_core);
                            map_ServerInfo.get(serverId).setB_memory(map_ServerInfo.get(serverId).getB_memory() + memory);
                        }

                    }

                }


            }
            /**
             * 先判断第i天是否购买了服务器,若买了则买 server_num_now 台
             */
            int server_num_now = map_ServerInfo.size() - server_num;
            server_num = map_ServerInfo.size();
            if(server_num_now == 0){
                System.out.println("(purchase,0)");
            }else{
                System.out.println("(purchase,1)");
                System.out.println("(hostW1Q5U," + server_num_now + ")");
            }
            System.out.println("(migration,0)");


            /**
             * 遍历vm_server获得server_id和node
             */
            for(num = vm_num; num < map_vmInfo.size(); num++ ){
                int id = vm_server.get(num).getServer_id();
                String node = vm_server.get(num).getNode();
                if(node == null || node.isEmpty()){
                    System.out.println("(" + id + ",A)");
                    System.out.println("(" + id + ",B)");
                }else if(node.equals("A")){
                    System.out.println("(" + id + ",A)");
                }else{
                    System.out.println("(" + id + ",B)");
                }
            }

            vm_num = map_vmInfo.size();
//            /**
//             * 在执行完每日的操作后，对每日的输出进行计算
//             */
//            int day = 0;
//            for (Map.Entry<Integer, ServerInfo> entry : map_ServerInfo.entrySet()){
//                if(entry.getValue().getStatus()){
//                    day++;
//                }
//            }
//            cost_energy_day = day * 224;

        }
//        cost_energy_all += cost_energy_day;
//        cost_hardware = map_ServerInfo.size() * 179861;
//        cost_all = cost_energy_all + cost_hardware;
//        System.out.println(cost_all);


    }














    /**
     * 购买服务器策略
     */
    public static void BuyServer() throws IOException{

        Map<String,Server> map_server = ReadServer();
        ServerInfo serverInfo = new ServerInfo();
        //暂时默认买第一种服务器
        serverInfo.setServer_name(map_server.keySet().iterator().next());

        serverInfo.setA_cpu_core(map_server.get(map_server.keySet().iterator().next()).getCpu_core() / 2);
        serverInfo.setA_memory(map_server.get(map_server.keySet().iterator().next()).getMemory() / 2);
        serverInfo.setB_cpu_core(map_server.get(map_server.keySet().iterator().next()).getCpu_core() / 2);
        serverInfo.setB_memory(map_server.get(map_server.keySet().iterator().next()).getMemory() / 2);
        serverInfo.setStatus(false);//默认没有开启使用

        map_ServerInfo.put(server_id,serverInfo);
        server_id++;

    }


    /**
     * 虚拟机为双节点部署
     */
    public static void ArrangeByDouble(){

    }


    /**
     * 虚拟机为单节点部署
     */
    public static void ArrangeBySingle(){

    }
}











