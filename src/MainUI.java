import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainUI {
    private JPanel main_panel;
    private JPanel panel_test, panel_option_questions, panel_result;
    private JRadioButton RB_Option1, RB_Option2, RB_Option3, RB_Option4,
            RB_Count_questions, RB_Choice_from_List;
    private ButtonGroup group_test = new ButtonGroup();

    private JButton Btn_choice_answer, Btn_Next_question, Btn_Start_test;
    private JTable table1;
    private JLabel Lbl_Question_test, Lbl_Result, Lbl_true_answers, Lbl_false_answers;
    private JSpinner spinner1, spinner2, spinner3;
    private JScrollPane s_pane1;
    private JList list1, list2;
    private JLabel Lbl_title;
    private JLabel Lbl_sp2_to_sp3;
    private JComboBox comboBox1;
    private JButton button1;
    private JLabel Lbl_Order_question;
    private JButton Btn_Reset;
    private JPanel panel_test2;
    private JTable table2;
    private JButton Btn_answer;
    private JButton Btn_Reset2;
    private JScrollPane s_pane2;
    private JTextField field_search1;
    private JTextField field_search2;
    private JButton Search_Ok;
    private JButton Btn_Restart_test2;
    private JCheckBox JCB_Clear_true_answers;
    private JTextField field_search3;
    private JCheckBox CB_Scope_questions;
    private Service service;
    private ArrayList<String> list_questions;
    private ArrayList<String> list_answers;
    private DefaultListModel model_answer_true;
    private DefaultListModel model_answer_false;

    private int n;
    private int answer_true;
    private int answer_false;
    private int sum2;
    private int[] table2_column_widths = {25, 200, 200, 23, 200, 0, 0};

    JScrollPane s_pane_list1;
    JScrollPane s_pane_list2;
    private JCheckBox JCB_Answers_show_hide;
    private JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
    private int columnValue = -1;
    private int columnNewValue = -1;

    private MainUI() {
        service = new Service();
        start_current_test();

        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);
        Btn_Restart_test2.setEnabled(false);
        CB_Scope_questions.setEnabled(false);

//      Can edit column table1
        boolean[] canEdit = {true, false, false, false, false, false, false};

        service.table(0, canEdit, "words_new");
        elements_name();
        table1.setModel(service.model[0]);
        table1.setSize(s_pane1.getSize().width, s_pane1.getSize().height);

        int[] table_column_widths = {25, 35, 200, 200, 60, 0, 0};
        for (int i = 0; i < table_column_widths.length; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }

        table2.getTableHeader().setReorderingAllowed(false);

        table1.removeColumn(table1.getColumnModel().getColumn(5));
        table1.removeColumn(table1.getColumnModel().getColumn(5));

        RB_Count_questions.setSelected(true);
        RB_Option1.setSelected(true);

        spinner1.setModel(new SpinnerNumberModel(5, 1, service.ja_words.length(), 1));
        spinner2.setModel(new SpinnerNumberModel(1, 1, service.ja_words.length() - 1, 1));
        spinner3.setModel(new SpinnerNumberModel(2, 2, service.ja_words.length(), 1));

        select_parameter_enable(true, false, false);
        Service.Language lang = new Service.Language(service.current_path[1]);
        color_elements();
        comboBox1.setModel(new DefaultComboBoxModel((String[]) lang.SetLanguage("CB_Elements_name")));

        JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};

        // Columns setReorderingAllowed disable
        table1.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {}
            public void columnMoved(TableColumnModelEvent e) {
                if (columnValue == -1)
                    columnValue = e.getFromIndex();

                columnNewValue = e.getToIndex();
            }
            public void columnRemoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
        });

        table1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                int[] cols_not_ra = {service.getCCI(service.nc_word_en),service.getCCI(service.nc_translate_en)};
                for (int aCols_not_ra : cols_not_ra) {
                    if (columnValue != -1 && (columnValue != aCols_not_ra && columnNewValue != aCols_not_ra)){
                        table1.moveColumn(columnNewValue, columnValue);
                    }
                }
                columnValue = -1;
                columnNewValue = -1;
            }
        });

        RB_Count_questions.addActionListener(actionEvent -> {
            select_parameter_enable(true, false, false);
            CB_Scope_questions.setEnabled(false);
            CB_Scope_questions.setSelected(false);
            spinner2.setValue(1);
            spinner3.setValue(2);
            for (int i = 0; i < table1.getRowCount(); i++) {
                table1.setValueAt(false, i, 0);
            }
        });

        CB_Scope_questions.addActionListener(actionEvent -> {
            if (CB_Scope_questions.isSelected()) {
                select_parameter_enable(false, true, false);
            } else select_parameter_enable(false, false, true);
            spinner_choice_item_table();
        });

        RB_Choice_from_List.addActionListener(actionEvent -> {
            table1.setEnabled(true);
            CB_Scope_questions.setEnabled(true);
        });

        spinner2.addChangeListener(changeEvent -> {
            int jS2 = (int) spinner2.getValue();
            int jS3 = (int) spinner3.getValue();
            if (jS2 == jS3) {
                spinner3.setValue(jS2 + 1);
            }
            spinner_choice_item_table();
        });

        spinner3.addChangeListener(changeEvent -> {
            int jS2 = (int) spinner2.getValue();
            int jS3 = (int) spinner3.getValue();
            if (jS2 == jS3) {
                spinner2.setValue(jS3 - 1);
            }
            spinner_choice_item_table();
        });

        Btn_Start_test.addActionListener(actionEvent -> {
            long seed = System.nanoTime();
            String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
//          Check count random questions
            if (RB_Count_questions.getModel().isSelected()) {

                int qc = (int) spinner1.getValue();
                list_questions.clear();
                if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)){

                    Collections.shuffle(service.list_questions_all, new Random(seed));
                    for (int i = 0; i < qc; i++) {
                        list_questions.add(i, service.list_questions_all.get(i));
                    }
                }
                else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){
                    Collections.shuffle(service.list_answers_all, new Random(seed));
                    for (int i = 0; i < qc; i++) {
                        list_questions.add(i, service.list_answers_all.get(i));
                    }
                }

            }

//          Check scope number questions from table
            if (CB_Scope_questions.getModel().isSelected()) {
                list_questions.clear();
                Integer iS = (Integer) spinner2.getValue();
                Integer iL = (Integer) spinner3.getValue();
                for (int i = 0; i < iL - iS + 1; i++) {
                    if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                        list_questions.add(service.jo_number_pair[iS + i - 1].names().getString(0));
                    }
                    else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){
                        String key = service.jo_number_pair[iS + i - 1].names().getString(0);
                        list_questions.add(service.jo_number_pair[iS + i - 1].getString(key));
                    }
                }

                Collections.shuffle(list_questions, new Random(seed));
            }

//          Check questions from table
            if (RB_Choice_from_List.getModel().isSelected()) {
                list_questions.clear();
                for (int i = 0; i < service.ja_words.length(); i++) {
                    Boolean checked = Boolean.valueOf(table1.getValueAt(i, 0).toString());
                    int col = (int) table1.getValueAt(i, 1) - 1;
                    if (checked) {
                        if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                            list_questions.add(service.jo_number_pair[col].names().getString(0));
                        }
                        else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){
                            String key = service.jo_number_pair[col].names().getString(0);
                            list_questions.add(service.jo_number_pair[col].getString(key));
                        }
                    }
                }
                Collections.shuffle(list_questions, new Random(seed));
            }

//          Order questions
            if (comboBox1.getSelectedIndex() == 0) {
                panel_option_questions.setVisible(false);
                panel_test.setVisible(false);
                panel_test2.setVisible(true);
                panel_test2.getTopLevelAncestor().setSize(720, 420);

                DefaultTableModel dtm = new DefaultTableModel() {
                    String[] employee = (String[]) lang.SetLanguage("TC_name2");
                    boolean[] canEdit2 = {false, false, true, false, false};

                    @Override
                    public int getColumnCount() {
                        return 5;
                    }

                    @Override
                    public int getRowCount() {
                        return list_questions.size();
                    }

                    @Override
                    public String getColumnName(int index) {
                        return employee[index];
                    }

                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return canEdit2[columnIndex];
                    }

                    @Override
                    public Class<?> getColumnClass(int column) {
                        switch (column) {
                            case 0:
                                return Integer.class;
                            case 1:
                                return String.class;
                            case 2:
                                return String.class;
                            case 3:
                                return ImageIcon.class;
                            case 4:
                                return String.class;
                            default:
                                return String.class;
                        }
                    }
                };

                table2.setModel(dtm);

                for (int i = 0; i < list_questions.size(); i++) {
                    dtm.setValueAt(i + 1, i, 0);
                    dtm.setValueAt(list_questions.get(i), i, 1);
                    dtm.setValueAt("", i, 2);
                }

                table2.removeColumn(table2.getColumnModel().getColumn(4));
                table2.removeColumn(table2.getColumnModel().getColumn(3));

                table2.setSize(s_pane2.getSize().width, s_pane2.getSize().height);

                for (int i = 0; i < 3; i++) {
                    table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
                }
            } else {
                panel_option_questions.setVisible(false);
                panel_test.setVisible(true);
                panel_result.setVisible(false);
                Btn_choice_answer.setEnabled(true);
                Btn_Next_question.setEnabled(false);
                Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());

//              JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
                JCB_Answers_show_hide.setSelected(true);
                answers_show_hide();

                panel_test.getTopLevelAncestor().setSize(720, 300);

                if (list_questions.size() == table1.getRowCount()) {
                    list_questions.remove(table1.getRowCount() - 1);
                }
                creating_query(list_questions.get(n));
            }

//          Check list_questions is not empty
            if (list_questions.size() == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Text_question_null").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        Btn_choice_answer.addActionListener((ActionEvent actionEvent) -> {
//            JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
            String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
//          Find true answer to the question
            String answer = "";
            String question;

            for (int i = 0; i < service.ja_words.length(); i++) {
                question = service.jo_number_pair[i].names().getString(0);

                if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)){
                    if (question.equals(Lbl_Question_test.getText())) {
                        answer = service.jo_number_pair[i].getString(question);
                    }
                }
                else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){

                    if (service.jo_number_pair[i].getString(question).equals(Lbl_Question_test.getText())) {
                        answer = service.jo_number_pair[i].names().getString(0);
                    }
                }
            }

//          Check true/false answer
            for (int i = 0; i < 4; i++) {
                group_test.add(rb[i]);
                if (group_test.isSelected(rb[i].getModel())) {
                    if (rb[i].getText().equals(answer)) {
                        Lbl_Result.setForeground(Color.decode("#009926"));
                        answer_true++;
                        Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + ": " + answer_true + "/" + answer_false);
                        model_answer_true.addElement(Lbl_Question_test.getText() + " - " + answer);
                    } else {
                        Lbl_Result.setForeground(Color.RED);
                        rb[i].setForeground(Color.RED);
                        answer_false++;
                        Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + ": " + answer_true + "/" + answer_false);
                        model_answer_false.addElement(Lbl_Question_test.getText() + " - " + answer);
                    }
                }
                if (rb[i].getText().equals(answer)) {
                    rb[i].setForeground(Color.decode("#009926"));
                }
            }
            Btn_choice_answer.setEnabled(false);
            Btn_Next_question.setEnabled(true);

//          +1 completed question
            sum2++;

//          check list_questions size == current number question from list
            if (list_questions.size() == sum2) {
//              next_question rename to result
                Btn_Next_question.setText(lang.SetLanguage("Lbl_Result_name").toString());
            }
        });

        Btn_Next_question.addActionListener(actionEvent -> {
            if (Btn_Next_question.getText().equals(lang.SetLanguage("Btn_Next_question_name"))) {
                n++;
                Btn_choice_answer.setEnabled(true);
                Lbl_Result.setText("");
                answers_show_hide();
                creating_query(list_questions.get(n));
            }
            if (Btn_Next_question.getText().equals(lang.SetLanguage("Lbl_Result_name"))) {
                panel_test.setVisible(false);
                panel_option_questions.setVisible(false);
                panel_result.setVisible(true);
                Lbl_true_answers.setText("true: " + answer_true + "");
                Lbl_true_answers.setForeground(Color.decode("#009926"));
                Lbl_false_answers.setText("false: " + answer_false + "");
                Lbl_false_answers.setForeground(Color.red);
                list1.setModel(model_answer_true);
                list2.setModel(model_answer_false);
                panel_result.getTopLevelAncestor().setSize(720, 360);
            }
        });

        button1.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
        });

        Btn_Reset.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
        });

        ArrayList<String> ast = new ArrayList<>();

        Btn_answer.addActionListener(actionEvent -> {

            Btn_Restart_test2.setEnabled(true);

            if (table2.getColumnCount() == 3) {
                table2.addColumn(new TableColumn(3));
                table2.addColumn(new TableColumn(4));
            }
            for (int i = 0; i < 5; i++) {
                table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
            }

            URL url1 = ToolsUI.class.getResource("/icons/ic_answ_true_20x20.png");
            URL url2 = ToolsUI.class.getResource("/icons/ic_answ_false_20x20.png");
            ImageIcon ii_true = new ImageIcon(url1);
            ImageIcon ii_false = new ImageIcon(url2);

            for (int j = 0; j < list_questions.size(); j++) {
                for (int i = 0; i < table1.getRowCount(); i++) {
                    if (table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString().toLowerCase().
                            equals(list_questions.get(j).toLowerCase())) {
                        ast.add(table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString());
                    }
                }

                String value_table = table2.getValueAt(j, 2).toString().toLowerCase();
                String value_current = ast.get(j).toLowerCase();


                if ((!value_table.equals("")) && (value_table.contains(value_current) || value_current.contains(value_table))) {
                    table2.setValueAt(ii_true, j, service.getCCI(service.nc_translate_en));
                } else table2.setValueAt(ii_false, j, service.getCCI(service.nc_translate_en));

                table2.setValueAt(value_current, j, service.getCCI("Type"));
            }
        });

        ArrayList<String> str_del_true = new ArrayList<>();

        ArrayList<Integer> str_del_true_index = new ArrayList<>();
        Btn_Restart_test2.addActionListener(actionEvent -> {
            ast.clear();
            Btn_answer.setEnabled(true);
            Btn_Restart_test2.setEnabled(false);
            boolean tf = false;
            for (int i = 0; i < table2.getRowCount(); i++) {
                if (table2.getValueAt(i, 3).toString().toLowerCase().contains("ic_answ_true_20x20.png")) {
                    tf = true;
                }
            }

            if (JCB_Clear_true_answers.isSelected() && tf) {
                str_del_true.clear();
                str_del_true_index.clear();
                for (int i = 0; i < table2.getRowCount(); i++) {
                    if (table2.getValueAt(i, 3).toString().toLowerCase().contains("ic_answ_true_20x20.png")) {
                        str_del_true.add(table2.getValueAt(i, 1).toString());
                    }
                }

                for (int i = 0; i < list_questions.size(); i++) {
                    for (String aStr_del_true : str_del_true) {
                        if (list_questions.get(i).equals(aStr_del_true.toLowerCase())) {
                            str_del_true_index.add(i);
                        }
                    }
                }

                int ldr_st = 0;

                for (Integer aStr_del_true_index : str_del_true_index) {
                    int ldr_value = aStr_del_true_index - ldr_st;
                    list_questions.remove(ldr_value);
                    ldr_st++;
                }
            }

            long seed = System.nanoTime();

            Collections.shuffle(list_questions, new Random(seed));

            DefaultTableModel dtm = new DefaultTableModel() {
                String[] employee = (String[]) lang.SetLanguage("TC_name2");
                boolean[] canEdit2 = {false, false, true, false, false};

                @Override
                public int getColumnCount() {
                    return 5;
                }

                @Override
                public int getRowCount() {
                    return list_questions.size();
                }

                @Override
                public String getColumnName(int index) {
                    return employee[index];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit2[columnIndex];
                }

                @Override
                public Class<?> getColumnClass(int column) {
                    switch (column) {
                        case 0:
                            return Integer.class;
                        case 1:
                            return String.class;
                        case 2:
                            return String.class;
                        case 3:
                            return ImageIcon.class;
                        case 4:
                            return String.class;
                        default:
                            return String.class;
                    }
                }
            };

            table2.setModel(dtm);

            for (int j = 0; j < list_questions.size(); j++) {
                for (int i = 0; i < table1.getRowCount(); i++) {
                    if (table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString().toLowerCase().
                            equals(list_questions.get(j).toLowerCase())) {
                        ast.add(table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString());
                    }
                }
                String value_current = ast.get(j).toLowerCase();

                table2.setValueAt(value_current, j, service.getCCI("Type"));
            }

            for (int i = 0; i < list_questions.size(); i++) {
                dtm.setValueAt(i + 1, i, service.getCCI("✓"));
                dtm.setValueAt(list_questions.get(i), i, service.getCCI("№"));
                dtm.setValueAt("", i, service.getCCI(service.nc_word_en));
            }
            table2.removeColumn(table2.getColumnModel().getColumn(3));
            table2.removeColumn(table2.getColumnModel().getColumn(3));

            table2.setSize(s_pane2.getSize().width, s_pane2.getSize().height);

            for (int i = 0; i < 3; i++) {
                table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
            }
        });

        Btn_Reset2.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
            list_questions.clear();
            ast.clear();
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
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

        Search_Ok.addActionListener(actionEvent -> service.wt_search(table1, field_search1, field_search2, field_search3));
        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                for (int i = 0; i < 4; i++) {
                    rb[i].setForeground(Color.BLACK);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                answers_show_hide();
            }
        };
        RB_Option1.addMouseListener(listener);
        RB_Option2.addMouseListener(listener);
        RB_Option3.addMouseListener(listener);
        RB_Option4.addMouseListener(listener);
        JCB_Answers_show_hide.addActionListener(actionEvent -> answers_show_hide());
    }

    private void answers_show_hide(){
        if(JCB_Answers_show_hide.isSelected()) {
            for (int i = 0; i < 4; i++) {
                rb[i].setForeground(panel_test.getBackground());
            }
        }
        else{
            for (int i = 0; i < 4; i++) {
                rb[i].setForeground(Color.BLACK);
            }
        }
    }

    private void elements_name(){
        Service.Language lang = new Service.Language(service.current_path[1]);
        Lbl_title.setText(lang.SetLanguage("Lbl_Title_name").toString());
        RB_Count_questions.setText(lang.SetLanguage("RB_Count_questions_name").toString());
        RB_Choice_from_List.setText(lang.SetLanguage("RB_Choice_from_List_name").toString());
        CB_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
        Lbl_sp2_to_sp3.setText(lang.SetLanguage("Lbl_sp2_to_sp3_name").toString());
        Lbl_Order_question.setText(lang.SetLanguage("Lbl_Order_question_name").toString());
        Btn_choice_answer.setText(lang.SetLanguage("Btn_Ok_name").toString());
        Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());
        Btn_Start_test.setText(lang.SetLanguage("Btn_Start_test_name").toString());
        Lbl_true_answers.setText(lang.SetLanguage("Lbl_True_answers_name").toString());
        Lbl_false_answers.setText(lang.SetLanguage("Lbl_False_answers_name").toString());
        button1.setText(lang.SetLanguage("Btn_Reset_start_test_name").toString());

        Btn_Reset.setText(lang.SetLanguage("Btn_Cancel_test").toString());
        Btn_Reset2.setText(lang.SetLanguage("Btn_Cancel_test").toString());
        Btn_Restart_test2.setText(lang.SetLanguage("Btn_Reset_test").toString());
        JCB_Clear_true_answers.setText(lang.SetLanguage("JCB_Clear_true_answers").toString());
    }

    private void color_elements(){
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"MainUI_Table_bg" ).val));
        panel_option_questions.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"MainUI_bg").val));
        panel_test.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"TestUI_bg" ).val));
        RB_Count_questions.setBackground(panel_option_questions.getBackground());
        RB_Choice_from_List.setBackground(panel_option_questions.getBackground());
        CB_Scope_questions.setBackground(panel_option_questions.getBackground());
        Lbl_sp2_to_sp3.setBackground(panel_option_questions.getBackground());

        JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
        for (JRadioButton aRb : rb) {
            aRb.setBackground(panel_test.getBackground());
        }
    }

    private void start_current_test(){
        list_questions = new ArrayList<>();
        list_answers = new ArrayList<>();
        model_answer_true = new DefaultListModel();
        model_answer_false = new DefaultListModel();
        n = 0;
        answer_true = 0;
        answer_false = 0;
        sum2 = 0;
        Lbl_Result.setText("");
    }

    private void spinner_choice_item_table(){
        if(CB_Scope_questions.isSelected()) {
            for (int i = 0; i < table1.getRowCount(); i++) {
                table1.setValueAt(false, i, service.getCCI("✓"));
            }
            for (int i = (int) spinner2.getValue() - 1; i < (int) spinner3.getValue(); i++) {
                if (table1.getRowCount() > (int) spinner3.getValue() - 1) {
                    table1.setValueAt(true, i, service.getCCI("✓"));
                }
            }
        }
        else{
            for (int i = (int) spinner2.getValue() - 1; i < (int) spinner3.getValue(); i++){
                if(table1.getRowCount()>0) {
                    table1.setValueAt(false, i, service.getCCI("✓"));
                }
            }
        }
    }

    private void reset_start_test(){
        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);
        panel_option_questions.setVisible(true);
        panel_option_questions.getTopLevelAncestor().setSize(720,500);
    }

    private void select_parameter_enable(boolean spinner1_enable, boolean spinner23_lbl_enable, boolean table1_enable){
        spinner1.setEnabled(spinner1_enable);
        spinner2.setEnabled(spinner23_lbl_enable);
        Lbl_sp2_to_sp3.setEnabled(spinner23_lbl_enable);
        spinner3.setEnabled(spinner23_lbl_enable);
        table1.setEnabled(table1_enable);
    }

    private void creating_query(String n) {
        String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
        if (list_questions.size() < service.ja_words.length()) {

            if (Btn_choice_answer.getModel().isEnabled()) {
                Btn_Next_question.setEnabled(false);
            }
            JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
            for (int i = 0; i < 4; i++) {
                group_test.add(rb[i]);
            }

//          select default answer
//          group_test.setSelected(jRB_Count_questions.getModel(), true);
            JRadioButton[] jRB_Options = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
            long seed = System.nanoTime();
            Lbl_Question_test.setText(n);
            list_answers.clear();

            Collections.shuffle(service.list_questions_all, new Random(seed));
            Collections.shuffle(service.list_answers_all, new Random(seed));

//          addition 3 false_answer into the list
            for (int i = 0; i < service.ja_words.length(); i++) {
                String question = service.jo_number_pair[i].names().getString(0);
                String answer = service.jo_number_pair[i].getString(question);
                if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                    if (question.equals(n)) {
                        list_answers.add(answer);
                    }
                }
                else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){
                    if (answer.equals(n)) {
                        list_answers.add(question);
                    }
                }
            }
            for (int i = 0; i < service.ja_words.length(); i++) {
                if(current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                    if (!(list_answers.get(0).equals(service.list_answers_all.get(i)))) {
                        list_answers.add(service.list_answers_all.get(i));
                    }
                    if (list_answers.size() == 4) {
                        break;
                    }
                }
                else if(current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)){
                    if (!(list_answers.get(0).equals(service.list_questions_all.get(i)))) {
                        list_answers.add(service.list_questions_all.get(i));
                    }
                    if (list_answers.size() == 4) {
                        break;
                    }
                }
            }
//          random elements in list
            Collections.shuffle(list_answers, new Random(seed));

//          copying elements from list to radiobuttons
            for (int i = 0; i < 4; i++) {
                jRB_Options[i].setText(list_answers.get(i));
            }
        }
    }

    class MenuUI extends JFrame {
        Service.Language lang = new Service.Language(service.current_path[1]);
        JMenuBar menuBar;
        JMenu m_settings,m_about, m_lang;
        JMenuItem mi_edit, mi_design;
        JMenuItem menuItem1,menuItem2;
        JMenuItem mi_description;

        private MenuUI(){
            menuBar = new JMenuBar();
            // Create Menu objects to add to the MenuBar
            m_settings = new JMenu(lang.SetLanguage("M_Settings_name").toString());
            m_about = new JMenu(lang.SetLanguage("M_About_name").toString());
            mi_edit = new JMenuItem(lang.SetLanguage("MI_Edit_name").toString());
            m_lang = new JMenu(lang.SetLanguage("M_Lang_name").toString());
            mi_design = new JMenuItem(lang.SetLanguage("MI_Design_name").toString());

            menuItem1 = new JMenuItem("English");
            menuItem2 = new JMenuItem("Українська");

            mi_description = new JMenuItem(lang.SetLanguage("MI_Description").toString());

            mi_edit.addActionListener(actionEvent -> {
                ToolsUI toolsUI = new ToolsUI();
                toolsUI.showFrame();
            });

            mi_design.addActionListener(actionEvent -> {
                DesignUI desingUI = new DesignUI();
                desingUI.showFrame();
            });

            menuItem1.addActionListener(actionEvent -> {
                lang_turn_on("en");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Restart_program").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            });

            menuItem2.addActionListener(actionEvent -> {
                lang_turn_on("ua");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Restart_program").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
            });

            mi_description.addActionListener(actionEvent -> {
                DescriptionUI descUI = new DescriptionUI();
                descUI.showFrame();
            });
        }

         private void lang_turn_on(String lang){
            String content_file = service.content_file(service.current_path[1]);
            JSONArray ja = new JSONArray(content_file);
            JSONArray ja_all = new JSONArray();
            JSONObject jo = ja.getJSONObject(1);
            JSONObject jo2 = new JSONObject();
            jo2.put("lang", lang);

            ja_all.put(jo2);
            ja_all.put(jo);

            try {
                service.write_content_in_file(service.current_path[1], ja_all);
            } catch (IOException e) {
                e.printStackTrace();
            }
         }

         private void showMenu(JFrame frame) {
             m_settings.add(mi_edit);
             m_settings.add(mi_design);
             menuBar.add(m_settings);
             m_lang.add(menuItem1);
             m_lang.add(menuItem2);
             m_settings.add(m_lang);
             m_about.add(mi_description);
             menuBar.add(m_settings);
             menuBar.add(m_about);
             frame.setJMenuBar(menuBar);
         }
     }

    public static void main(String[] args) {
        MainUI window = new MainUI();
        window.showFrame();
    }

    private void showFrame() {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().main_panel);
        frame.setPreferredSize(new Dimension(720,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        new MenuUI().showMenu(frame);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
