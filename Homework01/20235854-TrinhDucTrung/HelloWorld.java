import javax.swing.*;

public class HelloWorld {
	public static void main(String[] args) {
		String name = JOptionPane.showInputDialog("Nhập tên:");
		JOptionPane.showMessageDialog(null, "Chào bạn " + name + "!");
	}
}
