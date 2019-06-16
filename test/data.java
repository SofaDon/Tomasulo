import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class data{
    public static int pc; //��һ��ָ��

    public static boolean jump_exist;
    
    public static int[] cur_order; //��ǰ���е�ָ�� �±�0�����ָ���index
    
    //����վ���� 6+3
    public static int[] rs_busy; //busy
    public static int[] rs_index; //����վ���ָ����nel�ļ��еĵڼ���ָ��
    public static String[] rs_op; //op
    public static String[] rs_Vj; //Vj
    public static String[] rs_Vk; //Vk
    public static String[] rs_Qj; //Qj
    public static String[] rs_Qk; //Qk
    public static int[] rs_state; //state
    public static Queue<Integer> Ars_queue; // Ars�������� ���ڱ���վ���±�
    public static Queue<Integer> Mrs_queue; // Mrs�������� ���ڱ���վ���±�
    public static int Ars_queue_length;
    public static int Mrs_queue_length;
    public static boolean Ars_full; //Ars����վ�Ƿ�full
    public static boolean Mrs_full; //Mrs����վ�Ƿ�full


    //LoadBuffer���� 3
    public static int[] lb_index;
    public static int[] lb_busy; //lb�Ƿ�busy
    public static String[] lb_addr; //lb��address
    public static boolean lb_full; //lb�Ƿ�full
    public static int lb_wait; //lb�еȴ�����load��ָ���±�

    //�Ĵ���״̬
    public static String[] r_state; 

    //�Ĵ�����ֵ
    public static String[] r_value;
    public static int[] r_real_value;

    //�Ӽ����� 3
    public static int[] add_busy; //��ǰ�ӷ����Ƿ��ڱ�ʹ��
    public static int[] cur_add; //��ǰִ�е�ָ�� �ڱ���վ���±�
    public static int[] add_remain_circle; //ʣ��������
    public static boolean add_full; // �ӷ����Ƿ�ȫ���ڱ�ʹ��

    //�˳����� 2
    public static int[] mul_busy; //��ǰ�˷����Ƿ��ڱ�ʹ��
    public static int[] cur_mul; //��ǰִ�е�ָ�� �ڱ���վ���±�
    public static int[] mul_remain_circle; //ʣ��������
    public static boolean mul_full; // �˷����Ƿ�ȫ���ڱ�ʹ��

    //Load���� 2
    public static int[] load_busy; //��ǰload�����Ƿ��ڱ�ʹ��
    public static int[] cur_load; //��ǰִ�е�ָ�� �ڱ���վ���±�
    public static int[] load_remain_circle; //ʣ��������
    public static boolean load_full; // load�����Ƿ�ȫ���ڱ�ʹ��
}