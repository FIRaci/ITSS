import javax.swing.*;

public class HelloWorld {
	public static void main(String[] args) {
		String name = JOptionPane.showInputDialog("Nhập tên của bạn:");
		JOptionPane.showMessageDialog(null, "Hello " + name + "!");
	}
}

