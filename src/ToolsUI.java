import javafx.scene.control.SelectionMode;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeSet;

public class ToolsUI{
    private JScrollPane s_pane1;
    private JScrollPane s_pane2;
    private JTable table1;
    private JTable table2;
    private JButton Btn_Add, Btn_Del, Btn_Save;
    private JPanel tools_panel, panel_right, panel_table;
    private JLabel Lbl_info;
    public JPanel panel_settings;
    public JPanel panel_design;

    private JButton Btn_Words_up;
    private JButton Btn_Words_down;

    private JTextField field_search11;
    private JTextField field_search12;
    private JButton Btn_Search1;
    private JTextField field_search21;
    private JTextField field_search22;
    private JButton Btn_Search2;

    private JSpinner spinner1;
    private JSpinner spinner2;
    private JCheckBox JCB_Scope_questions;
    private JLabel Lbl_sp2_to_sp3;
    private JLabel Lbl_Scope_questions;
    private JTextField field_search13;
    private JTextField field_search23;

    private JSONArray ja_equal_words = new JSONArray();
    private JSONArray ja_equal_words2 = new JSONArray();
    private JSONArray t2_ja_equal_words = new JSONArray();
    private JSONArray t2_ja_equal_words2 = new JSONArray();
    private JSONArray ja_equal_error = new JSONArray();

    private DefaultTableModel model;
    private DefaultTableModel model2;
    private Service service;
    private Service service2;
    private Service.Language lang;
    int[] table_column_widths = {20,32,185,185,58,20,20};

    private ArrayList<String> t1t2_list_eq = new ArrayList<>();

    private ArrayList<String> table1_list_delete_rows_names = new ArrayList<>();
    private ArrayList<String> table1_list_delete_rows_translate = new ArrayList<>();
    private ArrayList<String> table1_list_delete_rows_word_type = new ArrayList<>();
    private ArrayList<Integer> table1_list_delete_rows_indexes = new ArrayList<>();
    private ArrayList<String> table2_list_delete_rows_names = new ArrayList<>();
    private ArrayList<String> table2_list_delete_rows_translate = new ArrayList<>();
    private ArrayList<String> table2_list_delete_rows_word_type = new ArrayList<>();
    private ArrayList<Integer> table2_list_delete_rows_indexes = new ArrayList<>();
    private boolean[] canEdit = {true, false, true, true, true, true, true};
    private int row1 = 0;
    private int col1 = 0;
    private int row2 = 0;
    private int col2 = 0;

    String t1ort2 = "none";

    private void elements_name(){
        Lbl_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
        Lbl_sp2_to_sp3.setText(lang.SetLanguage("Lbl_sp2_to_sp3_name").toString());
        Btn_Add.setToolTipText(lang.SetLanguage("Btn_Add_name").toString());
        Btn_Del.setToolTipText(lang.SetLanguage("Btn_Del_name").toString());
        Btn_Save.setToolTipText(lang.SetLanguage("Btn_Save_name").toString());
    }

    private void color_elements(){
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI_Table_bg" ).val));
        panel_settings.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI_bg" ).val));
        panel_table.setBackground(panel_settings.getBackground());
        panel_right.setBackground(panel_settings.getBackground());
    }

    private void scope_questions(boolean tf){
        spinner1.setEnabled(tf);
        spinner2.setEnabled(tf);
        Lbl_Scope_questions.setEnabled(tf);
        Lbl_sp2_to_sp3.setEnabled(tf);
    }

    private void autochoice(){
        if(JCB_Scope_questions.isSelected()) {
            for (int i = 0; i < table1.getRowCount(); i++) {
                table1.setValueAt(false, i, 0);
            }
            for (int i = 0; i < table2.getRowCount(); i++) {
                table2.setValueAt(false, i, 0);
            }
            for (int i = (int) spinner1.getValue() - 1; i < (int) spinner2.getValue(); i++) {
                if (table1.getRowCount() > (int) spinner2.getValue() - 1) {
                    table1.setValueAt(true, i, 0);
                }
                if (table2.getRowCount() > (int) spinner2.getValue() - 1) {
                    table2.setValueAt(true, i, 0);
                }
            }
        }
        else{
            for (int i = (int) spinner1.getValue() - 1; i < (int) spinner2.getValue(); i++){
                if(table1.getRowCount()>0) {
                    table1.setValueAt(false, i, 0);
                }
                if(table2.getRowCount()>0) {
                    table2.setValueAt(false, i, 0);
                }
            }
        }
    }

    private void table_header(JTable c_table){
        c_table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = c_table.columnAtPoint(e.getPoint());
                int col_num = service.getCurrentColumnIndex("№");
                for (int i = 0; i < c_table.getRowCount(); i++) {
                    if(i<c_table.getRowCount()-1) {
                        int v1 = (int) c_table.getValueAt(i, col_num);
                        int v2 = (int) c_table.getValueAt(i + 1, col_num);
                        if (v2 - v1 != 1) {
                            Btn_Words_down.setEnabled(false);
                            Btn_Words_up.setEnabled(false);
                            Btn_Save.setEnabled(false);
                            Btn_Add.setEnabled(false);
                            Btn_Del.setEnabled(false);
                            break;
                        }
                        else{
                            Btn_Add.setEnabled(true);
                            Btn_Del.setEnabled(true);
                            Btn_Save.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    public ToolsUI(){

        tools_panel.setSize(640,630);
        tools_panel.setMaximumSize(new Dimension(640,630));
        tools_panel.setMinimumSize(new Dimension(640,630));
        tools_panel.setPreferredSize(new Dimension(640,630));


        service = new Service();
        service2 = new Service();

        service.table(1,canEdit, "", "words_new");
        service2.table(1,canEdit, "","words_studied");
        model = service.model[1];
        model2 = service2.model[1];
        color_elements();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel> (model);
        TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<DefaultTableModel> (model2);
//        sorter.setRowFilter(RowFilter.regexFilter(".*1.*"));
        table1.setRowSorter(sorter);
        table2.setRowSorter(sorter2);

        table1.setModel(model);
        table2.setModel(model2);
        table1.getTableHeader().setReorderingAllowed(false);
        table2.getTableHeader().setReorderingAllowed(false);

        table_header(table1);
        table_header(table2);

        for(int i=0; i<7; i++){
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
            table2.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }
        JSONArray ob1 = new JSONArray();
        JSONArray ob2 = new JSONArray();
        for(int i=0; i<table1.getRowCount(); i++){
            ob1.put(i, new JSONObject().put(table1.getValueAt(i, 2).toString(), table1.getValueAt(i,3).toString()));
        }
        for(int i=0; i<table2.getRowCount(); i++) {
            ob2.put(i, new JSONObject().put(table2.getValueAt(i, 2).toString(), table2.getValueAt(i, 3).toString()));
        }
        Btn_Words_up.setEnabled(false);
        Btn_Words_down.setEnabled(false);

        lang = new Service.Language(service.current_path[1]);
        elements_name();

        int large_numb;
        if(service.ja_words.length() >= service2.ja_words.length()){
            large_numb = service.ja_words.length();
        }
        else large_numb = service2.ja_words.length();
        spinner1.setModel(new SpinnerNumberModel(1, 1, large_numb - 1, 1));
        spinner2.setModel(new SpinnerNumberModel(2, 2, large_numb, 1));

        scope_questions(false);

        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            Font font = new Font("TimesRoman", Font.PLAIN, 10);

            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                        row, column);
                setFont(font);
                return this;
            }

        };
        table1.getColumnModel().getColumn(service.getCurrentColumnIndex("Type")).setCellRenderer(r);
        table2.getColumnModel().getColumn(service.getCurrentColumnIndex("Type")).setCellRenderer(r);

        Btn_Add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                if(!table1.getValueAt(table1.getRowCount()-1, service.getCurrentColumnIndex("Word")).equals("")){
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    model.addRow(new Object[]{false, table1.getRowCount()+1, "","","","",""});

                    for(int i=0; i<6; i++){
                        table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
                    }
                    table1.changeSelection(table1.getRowCount()-1, 0,false,false);
                }
                else{
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Line_not_completed").toString(), lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });
        Btn_Del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ArrayList<String> list_delete_rows_names = new ArrayList<>();
                ArrayList<Integer> list_delete_rows_indexes = new ArrayList<>();

                ArrayList<String> list_delete_rows_names2 = new ArrayList<>();
                ArrayList<Integer> list_delete_rows_indexes2 = new ArrayList<>();

                try{
                    for(int i=0; i<table1.getRowCount(); i++){
                        Boolean checked = Boolean.valueOf(table1.getValueAt(i,service.getCurrentColumnIndex("✓")).toString());
                        int row = (int)table1.getValueAt(i, service.getCurrentColumnIndex("№")) - 1;
                        if(checked){
                            list_delete_rows_names.add(table1.getValueAt(row, service.getCurrentColumnIndex("Word")).toString());
                            list_delete_rows_indexes.add(row);
                        }
                    }

                    for(int i=0; i<table2.getRowCount(); i++){
                        Boolean checked = Boolean.valueOf(table2.getValueAt(i,service.getCurrentColumnIndex("✓")).toString());
                        int row = (int)table2.getValueAt(i, service.getCurrentColumnIndex("№")) - 1;
                        if(checked){
                            list_delete_rows_names2.add(table2.getValueAt(row, service.getCurrentColumnIndex("Word")).toString());
                            list_delete_rows_indexes2.add(row);
                        }
                    }
                    int ldr_st=0;
                    int ldr_st2=0;

                    for(int i = 0; i<list_delete_rows_indexes.size(); i++) {
                        int ldr_value = list_delete_rows_indexes.get(i)-ldr_st;
                        ((DefaultTableModel) table1.getModel()).removeRow(ldr_value);
                        ldr_st++;
                    }

                    for(int i = 0; i<list_delete_rows_indexes2.size(); i++) {
                        int ldr_value2 = list_delete_rows_indexes2.get(i)-ldr_st2;
                        ((DefaultTableModel) table2.getModel()).removeRow(ldr_value2);
                        ldr_st2++;
                    }

                    for(int i=0; i<table1.getRowCount(); i++) {
                        table1.setValueAt(i+1, i, 1);
                    }
                    for(int i=0; i<table2.getRowCount(); i++) {
                        table2.setValueAt(i+1, i, 1);
                    }

                    if(list_delete_rows_names.size() != 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                list_delete_rows_names + " " + lang.SetLanguage("OPM_Words_removed"),
                                lang.SetLanguage("OPM_Title").toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    if(list_delete_rows_names2.size() != 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                list_delete_rows_names2 + " " + lang.SetLanguage("OPM_Words_removed"),
                                lang.SetLanguage("OPM_Title").toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Words_not_removed") + "| "+e.getMessage(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        Btn_Save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JSONObject final_ja_words = new JSONObject();
                JSONArray ja_words_new = new JSONArray();
                JSONArray ja_words_studied = new JSONArray();
                JSONArray ja_words_type = new JSONArray();
                JSONObject jo_wt1 = new JSONObject();
                JSONObject jo_wt2 = new JSONObject();

                String[] awt = {"nn", "vr", "aj", "av", "pn", "cj"};

                for(int i=0;i< table2.getRowCount();i++){
                    String key = table2.getValueAt(i, service.getCurrentColumnIndex("Word")).toString();
                    String val = table2.getValueAt(i, service.getCurrentColumnIndex("Translate")).toString();
                    ja_words_studied.put(i, new JSONObject().put(key, val));
                    for(int j=0; j<awt.length;j++) {
                        if(table2.getValueAt(i, service.getCurrentColumnIndex("Type")).toString().toLowerCase().contains(awt[j].toLowerCase())) {
                            jo_wt2.append(awt[j].toLowerCase(), i);
                        }
                    }
                }

                for(int i=0;i< table1.getRowCount();i++){
                    String key = table1.getValueAt(i, service.getCurrentColumnIndex("Word")).toString();
                    String val = table1.getValueAt(i, service.getCurrentColumnIndex("Translate")).toString();
                    ja_words_new.put(i, new JSONObject().put(key, val));
                    for(int j=0; j<awt.length;j++) {
                        if(table1.getValueAt(i, service.getCurrentColumnIndex("Type")).toString().toLowerCase().contains(awt[j].toLowerCase())) {
                            jo_wt1.append(awt[j].toLowerCase(), i);
                        }
                    }
                }

                ja_words_type.put(0, new JSONObject().put("t1", jo_wt1));
                ja_words_type.put(1, new JSONObject().put("t2", jo_wt2));


                final_ja_words.put("words_studied", ja_words_studied);
                final_ja_words.put("words_new", ja_words_new);
                final_ja_words.put("words_type", ja_words_type);

                try {
                    Writer out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(service.current_path[0]), "windows-1251"));
                    try {
                        out.write(final_ja_words.toString(1));
                    } finally {
                        out.close();
                    }

                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Words_success_saved"), lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Words_not_saved") + "| "+e.getMessage(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        table1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                super.focusGained(focusEvent);
                wt_cols();
                add_warning_icon_to_col();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                super.focusLost(focusEvent);
                add_warning_icon_to_col();
            }
        });


        JPopupMenu menu_wt = new JPopupMenu();
        JCheckBoxMenuItem[] jcbmi = new JCheckBoxMenuItem[6];

        String[] jcbmis = {"nn ", "vr ", "aj ", "av ", "pn ", "cj "};

        for(int i=0; i<jcbmis.length; i++){
            jcbmi[i] = new JCheckBoxMenuItem(jcbmis[i]);
        }

        for(int i=0; i<6; i++) {
            menu_wt.add(jcbmi[i]);
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = "table1";

                add_warning_icon_to_col();
                message_warning_ew(table1, evt, ja_equal_words, "W*");
                message_warning_ew(table1, evt, ja_equal_words2, "T*");

                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());
                col1 = col;
                row1 = row;

                if (col == service.getCurrentColumnIndex("Type")) {
                    menu_wt.show(table1, (int) evt.getPoint().getX(), (int) evt.getPoint().getY());
                }

                buttons_enable_tf(col1, row1, table1, Btn_Words_down);

            }
        });


        table2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                super.focusGained(focusEvent);
                wt_cols();
                add_warning_icon_to_col();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                super.focusLost(focusEvent);
                add_warning_icon_to_col();
            }
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = "table2";
                add_warning_icon_to_col();

                message_warning_ew(table2, evt, t2_ja_equal_words, "W*");
                message_warning_ew(table2, evt, t2_ja_equal_words2, "T*");

                int row = table2.rowAtPoint(evt.getPoint());
                int col = table2.columnAtPoint(evt.getPoint());

                col2 = col;
                row2 = row;

                if (col == 4) {
                    menu_wt.show(table2, (int) evt.getPoint().getX(), (int) evt.getPoint().getY());
                }

                buttons_enable_tf(col2, row2, table2, Btn_Words_up);
            }
        });

        menu_wt.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                if(t1ort2.equals("table1")) {
                    word_type_from_table_to_combobox(jcbmi, table1, col1, row1);
                }
                if(t1ort2.equals("table2")) {
                    word_type_from_table_to_combobox(jcbmi, table2, col2, row2);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {}

        });

        for(int k=0; k<6;k++){
            jcbmi[k].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if(t1ort2.equals("table1")) {
                        word_type_from_combobox_to_table(jcbmi, table1, col1, row1);
                    }

                    else if(t1ort2.equals("table2")) {
                        word_type_from_combobox_to_table(jcbmi, table2, col2, row2);
                    }
                }
            });
        }

        table1.setDragEnabled(true);
        table1.setDropMode(DropMode.USE_SELECTION);
        table1.setTransferHandler(new TransferHelper());
        table1.setRowSelectionAllowed(false);
        table1.setCellSelectionEnabled(true);

        Btn_Words_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                qwe1(table2, table1, table2_list_delete_rows_names, table2_list_delete_rows_translate,
                                     table2_list_delete_rows_word_type, table2_list_delete_rows_indexes);
                table2_list_delete_rows_names.clear();
                table2_list_delete_rows_translate.clear();
                table2_list_delete_rows_word_type.clear();
                table2_list_delete_rows_indexes.clear();
                Btn_Words_up.setEnabled(false);
                spinner1.setValue(1);
                spinner2.setValue(2);
                scope_questions(false);
                JCB_Scope_questions.setSelected(false);
            }
        });
        Btn_Words_down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                qwe1(table1, table2, table1_list_delete_rows_names, table1_list_delete_rows_translate,
                                     table1_list_delete_rows_word_type, table1_list_delete_rows_indexes);
                table1_list_delete_rows_names.clear();
                table1_list_delete_rows_translate.clear();
                table1_list_delete_rows_word_type.clear();
                table1_list_delete_rows_indexes.clear();
                Btn_Words_down.setEnabled(false);
                spinner1.setValue(1);
                spinner2.setValue(2);
                scope_questions(false);
                JCB_Scope_questions.setSelected(false);
            }

        });
        Btn_Search1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                service.wt_search(table1, field_search11, field_search12, field_search13);
            }
        });
        Btn_Search2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                service.wt_search(table2, field_search21, field_search22, field_search23);
            }
        });


        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int jS1 = (int) spinner1.getValue();
                int jS2 = (int) spinner2.getValue();
                if (jS1 == jS2) {
                    spinner2.setValue(jS1 + 1);
                }
                autochoice();
            }
        });

        spinner2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                if((int)spinner2.getValue() > service.ja_words.length()){
                    Btn_Words_down.setEnabled(false);
                }else Btn_Words_down.setEnabled(true);

                if((int)spinner2.getValue() > service2.ja_words.length()){
                    Btn_Words_up.setEnabled(false);
                }else Btn_Words_up.setEnabled(true);

                int jS1 = (int) spinner1.getValue();
                int jS2 = (int) spinner2.getValue();
                if (jS1 == jS2) {
                    spinner1.setValue(jS2 - 1);
                }
                autochoice();
            }
        });

        JCB_Scope_questions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                autochoice();
                if(JCB_Scope_questions.isSelected()) {
                    scope_questions(true);
                    Btn_Words_up.setEnabled(true);
                    Btn_Words_down.setEnabled(true);
                }
                else{
                    scope_questions(false);

                    for (int i = 0; i < table1.getRowCount(); i++) {
                        if((boolean)table1.getValueAt(i, 0)) {
                            Btn_Words_down.setEnabled(true);
                            break;
                        }
                        else {
                            Btn_Words_down.setEnabled(false);
                        }
                    }

                    for (int i = 0; i < table2.getRowCount(); i++) {
                        if((boolean)table2.getValueAt(i, 0)) {
                            Btn_Words_up.setEnabled(true);
                            break;
                        }
                        else {
                            Btn_Words_up.setEnabled(false);
                        }
                    }
                }
            }
        });
    }

    public void buttons_enable_tf(int c_col, int c_row, JTable c_table, JButton c_button_updown){
        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_row == i) {
                String val = c_table.getValueAt(i, c_col).toString();
                c_table.setToolTipText(val);
            }
            if(c_col == 0){
                if((boolean)c_table.getValueAt(i, 0)) {
                    c_button_updown.setEnabled(true);
                    break;
                }
                else{ c_button_updown.setEnabled(false);}
                table_header(c_table);
            }
            if(i<c_table.getRowCount()-1) {
                int v1 = (int) c_table.getValueAt(i, 1);
                int v2 = (int) c_table.getValueAt(i + 1, 1);

                if (v2 - v1 != 1) {
                    Btn_Words_down.setEnabled(false);
                    Btn_Words_up.setEnabled(false);
                    Btn_Save.setEnabled(false);
                    Btn_Add.setEnabled(false);
                    Btn_Del.setEnabled(false);
                    break;
                }
                else{
                    Btn_Add.setEnabled(true);
                    Btn_Del.setEnabled(true);
                    Btn_Save.setEnabled(true);
                }
            }
        }
    }

    public void wt_cols(){
        for(int i=0;i<table1.getRowCount();i++) {
            table1.setValueAt("", i, service.getCurrentColumnIndex("W*"));
            table1.setValueAt("", i, service.getCurrentColumnIndex("T*"));
        }
        for(int i=0;i<table2.getRowCount();i++) {
            table2.setValueAt("", i, service.getCurrentColumnIndex("W*"));
            table2.setValueAt("", i, service.getCurrentColumnIndex("T*"));
        }
    }

    public void word_type_from_combobox_to_table(JCheckBoxMenuItem[] c_jcbmi, JTable c_table, int c_col, int c_row){
        for (int j = 0; j < c_table.getRowCount(); j++) {
            if (c_col == 4) {
                if (j ==  c_row) {
                    for(int i=0; i<6; i++) {
                        if (c_jcbmi[i].isSelected()) {
                            if (!c_table.getValueAt(c_row, c_col).toString().toLowerCase().contains
                                    (c_jcbmi[i].getText().toLowerCase())) {
                                c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase()+
                                        c_jcbmi[i].getText(), j, 4);
                            }
                        }
                        else if(c_jcbmi[i].isSelected() == false){
                            c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase().replace
                                    (c_jcbmi[i].getText().toLowerCase(), ""), j, 4);
                        }
                    }
                }
            }
        }
    }

    public void word_type_from_table_to_combobox(JCheckBoxMenuItem[] c_jcbmi, JTable c_table,
                                                 int c_col, int c_row){
        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_col == 4) {
                if (i == c_row) {
                    for (int j = 0; j < 6; j++) {
                        if (c_table.getValueAt(c_row, c_col).toString().toLowerCase().contains
                            (c_jcbmi[j].getText().toLowerCase())) {c_jcbmi[j].setSelected(true);}
                        else if (c_table.getValueAt(c_row, c_col).toString() == ""){c_jcbmi[j].setSelected(false);}
                        else{c_jcbmi[j].setSelected(false);}
                    }
                }
            }
        }
    }

    public void qwe1(JTable t1, JTable t2, ArrayList<String> al_name, ArrayList<String> al_translate,
                                           ArrayList<String> al_word_type, ArrayList<Integer> al_indexes){
        try {
            for (int i = 0; i < t1.getRowCount(); i++) {

                Boolean checked = Boolean.valueOf(t1.getValueAt(i, service.getCurrentColumnIndex("✓")).toString());
                int row = (int) t1.getValueAt(i, service.getCurrentColumnIndex("№")) - 1;
                if (checked) {
                    al_name.add(t1.getValueAt(row, service.getCurrentColumnIndex("Word")).toString());
                    al_translate.add(t1.getValueAt(row, service.getCurrentColumnIndex("Translate")).toString());
                    al_word_type.add(t1.getValueAt(row, service.getCurrentColumnIndex("Type")).toString());
                    al_indexes.add(row);
                }
            }

            int ldr_st = 0;
            for (int i = 0; i < al_indexes.size(); i++) {
                int ldr_value = al_indexes.get(i) - ldr_st;
                ((DefaultTableModel) t1.getModel()).removeRow(ldr_value);
                ldr_st++;
            }

            for (int i = 0; i < t1.getRowCount(); i++) {
                t1.setValueAt(i + 1, i, 1);
            }

            for (int i = 0; i < al_indexes.size(); i++) {
                DefaultTableModel model = (DefaultTableModel) t2.getModel();
                model.addRow(new Object[]{false, t2.getRowCount() + 1, al_name.get(i), al_translate.get(i),
                                            al_word_type.get(i), "", ""});
            }



            t2.changeSelection(t2.getRowCount() - 1, 0, false, false);
            JOptionPane.showMessageDialog(
                    null,
                    al_name + " " + lang.SetLanguage("OPM_Words_repositioned"),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null, "| " + e.getMessage(),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }


    public void add_warning_icon_to_col(){
        TreeSet set = new TreeSet();
        ArrayList<String> list_val_from_col2 = new ArrayList<>();
        ArrayList<String> list_val_from_col3 = new ArrayList<>();
        ArrayList<String> t2_list_val_from_col2 = new ArrayList<>();
        ArrayList<String> t2_list_val_from_col3 = new ArrayList<>();
        URL url = ToolsUI.class.getResource("/icons/ic_warning_20x20.png");
        ImageIcon im = new ImageIcon(url);
        ja_equal_words = new JSONArray();
        ja_equal_words2 = new JSONArray();
        t2_ja_equal_words = new JSONArray();
        t2_ja_equal_words2 = new JSONArray();

        int t2_n = table2.getRowCount()-1;
        if(table2.getRowCount()>0) {
            do {
                add_equal_el_to_ja(table2, t2_list_val_from_col2, t2_ja_equal_words, t2_n, set, im, "Word", "W*");
                add_equal_el_to_ja(table2, t2_list_val_from_col3, t2_ja_equal_words2, t2_n, set, im, "Translate", "T*");
            } while (--t2_n >= 0);
        }

        int t1_n = table1.getRowCount()-1;
        if(table1.getRowCount()>0) {
            do {
                add_equal_el_to_ja(table1, list_val_from_col2, ja_equal_words, t1_n, set, im, "Word", "W*");
                add_equal_el_to_ja(table1, list_val_from_col3, ja_equal_words2, t1_n, set, im, "Translate", "T*");
            } while (--t1_n >= 0);
        }


        for(int i=0; i< table1.getRowCount()-1; i++){
            for(int j=0; j< table2.getRowCount()-1; j++){
                if(table1.getValueAt(i,2).toString().toLowerCase().equals(table2.getValueAt(j,2).toString().toLowerCase())){
                    table1.setValueAt(im, i, 4);
                    table2.setValueAt(im, j, 4);
                    t1t2_list_eq.add("T1: № "+i+"T2: № "+j+" ");
                }
            }
        }
    }


    public void add_equal_el_to_ja(JTable tab, ArrayList<String> list_val_from_col, JSONArray ja,
                                   int n, TreeSet set, ImageIcon im, String tc, String tci){
        list_val_from_col.add(tab.getValueAt(n, service.getCurrentColumnIndex(tc)).toString());
        Object obj = tab.getValueAt(n, service.getCurrentColumnIndex(tc));
        if(!set.add(obj)){
            tab.setValueAt(im, n, service.getCurrentColumnIndex(tci));
            tab.setValueAt(im, tab.getRowCount()- 1-list_val_from_col.indexOf(obj),service.getCurrentColumnIndex(tci));
            ja_equal_error.put(2,2);
            ja.put(tab.getRowCount()- 1-list_val_from_col.indexOf(obj), obj);
            ja.put(n, obj);
        }
    }

    public void message_warning_ew(JTable tab, MouseEvent evt, JSONArray ja_ew, String col_v){
        int row = tab.rowAtPoint(evt.getPoint());
        int col = tab.columnAtPoint(evt.getPoint());

        for (int i = 0; i < ja_ew.length(); i++) {
            if (row == i && col == service.getCurrentColumnIndex(col_v)) {
                ArrayList<Integer> list_numbers_equal_values =
                        list_numbers_equal_values_col2(ja_ew, ja_ew.get(i).toString());
                if(list_numbers_equal_values.size()>1) {
                    if (ja_ew.get(i) != ja_equal_error.get(0)) {
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + ja_ew.get(i).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exists") + "№ " + list_numbers_equal_values,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            }
        }

        for(int i=0; i< table1.getRowCount()-1; i++){
            for(int j=0; j< table2.getRowCount()-1; j++){
                if(table1.getValueAt(i,2).toString().toLowerCase().equals(table2.getValueAt(j,2).toString().toLowerCase())){
                    if (row == i && col == service.getCurrentColumnIndex(col_v)) {
                        int numb = j+1;
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + table1.getValueAt(i, 2).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exists") + "2 | № " + numb,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                    if (row == j && col == service.getCurrentColumnIndex(col_v)) {
                        int numb = i+1;
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + table1.getValueAt(j, 2).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exists") + "1 | № " + numb,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            }
        }
    }

    static ArrayList<Integer> list_numbers_equal_values_col2(JSONArray ja_equal_words_v, String v) {
        ArrayList<Integer> list_numbers_equal_values = new ArrayList<>();
        for(int i =0; i<ja_equal_words_v.length(); i++) {
            if(ja_equal_words_v.get(i).toString().toLowerCase().equals(v)) {
                list_numbers_equal_values.add(i+1);
            }
        }
        return list_numbers_equal_values;
    }

    public class CellData {
        private final Object[] value = new Object[7];
        private final int col;
        private final JTable table;
        private final int row;

        public CellData(JTable source) {
            this.col = source.getSelectedColumn();
            this.row = source.getSelectedRow();

            for(int i=0; i<value.length;i++){
                this.value[i] = source.getValueAt(row, i);
            }

            this.table = source;
        }

        public int getColumn() {
            return col;
        }

        public JTable getTable() {
            return table;
        }

        public boolean swapValuesWith(int targetRow, int targetCol) {
            boolean swapped = false;
            if(col != service.getCurrentColumnIndex("✓")) {
                if (targetCol == col) {
                    if(col ==  service.getCurrentColumnIndex("№")) {
                        for(int z=0; z<value.length; z++){
                            if(z!=service.getCurrentColumnIndex("№")){
                                export_content(targetRow, z, z);
                            }
                        }
                    }
                    else if(col ==  service.getCurrentColumnIndex("Word")){
                        export_content(targetRow, service.getCurrentColumnIndex("Word"),
                                                  service.getCurrentColumnIndex("Word"));
                        export_content(targetRow, service.getCurrentColumnIndex("W*"),
                                                  service.getCurrentColumnIndex("W*"));
                    }
                    else if(col ==  service.getCurrentColumnIndex("Translate")){
                        export_content(targetRow, service.getCurrentColumnIndex("Translate"),
                                                  service.getCurrentColumnIndex("Translate"));
                        export_content(targetRow, service.getCurrentColumnIndex("T*"),
                                                  service.getCurrentColumnIndex("T*"));
                    }

                    swapped = true;
                }
            }
            return swapped;
        }

        Object[] exportValue = new Object[7];
        private void export_content(int targetRow, int numb_col, int val){
            exportValue[val] = table.getValueAt(targetRow, numb_col);
            table.setValueAt(value[val], targetRow, numb_col);
            table.setValueAt(exportValue[val], row, numb_col);
        }

    }

    public static final DataFlavor CELL_DATA_FLAVOR = createConstant(CellData.class, "application/x-java-celldata");

    public class CellDataTransferable implements Transferable {

        private CellData cellData;

        public CellDataTransferable(CellData cellData) {
            this.cellData = cellData;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{CELL_DATA_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean supported = false;
            for (DataFlavor available : getTransferDataFlavors()) {
                if (available.equals(flavor)) {
                    supported = true;
                }
            }
            return supported;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return cellData;
        }
    }

    static protected DataFlavor createConstant(Class clazz, String name) {
        try {
            return new DataFlavor(clazz, name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class TransferHelper extends TransferHandler {

        private static final long serialVersionUID = 1L;

        public TransferHelper() {
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent source) {
            // Create the transferable
            JTable table = (JTable) source;
//            int row = table.getSelectedRow();
//            int col = table.getSelectedColumn();
//            Object value = table.getValueAt(row, col);
            return new CellDataTransferable(new CellData(table));
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
        }

        @Override
        public boolean canImport(TransferSupport support) {
            // Reject the import by default...
            boolean canImport = false;
            // Can only import into another JTable
            Component comp = support.getComponent();
            if (comp instanceof JTable) {
                JTable target = (JTable) comp;
                // Need the location where the drop might occur
                DropLocation dl = support.getDropLocation();
                Point dp = dl.getDropPoint();
                // Get the column at the drop point
                int dragColumn = target.columnAtPoint(dp);
                try {
                    // Get the Transferable, we need to check
                    // the constraints
                    Transferable t = support.getTransferable();
                    CellData cd = (CellData) t.getTransferData(CELL_DATA_FLAVOR);
                    // Make sure we're not dropping onto ourselves...
                    if (cd.getTable() == target) {
                        // Do the columns match...?
                        if (dragColumn == cd.getColumn()) {
                            canImport = true;
                        }
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
            }
            return canImport;
        }

        @Override
        public boolean importData(TransferSupport support) {
            // Import failed for some reason...
            boolean imported = false;
            // Only import into JTables...
            Component comp = support.getComponent();
            if (comp instanceof JTable) {
                JTable target = (JTable) comp;
                // Need to know where we are importing to...
                DropLocation dl = support.getDropLocation();
                Point dp = dl.getDropPoint();
                int dropCol = target.columnAtPoint(dp);
                int dropRow = target.rowAtPoint(dp);
                try {
                    // Get the Transferable at the heart of it all
                    Transferable t = support.getTransferable();
                    CellData cd = (CellData) t.getTransferData(CELL_DATA_FLAVOR);
                    if (cd.getTable() == target) {
                        if (cd.swapValuesWith(dropRow, dropCol)) {
                            imported = true;
                        }
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }

            }
            return imported;
        }
    }

    public void showFrame() {
        JFrame frame = new JFrame("ToolsUI");
        frame.setContentPane(new ToolsUI().tools_panel);
//        frame.setPreferredSize(new Dimension(600,465));
//        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
