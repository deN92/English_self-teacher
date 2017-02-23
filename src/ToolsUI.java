import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
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
    private JTable table1;
    private JButton Btn_Add, Btn_Del, Btn_Save,
            Btn_Back, Btn_Next, Btn_Reset;
    private JScrollPane s_pane1;
    private JPanel tools_panel, panel_left, panel_right, panel_table;
    private JLabel Lbl_info;
    public JPanel panel_settings;
    public JPanel panel_design;
    private JScrollPane s_pane2;
    private JTable table2;
    private JButton Btn_Words_up;
    private JButton Btn_Words_down;

    private JSONArray ja_equal_words = new JSONArray();
    private JSONArray ja_equal_words2 = new JSONArray();
    private JSONArray t2_ja_equal_words = new JSONArray();
    private JSONArray t2_ja_equal_words2 = new JSONArray();
    private JSONArray ja_equal_error = new JSONArray();

    private int kn = 0;
    private int ks = 0;

    private StorageTables storageTables = new StorageTables();
//    private StorageTables storageTables2 = new StorageTables();

    private static JFrame frame;
    private DefaultTableModel model;
    private DefaultTableModel model2;
    private Service service;
    private Service service2;
    private Service.Language lang;
    int[] table_column_widths = {20,32,154,154,20,20};
    private Service.SetColor color1;

    private ArrayList<String> t1t2_list_eq = new ArrayList<>();

    private ArrayList<String> table1_list_delete_rows_names = new ArrayList<>();
    private ArrayList<String> table1_list_delete_rows_translate = new ArrayList<>();
    private ArrayList<Integer> table1_list_delete_rows_indexes = new ArrayList<>();
    private ArrayList<String> table2_list_delete_rows_names = new ArrayList<>();
    private ArrayList<String> table2_list_delete_rows_translate = new ArrayList<>();
    private ArrayList<Integer> table2_list_delete_rows_indexes = new ArrayList<>();
    boolean[] canEdit = {true, true, true, true, true, true};


    public JPanel vis(boolean tf){
        panel_settings.setVisible(tf);
        return panel_settings;
    }

    private void elements_name(){
        Btn_Add.setToolTipText(lang.SetLanguage("Btn_Add_name").toString());
        Btn_Del.setToolTipText(lang.SetLanguage("Btn_Del_name").toString());
        Btn_Save.setToolTipText(lang.SetLanguage("Btn_Save_name").toString());
        Btn_Back.setToolTipText(lang.SetLanguage("Btn_Back_step_name").toString());
        Btn_Next.setToolTipText(lang.SetLanguage("Btn_Next_step_name").toString());
        Btn_Reset.setToolTipText(lang.SetLanguage("Btn_Reset_name").toString());
    }

    private void color_elements(){
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI_Table_bg" ).val));
        panel_settings.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"ToolsUI_bg" ).val));
        panel_table.setBackground(panel_settings.getBackground());
        panel_left.setBackground(panel_settings.getBackground());
        panel_right.setBackground(panel_settings.getBackground());
    }

    public ToolsUI(){


        tools_panel.setSize(600,600);
        tools_panel.setMaximumSize(new Dimension(600,600));
        tools_panel.setMinimumSize(new Dimension(600,600));
        tools_panel.setPreferredSize(new Dimension(600,600));


        service = new Service();
        service2 = new Service();
        service.table(1,canEdit, "", "words_new");
        service2.table(1,canEdit, "","words_studied");
        model = service.model[1];
        model2 = service2.model[1];
        color_elements();
        table1.setModel(model);
        table2.setModel(model2);

        for(int i=0; i<6; i++){
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
        storageTables.val[0] = ob1.toString();
        storageTables.val2[0] = ob2.toString();

        Btn_Back.setEnabled(false);
        Btn_Next.setEnabled(false);
        lang = new Service.Language(service.current_path[1]);
        elements_name();

        Btn_Add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!table1.getValueAt(table1.getRowCount()-1, getCurrentColumnIndex(2)).equals("")){
                    DefaultTableModel model = (DefaultTableModel) table1.getModel();
                    model.addRow(new Object[]{false, table1.getRowCount()+1, "","","",""});
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
                        Boolean checked = Boolean.valueOf(table1.getValueAt(i,getCurrentColumnIndex(0)).toString());
                        int row = (int)table1.getValueAt(i, getCurrentColumnIndex(1)) - 1;
                        if(checked){
                            list_delete_rows_names.add(table1.getValueAt(row, getCurrentColumnIndex(2)).toString());
                            list_delete_rows_indexes.add(row);
                        }
                    }

                    for(int i=0; i<table2.getRowCount(); i++){
                        Boolean checked = Boolean.valueOf(table2.getValueAt(i,getCurrentColumnIndex(0)).toString());
                        int row = (int)table2.getValueAt(i, getCurrentColumnIndex(1)) - 1;
                        if(checked){
                            list_delete_rows_names2.add(table2.getValueAt(row, getCurrentColumnIndex(2)).toString());
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
                    save_to_storage();
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

                try {
                    for(int i=0;i< table2.getRowCount();i++){
                        String key = table2.getValueAt(i, getCurrentColumnIndex(2)).toString();
                        String val = table2.getValueAt(i, getCurrentColumnIndex(3)).toString();
                        ja_words_studied.put(i, new JSONObject().put(key, val));
                    }
                    for(int i=0;i< table1.getRowCount();i++){
                        String key = table1.getValueAt(i, getCurrentColumnIndex(2)).toString();
                        String val = table1.getValueAt(i, getCurrentColumnIndex(3)).toString();
                        ja_words_new.put(i, new JSONObject().put(key, val));
                    }

                    final_ja_words.put("words_studied", ja_words_studied);
                    final_ja_words.put("words_new", ja_words_new);
                    Writer out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(service.current_path[0]), "windows-1251"));
                    try {
                        out.write(final_ja_words.toString(2));
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
                for(int i=0;i<table1.getRowCount();i++) {
                    table1.setValueAt("", i, 4);
                    table1.setValueAt("", i, 5);
                }
                for(int i=0;i<table2.getRowCount();i++) {
                    table2.setValueAt("", i, 4);
                    table2.setValueAt("", i, 5);
                }


                add_warning_icon_to_col();
                save_to_storage();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                super.focusLost(focusEvent);

                add_warning_icon_to_col();

                save_to_storage();
            }
        });

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);

                add_warning_icon_to_col();
                message_warning_ew(table1, evt, ja_equal_words, 4);
                message_warning_ew(table1, evt, ja_equal_words2, 5);

                int row = table1.rowAtPoint(evt.getPoint());
                int col = table1.columnAtPoint(evt.getPoint());
                for (int i = 0; i < table1.getRowCount(); i++) {
                    if (row == i) {
                        String val = table1.getValueAt(i, col).toString();
                        table1.setToolTipText(val);
                    }
                }
            }
        });

        table2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                super.focusGained(focusEvent);
                for(int i=0;i<table1.getRowCount();i++) {
                    table1.setValueAt("", i, 4);
                    table1.setValueAt("", i, 5);
                }
                for(int i=0;i<table2.getRowCount();i++) {
                    table2.setValueAt("", i, 4);
                    table2.setValueAt("", i, 5);
                }
                add_warning_icon_to_col();
                save_to_storage();
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                super.focusLost(focusEvent);
                add_warning_icon_to_col();
                save_to_storage();
            }
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                add_warning_icon_to_col();
                message_warning_ew(table2, evt, t2_ja_equal_words, 4);
                message_warning_ew(table2, evt, t2_ja_equal_words2, 5);

                int row = table2.rowAtPoint(evt.getPoint());
                int col = table2.columnAtPoint(evt.getPoint());
                for (int i = 0; i < table2.getRowCount(); i++) {
                    if (row == i) {
                        String val = table2.getValueAt(i, col).toString();
                        table2.setToolTipText(val);
                    }
                }
            }
        });



        table1.setDragEnabled(true);
        table1.setDropMode(DropMode.USE_SELECTION);
        table1.setTransferHandler(new TransferHelper());
        table1.setRowSelectionAllowed(false);
        table1.setCellSelectionEnabled(true);

        Btn_Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                kn++;
                System.out.print(kn + " - " + ks);
                bt_up_down();
                if(ks>=0) {
                    bt_up_down2(Btn_Next);
                    if(ks == 0){
                        Btn_Back.setEnabled(false);
                    }
                }
            }
        });

        Btn_Next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                kn--;
                System.out.print(kn + " - " + ks);
                bt_up_down();
                if(kn>=0) {
                    bt_up_down2(Btn_Back);
                    if(kn == 0){
                        Btn_Next.setEnabled(false);
                    }
                }
            }
        });

        Btn_Reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                service.table(1,canEdit, "", "words_new");
                service2.table(1,canEdit, "", "words_studied");
                model = service.model[1];
                model2 = service2.model[1];
                table1.setModel(model);
                table2.setModel(model2);
                for(int i=0; i<6; i++){
                    table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
                    table2.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
                }
            }
        });
        Btn_Words_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                qwe1(table2, table1, table2_list_delete_rows_names, table2_list_delete_rows_translate, table2_list_delete_rows_indexes);
                table2_list_delete_rows_names.clear();
                table2_list_delete_rows_translate.clear();
                table2_list_delete_rows_indexes.clear();
            }
        });
        Btn_Words_down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                qwe1(table1, table2, table1_list_delete_rows_names, table1_list_delete_rows_translate, table1_list_delete_rows_indexes);
                table1_list_delete_rows_names.clear();
                table1_list_delete_rows_translate.clear();
                table1_list_delete_rows_indexes.clear();
            }

        });
    }

    private void bt_up_down(){
        for(int i=new StorageTables().val.length-1; i>0; i--){
            if(storageTables.val[i] != null){
                ks = i-kn;
                break;
            }
        }
        for(int i=new StorageTables().val2.length-1; i>0; i--){
            if(storageTables.val2[i] != null){
                ks = i-kn;
                break;
            }
        }
    }

    private void bt_up_down2(JButton btn){
        btn.setEnabled(true);
        if(storageTables.val[ks] != null) {
            service.table(2, canEdit, storageTables.val[ks], "words_new");
            DefaultTableModel model1 = service.model[2];
            table1.setModel(model1);
            table1.setSize(s_pane1.getSize().width, s_pane1.getSize().height);
        }
        if(storageTables.val2[ks] != null) {
            service2.table(2, canEdit, storageTables.val2[ks], "words_studied");
            DefaultTableModel model2 = service2.model[2];
            table2.setModel(model2);
            table2.setSize(s_pane1.getSize().width, s_pane1.getSize().height);
        }

        for (int i = 0; i < 6; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
            table2.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }
    }

    public void qwe1(JTable t1, JTable t2, ArrayList<String> al_name, ArrayList<String> al_translate, ArrayList<Integer> al_indexes){
        try {
            for (int i = 0; i < t1.getRowCount(); i++) {

                Boolean checked = Boolean.valueOf(t1.getValueAt(i, getCurrentColumnIndex(0)).toString());
                int row = (int) t1.getValueAt(i, getCurrentColumnIndex(1)) - 1;
                if (checked) {
                    al_name.add(t1.getValueAt(row, getCurrentColumnIndex(2)).toString());
                    al_translate.add(t1.getValueAt(row, getCurrentColumnIndex(3)).toString());
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
                model.addRow(new Object[]{false, t2.getRowCount() + 1, al_name.get(i), al_translate.get(i), "", ""});
            }
            t2.changeSelection(t2.getRowCount() - 1, 0, false, false);
            JOptionPane.showMessageDialog(
                    null,
                    al_name + " " + lang.SetLanguage("OPM_Words_repositioned"),
                    lang.SetLanguage("OPM_Title").toString(),
                    JOptionPane.INFORMATION_MESSAGE);
            save_to_storage();
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
        do {
            add_equal_el_to_ja(table2, t2_list_val_from_col2, t2_ja_equal_words, t2_n, set, im, 2,4);
            add_equal_el_to_ja(table2, t2_list_val_from_col3, t2_ja_equal_words2, t2_n, set, im, 3,5);
        }while(--t2_n>=0);

        int t1_n = table1.getRowCount()-1;
        do {
            add_equal_el_to_ja(table1, list_val_from_col2, ja_equal_words, t1_n, set, im, 2,4);
            add_equal_el_to_ja(table1, list_val_from_col3, ja_equal_words2, t1_n, set, im, 3,5);
        }while(--t1_n>=0);


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
                                   int n, TreeSet set, ImageIcon im, int tc, int tci){
        list_val_from_col.add(tab.getValueAt(n, getCurrentColumnIndex(tc)).toString());
        Object obj = tab.getValueAt(n, getCurrentColumnIndex(tc));
        if(!set.add(obj)){
            tab.setValueAt(im, n, getCurrentColumnIndex(tci));
            tab.setValueAt(im, tab.getRowCount()- 1-list_val_from_col.indexOf(obj), getCurrentColumnIndex(tci));
            ja_equal_error.put(2,2);
            ja.put(tab.getRowCount()- 1-list_val_from_col.indexOf(obj), obj);
            ja.put(n, obj);
        }
    }

    public void message_warning_ew(JTable tab, MouseEvent evt, JSONArray ja_ew, int col_v){
        int row = tab.rowAtPoint(evt.getPoint());
        int col = tab.columnAtPoint(evt.getPoint());

        for (int i = 0; i < ja_ew.length(); i++) {
            if (row == i && col == getCurrentColumnIndex(col_v)) {
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
                    if (row == i && col == getCurrentColumnIndex(col_v)) {
                        int numb = j+1;
                        JOptionPane.showMessageDialog(
                                null,
                                "'" + table1.getValueAt(i, 2).toString() + "'" +
                                        lang.SetLanguage("OPM_Already_exists") + "2 | № " + numb,
                                lang.SetLanguage("OPM_Title").toString(), JOptionPane.WARNING_MESSAGE
                        );
                    }
                    if (row == j && col == getCurrentColumnIndex(col_v)) {
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

    public int getCurrentColumnIndex(int index) {
        Object[] tc = (Object[]) lang.SetLanguage("TC_name");
        int current_col_index = 0;
        for(int i=0; i<6; i++) {
            if(tc[index].toString().toLowerCase().equals(table1.getColumnName(i).toLowerCase())) {
                current_col_index = i;
            }
        }
        return current_col_index;
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


    public void save_to_storage(){
        int t = 1;
        int t2 = 1;
        for(int i=1; i< new StorageTables().val.length; i++){
            if(storageTables.val[i] == null){
                t = i;
                break;
            }
        }
        for(int i=1; i< new StorageTables().val2.length; i++){
            if(storageTables.val2[i] == null){
                t2 = i;
                break;
            }
        }

        JSONArray ob1 = new JSONArray();
        JSONArray ob2 = new JSONArray();
        for(int i=0; i<table1.getRowCount(); i++){
            ob1.put(i, new JSONObject().put(
                    table1.getValueAt(i, getCurrentColumnIndex(2)).toString(),
                    table1.getValueAt(i, getCurrentColumnIndex(3)).toString()));
        }

        for(int i=0; i<table2.getRowCount(); i++){
            ob2.put(i, new JSONObject().put(
                    table2.getValueAt(i, getCurrentColumnIndex(2)).toString(),
                    table2.getValueAt(i, getCurrentColumnIndex(3)).toString()));
        }
        if((!storageTables.val[t-1].equals(ob1.toString()))||
           ((storageTables.val[t-1].equals(ob1.toString()))&&(!storageTables.val2[t2-1].equals(ob2.toString())))){
            storageTables.val[t] = ob1.toString();
            Btn_Back.setEnabled(true);
        }
        if((!storageTables.val2[t2-1].equals(ob2.toString()))||
           ((storageTables.val2[t2-1].equals(ob2.toString()))&&(!storageTables.val[t-1].equals(ob1.toString())))){
            storageTables.val2[t2] = ob2.toString();
            Btn_Back.setEnabled(true);
        }
    }

    class StorageTables {
        String[] val = new String[25];
        String[] val2 = new String[25];
    }

    public class CellData {
        private final Object[] value = new Object[6];
        private final int col;
        private final JTable table;
        private final int row;

        public CellData(JTable source) {
            this.col = source.getSelectedColumn();
            this.row = source.getSelectedRow();

            for(int i=0; i<value.length;i++){
                this.value[i] = source.getValueAt(row, getCurrentColumnIndex(i));
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
            if(col != getCurrentColumnIndex(0)) {
                if (targetCol == col) {
                    if(col ==  getCurrentColumnIndex(1)) {
                        for(int z=0; z<value.length; z++){
                            if(z!=1){
                                export_content(targetRow, getCurrentColumnIndex(z), z);
                            }
                        }
                    }
                    else if(col ==  getCurrentColumnIndex(2)){
                        export_content(targetRow, getCurrentColumnIndex(2), 2);
                        export_content(targetRow, getCurrentColumnIndex(4), 4);
                    }
                    else if(col ==  getCurrentColumnIndex(3)){
                        export_content(targetRow, getCurrentColumnIndex(3),3);
                        export_content(targetRow, getCurrentColumnIndex(5), 5);
                    }

                    swapped = true;
                }
            }
            return swapped;
        }

        Object[] exportValue = new Object[6];
        private void export_content(int targetRow, int numb_col, int val){
            exportValue[val] = table.getValueAt(targetRow, numb_col);
            table.setValueAt(value[val], targetRow, numb_col);
            table.setValueAt(exportValue[val], row, numb_col);
            save_to_storage();
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
