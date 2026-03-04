import javax.swing.*;

public class HelloWorld {
	public static void main(String[] args) {
		String name = JOptionPane.showInputDialog("Hay nhap ten cua ban:");
		JOptionPane.showMessageDialog(null, "Hello " + name  );
	}
}