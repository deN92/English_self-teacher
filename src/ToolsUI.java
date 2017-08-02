import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolsUI extends JFrame {
    JScrollPane s_pane1;
    JScrollPane s_pane2;
    JTable table1;
    JTable table2;
    private JButton Btn_Add, Btn_Del, Btn_Save, Btn_Words_up, Btn_Words_down, Btn_Search1, Btn_Search2;
    private JPanel tools_panel, panel_right, panel_table;
    public JPanel panel_settings, panel_design;

    private JTextField field_search11, field_search12, field_search21, field_search22;
    private JSpinner spinner1, spinner2;
    private JLabel Lbl_sp2_to_sp3;
    private JLabel Lbl_Scope_questions;
    private JLabel Lbl_TSW_Count_Checked, Lbl_TNW_Count_Checked;

    private JButton Btn_Check_duplicates;
    private JCheckBox JCB_Scope_questions;
    private JButton Btn_Exchange_positions;
    private JCheckBox CB_t1_all_checking;
    private JCheckBox CB_t2_all_checking;
    private JButton Btn_Parts_of_speech1;
    private JButton Btn_Parts_of_speech2;
    private JButton Btn_Add_multiple_items;
    private JSONArray[] ja_equal_words = new JSONArray[2];
    private JSONArray[] ja_equal_words2 = new JSONArray[2];

    private Service[] service;
    private Service.Language lang;
    private ArrayList<String> t1t2_list_eq = new ArrayList<>();
    private ArrayList<String>[] table_list_checked_rows_names = new ArrayList[2];
    private ArrayList<String>[] table_list_checked_rows_translate = new ArrayList[2];
    private ArrayList<String>[] table_list_checked_rows_word_type = new ArrayList[2];
    private ArrayList<Date>[] table_list_checked_rows_word_date = new ArrayList[2];
    private ArrayList<String>[] table_list_checked_rows_word_example = new ArrayList[2];
    private ArrayList<Integer>[] table_list_checked_rows_indexes = new ArrayList[2];


    private MouseEvent evt_t1, evt_t2;
    private JTable t1ort2 = null;
    private JPopupMenu menu_wt = new JPopupMenu();
    private DefaultTableCellRenderer dtcr;
    private TableRowSorter<DefaultTableModel> sorter1;
    private TableRowSorter<DefaultTableModel> sorter2;
    private String reg1 = "";
    private int[] table_column_widths = {20, 32, 185, 185, 55, 20, 20, 20, 48};
    private JTable[] tables_array = {table1, table2};

    private JSONObject[] jo_words_examples_new_studied = new JSONObject[2];

    private JCheckBox[] CB_WT1 = new JCheckBox[new Service().word_types.length];
    private JCheckBox[] CB_WT2 = new JCheckBox[new Service().word_types.length];

    private Service service_nc = new Service();
    private int nc_check = service_nc.getCCI(service_nc.nc_check);
    private int nc_number = service_nc.getCCI(service_nc.nc_number);

    private int nc_word_en = service_nc.getCCI(service_nc.nc_word_en);
    private int nc_translate_en = service_nc.getCCI(service_nc.nc_translate_en);
    private int nc_type_en = service_nc.getCCI(service_nc.nc_type_en);
    private int nc_example_en = service_nc.getCCI(service_nc.nc_example_en);
    private int nc_word_copy_en = service_nc.getCCI(service_nc.nc_word_copy_en);
    private int nc_translate_copy_en = service_nc.getCCI(service_nc.nc_translate_copy_en);
    private int nc_date_en = service_nc.getCCI(service_nc.nc_date_en);
    
    

    ToolsUI(ArrayList list_checked_indexes) {

//        Create menu for words_type(Parts of speech)
        for(int i=0; i< CB_WT1.length; i++){
            CB_WT1[i] = new JCheckBox();
        }

        for(int i=0; i< CB_WT2.length; i++){
            CB_WT2[i] = new JCheckBox();
        }

        final JPopupMenu menu1 = new JPopupMenu();
        final JPopupMenu menu2 = new JPopupMenu();

        for (JCheckBox aCb_word_types1 : CB_WT1) {
            menu1.add(aCb_word_types1);
        }

        for (JCheckBox aCb_word_types2 : CB_WT2) {
            menu2.add(aCb_word_types2);
        }

        Btn_Parts_of_speech1.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu1.show(Btn_Parts_of_speech1, 0, Btn_Parts_of_speech1.getHeight());
            }
        });

        Btn_Parts_of_speech2.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu2.show(Btn_Parts_of_speech2, 0, Btn_Parts_of_speech2.getHeight());
            }
        });


        tools_panel.setSize(715, 640);
        tools_panel.setMaximumSize(new Dimension(715, 640));
        tools_panel.setMinimumSize(new Dimension(715, 640));
        tools_panel.setPreferredSize(new Dimension(715, 640));

//        canEdit value in the cells for the tables
        boolean[] canEdit = {true, false, true, true, false, false, false, false, false};
        service = new Service[3];
        service[0] = new Service();
        service[1] = new Service();
        service[0].table(1, canEdit, "words_new");
        service[1].table(1, canEdit, "words_studied");
        jo_words_examples_new_studied[0] = new JSONObject();
        jo_words_examples_new_studied[1] = new JSONObject();

        try {
            service[0].dir_vocabulary_file(0);
            jo_words_examples_new_studied[0] = (JSONObject) new JSONObject(service[0].content_file(service[0].current_path[0])).
                    getJSONObject("words_example").get("words_new");
            jo_words_examples_new_studied[1] = (JSONObject) new JSONObject(service[0].content_file(service[0].current_path[0])).
                    getJSONObject("words_example").get("words_studied");
        } catch (IOException e) {
            e.printStackTrace();
        }

        DefaultTableModel model = service[0].model[1];
        DefaultTableModel model2 = service[1].model[1];
        elements_color();

        sorter1 = new TableRowSorter<>(model);
        sorter2 = new TableRowSorter<>(model2);
        sorter1.setRowFilter(RowFilter.regexFilter(reg1, nc_type_en));
        sorter2.setRowFilter(RowFilter.regexFilter(reg1, nc_type_en));
        table1.setRowSorter(sorter1);
        table2.setRowSorter(sorter2);
        table1.setModel(model);
        table2.setModel(model2);
        table1.getTableHeader().setReorderingAllowed(false);
        table2.getTableHeader().setReorderingAllowed(false);
        table_header(0);
        table_header(1);

        for (int i = 0; i < table_column_widths.length; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
            table2.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }

        lang = new Service.Language(service[0].current_path[1]);
        elements_name();

        int large_numb;
        if (service[0].ja_words.length() >= service[1].ja_words.length()) {
            large_numb = service[0].ja_words.length();
        } else large_numb = service[1].ja_words.length();
        spinner1.setModel(new SpinnerNumberModel(1, 1, large_numb - 1, 1));
        spinner2.setModel(new SpinnerNumberModel(2, 2, large_numb, 1));

        scope_questions(false);

        Btn_Words_up.setEnabled(false);
        Btn_Words_down.setEnabled(false);
        Btn_Del.setEnabled(false);
        Btn_Exchange_positions.setEnabled(false);

        dtcr = new DefaultTableCellRenderer() {
            Font font = new Font("TimesRoman", Font.PLAIN, 10);
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yy");
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if( value instanceof Date) {
                    value = f.format(value);
                }
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(font);
                return this;
            }
        };

        table1.getColumnModel().getColumn(nc_type_en).setCellRenderer(dtcr);
        table2.getColumnModel().getColumn(nc_type_en).setCellRenderer(dtcr);
        table1.getColumnModel().getColumn(nc_date_en).setCellRenderer(dtcr);
        table2.getColumnModel().getColumn(nc_date_en).setCellRenderer(dtcr);

        Lbl_TSW_Count_Checked.setText("0");
        Lbl_TNW_Count_Checked.setText("0");

        JCheckBoxMenuItem[] jcbmi = new JCheckBoxMenuItem[service[0].word_types.length];
        for (int i = 0; i < service[0].word_types.length; i++) {
            jcbmi[i] = new JCheckBoxMenuItem(service[0].word_types[i]);
        }

        table_list_checked_rows_names[0] = new ArrayList();
        table_list_checked_rows_names[1] = new ArrayList();

        if (list_checked_indexes.size() != 0) {
            for (int i = 0; i < list_checked_indexes.size(); i++) {
                table1.setValueAt(true, (int) list_checked_indexes.get(i), 0);
                table_list_checked_rows_names[0].add(table1.getValueAt((int) list_checked_indexes.get(i), 2).toString());
            }
            Lbl_TNW_Count_Checked.setText(list_checked_indexes.size() + "");
            Btn_Words_down.setEnabled(true);
        }

        for (int i = 0; i < service[0].word_types.length; i++) {
            menu_wt.add(jcbmi[i]);
        }

        table1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(table1.columnAtPoint(e.getPoint()) == 1){
                    sort_by_number(e, 0, canEdit);
                }
            }
        });

        table2.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(table2.columnAtPoint(e.getPoint()) == 1){
                    sort_by_number(e, 1, canEdit);
                }
            }
        });

        Btn_Add.addActionListener(actionEvent -> {
            if (!table1.getValueAt(table1.getRowCount() - 1, nc_word_en).equals("")) {
                DefaultTableModel model1 = (DefaultTableModel) table1.getModel();
                model1.addRow(new Object[]{false, table1.getRowCount() + 1, "", "", "", "", "", new Date()});

                for (int i = 0; i < table_column_widths.length; i++) {
                    table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
                }
                table1.changeSelection(table1.getRowCount() - 1, 0, false, false);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Line_not_completed").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        Btn_Add_multiple_items.addActionListener(e -> {
            JTextArea text_area[] = new JTextArea[2];
            JScrollPane jb[] = new JScrollPane[2];

            JPanel panel_wt = new JPanel();

            String[] columns_name = (String[])lang.SetLanguage("TC_name");
            text_area[0] = new JTextArea(""+lang.SetLanguage("Lbl_Example")+"\n"+columns_name[2]+"1\n"+
                                                           columns_name[2]+"2\n"+
                                                           columns_name[2]+"3\n", 10, 15);
            text_area[1] = new JTextArea(""+lang.SetLanguage("Lbl_Example")+"\n"+columns_name[3]+"1\n"+
                                                           columns_name[3]+"2\n"+
                                                           columns_name[3]+"3\n", 10, 15);

            jb[0] = new JScrollPane(text_area[0]);
            jb[1] = new JScrollPane(text_area[1]);

            panel_wt.add(jb[0]);
            panel_wt.add(jb[1]);

            switch (JOptionPane.showConfirmDialog(null, panel_wt,
                    lang.SetLanguage("OPM_Title").toString(), JOptionPane.OK_CANCEL_OPTION)) {
                case JOptionPane.OK_OPTION:
                    ArrayList<String>[] list_name_translate = new ArrayList[2];

                    list_name_translate[0] = new ArrayList<>();
                    list_name_translate[1] = new ArrayList<>();

                    Collections.addAll(list_name_translate[0], text_area[0].getText().split("\\r?\\n"));
                    Collections.addAll(list_name_translate[1], text_area[1].getText().split("\\r?\\n"));

                    if(list_name_translate[0].size()>list_name_translate[1].size()) {
                        list_name_translate[1] = add_value_to_empty_line(list_name_translate[0], list_name_translate[1]);
                    }
                    else if(list_name_translate[1].size()>list_name_translate[0].size()){
                        list_name_translate[0] = add_value_to_empty_line(list_name_translate[1], list_name_translate[0]);
                    }


                    for(int i=0; i<list_name_translate[0].size(); i++) {
                        if (!table1.getValueAt(table1.getRowCount() - 1, nc_word_en).equals("")) {
                            DefaultTableModel model1 = (DefaultTableModel) table1.getModel();
                            model1.addRow(new Object[]{false, table1.getRowCount() + 1,
                                    list_name_translate[0].get(i).
                                            replace(",","").
                                            replace("'","").
                                            replace("\"", ""),
                                    list_name_translate[1].get(i).
                                            replace(",","").
                                            replace("'","").
                                            replace("\"", ""),
                                    "", "", "", new Date()});
                            for (int j = 0; j < table_column_widths.length; j++) {
                                table1.getColumnModel().getColumn(j).setMaxWidth(table_column_widths[j]);
                            }
                            table1.changeSelection(table1.getRowCount() - 1, 0, false,
                                    false);
                        }
                    }
                    break;
            }
        });

        Btn_Del.addActionListener(actionEvent -> {
            int jp = JOptionPane.showConfirmDialog(null,
                    lang.SetLanguage("OPM_Are_you_sure"),
                    lang.SetLanguage("OPM_Title_removing").toString(), 0);
            if (jp == JOptionPane.YES_OPTION) {
                try {
                    Boolean t1 = false;
                    Boolean t2 = false;
                    for(int i=0; i< table1.getRowCount();i++){
                        Boolean c = (Boolean) table1.getValueAt(i, nc_check);
                        if(c){ t1 = true;}
                    }
                    for(int i=0; i< table2.getRowCount();i++){
                        Boolean c = (Boolean) table2.getValueAt(i, nc_check);
                        if(c){ t2 = true;}
                    }
                    if(t1){ delete_elements(0);}
                    if(t2){ delete_elements(1);}
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Words_not_removed") + "| " + e.getMessage(),
                            lang.SetLanguage("OPM_Title_removing").toString(),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        Btn_Save.addActionListener(actionEvent -> {
            if (table1.getRowCount() < 5) {
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Minimum_5_words").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            }
            wt_cols();
            add_warning_icon_to_col(true);
//
            for(int i=0; i< table1.getRowCount();i++){
                int n = i+1;
                if(!table1.getValueAt(i, nc_word_copy_en).equals("")){
                    table1.setValueAt(table1.getValueAt(i,nc_word_en).toString()+" "+n,
                            i, nc_word_en);
                }
                if(!table1.getValueAt(i, nc_translate_copy_en).equals("")){
                    table1.setValueAt(table1.getValueAt(i,nc_translate_en).toString()+" "+n,
                            i, nc_translate_en);
                }
            }

            wt_cols();
            add_warning_icon_to_col(false);

            JSONObject final_ja_words = new JSONObject();

            JSONArray[] ja_words_new_studied = new JSONArray[2];

            JSONObject[] jo_words_type_new_studied = new JSONObject[2];
            JSONObject[] jo_words_date_new_studied = new JSONObject[2];

            JSONObject jo_words_date = new JSONObject();
            JSONObject jo_words_type = new JSONObject();
            JSONObject jo_words_example = new JSONObject();

            ArrayList<String>[] list_unique_date = new ArrayList[2];

            String[] str_words_new_studied = {"words_new", "words_studied"};

            for(int n=0; n<tables_array.length; n++){
                ja_words_new_studied[n] = new JSONArray();
                jo_words_date_new_studied[n] = new JSONObject();
                jo_words_type_new_studied[n] = new JSONObject();
                list_unique_date[n] = new ArrayList<>();

                for (int i = 0; i < tables_array[n].getRowCount(); i++) {
                    String key = tables_array[n].getValueAt(i, nc_word_en).toString();
                    String val = tables_array[n].getValueAt(i, nc_translate_en).toString();
                    ja_words_new_studied[n].put(i, new JSONObject().put(key, val));
                    for (String anAwt : service[0].word_types) {
                        if (tables_array[n].getValueAt(i, nc_type_en) != null) {
                            if (tables_array[n].getValueAt(i, nc_type_en).toString().toLowerCase().
                                    contains(anAwt.toLowerCase())) {
                                jo_words_type_new_studied[n].append(anAwt.toLowerCase(), i);
                            }
                        }
                    }

                    try {
                        String key_date = tables_array[n].getValueAt(i, nc_date_en).toString();
                        if (!list_unique_date[n].contains(key_date)) {
                            list_unique_date[n].add(key_date);
                        }

                        for (String anAwt : list_unique_date[n]) {
                            if (tables_array[n].getValueAt(i, nc_date_en).toString().contains(anAwt)) {
                                jo_words_date_new_studied[n].append(anAwt, i);
                            }
                        }
                    }
                    catch (Exception ex) {
                        jo_words_date_new_studied[n].append(new Date().toString(), i);
                    }
                }
                for (String anAwt : service[0].word_types) {
                    if (!jo_words_type_new_studied[n].has(anAwt)) {
                        jo_words_type_new_studied[n].append(anAwt, -1);
                    }
                }
                jo_words_type.put(str_words_new_studied[n], jo_words_type_new_studied[n]);
                jo_words_date.put(str_words_new_studied[n], jo_words_date_new_studied[n]);
                jo_words_example.put(str_words_new_studied[n], jo_words_examples_new_studied[n]);
            }

            final_ja_words.put("words_example", jo_words_example);
            final_ja_words.put("words_type", jo_words_type);
            final_ja_words.put("words_date", jo_words_date);

            final_ja_words.put(str_words_new_studied[0], ja_words_new_studied[0]);
            final_ja_words.put(str_words_new_studied[1], ja_words_new_studied[1]);
            try {
                service[0].write_content_in_file(service[0].current_path[0], final_ja_words, "edit");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Words_success_saved").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = table1;
                mouse_events_in_tables(table1, evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                super.mousePressed(evt);
                t1ort2 = table1;
                mouse_events_in_tables(table1, evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                super.mouseReleased(evt);
                t1ort2 = table1;
                mouse_events_in_tables(table1, evt);
            }
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = table2;
                mouse_events_in_tables(table2, evt);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                super.mousePressed(evt);
                t1ort2 = table2;
                mouse_events_in_tables(table2, evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                super.mouseReleased(evt);
                t1ort2 = table2;
                mouse_events_in_tables(table2, evt);
            }
        });

        menu_wt.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                if (t1ort2.equals(table1)) {
                    word_type_from_table_to_combobox(jcbmi, table1, table1.columnAtPoint(evt_t1.getPoint()),
                            table1.rowAtPoint(evt_t1.getPoint()));
                } else if (t1ort2.equals(table2)) {
                    word_type_from_table_to_combobox(jcbmi, table2, table2.columnAtPoint(evt_t2.getPoint()),
                            table2.rowAtPoint(evt_t2.getPoint()));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {}

        });

        for (int k = 0; k < service[0].word_types.length; k++) {
            jcbmi[k].addActionListener(actionEvent -> {
                if (t1ort2.equals(table1)) {
                    word_type_from_combobox_to_table(jcbmi, table1,
                            table1.columnAtPoint(evt_t1.getPoint()), table1.rowAtPoint(evt_t1.getPoint()));
                } else if (t1ort2.equals(table2)) {
                    word_type_from_combobox_to_table(jcbmi, table2,
                            table2.columnAtPoint(evt_t2.getPoint()), table2.rowAtPoint(evt_t2.getPoint()));
                }
            });
        }

//      Export elements from table2 to table1
        Btn_Words_up.addActionListener(actionEvent -> {
            int jp = JOptionPane.showConfirmDialog(null,
                    lang.SetLanguage("OPM_Are_you_sure") + "\n" +
                            lang.SetLanguage("OPM_Title_transfer") + table_list_checked_rows_names[1].size() +
                            " " + lang.SetLanguage("OPM_Items"),
                    lang.SetLanguage("OPM_Title_transfer").toString() + "...", JOptionPane.YES_NO_OPTION);
            if (jp == JOptionPane.YES_OPTION) {
                export_elem_from_tab_to_tab(table2, table1, 1);
                Btn_Words_up.setEnabled(false);
                part_btn_words_updown();
                Lbl_TSW_Count_Checked.setText("" + table_list_checked_rows_indexes[1].size());
            }
        });

//      Export elements from table1 to table2
        Btn_Words_down.addActionListener(actionEvent -> {
            int jp = JOptionPane.showConfirmDialog(null,
                    lang.SetLanguage("OPM_Are_you_sure") + "\n" +
                            lang.SetLanguage("OPM_Title_transfer") + table_list_checked_rows_names[0].size() +
                            " " + lang.SetLanguage("OPM_Items"),
                    lang.SetLanguage("OPM_Title_transfer").toString() + "...", JOptionPane.YES_NO_OPTION);
            if (jp == JOptionPane.YES_OPTION) {
                export_elem_from_tab_to_tab(table1, table2, 0);
                Btn_Words_down.setEnabled(false);
                part_btn_words_updown();
                Lbl_TNW_Count_Checked.setText("" + table_list_checked_rows_indexes[0].size());
            }
        });

        Btn_Search1.addActionListener(actionEvent ->
                service[0].find_name_translate(table1, field_search11, field_search12));

        Btn_Search2.addActionListener(actionEvent ->
                service[0].find_name_translate(table2, field_search21, field_search22));

        spinner1.addChangeListener(changeEvent -> {
            int jS1 = (int) spinner1.getValue();
            int jS2 = (int) spinner2.getValue();
            if (jS1 == jS2) {
                spinner2.setValue(jS1 + 1);
            }
            spinner_choice_item_table();
            part_scope_questions();
        });

        spinner2.addChangeListener(changeEvent -> {
            int jS1 = (int) spinner1.getValue();
            int jS2 = (int) spinner2.getValue();
            if (jS1 == jS2) {
                spinner1.setValue(jS2 - 1);
            }
            spinner_choice_item_table();
            part_scope_questions();
        });

        JCB_Scope_questions.addActionListener(actionEvent -> {
            spinner_choice_item_table();
            if (JCB_Scope_questions.isSelected()) {
                scope_questions(true);
            } else {
                scope_questions(false);
            }
            part_scope_questions();
        });

        Btn_Check_duplicates.addActionListener(e -> {
            wt_cols();
            add_warning_icon_to_col(false);
        });

        if (table1.getRowCount() < 5) {
            JOptionPane.showMessageDialog(
                    null,
                    lang.SetLanguage("OPM_Minimum_5_words").toString(),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.WARNING_MESSAGE);
        }

        for(int i=0; i<CB_WT1.length; i++) {
            CB_WT1[i].addActionListener(e -> ordering_type_words(0));
        }
        for(int i=0; i<CB_WT1.length; i++) {
            CB_WT2[i].addActionListener(e -> ordering_type_words(1));
        }

        Btn_Exchange_positions.addActionListener(e -> {
            ArrayList list1 = table_list_checked_rows_indexes[0];
            ArrayList list2 = table_list_checked_rows_indexes[1];

            if(list1 != null){
                if(list1.size() == 2) {
                    int index11 = (int) list1.get(0) + 1;
                    int index12 = (int) list1.get(1) + 1;
                    current_row(index11, index12, table1);
                }
            }
            if(list2 != null){
                if(list2.size() == 2) {
                    int index21 = (int) list2.get(0) + 1;
                    int index22 = (int) list2.get(1) + 1;
                    current_row(index21, index22, table2);
                }
            }
        });

        CB_t1_all_checking.addActionListener(e -> {
            t1ort2 = table1;
            if (CB_t1_all_checking.isSelected()) {
                all_checked_in_tab_tf(table1, true);
            } else all_checked_in_tab_tf(table1, false);
        });

        CB_t2_all_checking.addActionListener(e -> {
            t1ort2 = table2;
            if (CB_t2_all_checking.isSelected()) {
                all_checked_in_tab_tf(table2, true);
            } else all_checked_in_tab_tf(table2, false);
        });
    }

//    if list_names != list_translate
    private ArrayList<String> add_value_to_empty_line(ArrayList<String> c_list1, ArrayList<String> c_list2){
        if(c_list1.size()>c_list2.size()){
            int qw = c_list1.size()-c_list2.size();
            int i=0;
            while(i<qw){
                c_list2.add("");
                i++;
            }
        }
        return c_list2;
    }

    private void sort_by_number(MouseEvent e, int n, boolean[] c_canEdit){
        String lib = "words_new";
        if(n == 0){lib = "words_new";}
        else if(n == 1){ lib = "words_studied";}

        if(tables_array[n].columnAtPoint(e.getPoint()) == 1){
            service[n] = new Service();
            service[n].table(1, c_canEdit, lib);
            DefaultTableModel model12 = service[n].model[1];
            tables_array[n].setModel(model12);

            for(int i=0; i<tables_array[n].getRowCount(); i++){
                for(int j=0; j<table_list_checked_rows_names[n].size(); j++){
                    if(tables_array[n].getValueAt(i, service[n].getCCI(service[n].nc_word_en)).toString().
                                                equals(table_list_checked_rows_names[n].get(j))){
                        tables_array[n].setValueAt(true,i, 0);
                    }
                }
            }
            for (int i = 0; i < table_column_widths.length; i++) {
                tables_array[n].getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
            }
            tables_array[n].getColumnModel().getColumn(nc_type_en).setCellRenderer(dtcr);
            tables_array[n].getColumnModel().getColumn(nc_date_en).setCellRenderer(dtcr);
        }
    }

    private void all_checked_in_tab_tf(JTable c_table, Boolean tf) {
        for (int i = 0; i < c_table.getRowCount(); i++) {
            c_table.setValueAt(tf, i, nc_check);
        }
        transfering_checked_elements_to_arrays(c_table);
        update_count_checked_elements();
        visible_elements();
    }

    private void delete_elements(int n) {
        ArrayList<String>[] list_delete_rows_names = new ArrayList[2];
        ArrayList<Integer>[] list_delete_rows_indexes = new ArrayList[2];
        ArrayList<String>[] list_delete_limit = new ArrayList[2];

        int[] ldr_st = {0,0};
        list_delete_rows_names[n] = new ArrayList<>();
        list_delete_rows_indexes[n] = new ArrayList<>();
        list_delete_limit[n] = new ArrayList<>();
        for (int i = 0; i < tables_array[n].getRowCount(); i++) {
            Boolean checked = Boolean.valueOf(tables_array[n].getValueAt(i, nc_check).toString());
            int row = (int) tables_array[n].getValueAt(i, nc_number) - 1;
            if (checked) {
                list_delete_rows_names[n].add(
                        tables_array[n].getValueAt(row, nc_word_en).toString());
                list_delete_rows_indexes[n].add(row);
            }
        }

        for (Integer list_delete_rows_index :
                convert_current_index(table_list_checked_rows_names[n], tables_array[n])) {
            int ldr_value = list_delete_rows_index - ldr_st[n];
            ((DefaultTableModel) tables_array[n].getModel()).removeRow(ldr_value);
            ldr_st[n]++;
        }

        for (int i = 0; i < tables_array[n].getRowCount(); i++) {
            tables_array[n].setValueAt(i + 1, i, 1);
        }


        for (int i = 0; i < jo_words_examples_new_studied[n].length(); i++) {
            String key = jo_words_examples_new_studied[n].names().getString(i);
            String val = jo_words_examples_new_studied[n].getString(key);
            for(int j=0; j<list_delete_rows_names[n].size();j++) {
                if(list_delete_rows_names[n].get(j).equals(val)) {
                    jo_words_examples_new_studied[n].remove(key);
                }
            }
        }

//      true/false visible names elements in messages
        if (list_delete_rows_names[n].size() < 10) {
            list_delete_limit[n] = list_delete_rows_names[n];
        }

        Lbl_TNW_Count_Checked.setText(0+"");
        Lbl_TSW_Count_Checked.setText(0+"");

        if (list_delete_rows_names[n].size() != 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "" + list_delete_rows_names[n].size() + lang.SetLanguage("OPM_Items") +
                            list_delete_limit[n] + " " + lang.SetLanguage("OPM_Words_removed"),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        }

        list_delete_rows_names[n].clear();
        list_delete_rows_indexes[n].clear();
        list_delete_limit[n].clear();
    }

    private void current_row(int index1, int index2, JTable c_table){
        Object[] obj = new Object[service[0].name_cols.length];
        Object[] obj_temp = new Object[service[0].name_cols.length];

        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (index1 == (int) c_table.getValueAt(i, nc_number)) {
                for (int j = 0; j < service[0].name_cols.length; j++) {
                    obj[j] = c_table.getValueAt(i, j);
                }
            }
            if (index2 == (int) c_table.getValueAt(i, nc_number)) {
                for (int j = 0; j < service[0].name_cols.length; j++) {
                    obj_temp[j] = c_table.getValueAt(i, j);
                    if (j != 1) {
                        c_table.setValueAt(obj[j], i, j);
                    }
                }
            }
        }
        for (int j = 0; j < service[0].name_cols.length; j++) {
            if (j != 1) {
                c_table.setValueAt(obj_temp[j], index1 - 1, j);
            }
        }
    }

    private void ordering_type_words(int n){
        JTable c_tab;
        if(n == 0){c_tab = table1;}
        else{
            c_tab = table2;
        }
        Boolean[] cb_words_type_selected1 = new Boolean[8];
        Boolean[] cb_words_type_selected2 = new Boolean[8];
        for(int i=0; i<cb_words_type_selected1.length; i++){
            cb_words_type_selected1[i] = CB_WT1[i].isSelected();
        }
        for(int i=0; i<cb_words_type_selected2.length; i++){
            cb_words_type_selected2[i] = CB_WT2[i].isSelected();
        }

        Boolean[][] cb_words_type_selected = {cb_words_type_selected1, cb_words_type_selected2};

        if (Arrays.asList(cb_words_type_selected[n]).contains(true)){
            Btn_Add.setEnabled(false);
            Btn_Del.setEnabled(false);
            Btn_Save.setEnabled(false);
            Btn_Check_duplicates.setEnabled(false);
            ArrayList<String> list_words_type = new ArrayList<>();

            for(int i=0; i< service[0].word_types.length; i++){
                if(cb_words_type_selected[n][i]){list_words_type.add(service[0].word_types[i]);}
            }

            StringBuilder list_words_type_final = new StringBuilder();

            for (String aList_words_type : list_words_type) {
                list_words_type_final.append(aList_words_type).append("|");
            }

//          deleting last symbol "|"
            if (list_words_type_final.length() > 0) {
                reg1 = list_words_type_final.substring(0, list_words_type_final.length() - 1);
            }

            if(n==0) {
                sorter1.setRowFilter(RowFilter.regexFilter(reg1, nc_type_en));
            }else {
                sorter2.setRowFilter(RowFilter.regexFilter(reg1, nc_type_en));
            }
            for (int i = 0; i < c_tab.getRowCount(); i++) {
                c_tab.setValueAt(i + 1, i, 1);
            }
        } else{
            if(n==0) {
                sorter1.setRowFilter(RowFilter.regexFilter("", nc_type_en));
            }else {sorter2.setRowFilter(RowFilter.regexFilter("", nc_type_en));}
            Btn_Add.setEnabled(true);
            Btn_Del.setEnabled(true);
            Btn_Save.setEnabled(true);
            Btn_Check_duplicates.setEnabled(true);
            for (int i = 0; i < c_tab.getRowCount(); i++) {
                c_tab.setValueAt(i + 1, i, 1);
            }
        }
    }

    private void part_btn_words_updown() {
        spinner1.setValue(1);
        spinner2.setValue(2);
        if (table1.getRowCount() > 0) {table1.setValueAt(false, 0, 0);}
        if (table1.getRowCount() > 1) {table1.setValueAt(false, 1, 0);}
        if (table2.getRowCount() > 0) {table2.setValueAt(false, 0, 0);}
        if (table2.getRowCount() > 1) {table2.setValueAt(false, 1, 0);}

        part_scope_questions();
        scope_questions(false);
        JCB_Scope_questions.setSelected(false);
    }

    private void mouse_events_in_tables(JTable c_table, MouseEvent c_evt) {
        t1ort2 = c_table;
        int n = 0;
        if (t1ort2.equals(table1)) {
            evt_t1 = c_evt; n = 0;
        } else if (t1ort2.equals(table2)) {
            evt_t2 = c_evt; n = 1;
        }

        int col = c_table.columnAtPoint(c_evt.getPoint());
        int row = c_table.rowAtPoint(c_evt.getPoint());

        if (col == nc_check) {
            transfering_checked_elements_to_arrays(c_table);
            update_count_checked_elements();
            visible_elements();
        }

        if (col == nc_word_copy_en || col == nc_translate_copy_en) {
            message_warning_ew(c_table, c_evt, ja_equal_words[n], service[0].nc_word_copy_en);
            message_warning_ew(c_table, c_evt, ja_equal_words2[n], service[0].nc_translate_copy_en);
        }

        if (col == nc_type_en) {
            menu_wt.show(c_table, (int) c_evt.getPoint().getX(), (int) c_evt.getPoint().getY());
        }

        if (col == nc_example_en) {

            Object example_value = "";
//            int index_value = 0;
            for (int j = 0; j < jo_words_examples_new_studied[n].length(); j++) {
                String current_example_value = jo_words_examples_new_studied[n].names().get(j).toString();
                String index_val = jo_words_examples_new_studied[n].getString(current_example_value);

                if (index_val.equals(c_table.getValueAt(c_table.rowAtPoint(c_evt.getPoint()), nc_word_en))) {
                   example_value = current_example_value;
//                   index_value =  c_table.rowAtPoint(c_evt.getPoint());
                }
            }

            if (SwingUtilities.isLeftMouseButton(c_evt)) {
                if (c_evt.getClickCount() == 2 && !c_evt.isConsumed()) {
                    c_evt.consume();
                    JTextArea text_area;
                    try { text_area = new JTextArea(example_value.toString(), 10, 40); }
                    catch (Exception ex){
                        text_area = new JTextArea("", 10, 40); }

                    switch (JOptionPane.showConfirmDialog(null, new JScrollPane(text_area),
                                        lang.SetLanguage("OPM_Title").toString(), JOptionPane.OK_CANCEL_OPTION)) {
                        case JOptionPane.OK_OPTION:
                            String str_text_area = text_area.getText();
                            if(jo_words_examples_new_studied[0].has(str_text_area)){
                                str_text_area = copy_str_text_area(str_text_area, 0);
                            }else if(jo_words_examples_new_studied[1].has(str_text_area)){
                                str_text_area = copy_str_text_area(str_text_area, 1);
                            }

                            for (int j = 0; j < jo_words_examples_new_studied[n].length(); j++) {
                                String key_date2 = jo_words_examples_new_studied[n].names().get(j).toString();
                                String ja_data2 = jo_words_examples_new_studied[n].getString(key_date2);

                                if(ja_data2.equals(c_table.getValueAt(row, nc_word_en))){
                                    jo_words_examples_new_studied[n].remove(key_date2);
                                }
                            }
                            if(!str_text_area.equals("")) {
                                jo_words_examples_new_studied[n].put(str_text_area, c_table.getValueAt(row,
                                        nc_word_en).toString());
                                c_table.setValueAt(service[0].im11, row, nc_example_en);
                            }
                            else{
                                c_table.setValueAt(service[0].im12, row, nc_example_en);
                            }
                            break;
                    }
                }
            }
            if (SwingUtilities.isRightMouseButton(c_evt)) {
                c_evt.consume();
                if (example_value != null && !example_value.equals("")) {
                    c_table.setToolTipText(example_value.toString());
                    JOptionPane.showMessageDialog(null, example_value);
                }
            }
        }

        if (t1ort2.equals(table1)) {
            buttons_enable_tf(evt_t1, c_table);
        } else if (t1ort2.equals(table2)) {
            buttons_enable_tf(evt_t2, c_table);
        }
    }

    private String copy_str_text_area(String str_text_area, int n){
        for(int i=0; i<jo_words_examples_new_studied[n].length(); i++) {
            String key = jo_words_examples_new_studied[n].names().getString(i);
            int val = jo_words_examples_new_studied[n].getInt(key);
            if(key.equals(str_text_area)) {
                int real_row = val + 1;
                int real_table = n+1;
                str_text_area += "\nCOPY: t"+real_table+"/r"+real_row+"\n";
            }
        }
        return str_text_area;
    }

    private void part_scope_questions() {
        transfering_checked_elements_to_arrays(table1);
        transfering_checked_elements_to_arrays(table2);
        part_updating_count_checked_elements(Lbl_TNW_Count_Checked, 0, Btn_Words_down);
        part_updating_count_checked_elements(Lbl_TSW_Count_Checked, 1, Btn_Words_up);
    }

    private void visible_elements(){
        int count_checked = 0;
        int count_checked2 = 0;
        for(int i=0; i< table1.getRowCount(); i++){
            if((Boolean) table1.getValueAt(i, 0)){
                count_checked++;
            }
        }
        for(int i=0; i< table2.getRowCount(); i++){
            if((Boolean) table2.getValueAt(i, 0)){
                count_checked2++;
            }
        }
        int sum_cc = count_checked+count_checked2;
        if(sum_cc>0){Btn_Del.setEnabled(true);}else {Btn_Del.setEnabled(false);}
        if(count_checked == 2 || count_checked2==2){
            Btn_Exchange_positions.setEnabled(true);}else {
            Btn_Exchange_positions.setEnabled(false);}
    }

    private void part_updating_count_checked_elements(JLabel c_label, int n, JButton c_button) {
        c_label.setText("" + table_list_checked_rows_indexes[n].size());
        if (table_list_checked_rows_indexes[n].size() == 0) {
            c_button.setEnabled(false);
        } else c_button.setEnabled(true);
    }

    private void update_count_checked_elements() {
        if (t1ort2.equals(table1)) {
            part_updating_count_checked_elements(Lbl_TNW_Count_Checked, 0, Btn_Words_down);
        } else if (t1ort2.equals(table2)) {
            part_updating_count_checked_elements(Lbl_TSW_Count_Checked, 1, Btn_Words_up);
        }
    }

    private void elements_name() {
        Lbl_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
        Lbl_sp2_to_sp3.setText(lang.SetLanguage("Lbl_sp2_to_sp3_name").toString());
        Btn_Check_duplicates.setToolTipText(lang.SetLanguage("Btn_Duplicates_name").toString());
        Btn_Add.setToolTipText(lang.SetLanguage("Btn_Add_name").toString());
        Btn_Add_multiple_items.setToolTipText(lang.SetLanguage("Btn_Add_more_name").toString());
        Btn_Del.setToolTipText(lang.SetLanguage("Btn_Del_name").toString());
        Btn_Save.setToolTipText(lang.SetLanguage("Btn_Save_name").toString());
        Btn_Exchange_positions.setToolTipText(lang.SetLanguage("Btn_Exchange_positions").toString());
        Btn_Parts_of_speech1.setText(lang.SetLanguage("Btn_Parts_of_speech").toString());
        Btn_Parts_of_speech2.setText(lang.SetLanguage("Btn_Parts_of_speech").toString());
        String[] str = (String[])lang.SetLanguage("CB_Words_type");
        for(int i=0;i< CB_WT1.length;i++){
            CB_WT1[i].setText(str[i]);
            CB_WT2[i].setText(str[i]);
        }
    }

    private void elements_color() {
        table1.setBackground(Color.decode(new Service.SetColor(service[0].current_path[1], "ToolsUI Table").val));
        panel_settings.setBackground(Color.decode(new Service.SetColor(service[0].current_path[1], "ToolsUI").val));
        panel_table.setBackground(panel_settings.getBackground());
        panel_right.setBackground(panel_settings.getBackground());
    }

    private void scope_questions(boolean tf) {
        spinner1.setEnabled(tf);
        spinner2.setEnabled(tf);
        Lbl_Scope_questions.setEnabled(tf);
        Lbl_sp2_to_sp3.setEnabled(tf);
    }

    private void spinner_choice_item_table() {
        if (JCB_Scope_questions.isSelected()) {
            for (int i = 0; i < table1.getRowCount(); i++) {
                table1.setValueAt(false, i, nc_check);
            }
            for (int i = 0; i < table2.getRowCount(); i++) {
                table2.setValueAt(false, i, nc_check);
            }
            for (int i = (int) spinner1.getValue() - 1; i < (int) spinner2.getValue(); i++) {
                if (table1.getRowCount() > (int) spinner2.getValue() - 1) {
                    table1.setValueAt(true, i, nc_check);
                }
                if (table2.getRowCount() > (int) spinner2.getValue() - 1) {
                    table2.setValueAt(true, i, nc_check);
                }
            }
        } else {
            for (int i = (int) spinner1.getValue() - 1; i < (int) spinner2.getValue(); i++) {
                if (table1.getRowCount() > 0) {
                    table1.setValueAt(false, i, nc_check);
                }
                if (table2.getRowCount() > 0) {
                    table2.setValueAt(false, i, nc_check);
                }
            }
        }
    }

    private void table_header(int n) {
        tables_array[n].getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(int i=0; i<tables_array[n].getRowCount(); i++){
                    tables_array[n].setValueAt(i+1, i, 1);
                }
            }
        });
    }

    private void buttons_enable_tf(MouseEvent c_evt, JTable c_table) {
        int c_row = c_table.rowAtPoint(c_evt.getPoint());
        int c_col = c_table.columnAtPoint(c_evt.getPoint());

        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_row == i) {
                String val;
                if (c_table.getValueAt(i, c_col) == null) {
                    val = "";
                } else {
                    val = c_table.getValueAt(i, c_col).toString();
                }
                c_table.setToolTipText(val);
            }
            if (i < c_table.getRowCount() - 1) {
                int v1 = (int) c_table.getValueAt(i, 1);
                int v2 = (int) c_table.getValueAt(i + 1, 1);

                if (v2 - v1 != 1) {
                    Btn_Save.setEnabled(false);
                    Btn_Add.setEnabled(false);
                    Btn_Del.setEnabled(false);
                    break;
                } else {


                    Boolean[] cb_words_type_selected1 = new Boolean[8];
                    Boolean[] cb_words_type_selected2 = new Boolean[8];
                    for(int q=0; q<cb_words_type_selected1.length; q++){
                        cb_words_type_selected1[q] = CB_WT1[q].isSelected();
                    }
                    for(int q=0; q<cb_words_type_selected2.length; q++){
                        cb_words_type_selected2[q] = CB_WT2[q].isSelected();
                    }

                    Boolean[][] cb_words_type_selected = {cb_words_type_selected1, cb_words_type_selected2};

                    if ((Arrays.asList(cb_words_type_selected[0]).contains(true))||
                        (Arrays.asList(cb_words_type_selected[1]).contains(true))) {
                        Btn_Save.setEnabled(false);
                        Btn_Add.setEnabled(false);
                        Btn_Del.setEnabled(false);
                    }else{
                        Btn_Save.setEnabled(true);
                        Btn_Add.setEnabled(true);
                        Btn_Del.setEnabled(true);
                    }
                }
            }
        }
    }

    private void wt_cols() {
        for (int i = 0; i < table1.getRowCount(); i++) {
            table1.setValueAt("", i, nc_word_copy_en);
            table1.setValueAt("", i, nc_translate_copy_en);
        }
        for (int i = 0; i < table2.getRowCount(); i++) {
            table2.setValueAt("", i, nc_word_copy_en);
            table2.setValueAt("", i, nc_translate_copy_en);
        }
    }

    private void word_type_from_combobox_to_table(JCheckBoxMenuItem[] c_jcbmi, JTable c_table, int c_col, int c_row) {
        for (int j = 0; j < c_table.getRowCount(); j++) {
            if (c_col == 4) {
                if (j == c_row) {
                    for (int i = 0; i < service[0].word_types.length; i++) {
                        if (c_jcbmi[i].isSelected()) {
                            if (c_table.getValueAt(c_row, c_col) == null) {
                                c_table.setValueAt("", c_row, c_col);
                            }
                            if (!c_table.getValueAt(c_row, c_col).toString().toLowerCase().contains
                                    (c_jcbmi[i].getText().toLowerCase())) {
                                c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase() +
                                        c_jcbmi[i].getText() + " ", j, nc_type_en);
                            }
                        } else if (!c_jcbmi[i].isSelected()) {
                            if (c_table.getValueAt(c_row, c_col) != null) {
                                c_table.setValueAt(c_table.getValueAt(c_row, c_col).toString().toLowerCase().
                                                replace(c_jcbmi[i].getText().toLowerCase() + " ", ""),
                                        j, nc_type_en);
                            }
                        }
                    }
                }
            }
        }
    }

    private void word_type_from_table_to_combobox(JCheckBoxMenuItem[] c_jcbmi, JTable c_table, int c_col, int c_row) {
        for (int i = 0; i < c_table.getRowCount(); i++) {
            if (c_col == nc_type_en) {
                if (i == c_row) {
                    for (int j = 0; j < service[0].word_types.length; j++) {
                        if ((c_table.getValueAt(c_row, nc_type_en) == null) ||
                                (Objects.equals(c_table.getValueAt(c_row, nc_type_en).toString(), ""))) {
                            c_jcbmi[j].setSelected(false);
                        } else if (c_table.getValueAt(c_row, nc_type_en).toString().toLowerCase().
                                contains(c_jcbmi[j].getText().toLowerCase())) {
                            c_jcbmi[j].setSelected(true);
                        } else {
                            c_jcbmi[j].setSelected(false);
                        }
                    }
                }
            }
        }
    }

    private void transfering_checked_elements_to_arrays(JTable t1) {
        int n = 0;
        if (t1 == table1) {
            n = 0;
        } else if (t1 == table2) {
            n = 1;
        }
        table_list_checked_rows_names[n] = new ArrayList<>();
        table_list_checked_rows_translate[n] = new ArrayList<>();
        table_list_checked_rows_word_type[n] = new ArrayList<>();
        table_list_checked_rows_word_date[n] = new ArrayList<>();
        table_list_checked_rows_indexes[n] = new ArrayList<>();
        table_list_checked_rows_word_example[n] = new ArrayList<>();

        for (int i = 0; i < t1.getRowCount(); i++) {
            Boolean checked = Boolean.valueOf(t1.getValueAt(i, nc_check).toString());
            int row = (int) t1.getValueAt(i, 1) - 1;
            if (checked) {
                table_list_checked_rows_names[n].add(t1.getValueAt(row, nc_word_en).toString());
                table_list_checked_rows_translate[n].
                        add(t1.getValueAt(row, nc_translate_en).toString());
                if (t1.getValueAt(row, 4) == null) {
                    table_list_checked_rows_word_type[n].add("");
                } else
                    table_list_checked_rows_word_type[n].
                            add(t1.getValueAt(row, nc_type_en).toString());
                table_list_checked_rows_indexes[n].add(row);
                if (t1.getValueAt(row, 7) == null) {
                    table_list_checked_rows_word_date[n].add(new Date());
                } else {
                    table_list_checked_rows_word_date[n].add(
                            service[0].getFormattingDate(t1.getValueAt(row, nc_date_en).toString()));
                }
            }
        }


        ArrayList<String> ar1 = new ArrayList<>();
        for (int i = 0; i < table_list_checked_rows_names[n].size(); i++) {
            String key_check = table_list_checked_rows_names[n].get(i);
            for (int j = 0; j < jo_words_examples_new_studied[n].length(); j++) {
                String key = jo_words_examples_new_studied[n].names().getString(j);
                String val = jo_words_examples_new_studied[n].getString(key);
                ar1.add(val);
                if (Objects.equals(val, key_check)) {
                    table_list_checked_rows_word_example[n].add(key);
                }
            }
            if(!ar1.contains(key_check)){
                table_list_checked_rows_word_example[n].add("$Description%empty!$"+ i +"");
            }
        }
    }


//    if sorted tables by word, translate, date, current id elements are changed
    private ArrayList<Integer> convert_current_index(ArrayList<String> al1, JTable c_tab) {
        ArrayList<Integer> c_list = new ArrayList<>();
        for (int i = 0; i < ((DefaultTableModel) c_tab.getModel()).getRowCount(); i++){
            for (int j = 0; j < al1.size(); j++) {
                if(((DefaultTableModel) c_tab.getModel()).getValueAt(i, nc_word_en).equals(al1.get(j))) {
                    c_list.add(i);
                }
            }
        }
        return c_list;
    }


    private void export_elem_from_tab_to_tab(JTable t1, JTable t2, int n) {
        transfering_checked_elements_to_arrays(t1);
        add_warning_icon_to_col(true);
        int ldr_st = 0;
        for (Integer al_indexe : convert_current_index(table_list_checked_rows_names[n], t1)) {
            int ldr_value = al_indexe - ldr_st;
            ((DefaultTableModel) t1.getModel()).removeRow(ldr_value);
            ldr_st++;
        }

        for (int i = 0; i < t1.getRowCount(); i++) {
            t1.setValueAt(i + 1, i, nc_number);
        }
        DefaultTableModel model = (DefaultTableModel) t2.getModel();
        for (int i = 0; i < table_list_checked_rows_indexes[n].size(); i++) {
            model.addRow(new Object[]{
                false,
                t2.getRowCount() + 1,
                table_list_checked_rows_names[n].get(i),
                table_list_checked_rows_translate[n].get(i),
                table_list_checked_rows_word_type[n].get(i),
                "", "", "", table_list_checked_rows_word_date[n].get(i)});
        }

        for (int i = 0; i < jo_words_examples_new_studied[n].length(); i++) {
            String key = jo_words_examples_new_studied[n].names().getString(i);
            String val = jo_words_examples_new_studied[n].getString(key);
            for(int j=0; j<table_list_checked_rows_names[n].size();j++) {
                if(table_list_checked_rows_names[n].get(j).equals(val)) {
                    if(n==1) {
                        jo_words_examples_new_studied[0].put(key, val);
                    }else{
                        jo_words_examples_new_studied[1].put(key,val);
                    }
                    jo_words_examples_new_studied[n].remove(key);
                }
            }
        }
        JSONObject jo_skin_words_examples_new_studied;
        if(n==0){jo_skin_words_examples_new_studied = jo_words_examples_new_studied[1];}
        else{jo_skin_words_examples_new_studied = jo_words_examples_new_studied[0];}
        for(int i=0; i<t2.getRowCount(); i++) {
            if (t2.getValueAt(i, nc_example_en).toString().equals("")){
                t2.setValueAt(service[0].im12, i, nc_example_en);
                for (int j = 0; j < jo_skin_words_examples_new_studied.length(); j++) {
                    String key_example = jo_skin_words_examples_new_studied.names().get(j).toString();
                    String index_example = jo_skin_words_examples_new_studied.getString(key_example);
                    if (index_example.equals(t2.getValueAt(i,nc_word_en).toString())) {
                        t2.setValueAt(service[0].im11, i, nc_example_en);
                    }
                }
            }
        }

        t2.changeSelection(t2.getRowCount() - 1, 0, false, false);

        try {
            JOptionPane.showMessageDialog(
                    null,
                    table_list_checked_rows_names[n].size() + " " + lang.SetLanguage("OPM_Items") +
                            lang.SetLanguage("OPM_Words_repositioned"),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null, "| " + e.getMessage(),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
        }

        table_list_checked_rows_indexes[n].clear();
        table_list_checked_rows_names[n].clear();
        table_list_checked_rows_translate[n].clear();
        table_list_checked_rows_word_type[n].clear();
        table_list_checked_rows_word_date[n].clear();
    }

    private void add_warning_icon_to_col(Boolean save) {
        URL url = ToolsUI.class.getResource("/icons/ic_warning_20x20.png");
        ImageIcon im = new ImageIcon(url);

        ArrayList<String>[] list_val_from_col2 = new ArrayList[2];
        ArrayList<String>[] list_val_from_col3 = new ArrayList[2];
        for (int i = 0; i < 2; i++) {
            list_val_from_col2[i] = new ArrayList<>();
            list_val_from_col3[i] = new ArrayList<>();
            ja_equal_words[i] = new JSONArray();
            ja_equal_words2[i] = new JSONArray();
            if(save){
                transferring_equivalent_elements_to_array(true, list_val_from_col2[i], ja_equal_words[i], im,
                        nc_word_en, i);
                transferring_equivalent_elements_to_array(true, list_val_from_col3[i], ja_equal_words2[i], im,
                        nc_translate_en, i);
            } else{
                transferring_equivalent_elements_to_array(false, list_val_from_col2[i], ja_equal_words[i], im,
                        nc_word_en, i);
                transferring_equivalent_elements_to_array(false, list_val_from_col3[i], ja_equal_words2[i], im,
                        nc_translate_en, i);
            }
        }

        for (int i = 0; i < table1.getRowCount() - 1; i++) {
            for (int j = 0; j < table2.getRowCount() - 1; j++) {
                if (table1.getValueAt(i, nc_word_en).toString().toLowerCase().
                        equals(table2.getValueAt(j, nc_word_en).toString().toLowerCase())) {
                    table1.setValueAt(im, i, nc_word_copy_en);
                    table2.setValueAt(im, j, nc_word_copy_en);
                    t1t2_list_eq.add("T1: № " + i + "T2: № " + j + " ");
                }
                if (table1.getValueAt(i, nc_translate_en).toString().toLowerCase().
                        equals(table2.getValueAt(j, nc_translate_en).toString().toLowerCase())) {
                    table1.setValueAt(im, i, nc_translate_copy_en);
                    table2.setValueAt(im, j, nc_translate_copy_en);
                    t1t2_list_eq.add("T1: № " + i + "T2: № " + j + " ");
                }
            }
        }
    }

    private void transferring_equivalent_elements_to_array(Boolean save, ArrayList<String> list_val_from_col, JSONArray current_ja_ee,
                                                     ImageIcon im, int c_col, int c_tab) {
        String tc = "";
        String tci = "";
        JTable tab = new JTable();
        TreeSet set = new TreeSet();

        if (c_tab == 0) {
            tab = table1;
        } else if (c_tab == 1) {
            tab = table2;
        }
        if (c_col == nc_word_en) {
            tc = service[0].nc_word_en;
            tci = service[0].nc_word_copy_en;
            current_ja_ee = ja_equal_words[c_tab];
        } else if (c_col == nc_translate_en) {
            tc = service[0].nc_translate_en;
            tci = service[0].nc_translate_copy_en;
            current_ja_ee = ja_equal_words2[c_tab];
        }
        int n = tab.getRowCount() - 1;
        if (tab.getRowCount() > 0) {
            do {
                String line = tab.getValueAt(n, service[0].getCCI(tc)).toString();
                String line_new = line;
                Pattern p = Pattern.compile(".*\\D");
                Matcher m = p.matcher(line);
                if (m.find( )) {
                    line_new = m.group(0);
                }
                if(save) {
                    line_new = line;
                }

                list_val_from_col.add(line_new);
                Object obj = line;
                Object obj_new  = line_new;
                if (obj.toString().length() != 0) {
                    if (!set.add(obj_new)) {
                        tab.setValueAt(im, n, service[0].getCCI(tci));
                        tab.setValueAt(im, tab.getRowCount() - 1 - list_val_from_col.indexOf(obj_new), service[0].getCCI(tci));
                        current_ja_ee.put(tab.getRowCount() - 1 - list_val_from_col.indexOf(obj_new), obj);
                        current_ja_ee.put(n, obj);
                    }
                }
            } while (--n >= 0);
        }
    }

    private void message_warning_ew(JTable tab, MouseEvent evt, JSONArray ja_ew, String col_v) {
        int row = tab.rowAtPoint(evt.getPoint());
        int col = tab.columnAtPoint(evt.getPoint());
        String col_s = "";
        if (Objects.equals(col_v, service[0].nc_word_copy_en)) {
            col_s = service[0].nc_word_en;
        } else if (Objects.equals(col_v, service[0].nc_translate_copy_en)) {
            col_s = service[0].nc_translate_en;
        }

        for (int i = 0; i < ja_ew.length(); i++) {

            if (row == i && col == service[0].getCCI(col_v)) {
                ArrayList<Integer> list_numbers_ee =
                        forming_numbers_list_with_equivalent_elements(ja_ew, ja_ew.get(i).toString());
                if (list_numbers_ee.size() > 1) {
                    if (!Objects.equals(ja_ew.get(i).getClass().toString(), "class org.json.JSONObject$Null")) {
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + ja_ew.get(i).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exist") + "№ " + list_numbers_ee,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
            }
        }

        for (int i = 0; i < table1.getRowCount() - 1; i++) {
            for (int j = 0; j < table2.getRowCount() - 1; j++) {
                if (table1.getValueAt(i, service[0].getCCI(col_s)).toString().toLowerCase().
                        equals(table2.getValueAt(j, service[0].getCCI(col_s)).toString().toLowerCase())) {
                    if (tab == table1) {
                        if (row == i && col == service[0].getCCI(col_v)) {
                            message_warn_show(j, i, col_s, table1);
                        }
                    }
                    if (tab == table2) {
                        if (row == j && col == service[0].getCCI(col_v)) {
                            message_warn_show(i, j, col_s, table2);
                        }
                    }
                }
            }
        }
    }

    private void message_warn_show(int i, int j, String col_s, JTable с_table) {
        int numb = i + 1;
        JOptionPane.showMessageDialog(
                null,
                "'" + с_table.getValueAt(j, service[0].getCCI(col_s)).toString() + "'" +
                        lang.SetLanguage("OPM_Already_exists") + "1 | № " + numb,
                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
        );
    }

    private ArrayList<Integer> forming_numbers_list_with_equivalent_elements(JSONArray ja_equal_words_v, String v) {
        ArrayList<Integer> list_numbers_equal_values = new ArrayList<>();
        for (int i = 0; i < ja_equal_words_v.length(); i++) {
            if (ja_equal_words_v.get(i).toString().toLowerCase().equals(v)) {
                list_numbers_equal_values.add(i + 1);
            }
        }
        return list_numbers_equal_values;
    }

    void showFrame(ArrayList list_checked_indexes) {
        JFrame frame = new JFrame("ToolsUI");
        frame.setContentPane(new ToolsUI(list_checked_indexes).tools_panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

}