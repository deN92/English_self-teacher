import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.io.IOException;

public class DesignUI{
    private JPanel panel_design;
    private JTable table1;
    private JButton button1;
    private JLabel l1;
    private final JColorChooser chooser = new JColorChooser();
    private DefaultTableModel model = new DefaultTableModel();
    private Service service;
    private JSONObject ja_colors;
    private ImageIcon[] ii;
    private String content_file;
    private JSONArray ja_content_all;
    private String[] str;

    public DesignUI(){
        service = new Service();
        try {
            service.dir_vocabulary_file(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        content_file = service.content_file(service.current_path[1]);
        ja_content_all = new JSONArray(content_file);
        JSONObject jo_colors = ja_content_all.getJSONObject(1);
        ja_colors = jo_colors.getJSONObject(jo_colors.names().getString(0));

        table();
        table1.setModel(model);
        int[] table_column_widths = {150,150};
        for (int i = 0; i < 2; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }

        TempRowCol trw = new TempRowCol();
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int row = table1.rowAtPoint(mouseEvent.getPoint());
                int col = table1.columnAtPoint(mouseEvent.getPoint());

                trw.row = row;
                ii[trw.row] = new ImageIcon();

                chooser.getSelectionModel().addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent arg0) {
                        Color color = chooser.getColor();
                        ii[trw.row].setImage(paintComponent(color));
                        str[trw.row] = "#"+Integer.toHexString(color.getRGB()).substring(2);
                        table1.getModel().setValueAt(ii[trw.row], trw.row, col);
                    }
                });

                JDialog dialog = JColorChooser.createDialog(null, "Color Chooser",
                        true, chooser, null, null);
                dialog.setVisible(true);
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JSONObject ja_color_create = new JSONObject();
                for(int i=0;i<ja_colors.length();i++) {
                    ja_color_create.put(table1.getValueAt(i, 0).toString(), str[i]);
                }
                JSONArray ja_final = new JSONArray();
                ja_final.put(ja_content_all.getJSONObject(0));
                ja_final.put(new JSONObject().put("color", ja_color_create));

                try {
                    service.write_content_in_file(service.current_path[1], ja_final);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class TempRowCol{
        public int row;
    }

    public void table(){
        model = new DefaultTableModel(new Object[] {"Name", "Color"}, ja_colors.length())
        {
            public Class<?> getColumnClass(int column){
                switch(column){
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
            String key = ja_colors.names().getString(i);
            String val = ja_colors.getString(key);
            ii[i] = new ImageIcon();
            ii[i].setImage(paintComponent(Color.decode(val)));
            model.setValueAt(key, i, 0);
            model.setValueAt(ii[i], i, 1);
            str[i] = val;
        }
    }

    public BufferedImage paintComponent(Color clr) {
        BufferedImage output = new BufferedImage(100, 20, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(clr);
        g2.fillRect(0, 0, output.getWidth(), output.getHeight());
        return output;
    }

    public void showFrame() {
        JFrame frame = new JFrame("DesignUI");
        frame.setContentPane(new DesignUI().panel_design);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
