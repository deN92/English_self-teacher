import javax.swing.*;
import java.awt.*;

public class DescriptionUI {
    private JPanel panel_main;
    private JPanel panel_content;
    private JLabel label1;
    private JLabel label2;

    DescriptionUI() {
        String str = "English self-teacher 2017";
        label1.setText(str);
        label2.setText("Author: Denys Slyusarchuk");
    }

    void showFrame() {
        JFrame frame = new JFrame("DescriptionUI");
        frame.setContentPane(new DescriptionUI().panel_main);
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

}
