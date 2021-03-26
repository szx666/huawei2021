package com.huawei.java.main;


import java.io.IOException;
import java.util.*;


public class Write {
    //设置全局Map
    public static ServerInfo[] serverInfo = new ServerInfo[100000];
    //public static Map<Integer, ServerInfo> map_ServerInfo = new HashMap<>();
    public static Map<Integer, VmInfo> map_vmInfo = new HashMap<>();
    public static Map<String, Server> map_server = new HashMap<>();
    public static Map<String, Vm> map_vm = new HashMap<>();
    public static List<List<Operation>> list_operation = new ArrayList<List<Operation>>();
    public static List<Server1> list_server = new ArrayList<>();

    //存放服务器id及对应虚拟机id的Map
    public static List<List<Integer>> list_server_vm = new ArrayList<List<Integer>>();
    //存放已买服务器型号及对应id的Map，用于id重映射
//    public static List<Integer> list_id = new ArrayList<>();
//    public static Map<String,List<Integer>> map_purchase = new HashMap<>();
    public static int[] map = new int[100000];
    public static Integer index = 0;


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
        long cost_all = 0;

        //每天操作计算
        int server_num = 0;
        int vm_num = 0;
        //每天迁移计算
        int vm_amount = 0;//每天当前为止还在运行的vm总量

        int i, j, num, m, n;
        List list_1 = Read.Read1();
        map_server = (Map<String, Server>) list_1.get(0);
        map_vm = (Map<String, Vm>) list_1.get(1);
        list_operation = (List<List<Operation>>) list_1.get(2);
        list_server = (List<Server1>) list_1.get(3);
//        //本地读取数据
//        map_server = (Map<String,Server>)Read.ReadServer().get(0);
//        list_server = (List<Server1>)Read.ReadServer().get(1);
//        map_vm = Read.ReadVm();
//        list_operation = Read.ReadOperation();


        //储存了800天的操作，每天的操作数如何获取

        //用来储存每一天操作的list
        List<Operation> list_by_day = new ArrayList<Operation>();

        //储存每一台vm对应信息list
        List<vm_server> vm_server = new ArrayList<>();
//        Map<Integer,VmInfo> map_VmInfo = new HashMap<>();
//        Map<Integer,ServerInfo> map_ServerInfo = new HashMap<>();

//        Integer server_id = 0;

//        BuyServer();
        int list1 = list_operation.size();
        for (i = 0; i < list1; i++) {
            list_by_day = list_operation.get(i);
//            System.out.println("第"+(i+1)+"天");

            int list2 = list_by_day.size();
            Map<String, List<Integer>> map_purchase = new HashMap<>();//每天更新储存服务器型号及类别的Map
            //i代表第几天 j代表第几个操作
            for (j = 0; j < list2; j++) {
                int[] array_score = new int[100000];
                int[] array_score_B = new int[100000];
                Arrays.fill(array_score,100000000);
                Arrays.fill(array_score_B,100000000);
                VmInfo vmInfo = new VmInfo();


                //判断是add还是del
                //add的话对照虚拟机vm型号判断是单节点部署还是双节点部署
                //判断当前有没有可供部署的服务器，如果没有的话就要购买服务器进行增加，同时还要记录是第i天增加的


                if (list_by_day.get(j).getOperation_name().equals("add")) {

                    //判断是否双节点 1为双节点
                    if (map_vm.get(list_by_day.get(j).getVm_name()).getDouble_node() == 1) {
                        //双节点部署需要将虚拟机两种资源减半
                        int cpu_core = map_vm.get(list_by_day.get(j).getVm_name()).getVm_cpu_core() / 2;
                        int memory = map_vm.get(list_by_day.get(j).getVm_name()).getVm_memory() / 2;
                        //serverInfo起始便有购买的第一个服务器
                        //如果serverInfo中有已购买的服务器，则看服务器剩余的CPU核数和内存是否能存入虚拟机
                        //挨个遍历已有的服务器，先比较AB节点剩余内核和内存与当前虚拟机，若有余量则加入，若没有则购买
                        /**
                         * 因为serverInfo的setserverid默认为0，id比数组下标多一个
                         */
                        for (m = 0; m < server_id; m++) {
                            //已购服务器的内核数和内存大小均大于等于map_vm中对应虚拟机的大小，则减去相应的大小
                            //服务器含有的虚拟机数量加一
                            //同时将存储进的服务器信息写入
                            /**
                             * 根据cpu和mem设置评价函数选择放置虚拟机的服务器
                             */
                            int score_cpu = 0;
                            int score_mem = 0;
                            int score = 0;

                            //选取双节点服务器中容量小的那个节点作为评判指标
                            if (serverInfo[m].getA_cpu_core() >= serverInfo[m].getB_cpu_core()) {
                                score_cpu = serverInfo[m].getB_cpu_core();
                            } else {
                                score_cpu = serverInfo[m].getA_cpu_core();
                            }
                            if (serverInfo[m].getA_memory() >= serverInfo[m].getB_memory()) {
                                score_mem = serverInfo[m].getB_memory();
                            } else {
                                score_mem = serverInfo[m].getA_memory();
                            }
                            if (score_cpu >= cpu_core && score_mem >= memory) {
                                score = 2 * (score_cpu - cpu_core) * (score_cpu - cpu_core) + (score_mem - memory) * (score_mem - memory);
                                array_score[m] = score;
                            }


//                            if (serverInfo[m].getA_cpu_core() >= cpu_core && serverInfo[m].getA_memory() >= memory && serverInfo[m].getB_cpu_core() >= cpu_core && serverInfo[m].getB_memory() >= memory) {
//                                serverInfo[m].setA_cpu_core(serverInfo[m].getA_cpu_core() - cpu_core);
//                                serverInfo[m].setB_cpu_core(serverInfo[m].getB_cpu_core() - cpu_core);
//                                serverInfo[m].setA_memory(serverInfo[m].getA_memory() - memory);
//                                serverInfo[m].setB_memory(serverInfo[m].getB_memory() - memory);
//                                serverInfo[m].setVm_num(serverInfo[m].getVm_num() + 1);
//                                vmInfo.setServer_id(m + 1);
////                                //针对已购服务器增加虚拟机
////                                List<Integer> list_vm_id = list_server_vm.get(m);
////                                list_vm_id.add(list_by_day.get(j).getVm_id());
////                                list_server_vm.set(m,list_vm_id);
//
////                                break;//若满足条件跳出for循环
//                            }
                            //若不满足条件跳出本次循环进入下一个已购买服务器
                        }
                        int min = min(array_score);
                        if(i != 0 && j != 0 && min != 100000000){
                            //获取最小值的下标
                            int min_index = minIndex(array_score);
                            serverInfo[min_index].setA_cpu_core(serverInfo[min_index].getA_cpu_core() - cpu_core);
                            serverInfo[min_index].setB_cpu_core(serverInfo[min_index].getB_cpu_core() - cpu_core);
                            serverInfo[min_index].setA_memory(serverInfo[min_index].getA_memory() - memory);
                            serverInfo[min_index].setB_memory(serverInfo[min_index].getB_memory() - memory);
                            serverInfo[min_index].setVm_num(serverInfo[min_index].getVm_num() + 1);
                            vmInfo.setServer_id(min_index + 1);
                        }

                        /**
                         * vmInfo的id默认为0
                         */
                        if (vmInfo.getServer_id() == 0) {
                            //如果没有服务器满足条件则购买
                            BuyServer(cpu_core, memory);

//                            //针对刚购买的服务器增加虚拟机
//                            List<Integer> list_vm_id = new ArrayList<>();
//                            list_vm_id.add(list_by_day.get(j).getVm_id());
//                            list_server_vm.set(server_id - 1,list_vm_id);

                            //重映射操作
                            //如果原来没有买这个型号的服务器 map_purchase中存储的id没有多一个
                            if (map_purchase.get(serverInfo[server_id - 1].getServer_name()) == null || map_purchase.get(serverInfo[server_id - 1].getServer_name()).isEmpty()) {
                                List<Integer> list = new ArrayList<>();
                                list.add(server_id - 1);
                                map_purchase.put(serverInfo[server_id - 1].getServer_name(), list);
                            } else {
                                List<Integer> list = map_purchase.get(serverInfo[server_id - 1].getServer_name());
                                list.add(server_id - 1);
                                map_purchase.put(serverInfo[server_id - 1].getServer_name(), list);
                            }
                            serverInfo[server_id - 1].setA_cpu_core(serverInfo[server_id - 1].getA_cpu_core() - cpu_core);
                            serverInfo[server_id - 1].setB_cpu_core(serverInfo[server_id - 1].getB_cpu_core() - cpu_core);
                            serverInfo[server_id - 1].setA_memory(serverInfo[server_id - 1].getA_memory() - memory);
                            serverInfo[server_id - 1].setB_memory(serverInfo[server_id - 1].getB_memory() - memory);
                            vmInfo.setServer_id(server_id);
                        }
                        /**
                         * 有问题，每次都要重新买服务器
                         */

                        //双节点
                        vmInfo.setNode(null);
                    } else {//若为单节点
                        int cpu_core = map_vm.get(list_by_day.get(j).getVm_name()).getVm_cpu_core();
                        int memory = map_vm.get(list_by_day.get(j).getVm_name()).getVm_memory();
                        //数组判断第一个元素是否为null从而判断是否存入元素
                        //如果map_ServerInfo中有已购买的服务器，则看服务器剩余的CPU核数和内存是否能存入虚拟机
                        //挨个遍历已有的服务器，先比较AB节点剩余内核和内存与当前虚拟机，若有余量则加入，若没有则购买
                        for (n = 0; n < server_id; n++) {
                            //已购服务器的内核数和内存大小均大于等于map_vm中对应虚拟机的大小，则在entry中减去相应的大小
                            //同时将存储进的服务器信息写入
                            /**
                             * 根据cpu和mem设置评价函数选择放置虚拟机的服务器
                             */
                            int score_cpu_A = 0;
                            int score_mem_A = 0;
                            int score_cpu_B = 0;
                            int score_mem_B = 0;
                            int score = 0;

                            score_cpu_A = serverInfo[n].getA_cpu_core();
                            score_mem_A = serverInfo[n].getA_memory();
                            score_cpu_B = serverInfo[n].getB_cpu_core();
                            score_mem_B = serverInfo[n].getB_memory();

                            if (score_cpu_A >= cpu_core && score_mem_A >= memory) {
                                score = 2 * (score_cpu_A - cpu_core) * (score_cpu_A - cpu_core) + (score_mem_A - memory) * (score_mem_A - memory);
                                array_score[n] = score;
                            } else if(score_cpu_B >= cpu_core && score_mem_B >= memory){
                                score = 2 * (score_cpu_B - cpu_core) * (score_cpu_B - cpu_core) + (score_mem_B - memory) * (score_mem_B - memory);
                                array_score_B[n] = score;

                            }


//                            if (serverInfo[n].getA_cpu_core() >= cpu_core && serverInfo[n].getA_memory() >= memory) {
//                                serverInfo[n].setA_cpu_core(serverInfo[n].getA_cpu_core() - cpu_core);
//                                serverInfo[n].setA_memory(serverInfo[n].getA_memory() - memory);
//                                serverInfo[n].setVm_num(serverInfo[n].getVm_num() + 1);
//
////                                //针对已购服务器增加虚拟机
////                                List<Integer> list_vm_id = list_server_vm.get(n);
////                                list_vm_id.add(list_by_day.get(j).getVm_id());
////                                list_server_vm.set(n,list_vm_id);
//
//                                vmInfo.setNode("A");
//                                vmInfo.setServer_id(n + 1);
//                                break;//若满足条件跳出for循环
//                            } else if (serverInfo[n].getB_cpu_core() >= cpu_core && serverInfo[n].getB_memory() >= memory) {
//                                serverInfo[n].setB_cpu_core(serverInfo[n].getB_cpu_core() - cpu_core);
//                                serverInfo[n].setB_memory(serverInfo[n].getB_memory() - memory);
//                                serverInfo[n].setVm_num(serverInfo[n].getVm_num() + 1);
//
////                                //针对已购服务器增加虚拟机
////                                List<Integer> list_vm_id = list_server_vm.get(n);
////                                list_vm_id.add(list_by_day.get(j).getVm_id());
////                                list_server_vm.set(n,list_vm_id);
//
//                                vmInfo.setNode("B");
//                                vmInfo.setServer_id(n + 1);
//                                break;//若满足条件跳出for循环
//                            }
                            //若不满足条件跳出本次循环进入下一个已购买服务器
                        }
                        if(i != 0 && j != 0){
                            //获取最小值的下标
                            int min_index_A = minIndex(array_score);
                            int min_index_B = minIndex(array_score_B);
                            int min_A = min(array_score);
                            int min_B = min(array_score_B);
                            if (min_A != 100000000 || min_B != 100000000){
                                if(min_A <= min_B){
                                    serverInfo[min_index_A].setA_cpu_core(serverInfo[min_index_A].getA_cpu_core() - cpu_core);
                                    serverInfo[min_index_A].setA_memory(serverInfo[min_index_A].getA_memory() - memory);
                                    serverInfo[min_index_A].setVm_num(serverInfo[min_index_A].getVm_num() + 1);
                                    vmInfo.setNode("A");
                                    vmInfo.setServer_id(min_index_A + 1);
                                }else {
                                    serverInfo[min_index_B].setB_cpu_core(serverInfo[min_index_B].getB_cpu_core() - cpu_core);
                                    serverInfo[min_index_B].setB_memory(serverInfo[min_index_B].getB_memory() - memory);
                                    serverInfo[min_index_B].setVm_num(serverInfo[min_index_B].getVm_num() + 1);
                                    vmInfo.setNode("B");
                                    vmInfo.setServer_id(min_index_B + 1);
                                }
                            }

                        }


                        if (vmInfo.getServer_id() == 0) {
                            //如果没有服务器满足条件则购买
                            BuyServer(cpu_core, memory);
                            //重映射操作
                            //如果原来没有买这个型号的服务器 map_purchase中存储的id没有多一个
                            if (map_purchase.get(serverInfo[server_id - 1].getServer_name()) == null || map_purchase.get(serverInfo[server_id - 1].getServer_name()).isEmpty()) {
                                List<Integer> list = new ArrayList<>();
                                list.add(server_id - 1);
                                map_purchase.put(serverInfo[server_id - 1].getServer_name(), list);
                            } else {
                                List<Integer> list = map_purchase.get(serverInfo[server_id - 1].getServer_name());
                                list.add(server_id - 1);
                                map_purchase.put(serverInfo[server_id - 1].getServer_name(), list);
                            }
//                            //针对刚购买的服务器增加虚拟机
//                            List<Integer> list_vm_id = new ArrayList<>();
//                            list_vm_id.add(list_by_day.get(j).getVm_id());
//                            list_server_vm.set(server_id - 1,list_vm_id);

                            //若新买服务器默认放A节点
                            serverInfo[server_id - 1].setA_cpu_core(serverInfo[server_id - 1].getA_cpu_core() - cpu_core);
                            serverInfo[server_id - 1].setA_memory(serverInfo[server_id - 1].getA_memory() - memory);
                            vmInfo.setNode("A");
                            vmInfo.setServer_id(server_id);
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
                }
                /**
                 * del操作
                 */
                else {
                    int vm_id = list_by_day.get(j).getVm_id();
                    //del虚拟机操作
                    map_vmInfo.get(vm_id).setStatus(false);
                    //判断虚拟机所在的服务器 此服务器id需要从map_vmInfo中读取
                    int serverId = map_vmInfo.get(vm_id).getServer_id() - 1;
//                String a = map_vmInfo.get(Integer.valueOf(list_by_day.get(j).getVm_id())).getNode();
                    //判断是否双节点 null为双节点

//                    //删除服务器中对应的虚拟机
//                    List<Integer> list_vm_id = list_server_vm.get(serverId);
//                    list_vm_id.remove(list_by_day.get(j).getVm_id());
//                    list_server_vm.set(serverId,list_vm_id);

                    if (map_vmInfo.get(vm_id).getNode() == null) {
                        //双节点部署需要将虚拟机两种资源减半
                        int cpu_core = map_vm.get(map_vmInfo.get(vm_id).getVm_name()).getVm_cpu_core() / 2;
                        int memory = map_vm.get(map_vmInfo.get(vm_id).getVm_name()).getVm_memory() / 2;

                        //Integer a = map_ServerInfo.get(serverId).getA_cpu_core();
                        serverInfo[serverId].setA_cpu_core(serverInfo[serverId].getA_cpu_core() + cpu_core);
                        serverInfo[serverId].setB_cpu_core(serverInfo[serverId].getB_cpu_core() + cpu_core);
                        serverInfo[serverId].setA_memory(serverInfo[serverId].getA_memory() + memory);
                        serverInfo[serverId].setB_memory(serverInfo[serverId].getB_memory() + memory);
                        serverInfo[serverId].setVm_num(serverInfo[serverId].getVm_num() - 1);

                    } else {//若为单节点
                        int cpu_core = map_vm.get(map_vmInfo.get(vm_id).getVm_name()).getVm_cpu_core();
                        int memory = map_vm.get(map_vmInfo.get(vm_id).getVm_name()).getVm_memory();

                        if (map_vmInfo.get(vm_id).getNode().equals("A")) {
                            serverInfo[serverId].setA_cpu_core(serverInfo[serverId].getA_cpu_core() + cpu_core);
                            serverInfo[serverId].setA_memory(serverInfo[serverId].getA_memory() + memory);
                            serverInfo[serverId].setVm_num(serverInfo[serverId].getVm_num() - 1);
                        } else {
                            serverInfo[serverId].setB_cpu_core(serverInfo[serverId].getB_cpu_core() + cpu_core);
                            serverInfo[serverId].setB_memory(serverInfo[serverId].getB_memory() + memory);
                            serverInfo[serverId].setVm_num(serverInfo[serverId].getVm_num() - 1);
                        }

                    }

                }


            }
            /**
             * 进行迁移操作
             */

//            int vm_amount_day = 0;
//            for(Map.Entry entry : map_vmInfo.entrySet()) {
//                VmInfo vmInfo = (VmInfo)entry.getValue();
//                if(vmInfo.getStatus() == true){
//                    vm_amount_day++;
//                }
//            }//计算该天当前虚拟机总量
//            vm_amount += vm_amount_day;

//            int vm_amount_bynow = 0;
//            //while可能陷入死循环，用if break判断
//            while(vm_amount_bynow <= vm_amount * 5 / 1000){
//                for(int x = 0; x < list_server_vm.size(); x++){
//                    if(list_server_vm.get(x).size() <= 2){
//                        List<Integer> list_use = list_server_vm.get(x);
//                        for(int y = 0; y < list_use.size(); y++){
//                            String vm_name = map_vmInfo.get(list_use.get(y)).getVm_name();
//                            int node = map_vm.get(vm_name).getDouble_node();
//                            int vm_cpu = map_vm.get(vm_name).getVm_cpu_core();
//                            int vm_mem = map_vm.get(vm_name).getVm_memory();
//                            //双节点
//                            if(node == 1){
//                                int cpu_core = vm_cpu / 2;
//                                int memory = vm_mem / 2;
//
//                                for (m = 0; m < server_id; m++) {
//                                    //已购服务器的内核数和内存大小均大于等于map_vm中对应虚拟机的大小，则减去相应的大小
//                                    //服务器含有的虚拟机数量加一
//                                    //同时将存储进的服务器信息写入
//                                    if (serverInfo[m].getA_cpu_core() >= cpu_core && serverInfo[m].getA_memory() >= memory && serverInfo[m].getB_cpu_core() >= cpu_core && serverInfo[m].getB_memory() >= memory) {
//                                        serverInfo[m].setA_cpu_core(serverInfo[m].getA_cpu_core() - cpu_core);
//                                        serverInfo[m].setB_cpu_core(serverInfo[m].getB_cpu_core() - cpu_core);
//                                        serverInfo[m].setA_memory(serverInfo[m].getA_memory() - memory);
//                                        serverInfo[m].setB_memory(serverInfo[m].getB_memory() - memory);
//                                        serverInfo[m].setVm_num(serverInfo[m].getVm_num() + 1);
//                                        vmInfo.setServer_id(m + 1);
//                                        //针对已购服务器增加虚拟机
//                                        List<Integer> list_vm_id = list_server_vm.get(m);
//                                        list_vm_id.add(list_by_day.get(j).getVm_id());
//                                        list_server_vm.set(m,list_vm_id);
//                                        break;//若满足条件跳出for循环
//                                    }
//
//
//
//                            }
//                        }
//
//                    }
//                }
//                vm_amount_bynow++;
//            }


            /**
             * 先判断第i天是否购买了服务器,若买了则买 server_num_now 台
             */
//            int server_num_now = server_id - server_num;
//            server_num = server_id;
//            if(server_num_now == 0){
//                System.out.println("(purchase, 0)");
//            }else{
//                System.out.println("(purchase, " + map_purchase.size() + ")");
//                System.out.println("(" + serverInfo[0].getServer_name() + ", " + server_num_now + ")");
//            }
            /**
             * 购买服务器
             */
            System.out.println("(purchase, " + map_purchase.size() + ")");
            for (Map.Entry entry : map_purchase.entrySet()) {
                List<Integer> server_amount = (List<Integer>) entry.getValue();
                System.out.println("(" + entry.getKey() + ", " + server_amount.size() + ")");
                for (int h = 0; h < server_amount.size(); h++) {
                    map[server_amount.get(h)] = index++;
                }
            }
            //需要按顺序输出id和node

            /**
             * 迁移
             */
            System.out.println("(migration, 0)");

            /**
             * 遍历vm_server获得server_id和node
             */
            for (num = vm_num; num < map_vmInfo.size(); num++) {
                int id = vm_server.get(num).getServer_id() - 1;
                String node = vm_server.get(num).getNode();
                int true_id = map[id];//重映射后的id
                if (node == null || node.isEmpty()) {
                    System.out.println("(" + true_id + ")");
                } else if (node.equals("A")) {
                    System.out.println("(" + true_id + ", A)");
                } else {
                    System.out.println("(" + true_id + ", B)");
                }
            }
            vm_num = map_vmInfo.size();
//            /**
//             * 在执行完每日的操作后，对每日的输出进行计算
//             */
//            int day = 0;
//            for (int l = 0; l < server_id; l++){
//                if(serverInfo[l].getStatus()){
//                    day++;
//                }
//            }
//            cost_energy_day = day * map_server.get(serverInfo[0].getServer_name()).getCost_energy();
//            cost_energy_all += cost_energy_day;
        }

//        cost_hardware = server_id * map_server.get(serverInfo[0].getServer_name()).getCost_hardware();
//        cost_all = cost_energy_all + cost_hardware;
//        System.out.println(cost_all);


    }


//    /**
//     * 购买服务器策略
//     */
//    public static void BuyServer(){
//
//
//        ServerInfo server = new ServerInfo();
//        //暂时默认买第一种服务器
//        server.setServer_name(map_server.keySet().iterator().next());
//
//        server.setA_cpu_core(map_server.get(map_server.keySet().iterator().next()).getCpu_core() / 2);
//        server.setA_memory(map_server.get(map_server.keySet().iterator().next()).getMemory() / 2);
//        server.setB_cpu_core(map_server.get(map_server.keySet().iterator().next()).getCpu_core() / 2);
//        server.setB_memory(map_server.get(map_server.keySet().iterator().next()).getMemory() / 2);
//        server.setStatus(true);//默认没有开启使用
//
//        serverInfo[server_id] = server;
//        server_id++;
//
//    }

    /**
     * 购买服务器策略(满足条件的服务器按硬件成本由低到高排序)
     */
    public static void BuyServer(int cpu_core, int memory) {
        ServerInfo server = new ServerInfo();
        for (int i = 0; i < list_server.size(); i++) {
            if (list_server.get(i).getCpu_core() / 2 >= cpu_core && list_server.get(i).getMemory() / 2 >= memory) {
                server.setServer_name(list_server.get(i).getServer_name());
                server.setA_cpu_core(list_server.get(i).getCpu_core() / 2);
                server.setA_memory(list_server.get(i).getMemory() / 2);
                server.setB_cpu_core(list_server.get(i).getCpu_core() / 2);
                server.setB_memory(list_server.get(i).getMemory() / 2);
//                server.setStatus(true);
                server.setVm_num(1);//当前这个服务器含有一个虚拟机
                break;

            }
        }


        serverInfo[server_id] = server;
        server_id++;

    }


    //获取最小值索引
    public static int minIndex(int[] arr) {
        int minIndex = 0;
        ;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < arr[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
    //获取最小值
    public static int min(int[] arr){
        int min=arr[0];
        for(int i=0;i<arr.length;i++){
            if(arr[i]<min){
                min=arr[i];
            }
        }
        return min;
    }


}









