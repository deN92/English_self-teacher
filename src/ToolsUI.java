import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
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
import java.util.Objects;
import java.util.TreeSet;

public class ToolsUI{
    JScrollPane s_pane1;
    JScrollPane s_pane2;
    private JTable table1;
    private JTable table2;
    private JButton Btn_Add, Btn_Del, Btn_Save;
    private JPanel tools_panel, panel_right, panel_table;
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
    private JButton Btn_Check_duplicates;

    private JSONArray[] ja_equal_words = new JSONArray[2];
    private JSONArray[] ja_equal_words2 = new JSONArray[2];

    private Service service;
    private Service service2;
    private Service.Language lang;
    private int[] table_column_widths = {20,32,185,185,58,20,20};

    private ArrayList<String> t1t2_list_eq = new ArrayList<>();

    private ArrayList<String>[] table_list_delete_rows_names = new ArrayList[2];
    private ArrayList<String>[]  table_list_delete_rows_translate = new ArrayList[2];
    private ArrayList<String>[]  table_list_delete_rows_word_type = new ArrayList[2];
    private ArrayList<Integer>[] table_list_delete_rows_indexes = new ArrayList[2];
    private int row1 = 0;
    private int col1 = 0;
    private int row2 = 0;
    private int col2 = 0;
    private String t1ort2 = "none";

    ToolsUI(){
        tools_panel.setSize(640,630);
        tools_panel.setMaximumSize(new Dimension(640,630));
        tools_panel.setMinimumSize(new Dimension(640,630));
        tools_panel.setPreferredSize(new Dimension(640,630));

        service = new Service();
        service2 = new Service();

        boolean[] canEdit = {true, false, true, true, false, false, false};
        service.table(1, canEdit, "words_new");
        service2.table(1, canEdit, "words_studied");
        DefaultTableModel model = service.model[1];
        DefaultTableModel model2 = service2.model[1];
        elements_color();

        TableRowSorter<DefaultTableModel> sorter  = new TableRowSorter<> (model);
        TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<> (model2);
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
            ob1.put(i, new JSONObject().put(table1.getValueAt(i, 2).toString(),
                                            table1.getValueAt(i,3).toString()));
        }
        for(int i=0; i<table2.getRowCount(); i++) {
            ob2.put(i, new JSONObject().put(table2.getValueAt(i, 2).toString(),
                                            table2.getValueAt(i, 3).toString()));
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
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(font);
                return this;
            }
        };

        table1.getColumnModel().getColumn(service.getCCI(service.nc_type_en)).setCellRenderer(r);
        table2.getColumnModel().getColumn(service.getCCI(service.nc_type_en)).setCellRenderer(r);

        Btn_Add.addActionListener(actionEvent -> {
            if(!table1.getValueAt(table1.getRowCount()-1, service.getCCI(service.nc_word_en)).equals("")){
                DefaultTableModel model1 = (DefaultTableModel) table1.getModel();
                model1.addRow(new Object[]{false, table1.getRowCount()+1, "","","","",""});

                for(int i=0; i<6; i++){
                    table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
                }
                table1.changeSelection(table1.getRowCount()-1, 0,false,false);
            }
            else{
                JOptionPane.showMessageDialog(
                    null,
                    lang.SetLanguage("OPM_Line_not_completed").toString(),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        Btn_Del.addActionListener(actionEvent -> {
            ArrayList<String> list_delete_rows_names = new ArrayList<>();
            ArrayList<Integer> list_delete_rows_indexes = new ArrayList<>();

            ArrayList<String> list_delete_rows_names2 = new ArrayList<>();
            ArrayList<Integer> list_delete_rows_indexes2 = new ArrayList<>();
            int jp = JOptionPane.showConfirmDialog(null,
                                                    lang.SetLanguage("OPM_Question_delete"),"",0);
            if(jp ==  JOptionPane.YES_OPTION) {
                try {
                    for (int i = 0; i < table1.getRowCount(); i++) {
                        Boolean checked = Boolean.valueOf(table1.getValueAt(i, service.getCCI("✓")).toString());
                        int row = (int) table1.getValueAt(i, service.getCCI("№")) - 1;
                        if (checked) {
                            list_delete_rows_names.add(
                                table1.getValueAt(row, service.getCCI(service.nc_word_en)).toString());
                            list_delete_rows_indexes.add(row);
                        }
                    }

                    for (int i = 0; i < table2.getRowCount(); i++) {
                        Boolean checked = Boolean.valueOf(table2.getValueAt(i, service.getCCI("✓")).toString());
                        int row = (int) table2.getValueAt(i, service.getCCI("№")) - 1;
                        if (checked) {
                            list_delete_rows_names2.add(
                                table2.getValueAt(row, service.getCCI(service.nc_word_en)).toString());
                            list_delete_rows_indexes2.add(row);
                        }
                    }
                    int ldr_st = 0;
                    int ldr_st2 = 0;

                    for (Integer list_delete_rows_indexe : list_delete_rows_indexes) {
                        int ldr_value = list_delete_rows_indexe - ldr_st;
                        ((DefaultTableModel) table1.getModel()).removeRow(ldr_value);
                        ldr_st++;
                    }

                    for (Integer aList_delete_rows_indexes2 : list_delete_rows_indexes2) {
                        int ldr_value2 = aList_delete_rows_indexes2 - ldr_st2;
                        ((DefaultTableModel) table2.getModel()).removeRow(ldr_value2);
                        ldr_st2++;
                    }

                    for (int i = 0; i < table1.getRowCount(); i++) {
                        table1.setValueAt(i + 1, i, 1);
                    }
                    for (int i = 0; i < table2.getRowCount(); i++) {
                        table2.setValueAt(i + 1, i, 1);
                    }

                    if (list_delete_rows_names.size() != 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                ""+ list_delete_rows_names.size() +"pos: " +
                                        list_delete_rows_names + " " + lang.SetLanguage("OPM_Words_removed"),
                                lang.SetLanguage("OPM_Title").toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    if (list_delete_rows_names2.size() != 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                ""+ list_delete_rows_names.size() +" pos: " +
                                        list_delete_rows_names2 + " " + lang.SetLanguage("OPM_Words_removed"),
                                lang.SetLanguage("OPM_Title").toString(),
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Words_not_removed") + "| " + e.getMessage(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        Btn_Save.addActionListener(actionEvent -> {
            JSONObject final_ja_words = new JSONObject();
            JSONArray ja_words_new = new JSONArray();
            JSONArray ja_words_studied = new JSONArray();
            JSONArray ja_words_type = new JSONArray();
            JSONObject jo_wt1 = new JSONObject();
            JSONObject jo_wt2 = new JSONObject();

            for(int i=0;i< table2.getRowCount();i++){
                String key = table2.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
                String val = table2.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
                ja_words_studied.put(i, new JSONObject().put(key, val));
                for (String anAwt : service.word_types) {
                    if (table2.getValueAt(i, service.getCCI(service.nc_type_en)) != null) {
                        if (table2.getValueAt(i, service.getCCI(service.nc_type_en)).toString().toLowerCase().
                                contains(anAwt.toLowerCase())) {
                            jo_wt2.append(anAwt.toLowerCase(), i);
                        }
                    }
                }
            }

            for(int i=0;i< table1.getRowCount();i++){
                String key = table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
                String val = table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
                ja_words_new.put(i, new JSONObject().put(key, val));
                for (String anAwt : service.word_types) {
                    if (table1.getValueAt(i, service.getCCI(service.nc_type_en)) != null) {
                        if (table1.getValueAt(i, service.getCCI(service.nc_type_en)).toString().toLowerCase().
                                contains(anAwt.toLowerCase())) {
                            jo_wt1.append(anAwt.toLowerCase(), i);
                        }
                    }
                }
            }

            for (String anAwt : service.word_types) {
                if(!jo_wt2.has(anAwt)){
                    jo_wt2.append(anAwt.toLowerCase(), -1);
                }
                if(!jo_wt1.has(anAwt)){
                    jo_wt1.append(anAwt.toLowerCase(), -1);
                }
            }

            ja_words_type.put(0, new JSONObject().put("words_new", jo_wt1));
            ja_words_type.put(1, new JSONObject().put("words_studied", jo_wt2));

            final_ja_words.put("words_studied", ja_words_studied);
            final_ja_words.put("words_new", ja_words_new);
            final_ja_words.put("words_type", ja_words_type);

            try {
                service.write_content_in_file(service.current_path[0], final_ja_words, "edit");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Words_success_saved").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        JCheckBoxMenuItem[] jcbmi = new JCheckBoxMenuItem[service.word_types.length];
        for(int i=0; i<service.word_types.length; i++){
            jcbmi[i] = new JCheckBoxMenuItem(service.word_types[i]);
        }

        JPopupMenu menu_wt = new JPopupMenu();
        for(int i=0; i<service.word_types.length; i++) {
            menu_wt.add(jcbmi[i]);
        }

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = "table1";

                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());

                if(col == service.getCCI("W*")||col == service.getCCI("T*")){
                    message_warning_ew(table1, evt, ja_equal_words[0], "W*");
                    message_warning_ew(table1, evt, ja_equal_words2[0], "T*");
                }

                col1 = col;
                row1 = row;

                if (col == service.getCCI(service.nc_type_en)) {
                    menu_wt.show(table1, (int) evt.getPoint().getX(), (int) evt.getPoint().getY());
                }

                buttons_enable_tf(col1, row1, table1, Btn_Words_down);
            }
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = "table2";

                int row = table2.rowAtPoint(evt.getPoint());
                int col = table2.columnAtPoint(evt.getPoint());

                message_warning_ew(table2, evt, ja_equal_words[1], "W*");
                message_warning_ew(table2, evt, ja_equal_words2[1], "T*");

                col2 = col;
                row2 = row;

                if (col == service.getCCI(service.nc_type_en)) {
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

        for(int k=0; k<service.word_types.length;k++){
            jcbmi[k].addActionListener(actionEvent -> {
                if(t1ort2.equals("table1")) {
                    word_type_from_combobox_to_table(jcbmi, table1, col1, row1);
                }

                else if(t1ort2.equals("table2")) {
                    word_type_from_combobox_to_table(jcbmi, table2, col2, row2);
                }
            });
        }

        table_row_export_enable(table1);
        table_row_export_enable(table2);

//      Export elements from table2 to table1
        Btn_Words_up.addActionListener(actionEvent -> {
            export_elem_from_tab_to_tab(table2, table1, 1);
            Btn_Words_up.setEnabled(false);
            spinner1.setValue(1);
            spinner2.setValue(2);
            scope_questions(false);
            JCB_Scope_questions.setSelected(false);
        });

//      Export elements from table1 to table2
        Btn_Words_down.addActionListener(actionEvent -> {
            export_elem_from_tab_to_tab(table1, table2, 0);
            Btn_Words_down.setEnabled(false);
            spinner1.setValue(1);
            spinner2.setValue(2);
            scope_questions(false);
            JCB_Scope_questions.setSelected(false);
        });

        Btn_Search1.addActionListener(actionEvent -> service.wt_search(table1,
                                                                       field_search11, field_search12, field_search13));
        Btn_Search2.addActionListener(actionEvent -> service.wt_search(table2,
                                                                       field_search21, field_search22, field_search23));

        spinner1.addChangeListener(changeEvent -> {
            int jS1 = (int) spinner1.getValue();
            int jS2 = (int) spinner2.getValue();
            if (jS1 == jS2) {
                spinner2.setValue(jS1 + 1);
            }
            spinner_choice_item_table();
        });

        spinner2.addChangeListener(changeEvent -> {
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
            spinner_choice_item_table();
        });

        JCB_Scope_questions.addActionListener(actionEvent -> {
            spinner_choice_item_table();
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
        });
        Btn_Check_duplicates.addActionListener(e -> {
            wt_cols();
            add_warning_icon_to_col();
        });
    }

    private void elements_name(){
        Lbl_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
        Lbl_sp2_to_sp3.setText(lang.SetLanguage("Lbl_sp2_to_sp3_name").toString());
        Btn_Check_duplicates.setToolTipText(lang.SetLanguage("Btn_Duplicates_name").toString());
        Btn_Add.setToolTipText(lang.SetLanguage("Btn_Add_name").toString());
        Btn_Del.setToolTipText(lang.SetLanguage("Btn_Del_name").toString());
        Btn_Save.setToolTipText(lang.SetLanguage("Btn_Save_name").toString());
    }

    private void elements_color(){
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI Table" ).val));
        panel_settings.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI" ).val));
        panel_table.setBackground(panel_settings.getBackground());
        panel_right.setBackground(panel_settings.getBackground());
    }

    private void scope_questions(boolean tf){
        spinner1.setEnabled(tf);
        spinner2.setEnabled(tf);
        Lbl_Scope_questions.setEnabled(tf);
        Lbl_sp2_to_sp3.setEnabled(tf);
    }

    private void spinner_choice_item_table(){
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
                int col_num = service.getCCI("№");
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

    private void table_row_export_enable(JTable c_table){
        c_table.setDragEnabled(true);
        c_table.setDropMode(DropMode.USE_SELECTION);
        c_table.setTransferHandler(new TransferHelper());
        c_table.setRowSelectionAllowed(false);
        c_table.setCellSelectionEnabled(true);
    }

    private void buttons_enable_tf(int c_col, int c_row, JTable c_table, JButton c_button_updown){
        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_row == i) {
                String val;
                if(c_table.getValueAt(i, c_col)==null) {
                    val = "";
                } else {
                    val = c_table.getValueAt(i, c_col).toString();
                }
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

    private void wt_cols(){
        for(int i=0;i<table1.getRowCount();i++) {
            table1.setValueAt("", i, service.getCCI("W*"));
            table1.setValueAt("", i, service.getCCI("T*"));
        }
        for(int i=0;i<table2.getRowCount();i++) {
            table2.setValueAt("", i, service.getCCI("W*"));
            table2.setValueAt("", i, service.getCCI("T*"));
        }
    }

    private void word_type_from_combobox_to_table(JCheckBoxMenuItem[] c_jcbmi, JTable c_table, int c_col, int c_row){
        for (int j = 0; j < c_table.getRowCount(); j++) {
            if (c_col == 4) {
                if (j ==  c_row) {
                    for(int i=0; i<service.word_types.length; i++) {
                        if (c_jcbmi[i].isSelected()) {
                            if(c_table.getValueAt(c_row, c_col)==null) {
                                c_table.setValueAt("", c_row, c_col);
                            }
                            if (!c_table.getValueAt(c_row, c_col).toString().toLowerCase().contains
                                    (c_jcbmi[i].getText().toLowerCase())) {
                                c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase() +
                                        c_jcbmi[i].getText()+" ", j, service.getCCI(service.nc_type_en));
                            }
                        }
                        else if(!c_jcbmi[i].isSelected()){
                            if(c_table.getValueAt(c_row, c_col)!=null) {
                                c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase().
                                        replace(c_jcbmi[i].getText().toLowerCase()+" ", ""),
                                                       j, service.getCCI(service.nc_type_en));
                            }
                        }
                    }
                }
            }
        }
    }

    private void word_type_from_table_to_combobox(JCheckBoxMenuItem[] c_jcbmi, JTable c_table, int c_col, int c_row){
        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_col == service.getCCI(service.nc_type_en)) {
                if (i == c_row) {
                    for (int j = 0; j < service.word_types.length; j++) {
                        if((c_table.getValueAt(c_row, service.getCCI(service.nc_type_en)) == null) ||
                           (Objects.equals(c_table.getValueAt(c_row, service.getCCI(service.nc_type_en)).toString(), "")))
                        {c_jcbmi[j].setSelected(false);}
                        else if (c_table.getValueAt(c_row, service.getCCI(service.nc_type_en)).toString().toLowerCase().
                                contains(c_jcbmi[j].getText().toLowerCase())){c_jcbmi[j].setSelected(true);}
                        else{c_jcbmi[j].setSelected(false);}
                    }
                }
            }
        }
    }

    private void export_elem_from_tab_to_tab(JTable t1, JTable t2, int n){
        table_list_delete_rows_names[n] = new ArrayList();
        table_list_delete_rows_translate[n] = new ArrayList();
        table_list_delete_rows_word_type[n] = new ArrayList();
        table_list_delete_rows_indexes[n] = new ArrayList();

        for (int i = 0; i < t1.getRowCount(); i++) {

            Boolean checked = Boolean.valueOf(t1.getValueAt(i, 0).toString());
            int row = (int) t1.getValueAt(i, 1) - 1;
            if (checked) {
                table_list_delete_rows_names[n].add(t1.getValueAt(row, 2).toString());
                table_list_delete_rows_translate[n].add(t1.getValueAt(row, 3).toString());
                if(t1.getValueAt(row, 4)== null) {
                    table_list_delete_rows_word_type[n].add("");
                }
                else
                    table_list_delete_rows_word_type[n].add(t1.getValueAt(row, 4).toString());
                table_list_delete_rows_indexes[n].add(row);
            }
        }

        int ldr_st = 0;
        for (Integer al_indexe : table_list_delete_rows_indexes[n]) {
            int ldr_value = al_indexe - ldr_st;
            ((DefaultTableModel) t1.getModel()).removeRow(ldr_value);
            ldr_st++;
        }

        for (int i = 0; i < t1.getRowCount(); i++) {
            t1.setValueAt(i + 1, i, 1);
        }

        for (int i = 0; i < table_list_delete_rows_indexes[n].size(); i++) {
            DefaultTableModel model = (DefaultTableModel) t2.getModel();
            model.addRow(new Object[]{false, t2.getRowCount() + 1, table_list_delete_rows_names[n].get(i),
                                                                   table_list_delete_rows_translate[n].get(i),
                                                                   table_list_delete_rows_word_type[n].get(i),
                                                                   "", ""});
        }

        t2.changeSelection(t2.getRowCount() - 1, 0, false, false);

        try {
            JOptionPane.showMessageDialog(
                    null,
                    table_list_delete_rows_names[n] + " " + lang.SetLanguage("OPM_Words_repositioned"),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null, "| " + e.getMessage(),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        }

        table_list_delete_rows_names[n].clear();
        table_list_delete_rows_translate[n].clear();
        table_list_delete_rows_word_type[n].clear();
        table_list_delete_rows_indexes[n].clear();
    }

    private void add_warning_icon_to_col(){
        URL url = ToolsUI.class.getResource("/icons/ic_warning_20x20.png");
        ImageIcon im = new ImageIcon(url);

        ArrayList<String>[] list_val_from_col2 = new ArrayList[2];
        ArrayList<String>[] list_val_from_col3 = new ArrayList[2];
        for(int i=0; i<2; i++){
            list_val_from_col2[i] = new ArrayList<>();
            list_val_from_col3[i] = new ArrayList<>();
            ja_equal_words[i] = new JSONArray();
            ja_equal_words2[i] = new JSONArray();
            moving_equivalent_elements_to_array(list_val_from_col2[i], ja_equal_words[i], im,
                                                   service.getCCI(service.nc_word_en),i);
            moving_equivalent_elements_to_array(list_val_from_col3[i], ja_equal_words2[i], im,
                                                   service.getCCI(service.nc_translate_en),i);
        }

        for(int i=0; i< table1.getRowCount()-1; i++){
            for(int j=0; j< table2.getRowCount()-1; j++){
                if(table1.getValueAt(i,service.getCCI(service.nc_word_en)).toString().toLowerCase().
                        equals(table2.getValueAt(j,service.getCCI(service.nc_word_en)).toString().toLowerCase())){
                    table1.setValueAt(im, i, service.getCCI("W*"));
                    table2.setValueAt(im, j, service.getCCI("W*"));
                    t1t2_list_eq.add("T1: № "+i+"T2: № "+j+" ");
                }
                if(table1.getValueAt(i,service.getCCI(service.nc_translate_en)).toString().toLowerCase().
                        equals(table2.getValueAt(j,service.getCCI(service.nc_translate_en)).toString().toLowerCase())){
                    table1.setValueAt(im, i, service.getCCI("T*"));
                    table2.setValueAt(im, j, service.getCCI("T*"));
                    t1t2_list_eq.add("T1: № "+i+"T2: № "+j+" ");
                }
            }
        }
    }

    private void moving_equivalent_elements_to_array(ArrayList<String> list_val_from_col, JSONArray current_ja_ee,
                                    ImageIcon im, int c_col, int c_tab){
        String tc = "";
        String tci = "";
        JTable tab = new JTable();
        TreeSet set = new TreeSet();

        if(c_tab == 0){
            tab = table1;
        }
        else if(c_tab == 1) {
            tab = table2;
        }
        if(c_col == service.getCCI(service.nc_word_en)){
            tc = service.nc_word_en;
            tci = "W*";
            current_ja_ee = ja_equal_words[c_tab];
        }
        else if(c_col == service.getCCI(service.nc_translate_en)){
            tc = service.nc_translate_en;
            tci = "T*";
            current_ja_ee = ja_equal_words2[c_tab];
        }
        int n = tab.getRowCount()-1;
        if(tab.getRowCount()>0) {
            do {
                list_val_from_col.add(tab.getValueAt(n, service.getCCI(tc)).toString());
                Object obj = tab.getValueAt(n, service.getCCI(tc));
                if(obj.toString().length() != 0) {
                    if (!set.add(obj)) {
                        tab.setValueAt(im, n, service.getCCI(tci));
                        tab.setValueAt(im, tab.getRowCount()-1-list_val_from_col.indexOf(obj), service.getCCI(tci));
                        current_ja_ee.put(tab.getRowCount()-1-list_val_from_col.indexOf(obj), obj);
                        current_ja_ee.put(n, obj);
                    }
                }
            } while (--n >= 0);
        }
    }

    private void message_warning_ew(JTable tab, MouseEvent evt, JSONArray ja_ew, String col_v){
        int row = tab.rowAtPoint(evt.getPoint());
        int col = tab.columnAtPoint(evt.getPoint());
        String col_s = "";
        if(Objects.equals(col_v, "W*")){
            col_s = service.nc_word_en;
        }
        else if(Objects.equals(col_v, "T*")){
            col_s = service.nc_translate_en;
        }

        for (int i = 0; i < ja_ew.length(); i++) {
            if (row == i && col == service.getCCI(col_v)) {
                ArrayList<Integer> list_numbers_ee =
                        forming_numbers_list_with_equivalent_elements(ja_ew, ja_ew.get(i).toString());
                if(list_numbers_ee.size()>1) {
                    if (!Objects.equals(ja_ew.get(i).getClass().toString(), "class org.json.JSONObject$Null")) {
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + ja_ew.get(i).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exists") + "№ " + list_numbers_ee,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            }
        }

        for(int i=0; i< table1.getRowCount()-1; i++){
            for(int j=0; j< table2.getRowCount()-1; j++){
                if(table1.getValueAt(i,service.getCCI(col_s)).toString().toLowerCase().
                        equals(table2.getValueAt(j,service.getCCI(col_s)).toString().toLowerCase())){
                    if(tab == table1) {
                        if (row == i && col == service.getCCI(col_v)) {
                            message_warn_show(j, i, col_s, table1);
                        }
                    }
                    if(tab == table2) {
                        if (row == j && col == service.getCCI(col_v)) {
                            message_warn_show(i, j, col_s, table2);
                        }
                    }
                }
            }
        }
    }

    private void message_warn_show(int i, int j, String col_s, JTable с_table){
        int numb = i + 1;
        JOptionPane.showMessageDialog(
                null,
                "'" + с_table.getValueAt(j, service.getCCI(col_s)).toString() + "'" +
                        lang.SetLanguage("OPM_Already_exists") + "1 | № " + numb,
                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
        );
    }

    private ArrayList<Integer> forming_numbers_list_with_equivalent_elements(JSONArray ja_equal_words_v, String v) {
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

        private CellData(JTable source) {
            this.col = source.getSelectedColumn();
            this.row = source.getSelectedRow();

            for(int i=0; i<value.length;i++){
                this.value[i] = source.getValueAt(row, i);
            }

            this.table = source;
        }

        private int getColumn() {
            return col;
        }

        private JTable getTable() {
            return table;
        }

        private boolean swapValuesWith(int targetRow, int targetCol) {
            boolean swapped = false;
            if(col != service.getCCI("✓")) {
                if (targetCol == col) {
                    if(col ==  service.getCCI("№")) {
                        for(int z=0; z<value.length; z++){
                            if(z!=service.getCCI("№")){
                                export_content(targetRow, z, z);
                            }
                        }
                    }
                    else if(col ==  service.getCCI(service.nc_word_en)){
                        export_content(targetRow, service.getCCI(service.nc_word_en),
                                                  service.getCCI(service.nc_word_en));
                        export_content(targetRow, service.getCCI("W*"),
                                                  service.getCCI("W*"));
                    }
                    else if(col ==  service.getCCI(service.nc_translate_en)){
                        export_content(targetRow, service.getCCI(service.nc_translate_en),
                                                  service.getCCI(service.nc_translate_en));
                        export_content(targetRow, service.getCCI("T*"),
                                                  service.getCCI("T*"));
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

    private static final DataFlavor CELL_DATA_FLAVOR = createConstant(CellData.class);

    public class CellDataTransferable implements Transferable {

        private CellData cellData;

        private CellDataTransferable(CellData cellData) {
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

    static private DataFlavor createConstant(Class clazz) {
        try {
            return new DataFlavor(clazz, "application/x-java-celldata");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class TransferHelper extends TransferHandler {

        private static final long serialVersionUID = 1L;

        private  TransferHelper() {
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent source) {
            // Create the transferable
            JTable table = (JTable) source;
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

    void showFrame() {
        JFrame frame = new JFrame("ToolsUI");
        frame.setContentPane(new ToolsUI().tools_panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
