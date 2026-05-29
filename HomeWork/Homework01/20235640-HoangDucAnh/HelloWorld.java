import javax.swing.JOptionPane; 
 
public class HelloWorld { 
    public static void main(String[] args) { 
        String result; 
        result = JOptionPane.showInputDialog("Hi! What's your name?"); 
        JOptionPane.showMessageDialog(null, "Hello " + result + "!"); 
        System.exit(0); 
    } 
} 
