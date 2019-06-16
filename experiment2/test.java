import java.io.*;

public class tomasulo {
    String file_0 = "test0.nel";
    public static void main(String[] args) {
        read_file(file_0);
    }

    public void read_file(String file_name) {
        try {
            BufferedReader in = new BufferedReader(new FileReader("test0.nel"));
            StringBuffer sb;
            while (in.ready()) {
                sb = (new StringBuffer(in.readLine()));
                System.out.println(sb);
            }
            in.close();
        } catch (IOException e) {
           
        }
    }
}