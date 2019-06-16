import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.math.BigInteger;
import java.util.Scanner;

public class test {
    public static boolean if_finished = false;

    public static int[][] time_for_orders = new int[2000][3];
    public static String[] string_for_orders = new String[2000];
    public static int tmp_circle = 0;


    public static int LD_cirle =  3;
    public static int JUMP_cirle =  1;
    public static int ADD_cirle =  3;
    public static int SUB_cirle =  3;
    public static int MUL_cirle =  12;
    public static int DIV_cirle =  40;

    public static String[] RS_finished = new String[8];
    public static String[] RS_finished_ans = new String[8];
    public static int RS_finished_cnt = 0;


    public static datainit di;

    //public static String file_0 = "test2.nel";
    public static String file_0 = "../test/data/mul.nel";
    public static int[][] all_orders = new int[2000][5]; //save all orders
    public static int all_orders_length = 0;
    public static void main(String[] args) {
        di = new datainit();
        di.init();

        read_file(file_0);
        //test();

        int test_circle = 0;
        Scanner sc = new Scanner(System.in); 
        System.out.println("please input the test_circle: "); 
        test_circle = sc.nextInt();
        for (int i = 0; i < test_circle; ++i) {
        //while (!if_finished){
            judge_finish();
            tmp_circle++;
            //System.out.println("-----------------");
            //System.out.println("Cirle " + (tmp_circle));
            before_deal_order();
            deal_one_oreder();
            after_deal_order();
            RS_update();
            update_ready_queue();
            issue_ready_order();
            // System.out.println("-----------------");
            // System.out.println();
            // System.out.println();
            // System.out.println();
        }
        show();
        print_time();
    }

    public static void issue_ready_order() {
        //System.out.println("issue_ready_order:");
        if (!di.tomdata.add_full) {
            for (int i = 0; i < 3; ++i) {
                if (di.tomdata.add_busy[i] == 0 && di.tomdata.Ars_queue_length > 0) {
                    di.tomdata.Ars_queue_length -= 1;
                    int issue_index = di.tomdata.Ars_queue.poll();
                    di.tomdata.add_busy[i] = 1;
                    //System.out.println("di.tomdata.rs_op[i]: " + di.tomdata.rs_op[i]);
                    if (di.tomdata.rs_op[issue_index].equals("JUMP")) {
                        //System.out.println("issue a jump");
                        di.tomdata.add_remain_circle[i] = JUMP_cirle;
                    }
                        
                    else {
                        //System.out.println("issue an add or sub");
                        di.tomdata.add_remain_circle[i] = ADD_cirle;
                    }
                        
                    di.tomdata.cur_add[i] = issue_index;

                    di.tomdata.rs_state[issue_index] = 2;
                }
            }
            di.tomdata.add_full = true;
            for (int i = 0; i < 3; ++i) {
                if (di.tomdata.add_busy[i] == 0) {
                    di.tomdata.add_full = false;
                    break;
                }
            }
        } 

        if (!di.tomdata.mul_full) {
            for (int i = 0; i < 2; ++i) {
                if (di.tomdata.mul_busy[i] == 0 && di.tomdata.Mrs_queue_length > 0) {
                    di.tomdata.Mrs_queue_length -= 1;
                    int issue_index = di.tomdata.Mrs_queue.poll();
                    di.tomdata.mul_busy[i] = 1;

                    //System.out.println(di.tomdata.rs_op[i+6]);

                    if (di.tomdata.rs_op[issue_index].equals("MUL")) {
                        //System.out.println("issue a mul");
                        di.tomdata.mul_remain_circle[i] = MUL_cirle;
                    }
                    else if (di.tomdata.rs_op[issue_index].equals("DIV")) {
                        //System.out.println("issue a div!");
                        if (hex_2_int(di.tomdata.rs_Vk[issue_index]) != 0)
                            di.tomdata.mul_remain_circle[i] = DIV_cirle;
                        else 
                            di.tomdata.mul_remain_circle[i] = 1;
                    }
                    di.tomdata.cur_mul[i] = issue_index;

                    di.tomdata.rs_state[issue_index] = 2;
                }
            }
            di.tomdata.mul_full = true;
            for (int i = 0; i < 2; ++i) {
                if (di.tomdata.mul_busy[i] == 0) {
                    di.tomdata.mul_full = false;
                    break;
                }
            }
        }

        if (!di.tomdata.load_full && di.tomdata.lb_wait >= 0) {
            for (int i = 0; i < 2; ++i) {
                if (di.tomdata.load_busy[i] == 0) {
                    di.tomdata.load_busy[i] = 1;
                    di.tomdata.cur_load[i] = di.tomdata.lb_wait;
                    //System.out.println("------------" + di.tomdata.lb_wait);
                    di.tomdata.load_remain_circle[i] = LD_cirle;
                    di.tomdata.lb_wait = -1;
                }
            }
            di.tomdata.load_full = true;
            for (int i = 0; i < 2; ++i) {
                if (di.tomdata.load_busy[i] == 0) {
                    di.tomdata.load_full = false;
                    break;
                }
            }
        }
        

    }

    public static void update_ready_queue() {
        //System.out.println("update_ready_queue:");
        int[] Ars_ready_index = new int[]{100000, 100000, 100000, 100000, 100000}; 
        int Ars_cnt = 0;
        for (int i = 0; i < 6; ++i) {
            if (di.tomdata.rs_busy[i] == 1 && di.tomdata.rs_state[i] == 0) {
                String Vj = di.tomdata.rs_Vj[i];
                String Vk = di.tomdata.rs_Vk[i];
                //System.out.println("Vj: " + Vj.charAt(0) + "   Vk: " + Vk.charAt(0));
                if ((!di.tomdata.rs_op[i].equals("JUMP")) && Vj.charAt(0) == '0' && Vk.charAt(0) == '0') {
                    //System.out.println("Ars" + (i+1) + " is ready!");
                    Ars_ready_index[Ars_cnt++] = di.tomdata.rs_index[i];
                    di.tomdata.rs_state[i] = 1;
                }
                if (di.tomdata.rs_op[i].equals("JUMP") && Vj.charAt(0) == '0') {
                    //System.out.println("Jump" + (i+1) + " is ready!");
                    Ars_ready_index[Ars_cnt++] = di.tomdata.rs_index[i];
                    di.tomdata.rs_state[i] = 1;
                }
            }
        }
        if (Ars_cnt >= 0) {
            Arrays.sort(Ars_ready_index); //from zmall to large
            for (int i = 0; i < 5; ++i) {
                //System.out.print(Ars_ready_index[i] + "  ");
            }
            //System.out.println();

            for (int i = 0; i < Ars_cnt; ++i) {
                for (int j = 0; j < 6; ++j) {
                    if (di.tomdata.rs_index[j] == Ars_ready_index[i]) {
                        di.tomdata.Ars_queue.offer(j);
                        di.tomdata.Ars_queue_length += 1;
                    }
                }
            }

            // System.out.print("Ars_queue : ");
            // for (int ii : di.tomdata.Ars_queue) {
            //     System.out.print(ii + "  ");
            // }
            // System.out.println();              
        }

        int[] Mrs_ready_index = new int[]{100000, 100000, 100000}; 
        int Mrs_cnt = 0;
        for (int i = 6; i < 9; ++i) {
            if (di.tomdata.rs_busy[i] == 1 && di.tomdata.rs_state[i] == 0) {
                String Vj = di.tomdata.rs_Vj[i];
                String Vk = di.tomdata.rs_Vk[i];
                //System.out.println("Vj: " + Vj.charAt(0) + "   Vk: " + Vk.charAt(0));
                if (Vj.charAt(0) == '0' && Vk.charAt(0) == '0') {
                    //System.out.println("Mrs" + (i-5) + " is ready!");
                    Mrs_ready_index[Mrs_cnt++] = di.tomdata.rs_index[i];
                    di.tomdata.rs_state[i] = 1;
                }
            }
        }
        if (Mrs_cnt >= 0) {
            Arrays.sort(Mrs_ready_index); //from zmall to large
            // for (int i = 0; i < 3; ++i) {
            //     System.out.print(Mrs_ready_index[i] + "  ");
            // }
            // System.out.println();

            for (int i = 0; i < Mrs_cnt; ++i) {
                for (int j = 6; j < 9; ++j) {
                    if (di.tomdata.rs_index[j] == Mrs_ready_index[i] && di.tomdata.rs_state[j] == 1) {
                        di.tomdata.Mrs_queue.offer(j);
                        di.tomdata.Mrs_queue_length += 1;
                    }
                }
            }

            // System.out.print("Mrs_queue : ");
            // for (int ii : di.tomdata.Mrs_queue) {             
            //     System.out.print(ii + "  ");
            // }
            // System.out.println();              
        }

        //System.out.println();
    }

    public static void RS_update() {
        //System.out.println("RS_update : ");
        for (int i = 0; i < RS_finished_cnt; ++i) {
            //System.out.println(RS_finished[i] + " " + RS_finished_ans[i]);
            for (int ii = 0; ii < 9; ++ii) {
                if (di.tomdata.rs_busy[ii] == 1 && di.tomdata.rs_state[ii] == 0) {
                    if (di.tomdata.rs_Qj[ii].equals(RS_finished[i])) {
                        di.tomdata.rs_Qj[ii] = "    ";
                        di.tomdata.rs_Vj[ii] = RS_finished_ans[i];
                    }
                    if (di.tomdata.rs_Qk[ii].equals(RS_finished[i])) {
                        di.tomdata.rs_Qk[ii] = "    ";
                        di.tomdata.rs_Vk[ii] = RS_finished_ans[i];
                    }
                } 
            }
        }
    }

    public static void after_deal_order() {
        //System.out.println("after_deal_order : ");
        for (int i = 0; i < RS_finished_cnt; ++i) {
            String finished_tmp = RS_finished[i];
            String index = finished_tmp.substring(finished_tmp.length()-1);
            String type = finished_tmp.substring(0, finished_tmp.length()-1);
            int tmp_index = Integer.parseInt(index) - 1;
            //System.out.println(finished_tmp + "  " + type + "  " + tmp_index);

            int place = 0;
            if (type.equals("Ars")) {
                for (int ii = 0; ii < 3; ++ii) {
                    if (di.tomdata.cur_add[ii] == tmp_index && di.tomdata.add_busy[ii] == 1) {
                        place = ii;
                        //System.out.println("Ars finished and place is" + ii);
                    }                        
                }
                int finish_index = di.tomdata.rs_index[tmp_index];
                if (time_for_orders[finish_index][2] == 0) {
                    time_for_orders[finish_index][2] = tmp_circle;
                    time_for_orders[finish_index][1] = tmp_circle - 1;
                }

                RS_clear(tmp_index);
                di.tomdata.add_busy[place] = 0;
                di.tomdata.add_full = false;
                di.tomdata.cur_add[place] = 0;
                di.tomdata.Ars_full = false;
            }

            else if (type.equals("Mrs")) {
                if (di.tomdata.cur_mul[0] == tmp_index+6 && di.tomdata.mul_busy[0] == 1)
                    place = 0;
                else if (di.tomdata.cur_mul[1] == tmp_index+6 && di.tomdata.mul_busy[1] == 1)
                    place = 1;

                int finish_index = di.tomdata.rs_index[tmp_index+6];
                if (time_for_orders[finish_index][2] == 0) {
                    time_for_orders[finish_index][2] = tmp_circle;
                    time_for_orders[finish_index][1] = tmp_circle - 1;
                }

                RS_clear(tmp_index+6);
                di.tomdata.mul_busy[place] = 0;
                di.tomdata.mul_full = false;
                di.tomdata.cur_mul[place] = 0;        
                di.tomdata.Mrs_full = false;
            }

            else if (type.equals("LB")) {
                if (di.tomdata.cur_load[0] == tmp_index && di.tomdata.load_busy[0] == 1)
                    place = 0;
                else if (di.tomdata.cur_load[1] == tmp_index && di.tomdata.load_busy[0] == 1)
                    place = 1;
                //System.out.println("LB_place:" + LB_place);

                int finish_index = di.tomdata.lb_index[tmp_index];
                if (time_for_orders[finish_index][2] == 0) {
                    time_for_orders[finish_index][2] = tmp_circle;
                    time_for_orders[finish_index][1] = tmp_circle - 1;
                }

                di.tomdata.lb_busy[tmp_index] = 0;
                di.tomdata.lb_index[tmp_index] = 0; 
                di.tomdata.lb_full = false;
                //System.out.println("di.tomdata.lb_full: " + di.tomdata.lb_full);
                di.tomdata.lb_addr[tmp_index] = "";
                di.tomdata.load_busy[place] = 0;
                di.tomdata.load_full = false;
                di.tomdata.cur_load[place] = 0;         
            }
        }
        //System.out.println();
    }

    public static void before_deal_order() {
        RS_finished_cnt = 0;
        for (int i = 0; i < 3; ++i) {
            if (di.tomdata.add_remain_circle[i] == 0 && di.tomdata.add_busy[i] == 1) {
                //di.tomdata.add_busy[i] = 0;
                //di.tomdata.add_full = false;
                int RS_place = di.tomdata.cur_add[i];
                di.tomdata.rs_state[RS_place] = 0;
                RS_finished[RS_finished_cnt] = "Ars" + String.valueOf(RS_place+1);
                //System.out.println(RS_finished[RS_finished_cnt] + " finsished!!!");
                
                if (!di.tomdata.rs_op[RS_place].equals("JUMP")) {
                    int order_index = di.tomdata.rs_index[RS_place];
                    int[] finish_order = new int[5];
                    for (int ii =0 ; ii < 5; ++ii)
                        finish_order[ii] = all_orders[order_index][ii];
                    int x = finish_order[2];
                    String x1 = di.tomdata.rs_Vj[RS_place];
                    String x2 = di.tomdata.rs_Vk[RS_place];
                    int add_ans = 0;
                    if (finish_order[1] == 1)
                        add_ans = hex_2_int(x1) + hex_2_int(x2);
                    else if (finish_order[1] == 2)
                        add_ans = hex_2_int(x1) - hex_2_int(x2);
                    di.tomdata.r_real_value[x] = add_ans;
                    if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                        di.tomdata.r_value[x] = int_2_hex(add_ans);
                    if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                        di.tomdata.r_state[x] = int_2_hex(add_ans);
                    RS_finished_ans[RS_finished_cnt] = int_2_hex(add_ans);
                }
                else {
                    int order_index = di.tomdata.rs_index[RS_place];
                    int[] finish_order = new int[5];
                    for (int ii =0 ; ii < 5; ++ii)
                        finish_order[ii] = all_orders[order_index][ii];
                    //int x = finish_order[2];
                    String x1 = di.tomdata.rs_Vj[RS_place];
                    int x2 = finish_order[2];
                    int x3 = finish_order[4];
                    if (x2 == hex_2_int(x1))
                        di.tomdata.pc += x3;
                    else 
                        di.tomdata.pc += 1;
                    di.tomdata.jump_exist = false;
                }
                

                RS_finished_cnt++;
                //RS_clear(RS_place);
                //di.tomdata.Ars_full = false;

            }
            if (di.tomdata.add_remain_circle[i] > 0 && di.tomdata.add_busy[i] == 1) {
                di.tomdata.add_remain_circle[i]--;
            }
        }

        for (int i = 0; i < 2; ++i) {
            if (di.tomdata.mul_remain_circle[i] == 0 && di.tomdata.mul_busy[i] == 1) {
                //di.tomdata.mul_busy[i] = 0;
                //di.tomdata.mul_full = false;
                int RS_place = di.tomdata.cur_mul[i];
                di.tomdata.rs_state[RS_place] = 0;
                RS_finished[RS_finished_cnt] = "Mrs" + String.valueOf(RS_place-5);
                //System.out.println(RS_finished[RS_finished_cnt] + " finsished!!!");
                
                                    
                int order_index = di.tomdata.rs_index[RS_place];
                int[] finish_order = new int[5];
                for (int ii =0 ; ii < 5; ++ii)
                    finish_order[ii] = all_orders[order_index][ii];
                int x = finish_order[2];
                String x1 = di.tomdata.rs_Vj[RS_place];
                String x2 = di.tomdata.rs_Vk[RS_place];
                int mul_ans = 0;
                if (finish_order[1] == 3)
                    mul_ans = hex_2_int(x1) * hex_2_int(x2);
                else if (finish_order[1] == 4) {
                    if (hex_2_int(x2) != 0)
                        mul_ans = hex_2_int(x1) / hex_2_int(x2);
                    else 
                        mul_ans = hex_2_int(x1);
                }
                    
                di.tomdata.r_real_value[x] = mul_ans;
                if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                    di.tomdata.r_value[x] = int_2_hex(mul_ans);
                if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                    di.tomdata.r_state[x] = int_2_hex(mul_ans);
                RS_finished_ans[RS_finished_cnt] = int_2_hex(mul_ans);

                RS_finished_cnt++;
                //RS_clear(RS_place);
                //di.tomdata.Mrs_full = false;

            }
            if (di.tomdata.mul_remain_circle[i] > 0 && di.tomdata.mul_busy[i] == 1) {
                di.tomdata.mul_remain_circle[i]--;
            }
        }

        for (int i = 0; i < 2; ++i) {
            if (di.tomdata.load_remain_circle[i] == 0 && di.tomdata.load_busy[i] == 1) {
                //di.tomdata.load_busy[i] = 0;
                //di.tomdata.load_full = false;
                int LB_place = di.tomdata.cur_load[i];
                
                RS_finished[RS_finished_cnt] = "LB" + String.valueOf(LB_place+1);
                //System.out.println(RS_finished[RS_finished_cnt] + " finsished!!!");
                
                //di.tomdata.lb_busy[LB_place] = 0;
                //di.tomdata.lb_full = false;
                //di.tomdata.lb_addr[LB_place] = "";

                int order_index = di.tomdata.lb_index[LB_place];
                //System.out.println("order_index = " + order_index);
                int[] finish_order = new int[5];
                for (int ii =0 ; ii < 5; ++ii)
                    finish_order[ii] = all_orders[order_index][ii];
                int x = finish_order[2];
                int y = finish_order[3];
                di.tomdata.r_real_value[x] = y;
                if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                    di.tomdata.r_value[x] = int_2_hex(y);
                if (di.tomdata.r_state[x].equals(RS_finished[RS_finished_cnt]))
                    di.tomdata.r_state[x] = int_2_hex(y);

                //System.out.println("RS_finished_cnt"+RS_finished_cnt);
                RS_finished_ans[RS_finished_cnt] = int_2_hex(y);
                RS_finished_cnt++;
            }
            
            if (di.tomdata.load_remain_circle[i] > 0 && di.tomdata.load_busy[i] == 1) {
                di.tomdata.load_remain_circle[i]--;
            }
        }
        //System.out.println();
    }

    public static void RS_clear(int index) {
        di.tomdata.rs_busy[index] = 0;
        di.tomdata.rs_Vj[index] = di.tomdata.rs_Vk[index] = di.tomdata.rs_Qj[index] = di.tomdata.rs_Qk[index] = "    ";
        di.tomdata.rs_state[index] = 0;
        di.tomdata.rs_index[index] = 0;
        di.tomdata.rs_op[index] = "";
    }

    public static void deal_one_oreder() {
        if (di.tomdata.jump_exist) {
            //System.out.println("JUMP order exists!");
            return;
        }

        if (di.tomdata.pc >= all_orders_length) {
            //System.out.println("pc is out of range and the work will soon finish");
            return;
        }

        for (int i = 0; i < 5; ++i) {
            di.tomdata.cur_order[i] = all_orders[di.tomdata.pc][i];
            //System.out.println(di.tomdata.cur_order[i] + "  ");
        }

        switch(di.tomdata.cur_order[1])
        {
            case 0 :
                //System.out.println("Current order : LD");
                
                deal_type_1();
                break;

            case 1 :
                //System.out.println("Current order : ADD");
                deal_type_2(1);
                break;

            case 2 :
                //System.out.println("Current order : SUB");
                deal_type_2(2);
                break;

            case 3 :
               // System.out.println("Current order : MUL");
                deal_type_2(3);
                break;

            case 4 :
               // System.out.println("Current order : DIV");
                deal_type_2(4);
                break;
            
            case 5 :
                //System.out.println("Current order : JUMP");
                deal_type_2(5);
                break;                
        }
        //System.out.println();
    }

    public static void deal_type_1() {
        int RS_place = -1;

        if (!di.tomdata.lb_full && di.tomdata.load_full) {
            //System.out.println("Ceeeeeeeeeeeeeeeeb"); 
            di.tomdata.lb_wait = 3 - di.tomdata.cur_load[0] - di.tomdata.cur_load[1];
        }

        if (!di.tomdata.lb_full) {
            int order_place = di.tomdata.pc;
            if (time_for_orders[order_place][0] == 0) {
                time_for_orders[order_place][0] = tmp_circle;
            }

            di.tomdata.pc += 1;
           // System.out.println("lb is not full!");
            for (int i = 0; i < 3; ++i) {
                if (di.tomdata.lb_busy[i] == 0) {
                    RS_place = i;
                    di.tomdata.lb_busy[i] = 1;
                    di.tomdata.lb_addr[i] = int_2_hex(di.tomdata.cur_order[3]);
                    //System.out.println(di.tomdata.lb_addr[i]);  
                    int n = di.tomdata.cur_order[2];
                    String state = "LB" + (i+1);
                    di.tomdata.r_state[n] = state;
                    di.tomdata.lb_index[i] = di.tomdata.pc - 1;
                    break;          
                }  
            }
            if (di.tomdata.lb_busy[0] == 1 && di.tomdata.lb_busy[1] == 1 && di.tomdata.lb_busy[2] == 1) {
                di.tomdata.lb_full = true;
               // System.out.println("lb become full!");  
            } 
        }

        if (!di.tomdata.load_full) {
           // System.out.println("load_part is not full!");
            for (int i = 0; i < 2; ++i) {
                if (di.tomdata.load_busy[i] == 0) {
                    di.tomdata.load_busy[i] = 1;
                    di.tomdata.cur_load[i] = RS_place;
                    di.tomdata.load_remain_circle[i] = LD_cirle;
                    //System.out.println("xxxxxxxx" + RS_place);
                    break;
                }
            }
            if (di.tomdata.load_busy[0] == 1 && di.tomdata.load_busy[1] == 1) {
                di.tomdata.load_full = true;
                //di.tomdata.lb_wait = 3 - di.tomdata.cur_load[0] - di.tomdata.cur_load[1];
                //System.out.println("load_part become full!");  
                //int remain = di.tomdata.lb_wait;
                //System.out.println("di.tomdata.lb_wait : " + remain);  
            }
        }   

    }

    public static void deal_type_2(int type) {
        int RS_place = -1;
        if (type == 1 || type == 2 || type == 5) {
            if (!di.tomdata.Ars_full) {

                int order_place = di.tomdata.pc;
                if (time_for_orders[order_place][0] == 0) {
                    time_for_orders[order_place][0] = tmp_circle;
                }

                if (type == 1 || type == 2)
                    di.tomdata.pc += 1;
                else if (type == 5)
                    di.tomdata.jump_exist = true;
                //System.out.println("ARS is not full!");
                for (int i = 0; i < 6; ++i) {
                    if (di.tomdata.rs_busy[i] == 0) {
                        RS_place = i;
                        di.tomdata.rs_busy[i] = 1;
                        if (type == 1)
                            di.tomdata.rs_op[i] = "ADD";
                        else if (type == 2)
                            di.tomdata.rs_op[i] = "SUB";
                        else if (type == 5)
                            di.tomdata.rs_op[i] = "JUMP";
                        int j = di.tomdata.cur_order[3];
                        int k = di.tomdata.cur_order[4];
                        String Sj = di.tomdata.r_state[j];
                        
                        if (Sj.charAt(0) == '0') 
                            di.tomdata.rs_Vj[i] = Sj;
                        else 
                            di.tomdata.rs_Qj[i] = Sj;
                        if (type != 5) {
                            String Sk = di.tomdata.r_state[k];
                            if (Sk.charAt(0) == '0') 
                                di.tomdata.rs_Vk[i] = Sk;
                            else 
                                di.tomdata.rs_Qk[i] = Sk;
                        }
                        if (type == 1 || type == 2)
                            di.tomdata.rs_index[i] = di.tomdata.pc - 1;
                        else 
                            di.tomdata.rs_index[i] = di.tomdata.pc;
                        di.tomdata.rs_state[i] = 0;

                        if (type == 1 || type == 2) {
                            int n = di.tomdata.cur_order[2];
                            di.tomdata.r_state[n] = "Ars" + (i + 1);
                        }
                        
                        break;
                    }
                }

                boolean if_add_full = true;
                for (int i = 0; i < 6; ++i) {
                    if (di.tomdata.rs_busy[i] == 0) {
                        if_add_full = false;
                        break;
                    }
                }
                di.tomdata.Ars_full = if_add_full;
               // if (if_add_full)
                    //System.out.println("ARS become full!");
            }
        }

        else if (type > 2 && type < 5) {
            if (!di.tomdata.Mrs_full) {

                int order_place = di.tomdata.pc;
                if (time_for_orders[order_place][0] == 0) {
                    time_for_orders[order_place][0] = tmp_circle;
                }

                di.tomdata.pc += 1;
               // System.out.println("MRS is not full!");
                for (int i = 6; i < 9; ++i) {
                    if (di.tomdata.rs_busy[i] == 0) {
                        RS_place = i;
                        di.tomdata.rs_busy[i] = 1;
                        if (type == 3)
                            di.tomdata.rs_op[i] = "MUL";
                        else if (type == 4)
                            di.tomdata.rs_op[i] = "DIV";
                        int j = di.tomdata.cur_order[3];
                        int k = di.tomdata.cur_order[4];
                        String Sj = di.tomdata.r_state[j];
                        String Sk = di.tomdata.r_state[k];
                        if (Sj.charAt(0) == '0') 
                            di.tomdata.rs_Vj[i] = Sj;
                        else 
                            di.tomdata.rs_Qj[i] = Sj;
                        if (Sk.charAt(0) == '0') 
                            di.tomdata.rs_Vk[i] = Sk;
                        else 
                            di.tomdata.rs_Qk[i] = Sk;
                        di.tomdata.rs_index[i] = di.tomdata.pc - 1;
                        di.tomdata.rs_state[i] = 0;

                        int n = di.tomdata.cur_order[2];
                        di.tomdata.r_state[n] = "Mrs" + (i -5);
                        break;
                    }
                }

                boolean if_mul_full = true;
                for (int i = 6; i < 9; ++i) {
                    if (di.tomdata.rs_busy[i] == 0) {
                        if_mul_full = false;
                        break;
                    }
                }
                di.tomdata.Mrs_full = if_mul_full;
                //if (if_mul_full)
                    //System.out.println("MRS become full!");
            }
        }

    }

    public static int hex_2_int(String hex) {
        hex = hex.substring(2);
        BigInteger bi = new BigInteger(hex, 16);
        int a = bi.intValue();
        return a;
    }

    public static String int_2_hex(int var) {
        String hex = Integer.toHexString(var);
        hex = hex.toUpperCase();
        hex = "0x" + hex;
        return hex;
    }

    public static void test() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 5; ++j) {
                System.out.print(all_orders[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void read_file(String file_name) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file_0));
            String sb;
            int cnt = 0;
            while (in.ready()) {
                sb = in.readLine();
                string_for_orders[cnt] = sb;
                String[] split = sb.split(",");
                int p = 1;
                all_orders[cnt][0] = cnt;
                if (split[0].equals("LD")) {
                    //System.out.println(cnt + " LD ");
                    all_orders[cnt][1] = 0;
                    String Fn = split[1];
                    Fn = Fn.substring(1);
                    int n = Integer.parseInt(Fn);
                    all_orders[cnt][2] = n;
                    //System.out.println(all_orders[cnt][2]);
                    String buma = split[2];
                    all_orders[cnt][3] = hex_2_int(buma);
                    //System.out.println(all_orders[cnt][3]);
                }
                else if (split[0].equals("SUB") || split[0].equals("ADD") || split[0].equals("DIV") || split[0].equals("MUL")) {
                    for (int i = 1; i < 4; ++i) {
                        String Fn = split[i];
                        Fn = Fn.substring(1);
                        int n = Integer.parseInt(Fn);
                        all_orders[cnt][i+1] = n;
                        //System.out.println(all_orders[cnt][i+1]);
                    }
                    if (split[0].equals("ADD"))
                        all_orders[cnt][1] = 1;
                    else if (split[0].equals("SUB"))
                        all_orders[cnt][1] = 2;
                    else if (split[0].equals("MUL"))
                        all_orders[cnt][1] = 3;
                    else if (split[0].equals("DIV"))
                        all_orders[cnt][1] = 4;
                }
                else if (split[0].equals("JUMP")) {
                    all_orders[cnt][1] = 5;
                    String buma = split[1];  
                    all_orders[cnt][2] = hex_2_int(buma);
                    String Fn = split[2];
                    Fn = Fn.substring(1);
                    int n = Integer.parseInt(Fn);
                    all_orders[cnt][3] = n;
                    buma = split[3];
                    all_orders[cnt][4] = hex_2_int(buma);
                }
                cnt++;
                all_orders_length = cnt;
            }
            in.close();
 
        } catch (IOException e) {
           
        }
    }

    public static void show() {
        //print the RS
        System.out.println();
        System.out.println("//////////////////////////////////////////////////RS Status//////////////////////////////////////////////////");
        System.out.println();
        System.out.println("      " + "   Busy   " + "     OP     " + "     Vj     " + "     Vk     " + "     Qj     " + "     Qk     " + "     index" + "      " + "state");
        for (int i = 1; i < 10; ++i) {

            if (i < 7) {
                System.out.print("Ars" + i + "       ");
            }
            else {
                int j = i - 6;
                System.out.print("Mrs" + j + "       ");
            }
            System.out.print(di.tomdata.rs_busy[i-1]+"        ");
            System.out.print(di.tomdata.rs_op[i-1]+"         ");
            System.out.print(di.tomdata.rs_Vj[i-1]+"        ");
            System.out.print(di.tomdata.rs_Vk[i-1]+"        ");
            System.out.print(di.tomdata.rs_Qj[i-1]+"        ");
            System.out.print(di.tomdata.rs_Qk[i-1]+"             ");
            System.out.print(di.tomdata.rs_index[i-1]+"        ");
            System.out.println(di.tomdata.rs_state[i-1]);
        }

        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////LB Status//////////////////////////////////////////////////");
        System.out.println();
        System.out.println("    " + "     Busy     " + "     Address     " + "index");
        for (int i = 1; i < 4; ++i) {
            System.out.print("LB" + i + "        ");
            System.out.print(di.tomdata.lb_busy[i-1]+"          ");
            System.out.print(di.tomdata.lb_addr[i-1]+"          ");
            System.out.println(di.tomdata.lb_index[i-1]);
        }

        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////Register Value//////////////////////////////////////////////////");
        System.out.println();
        for (int i = 0; i < 10; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"         ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 0; i < 10; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_value[i]+"        ");
            else 
            System.out.println(di.tomdata.r_value[i]);
        }
        System.out.println();
        for (int i = 10; i < 20; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"        ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 10; i < 20; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_value[i]+"        ");
            else 
            System.out.println(di.tomdata.r_value[i]);
        }
        System.out.println();
        for (int i = 20; i < 30; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"        ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 20; i < 30; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_value[i]+"        ");
            else 
            System.out.println(di.tomdata.r_value[i]);
        }
        System.out.println();
        for (int i = 30; i < 32; ++i) {
            System.out.print("F"+i+"        ");
        }
        System.out.println();
        for (int i = 30; i < 32; ++i) {
            System.out.print(di.tomdata.r_value[i]+"        ");
        }


        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////Register Status//////////////////////////////////////////////////");
        System.out.println();
        for (int i = 0; i < 10; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"         ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 0; i < 10; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_state[i]+"        ");
            else 
            System.out.println(di.tomdata.r_state[i]);
        }
        System.out.println();
        for (int i = 10; i < 20; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"        ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 10; i < 20; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_state[i]+"       ");
            else 
            System.out.println(di.tomdata.r_state[i]);
        }
        System.out.println();
        for (int i = 20; i < 30; ++i) {
            if (i % 10 != 9)
                System.out.print("F"+i+"        ");
            else 
            System.out.println("F"+i);
        }
        for (int i = 20; i < 30; ++i) {
            if (i % 10 != 9)
                System.out.print(di.tomdata.r_state[i]+"       ");
            else 
            System.out.println(di.tomdata.r_state[i]);
        }
        System.out.println();
        for (int i = 30; i < 32; ++i) {
            System.out.print("F"+i+"        ");
        }
        System.out.println();
        for (int i = 30; i < 32; ++i) {
            System.out.print(di.tomdata.r_state[i]+"        ");
        }
        System.out.println();

        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////Real Register//////////////////////////////////////////////////");
        System.out.println();
        for (int i = 0; i < 32; ++i) {
            if (di.tomdata.r_real_value[i] != 0) {
                System.out.println("F" + i + ": " + di.tomdata.r_real_value[i]);
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////Calculate Status//////////////////////////////////////////////////");
        System.out.println();
        System.out.println("      " + "index" + "      " + "remain" + "      " + "busy");
        for (int i = 0; i < 3; ++i) {
            System.out.print("Add" + (i+1) + "    ");
            System.out.println(di.tomdata.cur_add[i] + "          " + di.tomdata.add_remain_circle[i] + "          " + di.tomdata.add_busy[i]);
        }
        for (int i = 0; i < 2; ++i) {
            System.out.print("Mul" + (i+1) + "    ");
            System.out.println(di.tomdata.cur_mul[i] + "          " + di.tomdata.mul_remain_circle[i] + "          " + di.tomdata.mul_busy[i]);
        }
        for (int i = 0; i < 2; ++i) {
            System.out.print("Load" + (i+1) + "   ");
            System.out.println(di.tomdata.cur_load[i] + "          " + di.tomdata.load_remain_circle[i] + "          " + di.tomdata.load_busy[i]);
        }

        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////Other things//////////////////////////////////////////////////");
        System.out.println();
        System.out.println("lb_wait: " + di.tomdata.lb_wait); 
        System.out.println("pc: " + di.tomdata.pc); 
        System.out.println("all_orders_length: " + all_orders_length); 
        System.out.println("di.tomdata.lb_full: " + di.tomdata.lb_full); 

    }
    
    public static void print_time() {
        System.out.println();
        System.out.println();
        System.out.println("//////////////////////////////////////////////////final answer//////////////////////////////////////////////////");
        System.out.println();
        String str0 = String.format("%-40s", "");
        String str1 = String.format("%-15s", "Issue");
        String str2 = String.format("%-15s", "Exec Comp");
        String str3 = String.format("%-15s", "Write Result");
        System.out.print(str0);
        System.out.print("|   ");
        System.out.print(str1);
        System.out.print("|   ");
        System.out.print(str2);
        System.out.print("|   ");
        System.out.println(str3);
        String tmp_order = "";
        String tmp_issue = "";
        String tmp_exec = "";
        String tmp_wb = "";
        for (int i = 0; i < all_orders_length; ++i){
            tmp_order = String.format("%-40s", string_for_orders[i]);
            System.out.print(tmp_order);
            System.out.print("|   ");

            tmp_issue = String.format("%-15s", time_for_orders[i][0]);
            System.out.print(tmp_issue);
            System.out.print("|   ");

            tmp_exec = String.format("%-15s", time_for_orders[i][1]);
            System.out.print(tmp_exec);
            System.out.print("|   ");

            tmp_wb = String.format("%-15s", time_for_orders[i][2]);
            System.out.print(tmp_wb);
            System.out.println();
        }
    }

    public static void judge_finish() {
        for (int i = 0; i < 6; ++i) {
            if (di.tomdata.rs_busy[i] == 1) {
                if_finished = false;
                return;
            }                 
        }
        for (int i = 0; i < 3; ++i) {
            if (di.tomdata.lb_busy[i] == 1) {
                if_finished = false;
                return;
            }
        }
        for (int i = 0; i < 3; ++i) {
            if (di.tomdata.add_busy[i] == 1) {
                if_finished = false;
                return;
            }
        }
        for (int i = 0; i < 2; ++i) {
            if (di.tomdata.mul_busy[i] == 1) {
                if_finished = false;
                return;
            }
        }
        for (int i = 0; i < 2; ++i) {
            if (di.tomdata.load_remain_circle[i] == 1) {
                if_finished = false;
                return;
            }
        }
        if (di.tomdata.pc >= all_orders_length)
            if_finished = true;
    }
    

}