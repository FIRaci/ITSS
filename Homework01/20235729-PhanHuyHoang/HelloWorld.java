import javax.swing.*;

public class HelloWorld {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        String name = JOptionPane.showInputDialog(frame, "Nhập tên của bạn:");
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        JOptionPane.showMessageDialog(frame, "Hello " + name + "!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        System.exit(0);
    }
}
