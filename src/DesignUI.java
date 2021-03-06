import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class DesignUI {
    private JPanel panel_design;
    private JTable table1;
    private JButton Btn_Save_color;
    private JLabel l1;
    private final JColorChooser chooser = new JColorChooser();
    private DefaultTableModel model = new DefaultTableModel();
    private Service service;
    private ImageIcon[] ii;
    private JSONArray ja_content_all;
    private JSONArray ja_colors;
    private String[] str;
    private Service.Language lang;

    DesignUI() {
        service = new Service();
        try {
            service.dir_vocabulary_file(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content_file = service.content_file(service.current_path[1]);
        ja_content_all = new JSONArray(content_file);
        JSONObject jo_colors = ja_content_all.getJSONObject(1);
        ja_colors = jo_colors.getJSONArray("color");

        table();
        table1.setModel(model);
        int[] table_column_widths = {150, 150};
        for (int i = 0; i < 2; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }
        lang = new Service.Language(service.current_path[1]);
        TempRowCol trw = new TempRowCol();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int row = table1.rowAtPoint(mouseEvent.getPoint());
                int col = table1.columnAtPoint(mouseEvent.getPoint());

                trw.row = row;
                ii[trw.row] = new ImageIcon();

                chooser.getSelectionModel().addChangeListener(arg0 -> {
                    Color color = chooser.getColor();
                    ii[trw.row].setImage(paintComponent(color));
                    str[trw.row] = "#" + Integer.toHexString(color.getRGB()).substring(2);
                    table1.getModel().setValueAt(ii[trw.row], trw.row, col);
                });

                JDialog dialog = JColorChooser.createDialog(null, "Color Chooser",
                        true, chooser, null, null);
                dialog.setVisible(true);
            }
        });

        Btn_Save_color.addActionListener(actionEvent -> {
            JSONArray ja_color_create = new JSONArray();
            for (int i = 0; i < ja_colors.length(); i++) {
                ja_color_create.put(i, new JSONObject().put(table1.getValueAt(i, 0).toString(), str[i]));
            }
            JSONArray ja_final = new JSONArray();
            ja_final.put(ja_content_all.getJSONObject(0));
            ja_final.put(new JSONObject().put("color", ja_color_create));

            try {
                service.write_content_in_file(service.current_path[1], ja_final, "edit");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Restart_program").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    class TempRowCol {
        int row;
    }

    private void table() {
        model = new DefaultTableModel(new Object[]{"Name", "Color"}, ja_colors.length()) {
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return ImageIcon.class;
                    default:
                        return String.class;
                }
            }

            boolean[] canEdit = {false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        ii = new ImageIcon[ja_colors.length()];
        str = new String[ja_colors.length()];

        for (int i = 0; i < ja_colors.length(); i++) {
            String key = ja_colors.getJSONObject(i).names().getString(0);
            String val = ja_colors.getJSONObject(i).getString(key);
            ii[i] = new ImageIcon();
            ii[i].setImage(paintComponent(Color.decode(val)));
            model.setValueAt(key, i, 0);
            model.setValueAt(ii[i], i, 1);
            str[i] = val;
        }
    }

    private BufferedImage paintComponent(Color clr) {
        BufferedImage output = new BufferedImage(100, 20, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(clr);
        g2.fillRect(0, 0, output.getWidth(), output.getHeight());
        return output;
    }

    void showFrame() {
        JFrame frame = new JFrame("DesignUI");
        frame.setContentPane(new DesignUI().panel_design);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
