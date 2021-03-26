package com.huawei.java.main;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 根据文件读取信息
 */
public class Read {

    public final static String path = "C:\\Users\\11931\\Desktop\\华为软挑\\training-2.txt";
    //public static List lines = new ArrayList();
    /**
     * 读取服务器传来的信息
     * @return map
     * @throws
     */
    public static List Read1(){
        //服务器上读取数据方式

        Scanner scanner = new Scanner(System.in);

        //服务器种类
        int kinds_server = Integer.parseInt(scanner.nextLine());
        Server1[] server1s = new Server1[kinds_server];
        List<Server1> list_server = new ArrayList<>();

        Map<String,Server> map_server = new HashMap<String,Server>(kinds_server);

        for(int i = 0; i < kinds_server; i++){
            Server1 server1 = new Server1();
            String string = (String)(scanner.nextLine());
            String subString = string.substring(1,string.length() - 1);
            String[] splitString = subString.split(",");
            server1.setServer_name(splitString[0]);
            server1.setCpu_core(Integer.parseInt(splitString[1].trim()));
            server1.setMemory(Integer.parseInt(splitString[2].trim()));
            server1.setCost_hardware(Integer.parseInt(splitString[3].trim()));
            server1.setCost_energy(Integer.parseInt(splitString[4].trim()));
            server1s[i] = server1;
            list_server.add(server1);
        }
        Arrays.sort(server1s);
        //存服务器数据
        Collections.sort(list_server);


        for(int j = 0; j < kinds_server; j++){
            Server server = new Server();
            server.setCpu_core(server1s[j].getCpu_core());
            server.setMemory(server1s[j].getMemory());
            server.setCost_hardware(server1s[j].getCost_hardware());
            server.setCost_energy(server1s[j].getCost_energy());
            map_server.put(server1s[j].getServer_name(),server);
        }
//        return map_server;
        //虚拟机种类
        int kinds_vm = Integer.parseInt(scanner.nextLine());
        Map<String,Vm> map_vm = new HashMap<>();
        //向map_vm中存虚拟机数据
        for(int j = 0; j < kinds_vm; j++){
            Vm vm = new Vm();
            String string = (String)(scanner.nextLine());
            String subString = string.substring(1,string.length() - 1);
            String[] splitString = subString.split(",");
            //vm.setVm_name(splitString[0]);
            vm.setVm_cpu_core(Integer.parseInt(splitString[1].trim()));
            vm.setVm_memory(Integer.parseInt(splitString[2].trim()));
            vm.setDouble_node(Integer.parseInt(splitString[3].trim()));
            map_vm.put(splitString[0],vm);
        }


        //获取操作天数
        int num_operation_day = Integer.parseInt(scanner.nextLine());
        //数组索引从0开始，一共操作天数个
        int[] num_operation = new int[num_operation_day + 1];
        num_operation[0] = 0;

        //用来存储所有天操作的list
        List<List<Operation>> list_operation = new ArrayList<List<Operation>>(num_operation_day);

        int total = 0;
        /**
         * 从获得操作天数后需要进行一个两重for循环将数据存入list中
         */
        for(int i = 0; i < num_operation_day; i++){

            //获取第一天操作命令的数量
            total = total + num_operation[i];
            num_operation[i + 1] = Integer.parseInt(scanner.nextLine());
            //用来储存每一天操作的list
            List<Operation> list_by_day = new ArrayList<Operation>();
            for(int j = 0; j < num_operation[i + 1] ; j++){


                Operation op = new Operation();
                String string = (String)(scanner.nextLine());
                String subString = string.substring(1,string.length() - 1);
                String[] splitString = subString.split(",");
                if(splitString[0].equals("add")){
                    op.setOperation_name(splitString[0]);
                    op.setVm_name(splitString[1].substring(1,splitString[1].length()));
                    op.setVm_id(Integer.parseInt(splitString[2].substring(1,splitString[2].length()).trim()));//去掉数据中的空格
                    list_by_day.add(op);
                }else {
                    op.setOperation_name(splitString[0]);
                    op.setVm_name(null);
                    op.setVm_id(Integer.parseInt(splitString[1].substring(1,splitString[1].length()).trim()));
                    list_by_day.add(op);
                }

            }
            list_operation.add(list_by_day);

        }
        scanner.close();
        List list = new ArrayList();
        list.add(map_server);
        list.add(map_vm);
        list.add(list_operation);
        list.add(list_server);
        return list;


    }
    /**
     * 读取服务器信息
     * @return map
     * @throws IOException
     */
    public static List ReadServer() throws IOException {

        List list = new ArrayList();
        Path path = Paths.get(Read.path);

        List lines = Files.readAllLines(path);

        //80 800
        int kinds_server = Integer.parseInt((String)lines.get(0));

        Server1[] server1s = new Server1[kinds_server];

        Map<String,Server> map = new HashMap<String,Server>(kinds_server);

        List<Server1> list_server = new ArrayList<>();

        for(int i = 0; i < kinds_server; i++){
            Server1 server1 = new Server1();
            String string = (String) lines.get(i + 1);
            String subString = string.substring(1,string.length() - 1);
            String[] splitString = subString.split(",");
            server1.setServer_name(splitString[0]);
            server1.setCpu_core(Integer.parseInt(splitString[1].trim()));
            server1.setMemory(Integer.parseInt(splitString[2].trim()));
            server1.setCost_hardware(Integer.parseInt(splitString[3].trim()));
            server1.setCost_energy(Integer.parseInt(splitString[4].trim()));
            server1s[i] = server1;
            list_server.add(server1);

        }
        Arrays.sort(server1s);
        Collections.sort(list_server);//按硬件成本从小到大排列
        //存服务器数据

        for(int j = 0; j < kinds_server; j++){
            Server server = new Server();
//            server.setServer_name(splitString[0]);
            server.setCpu_core(server1s[j].getCpu_core());
            server.setMemory(server1s[j].getMemory());
            server.setCost_hardware(server1s[j].getCost_hardware());
            server.setCost_energy(server1s[j].getCost_energy());
            map.put(server1s[j].getServer_name(),server);
        }
        list.add(map);
        list.add(list_server);

        return list;
    }

    /**
     * 读取虚拟器信息
     * @return map
     * @throws IOException
     */
    public static Map<String,Vm> ReadVm() throws IOException{
        Path path = Paths.get(Read.path);

        List lines = Files.readAllLines(path);

        //80 800
        int kinds_server = Integer.parseInt((String)lines.get(0));
        int kinds_vm = Integer.parseInt((String)lines.get(kinds_server + 1));


        Map<String,Vm> map = new HashMap<String,Vm>(kinds_vm);

        //向list_vm中存虚拟机数据
        for(int j = kinds_server + 1; j < kinds_server + kinds_vm + 1; j++){
            Vm vm = new Vm();
            String string = (String) lines.get(j + 1);
            String subString = string.substring(1,string.length() - 1);
            String[] splitString = subString.split(",");
            //vm.setVm_name(splitString[0]);
            vm.setVm_cpu_core(Integer.parseInt(splitString[1].trim()));
            vm.setVm_memory(Integer.parseInt(splitString[2].trim()));
            vm.setDouble_node(Integer.parseInt(splitString[3].trim()));
            map.put(splitString[0],vm);
        }
        return map;
    }


    /**
     * 读取每日操作信息
     * @return list_operation
     * @throws IOException
     */
    public static List<List<Operation>> ReadOperation() throws IOException{

        //服务器上读取数据方式
//        List lines = new ArrayList();
//        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNextLine()) {
//            String str = scanner.nextLine();
//            lines.add(str);
//        }
//        scanner.close();

        //本地读取文件方式
        Path path = Paths.get(Read.path);

        List lines = Files.readAllLines(path);

        //80 800
        int kinds_server = Integer.parseInt((String)lines.get(0));
        int kinds_vm = Integer.parseInt((String)lines.get(kinds_server + 1));

        //输出操作天数 training_1里面是800的所在行
        //System.out.println(lines.get(kinds_server + kinds_vm + 2));
        //获取操作天数
        int num_operation_day = Integer.parseInt((String)lines.get(kinds_server + kinds_vm + 2));
        //从location_line_day开始进入循环的行数计数 142所在行
        int location_line_day = kinds_server + kinds_vm + 3;
        //数组索引从0开始，一共操作天数个
        int[] num_operation = new int[num_operation_day + 1];
        num_operation[0] = 0;

        //用来存储所有天操作的list
        List<List<Operation>> list_operation = new ArrayList<List<Operation>>(num_operation_day);

        int total = 0;
        /**
         * 从获得操作天数后需要进行一个两重for循环将数据存入list中
         */
        for(int i = 0; i < num_operation_day; i++){

            //获取第一天操作命令的数量
            total = total + num_operation[i];
            num_operation[i + 1] = Integer.parseInt((String)lines.get(location_line_day + total + i));
            //用来储存每一天操作的list
            List<Operation> list_by_day = new ArrayList<Operation>();
            for(int j = 0; j < num_operation[i + 1] ; j++){


                Operation op = new Operation();
                String string = (String) lines.get(location_line_day + total + i + j + 1);
                String subString = string.substring(1,string.length() - 1);
                String[] splitString = subString.split(",");
                if(splitString[0].equals("add")){
                    op.setOperation_name(splitString[0]);
                    op.setVm_name(splitString[1].substring(1,splitString[1].length()));
                    op.setVm_id(Integer.parseInt(splitString[2].substring(1,splitString[2].length()).trim()));//去掉数据中的空格
                    list_by_day.add(op);
                }else {
                    op.setOperation_name(splitString[0]);
                    op.setVm_name(null);
                    op.setVm_id(Integer.parseInt(splitString[1].substring(1,splitString[1].length()).trim()));
                    list_by_day.add(op);
                }

            }
            list_operation.add(list_by_day);

        }
        return list_operation;
    }


}
