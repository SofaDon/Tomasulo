import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class datainit {
    public static data tomdata;
    public static void init() {
        tomdata.pc = 0;

        tomdata.jump_exist = false;

        tomdata.cur_order = new int[5];

        tomdata.rs_busy = new int[9];
        tomdata.rs_index = new int[9];
        tomdata.rs_op = new String[]{"   ","  ","   ","   ","   ","   ","   ","   ","   "};
        tomdata.rs_Vj = new String[]{"    ","    ","    ","    ","    ","    ","    ","    ","    "};
        tomdata.rs_Vk = new String[]{"    ","    ","    ","    ","    ","    ","    ","    ","    "};
        tomdata.rs_Qj = new String[]{"    ","    ","    ","    ","    ","    ","    ","    ","    "};
        tomdata.rs_Qk = new String[]{"    ","    ","    ","    ","    ","    ","    ","    ","    "};
        tomdata.rs_state = new int[9];
        tomdata.Ars_queue = new LinkedList<Integer>();
        tomdata.Mrs_queue = new LinkedList<Integer>();
        tomdata.Ars_queue_length = 0;
        tomdata.Mrs_queue_length = 0;
        tomdata.Ars_full = false;
        tomdata.Mrs_full = false;

        tomdata.lb_index = new int[3];
        tomdata.lb_busy = new int[3];
        tomdata.lb_addr = new String[]{"","",""};
        tomdata.lb_full = false;
        tomdata.lb_wait = -1;

        tomdata.r_state = new String[32];
        for (int i = 0; i < 32; ++i) {
            tomdata.r_state[i] = "0x0";
        }

        tomdata.r_value = new String[32];
        tomdata.r_real_value = new int[32];
        for (int i = 0; i < 32; ++i) {
            tomdata.r_value[i] = "0x0";
            tomdata.r_real_value[i] = 0;
        }

        tomdata.add_busy = new int[3];
        tomdata.cur_add = new int[3];
        tomdata.add_remain_circle = new int[3];
        tomdata.add_full = false;

        tomdata.mul_busy = new int[2];
        tomdata.cur_mul = new int[2];
        tomdata.mul_remain_circle = new int[2];
        tomdata.mul_full = false;

        tomdata.load_busy = new int[2];
        tomdata.cur_load = new int[2];
        tomdata.load_remain_circle = new int[2];
        tomdata.load_full = false;
    }
}