import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainUI {

    private JPanel main_panel;
    private JPanel panel_test, panel_option_questions, panel_result;
    private JPanel panel_test2;

    private JTable table1;
    private JTable table2;
    private JTable t1ort2 = null;

    private JLabel Lbl_Question_test, Lbl_Result, Lbl_true_answers, Lbl_false_answers;
    private JLabel Lbl_title;
    private JLabel Lbl_sp2_to_sp3;
    private JLabel Lbl_Type_test;

    private JRadioButton RB_Option1, RB_Option2, RB_Count_questions, RB_Choice_from_List;
    private JRadioButton[] rbs_options = {RB_Option1, RB_Option2};
    private ButtonGroup group_test = new ButtonGroup();

    private JSpinner spinner1, spinner2, spinner3;
    private JScrollPane s_pane1;
    private JScrollPane s_pane2;

    private JComboBox comboBox1;

    private JTextField field_search1;
    private JTextField field_search2;

    private JButton Btn_choice_answer, Btn_Next_question, Btn_Start_test;
    private JButton Btn_Cancel_test_variants;
    private JButton Btn_Cancel_test;
    private JButton Btn_answer_for_written_test;
    private JButton Btn_Cancel_written_test;
    private JButton Search_Ok;
    private JButton Btn_Restart_written_test;
    private JButton Btn_Reload_table1;
    private JButton Btn_Restart_tests;
    private JButton Btn_go_to_written_test;
    private JButton Btn_go_to_spoken_test;
    private JButton Btn_Words_transfer_to_studied_words;
    private JButton Btn_Clear_answers;
    private JButton Btn_Words_right;
    private JButton Btn_Words_left;
    private JButton Btn_Additionally;
    private JButton Btn_All_answers_for_written_test;
    private JButton Btn_Restart_spoken_test;

    private JCheckBox CB_Scope_questions;
    private JCheckBox JCB_Answers_show_hide;
    private JCheckBox CB_ta_all_checking;
    private JCheckBox CB_fa_all_checking;

    private JScrollPane s_pane_list1, s_pane_list2;

    private JTable Tab_True_answers;
    private JTable Tab_False_answers;


    private JSlider Sld_Count_questions;
    private JSlider Sld_Checked_count_questions;
    private JButton Btn_info;
    private JButton Btn_Parts_of_speech;

    private ArrayList<String> list_questions;
    private ArrayList<String> list_answers;
    private JSONObject jo_list_answer_true;
    private JSONObject jo_list_answer_false;
    private JSONObject jo_list_answer_null;

    private ArrayList<Integer>[] table_list_delete_rows_indexes = new ArrayList[2];
    private ArrayList<String>[] table_list_delete_rows_names = new ArrayList[2];
    private ArrayList<String>[] table_list_delete_rows_translate = new ArrayList[2];

    private ArrayList<String> list_current_answers;
    private DefaultTableModel dtm_true_false_answers;

    private boolean Tab_True_answers_checked = false;
    private boolean Tab_False_answers_checked = false;
    private int number_question;
    private int[] table2_column_widths = {25, 200, 200, 23, 200, 0, 0, 0};
    private int columnValue = -1;
    private int columnNewValue = -1;

    private Service service;
    private Service.Language lang;

    private URL url1 = MainUI.class.getResource("/icons/ic_answ_true_20x20.png");
    private URL url2 = MainUI.class.getResource("/icons/ic_answ_false_20x20.png");
    private ImageIcon ii_true = new ImageIcon(url1);
    private ImageIcon ii_false = new ImageIcon(url2);

    private JCheckBox[] CB_WT1 = new JCheckBox[new Service().word_types.length];

    //      Can edit column table1
    private boolean[] canEdit = {true, false, false, false, false, false, false, false};
    private int[] table_column_widths = {25, 35, 200, 200, 60, 0, 0, 0, 0};

    private MainUI() {
        service = new Service();
        start_current_test();

        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);
        Btn_Additionally.setEnabled(false);
        CB_Scope_questions.setEnabled(false);

        for(int i=0; i< CB_WT1.length; i++){
            CB_WT1[i] = new JCheckBox();
        }

        final JPopupMenu menu_wt = new JPopupMenu();

        for (JCheckBox aCb_word_types1 : CB_WT1) {
            menu_wt.add(aCb_word_types1);
        }

        Btn_Parts_of_speech.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu_wt.show(Btn_Parts_of_speech, 0, Btn_Parts_of_speech.getHeight());
            }
        });

        load_data_table1(canEdit, table_column_widths);

        table1.setSize(s_pane1.getSize().width, s_pane1.getSize().height);
        elements_name();
        table2.getTableHeader().setReorderingAllowed(false);
        RB_Count_questions.setSelected(true);
        RB_Option1.setSelected(true);

        select_parameter_enable(true, false, false);
        lang = new Service.Language(service.current_path[1]);
        color_elements();
        comboBox1.setModel(new DefaultComboBoxModel((String[]) lang.SetLanguage("CB_Elements_name")));

        Btn_Words_transfer_to_studied_words.setEnabled(false);
        Btn_Clear_answers.setEnabled(false);
        Btn_Parts_of_speech.setEnabled(false);
        // Columns setReorderingAllowed disable
        table1.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            public void columnAdded(TableColumnModelEvent e) {}
            public void columnMarginChanged(ChangeEvent e) {}
            public void columnMoved(TableColumnModelEvent e) {
                if (columnValue == -1) columnValue = e.getFromIndex();
                    columnNewValue = e.getToIndex();
            }
            public void columnRemoved(TableColumnModelEvent e) {}
            public void columnSelectionChanged(ListSelectionEvent e) {}
        });

        table1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int[] cols_not_ra = {service.getCCI(service.nc_word_en), service.getCCI(service.nc_translate_en)};
                for (int aCols_not_ra : cols_not_ra) {
                    if (columnValue != -1 && (columnValue != aCols_not_ra && columnNewValue != aCols_not_ra)) {
                        table1.moveColumn(columnNewValue, columnValue);
                    }
                }
                columnValue = -1;
                columnNewValue = -1;
            }
        });

        slider(Sld_Checked_count_questions, table1.getRowCount());

        RB_Count_questions.addActionListener(actionEvent -> {
            select_parameter_enable(true, false, false);
            CB_Scope_questions.setEnabled(false);
            CB_Scope_questions.setSelected(false);
            Btn_Parts_of_speech.setEnabled(false);
            Sld_Checked_count_questions.setEnabled(true);
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
            Btn_Parts_of_speech.setEnabled(true);
            Sld_Checked_count_questions.setEnabled(false);
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
//            service.list_questions_all.
            long seed = System.nanoTime();
//            Service service = new Service();
            String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
//          Check count random questions
            if (RB_Count_questions.getModel().isSelected()) {

                int qc = (int) spinner1.getValue();
                list_questions.clear();
                if (current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {

                    Collections.shuffle(service.list_questions_all, new Random(seed));
                    for (int i = 0; i < qc; i++) {
                        list_questions.add(i, service.list_questions_all.get(i));
                    }
                } else if (current_column_word.equals(service.nc_translate_en) ||
                           current_column_word.equals(service.nc_translate_ua)) {
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
                    if (current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                        list_questions.add(service.jo_number_pair[iS + i - 1].names().getString(0));
                    } else if (current_column_word.equals(service.nc_translate_en) ||
                               current_column_word.equals(service.nc_translate_ua)) {
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
                        if (current_column_word.equals(service.nc_word_en) ||
                            current_column_word.equals(service.nc_word_ua)) {
                            list_questions.add(service.jo_number_pair[col].names().getString(0));
                        } else if (current_column_word.equals(service.nc_translate_en) ||
                                   current_column_word.equals(service.nc_translate_ua)) {
                            String key = service.jo_number_pair[col].names().getString(0);
                            list_questions.add(service.jo_number_pair[col].getString(key));
                        }
                    }
                }
                Collections.shuffle(list_questions, new Random(seed));
            }


//          Check list_questions is not empty
            if (list_questions.size() != 0) {
//          Order questions
                if (comboBox1.getSelectedIndex() == 0) {
                    written_test();
                } else {
                    panel_option_questions.setVisible(false);
                    panel_test.setVisible(true);
                    panel_result.setVisible(false);
                    Btn_choice_answer.setEnabled(true);
                    Btn_Next_question.setEnabled(false);
                    Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());
                    JCB_Answers_show_hide.setSelected(true);
                    answers_show_hide();

                    panel_test.getTopLevelAncestor().setSize(720, 340);

                    if (list_questions.size() == table1.getRowCount()) {
                        list_questions.remove(table1.getRowCount() - 1);
                    }

                    slider(Sld_Count_questions, list_questions.size());
                    creating_query(list_questions.get(number_question));
                }
            } else
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Text_question_null").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
        });

        Btn_choice_answer.addActionListener((ActionEvent actionEvent) -> {
            String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
//          Find true answer to the question
            String answer = "";
            String question;

            for (int i = 0; i < service.ja_words.length(); i++) {
                question = service.jo_number_pair[i].names().getString(0);

                if (current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                    if (question.equals(Lbl_Question_test.getText())) {
                        answer = service.jo_number_pair[i].getString(question);
                    }
                } else if (current_column_word.equals(service.nc_translate_en) ||
                           current_column_word.equals(service.nc_translate_ua)) {

                    if (service.jo_number_pair[i].getString(question).equals(Lbl_Question_test.getText())) {
                        answer = service.jo_number_pair[i].names().getString(0);
                    }
                }
            }

            Lbl_Result.setVisible(true);

//          Check true/false answer
            for (int i = 0; i < 2; i++) {
                group_test.add(rbs_options[i]);
                if (group_test.isSelected(rbs_options[i].getModel())) {
                    if (rbs_options[i].getText().equals(answer)) {
                        Lbl_Result.setForeground(Color.decode("#009926"));
                        if (jo_list_answer_true.length() > 0) {
                            if (!jo_list_answer_true.names().get(jo_list_answer_true.length() - 1).
                                    equals(Lbl_Question_test.getText())) {
                                jo_list_answer_true.put(Lbl_Question_test.getText(), rbs_options[0].getText());
                            }
                        } else {
                            jo_list_answer_true.put(Lbl_Question_test.getText(), rbs_options[0].getText());
                        }
                        if (jo_list_answer_true.length() > 0 && jo_list_answer_false.length() > 0) {
                            for (int j = 0; j < jo_list_answer_false.length(); j++) {
                                if (jo_list_answer_false.names().get(j).equals(Lbl_Question_test.getText())) {
                                    jo_list_answer_false.remove(Lbl_Question_test.getText());
                                }
                            }
                        }
                        Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + " (TRUE) : " +
                                jo_list_answer_true.length() + "/" + jo_list_answer_false.length());
                    } else {
                        Lbl_Result.setForeground(Color.RED);
                        rbs_options[i].setForeground(Color.RED);
                        if (jo_list_answer_false.length() > 0) {
                            if (!jo_list_answer_false.names().get(jo_list_answer_false.length() - 1).
                                    equals(Lbl_Question_test.getText())) {
                                jo_list_answer_false.put(Lbl_Question_test.getText(), rbs_options[0].getText());
                            }
                        } else {
                            jo_list_answer_false.put(Lbl_Question_test.getText(), rbs_options[0].getText());
                        }
                        if (jo_list_answer_true.length() > 0 && jo_list_answer_false.length() > 0) {
                            for (int j = 0; j < jo_list_answer_true.length(); j++) {
                                if (jo_list_answer_true.names().get(j).equals(Lbl_Question_test.getText())) {
                                    jo_list_answer_true.remove(Lbl_Question_test.getText());
                                }
                            }
                        }
                        Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + " (FALSE) : " +
                                jo_list_answer_true.length() + "/" + jo_list_answer_false.length());
                    }
                }
                if (rbs_options[i].getText().equals(answer)) {
                    rbs_options[i].setForeground(Color.decode("#009926"));
                }
            }
//          Btn_choice_answer.setEnabled(false);
            Btn_Next_question.setEnabled(true);

//          +1 completed question

            if (list_questions.size() == number_question + 1) {
//              next_question rename to result
                Btn_Next_question.setText(lang.SetLanguage("Lbl_Result_name").toString());
                Btn_go_to_written_test.setEnabled(false);
            }
        });

        Btn_Next_question.addActionListener(actionEvent -> {
            if (Btn_Next_question.getText().equals(lang.SetLanguage("Btn_Next_question_name"))) {
                number_question++;
                Btn_choice_answer.setEnabled(true);
                Lbl_Result.setText("");
                answers_show_hide();
                Sld_Count_questions.setValue(number_question);
                if (list_questions.size() == number_question) {
                    number_question = 0;
                }

                creating_query(list_questions.get(number_question));
                slider(Sld_Count_questions, list_questions.size());
            }

            if (Btn_Next_question.getText().equals(lang.SetLanguage("Lbl_Result_name"))) {
                panel_test.setVisible(false);
                panel_option_questions.setVisible(false);
                panel_result.setVisible(true);
                detail_result();
            }
        });

        Btn_answer_for_written_test.addActionListener(actionEvent -> {
            Btn_go_to_spoken_test.setEnabled(false);
            for (int i = 0; i < 6; i++) {
                table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
            }

            jo_list_answer_true = new JSONObject();
            jo_list_answer_false = new JSONObject();
            jo_list_answer_null = new JSONObject();


            JSONObject jo_words_examples_new_studied = jo_words_examples_ns();

            for (int j = 0; j < list_questions.size(); j++) {
                for (int i = 0; i < table1.getRowCount(); i++) {
                    if (table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString().toLowerCase().
                            equals(list_questions.get(j).toLowerCase())) {
                        list_current_answers.add(table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString());
                    }
                }

                String question = table2.getValueAt(j, 1).toString().toLowerCase();
                String answer = table2.getValueAt(j, 2).toString().toLowerCase();
                String value_current = list_current_answers.get(j).toLowerCase();


                if (!answer.equals("")) {
                    if (answer.contains(value_current) || value_current.contains(answer)) {
                        table2.setValueAt(ii_true, j, 3);
                        jo_list_answer_true.put(question, value_current);
                    } else if (!answer.contains(value_current) && !value_current.contains(answer)) {
                        table2.setValueAt(ii_false, j, 3);
                        jo_list_answer_false.put(question, value_current);
                    }
                    table2.setValueAt(value_current, j, 4);

                    icon_for_examples_words_table2(j,jo_words_examples_new_studied);

                } else {
                    jo_list_answer_null.put(question, value_current);
                }
            }
            for (int i = 0; i < table2.getRowCount(); i++) {
                if (table2.getValueAt(i, 4).toString().equals("")) {
                    Btn_go_to_spoken_test.setEnabled(true);
                }
            }
        });

        Btn_All_answers_for_written_test.addActionListener(e -> {
            list_current_answers = new ArrayList<>();
            jo_list_answer_false = new JSONObject();
            jo_list_answer_true = new JSONObject();
            Btn_Additionally.setEnabled(true);
            JSONObject jo_words_examples_new_studied = jo_words_examples_ns();

            if (table2.getColumnCount() == 3) {
                table2.addColumn(new TableColumn(3));
                table2.addColumn(new TableColumn(4));
            }
            for (int i = 0; i < 6; i++) {
                table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
            }

            for (int j = 0; j < list_questions.size(); j++) {
                for (int i = 0; i < table1.getRowCount(); i++) {
                    if (table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString().toLowerCase().
                            equals(list_questions.get(j).toLowerCase())) {
                        list_current_answers.add(table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString());
                    }
                }

                String question = table2.getValueAt(j, 1).toString().toLowerCase();
                String answer = table2.getValueAt(j, 2).toString().toLowerCase();
                String value_current = list_current_answers.get(j).toLowerCase();

                icon_for_examples_words_table2(j,jo_words_examples_new_studied);

                if ((!answer.equals("")) && (answer.contains(value_current) || value_current.contains(answer))) {
                    table2.setValueAt(ii_true, j, 3);
                    jo_list_answer_true.put(question, value_current);
                } else {
                    table2.setValueAt(ii_false, j, 3);
                    jo_list_answer_false.put(question, value_current);
                }

                table2.setValueAt(value_current, j, service.getCCI("Type"));
            }
            Btn_go_to_spoken_test.setEnabled(false);
        });


        ArrayList<String> str_del_true = new ArrayList<>();
        ArrayList<Integer> str_del_true_index = new ArrayList<>();

        Btn_Restart_written_test.addActionListener(actionEvent -> {
            Btn_answer_for_written_test.setEnabled(true);
            Btn_answer_for_written_test.setEnabled(true);
            Btn_Additionally.setEnabled(false);
            boolean tf = false;
            for (int i = 0; i < table2.getRowCount(); i++) {
                try {
                    if (table2.getValueAt(i, 3).toString().toLowerCase().contains("ic_answ_true_20x20.png")) {
                        tf = true;
                    }
                } catch (Exception ignored) {}
            }

            if (tf) {
                int jp = JOptionPane.showConfirmDialog(null,
                        lang.SetLanguage("OPM_Clear_true_answers").toString(), "", 0);
                if (jp == JOptionPane.YES_OPTION) {
                    str_del_true.clear();
                    str_del_true_index.clear();
                    for (int i = 0; i < table2.getRowCount(); i++) {
                        try {
                            if (table2.getValueAt(i, 3).toString().toLowerCase().contains("ic_answ_true_20x20.png")) {
                                str_del_true.add(table2.getValueAt(i, 1).toString());
                            }
                        } catch (Exception ignored) {
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
            }

            long seed = System.nanoTime();

            Collections.shuffle(list_questions, new Random(seed));

            jo_list_answer_true = new JSONObject();
            jo_list_answer_false = new JSONObject();
            jo_list_answer_null = new JSONObject();

            written_test();

            table2.setSize(s_pane2.getSize().width, s_pane2.getSize().height);

            for (int i = 0; i < 3; i++) {
                table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
            }
        });

        Btn_Restart_spoken_test.addActionListener(e -> {
            long seed = System.nanoTime();
            Collections.shuffle(list_questions, new Random(seed));
            spoken_test();
            slider(Sld_Count_questions, list_questions.size());
        });

        Btn_Restart_tests.addActionListener(e -> {
            JPanel panel = new JPanel();
            ButtonGroup bg = new ButtonGroup();
            JRadioButton rb1 = new JRadioButton(lang.SetLanguage("Btn_go_to_spoken_test").toString());
            JRadioButton rb2 = new JRadioButton(lang.SetLanguage("Btn_go_to_written_test").toString());
            rb1.setSelected(true);
            bg.add(rb1);
            bg.add(rb2);
            panel.add(rb1);
            panel.add(rb2);

            int jp = JOptionPane.showOptionDialog(null, panel,
                    lang.SetLanguage("Btn_Choose_test").toString(), JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (jp == JOptionPane.YES_OPTION) {

                list_questions.clear();

                if (jo_list_answer_true.length() > 0) {
                    for (int i = 0; i < jo_list_answer_true.names().length(); i++) {
                        list_questions.add(jo_list_answer_true.names().getString(i));
                    }
                }
                if (jo_list_answer_false.length() > 0) {
                    for (int i = 0; i < jo_list_answer_false.names().length(); i++) {
                        list_questions.add(jo_list_answer_false.names().getString(i));
                    }
                }

                if (rb1.isSelected()) {
                    spoken_test();
                    slider(Sld_Count_questions, list_questions.size());
                } else if (rb2.isSelected()) {
                    panel_result.setVisible(false);
                    jo_list_answer_true = new JSONObject();
                    jo_list_answer_false = new JSONObject();
                    written_test();
                }
            }
        });

        Btn_Cancel_test_variants.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
            list_questions.clear();
        });

        Btn_Cancel_written_test.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
            list_questions.clear();
        });

        Btn_Cancel_test.addActionListener(actionEvent -> {
            start_current_test();
            reset_start_test();
            list_questions.clear();
        });

        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                int row = table2.rowAtPoint(evt.getPoint());
                int col = table2.columnAtPoint(evt.getPoint());

                JSONObject jo_words_examples_new_studied = jo_words_examples_ns();
                for(int i=0; i<table1.getRowCount(); i++){
                    String key_word = table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
                    String key_translate = table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
                    if(col == 5 && !table2.getValueAt(row,4).toString().equals("")) {
                        if (table2.getValueAt(row, 1).toString().equals(key_word) ||
                            table2.getValueAt(row, 1).toString().equals(key_translate)) {
                            for (int j = 0; j < jo_words_examples_new_studied.length(); j++) {
                                String key = jo_words_examples_new_studied.names().getString(j);
                                String val = jo_words_examples_new_studied.getString(key);
                                if (val.equals(table1.getValueAt(i, service.getCCI(service.nc_word_en)))||
                                        val.equals(table1.getValueAt(i, service.getCCI(service.nc_translate_en)))){
                                    JOptionPane.showMessageDialog(null, key);
                                }
                            }
                        }
                    }
                }
            }
        });

        Search_Ok.addActionListener(actionEvent -> service.find_name_translate(table1, field_search1, field_search2));

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                rbs_options[0].setForeground(Color.decode("#009926"));
                rbs_options[1].setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                answers_show_hide();
            }
        };
        RB_Option1.addMouseListener(listener);
        RB_Option2.addMouseListener(listener);

        JCB_Answers_show_hide.addActionListener(actionEvent -> answers_show_hide());

        Btn_Reload_table1.addActionListener(e -> {
            reload_data();
        });

        Tab_True_answers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = Tab_True_answers;
                transferring_checked_elements_to_arrays(Tab_True_answers, 0);
                if (Tab_True_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                super.mousePressed(evt);
                t1ort2 = Tab_True_answers;
                transferring_checked_elements_to_arrays(Tab_True_answers, 0);
                if (Tab_True_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                super.mouseReleased(evt);
                t1ort2 = Tab_True_answers;
                transferring_checked_elements_to_arrays(Tab_True_answers, 0);
                if (Tab_True_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }
        });

        Tab_False_answers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                t1ort2 = Tab_False_answers;
                transferring_checked_elements_to_arrays(Tab_False_answers, 1);
                if (Tab_False_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                super.mousePressed(evt);
                t1ort2 = Tab_False_answers;
                transferring_checked_elements_to_arrays(Tab_False_answers, 1);
                if (Tab_False_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                super.mouseReleased(evt);
                t1ort2 = Tab_False_answers;
                transferring_checked_elements_to_arrays(Tab_False_answers, 1);
                if (Tab_False_answers.columnAtPoint(evt.getPoint()) == 0) {
                    update_count_checked_elements();
                }
            }
        });

        Btn_go_to_written_test.addActionListener(e -> {
            panel_test.setVisible(false);
            written_test();
        });

        Btn_go_to_spoken_test.addActionListener(e -> {
            if (list_questions.size() == table1.getRowCount()) {
                list_questions.remove(table1.getRowCount() - 1);
            }

            number_question = 0;
            panel_test2.setVisible(false);
            panel_option_questions.setVisible(false);
            panel_test.setVisible(true);
            panel_result.setVisible(false);
            Btn_choice_answer.setEnabled(true);
            Btn_Next_question.setEnabled(false);
            Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());

            JCB_Answers_show_hide.setSelected(true);
            answers_show_hide();

            panel_test.getTopLevelAncestor().setSize(720, 340);

            jo_list_answer_false = new JSONObject();
            jo_list_answer_true = new JSONObject();
            jo_list_answer_null = new JSONObject();
            ArrayList<String> temp_list = new ArrayList<>();

            for (int i = 0; i < table2.getRowCount(); i++) {
                try {
                    if (table2.getValueAt(i, 3).toString().contains(ii_true.toString())) {
                        jo_list_answer_true.put(table2.getValueAt(i, 1).toString(),
                                table2.getValueAt(i, 4).toString());
                    } else if (table2.getValueAt(i, 3).toString().contains(ii_false.toString())) {
                        jo_list_answer_false.put(table2.getValueAt(i, 1).toString(),
                                table2.getValueAt(i, 4).toString());
                    }

                    if (table2.getValueAt(i, 3).toString().length() > 0) {
                        number_question++;
                        temp_list.add(table2.getValueAt(i, 1).toString());
                    }
                } catch (Exception ignored) {}
            }

            for (int i = 0; i < table2.getRowCount(); i++) {
                for (int j = 0; j < temp_list.size(); j++) {
                    if (!temp_list.contains(table2.getValueAt(i, 1).toString())) {
                        temp_list.add(table2.getValueAt(i, 1).toString());
                    }
                }
            }

            if (temp_list.size() > 0) {list_questions = temp_list;}

            Lbl_Result.setForeground(panel_test.getBackground());
            creating_query(list_questions.get(number_question));
            slider(Sld_Count_questions, list_questions.size());
        });

        Btn_Words_transfer_to_studied_words.addActionListener(e -> {
            ArrayList<String> current_table_list_delete_rows_names = new ArrayList<>();
            ArrayList<String> table_list_delete_rows_names = new ArrayList<>();
            ArrayList<String> table_list_delete_rows_translate = new ArrayList<>();
            ArrayList<Integer> table_list_delete_rows_indexes = new ArrayList<>();

            for (int i = 0; i < Tab_True_answers.getRowCount(); i++) {
                Boolean checked = Boolean.valueOf(Tab_True_answers.getValueAt(i, 0).toString());
                int row = (int) Tab_True_answers.getValueAt(i, 1) - 1;
                if (checked) {
                    current_table_list_delete_rows_names.add(Tab_True_answers.getValueAt(row, 2).toString());
                }
            }
            JSONArray ja11 = new JSONArray(new JSONObject(service.content_file(service.current_path[0])).
                                                                            getJSONArray("words_new").toString());
            for (int i = 0; i < ja11.length(); i++) {
                JSONObject jo_q = ja11.getJSONObject(i);
                String key = jo_q.names().getString(0);
                String val = jo_q.getString(key);
                for (Object current_table_list_delete_rows_name : current_table_list_delete_rows_names) {
                    if (key.equals(current_table_list_delete_rows_name)) {
                        table_list_delete_rows_names.add(key);
                        table_list_delete_rows_indexes.add(i);
                    } else if (val.equals(current_table_list_delete_rows_name)) {
                        table_list_delete_rows_translate.add(key);
                        table_list_delete_rows_indexes.add(i);
                    }
                }
            }

            ToolsUI toolsUI = new ToolsUI(table_list_delete_rows_indexes);
            toolsUI.showFrame(table_list_delete_rows_indexes);
        });

        Btn_Clear_answers.addActionListener(e -> {
            int jp = JOptionPane.showConfirmDialog(null,
                    lang.SetLanguage("OPM_Are_you_sure"),
                    lang.SetLanguage("OPM_Title_removing").toString(), 0);
            if (jp == JOptionPane.YES_OPTION) {
                jo_list_answer_true = new JSONObject();
                jo_list_answer_false = new JSONObject();

                for (int i = 0; i < Tab_True_answers.getRowCount(); i++) {
                    Boolean checked = Boolean.valueOf(Tab_True_answers.getValueAt(i, 0).toString());
                    if (!checked) {
                        jo_list_answer_true.put(Tab_True_answers.getValueAt(i, 2).toString(),
                                Tab_True_answers.getValueAt(i, 3).toString());
                    }
                }
                for (int i = 0; i < Tab_False_answers.getRowCount(); i++) {
                    Boolean checked = Boolean.valueOf(Tab_False_answers.getValueAt(i, 0).toString());
                    if (!checked) {
                        jo_list_answer_false.put(Tab_False_answers.getValueAt(i, 2).toString(),
                                Tab_False_answers.getValueAt(i, 3).toString());
                    }
                }
                dtm_true_false_answers_test(jo_list_answer_true.length(), jo_list_answer_true);
                Tab_True_answers.setModel(dtm_true_false_answers);
                dtm_true_false_answers_test(jo_list_answer_false.length(), jo_list_answer_false);
                Tab_False_answers.setModel(dtm_true_false_answers);

                int[] tab_true_false_answers_column_widths = {25, 35, 120, 120};
                for (int i = 0; i < tab_true_false_answers_column_widths.length; i++) {
                    Tab_True_answers.getColumnModel().getColumn(i).setMaxWidth(tab_true_false_answers_column_widths[i]);
                    Tab_False_answers.getColumnModel().getColumn(i).setMaxWidth(tab_true_false_answers_column_widths[i]);
                }
                Lbl_true_answers.setText("true: " + Tab_True_answers.getRowCount() + "");
                Lbl_false_answers.setText("false: " + Tab_False_answers.getRowCount() + "");
                Btn_Words_transfer_to_studied_words.setEnabled(false);
                Btn_Words_right.setEnabled(false);
                Btn_Words_left.setEnabled(false);
                Btn_Clear_answers.setEnabled(false);
                reload_data();
            }
        });

        Btn_Words_right.addActionListener(e -> {
            export_elem_from_tab_to_tab(Tab_True_answers, Tab_False_answers, 0);
            Btn_Words_right.setEnabled(false);
            Btn_Words_transfer_to_studied_words.setEnabled(false);
            Tab_True_answers_checked = false;
            clear_checked_elements_from_tables_tf();
            Lbl_true_answers.setText("true: " + Tab_True_answers.getRowCount() + "");
            Lbl_false_answers.setText("false: " + Tab_False_answers.getRowCount() + "");
        });
        Btn_Words_left.addActionListener(e -> {
            export_elem_from_tab_to_tab(Tab_False_answers, Tab_True_answers, 1);
            Btn_Words_left.setEnabled(false);
            Tab_False_answers_checked = false;
            clear_checked_elements_from_tables_tf();
            Lbl_true_answers.setText("true: " + Tab_True_answers.getRowCount() + "");
            Lbl_false_answers.setText("false: " + Tab_False_answers.getRowCount() + "");
        });

        Btn_Additionally.addActionListener(e -> {
            panel_test2.setVisible(false);
            panel_result.setVisible(true);
            detail_result();
        });

//      Checking elements for tables in the final_results
        CB_ta_all_checking.addActionListener(e -> {
            if (CB_ta_all_checking.isSelected()) {
                all_checked_in_tab_tf(Tab_True_answers, true);
            } else all_checked_in_tab_tf(Tab_True_answers, false);
            transferring_checked_elements_to_arrays(Tab_True_answers, 0);
            t1ort2 = Tab_True_answers;
            update_count_checked_elements();
        });
        CB_fa_all_checking.addActionListener(e -> {
            if (CB_fa_all_checking.isSelected()) {
                all_checked_in_tab_tf(Tab_False_answers, true);
            } else all_checked_in_tab_tf(Tab_False_answers, false);
            transferring_checked_elements_to_arrays(Tab_False_answers, 1);
            t1ort2 = Tab_False_answers;
            update_count_checked_elements();
        });

        Sld_Checked_count_questions.addChangeListener(e -> {
            spinner1.setValue(Sld_Checked_count_questions.getValue());
            if (Sld_Checked_count_questions.getValue() == 0) {
                Sld_Checked_count_questions.setValue(1);
            }
        });

        Sld_Count_questions.addChangeListener(e -> {
            boolean exist_afq_t1 = false;
            boolean exist_afq_t2 = false;

            for (int i = 0; i < jo_list_answer_true.length(); i++) {
                if (Lbl_Question_test.getText().equals(jo_list_answer_true.names().get(i))) {
                    exist_afq_t1 = true;
                }
            }
            for (int i = 0; i < jo_list_answer_false.length(); i++) {
                if (Lbl_Question_test.getText().equals(jo_list_answer_false.names().get(i))) {
                    exist_afq_t2 = true;
                }
            }

            if (exist_afq_t1 || exist_afq_t2) {
                if (exist_afq_t1) {
                    rbs_options[0].setSelected(true);
                    rbs_options[0].setForeground(Color.decode("#009926"));
                    rbs_options[1].setForeground(panel_test.getBackground());
                    Lbl_Result.setVisible(true);
                    Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + ": TRUE");
                    Lbl_Result.setForeground(Color.decode("#009926"));
                } else {
                    rbs_options[1].setSelected(true);
                    rbs_options[1].setForeground(Color.RED);
                    rbs_options[0].setForeground(panel_test.getBackground());
                    Lbl_Result.setVisible(true);
                    Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name") + ": FALSE");
                    Lbl_Result.setForeground(Color.RED);
                }
            } else {
                rbs_options[0].setForeground(panel_test.getBackground());
                rbs_options[1].setForeground(panel_test.getBackground());
            }
            if (Sld_Count_questions.getValue() == 0) {
                Sld_Count_questions.setValue(1);
            }


            int count_tf = jo_list_answer_true.length() + jo_list_answer_false.length();

            if (Sld_Count_questions.getValue() == count_tf + 1) {
                Lbl_Result.setForeground(panel_test.getBackground());
            }

            if (Sld_Count_questions.getValue() > count_tf + 1) {
                Sld_Count_questions.setValue(count_tf + 1);
            }
            number_question = Sld_Count_questions.getValue() - 1;
            creating_query(list_questions.get(number_question));
        });

        spinner1.addChangeListener(e -> Sld_Checked_count_questions.setValue((int)spinner1.getValue()));

        Btn_info.addActionListener(e -> {
            JSONObject[] jo_words_examples_new_studied = new JSONObject[2];
            jo_words_examples_new_studied[0] = new JSONObject();
            try {
                service.dir_vocabulary_file(0);
                jo_words_examples_new_studied[0] =
                        (JSONObject) new JSONObject(service.content_file(service.current_path[0])).
                                                                        getJSONObject("words_example").get("words_new");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            for(int i=0; i<table1.getRowCount(); i++){
                String key_word = table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
                String key_tr = table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
                if(Lbl_Question_test.getText().equals(key_word) || Lbl_Question_test.getText().equals(key_tr)){
                    for(int j=0; j<jo_words_examples_new_studied[0].length(); j++){
                        String key = jo_words_examples_new_studied[0].names().getString(j);
                        String val = jo_words_examples_new_studied[0].getString(key);
                        if(val.equals(table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString())||
                           val.equals(table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString())){
                            Btn_info.setToolTipText(key);
                            JOptionPane.showMessageDialog(null, key);
                        }
                    }
                }
            }
        });

        for (JCheckBox aCB_WT1 : CB_WT1) {
            aCB_WT1.addActionListener(e -> ordering_type_words());
        }
    }

    private void reload_data(){
        service.list_questions_all.clear();
        load_data_table1(canEdit, table_column_widths);
        slider(Sld_Checked_count_questions, table1.getRowCount());
    }

    private void ordering_type_words(){
        Boolean[] cb_words_type_selected1 = new Boolean[service.word_types.length];
        for(int i=0; i<cb_words_type_selected1.length; i++){
            cb_words_type_selected1[i] = CB_WT1[i].isSelected();
        }

        if (Arrays.asList(cb_words_type_selected1).contains(true)) {
            for (int i = 0; i < service.word_types.length; i++) {
                for (int j = 0; j < table1.getRowCount(); j++) {
                    try {
                        if (table1.getValueAt(j, service.getCCI(service.nc_type_en)).toString().toLowerCase().
                                                                        contains(service.word_types[i].toLowerCase())) {
                            if (cb_words_type_selected1[i]) {
                                table1.setValueAt(true, j, service.getCCI(service.nc_check));
                            }else{
                                table1.setValueAt(false, j, service.getCCI(service.nc_check));
                            }
                        }
                    }
                    catch(Exception ignored){};
                }
            }
        }
        else{
            for (int j = 0; j < table1.getRowCount(); j++) {
                table1.setValueAt(false, j, service.getCCI(service.nc_check));
            }
        }
    }

    private void icon_for_examples_words_table2(int c_j, JSONObject c_jo_words_examples_new_studied){
        table2.setValueAt(service.im12, c_j, 5);
        for (int i = 0; i < table1.getRowCount(); i++) {
            String key_1 = table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
            String val_1 = table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
            if (list_questions.get(c_j).equals(key_1) || list_questions.get(c_j).equals(val_1)){
                for (int u = 0; u < c_jo_words_examples_new_studied.length(); u++) {
                    String key_jo = c_jo_words_examples_new_studied.names().getString(u);
                    String val_jo = c_jo_words_examples_new_studied.getString(key_jo);
                    if (val_jo.equals(table1.getValueAt(i, service.getCCI(service.nc_word_en)))||
                            val_jo.equals(table1.getValueAt(i, service.getCCI(service.nc_translate_en)))) {
                        table2.setValueAt(service.im11, c_j, 5);
                    }
                }
            }
        }
    }

    private JSONObject jo_words_examples_ns(){
        JSONObject jo_words_examples_new_studied = new JSONObject();
        try {
            service.dir_vocabulary_file(0);
            jo_words_examples_new_studied = (JSONObject) new JSONObject(service.content_file(service.current_path[0])).
                    getJSONObject("words_example").get("words_new");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return  jo_words_examples_new_studied;
    }


    private void start_current_test() {
        list_questions = new ArrayList<>();
        list_answers = new ArrayList<>();
        jo_list_answer_true = new JSONObject();
        jo_list_answer_false = new JSONObject();
        jo_list_answer_null = new JSONObject();
        number_question = 0;
        Lbl_Result.setText("");
    }

    private void creating_query(String n) {
        String current_column_word = table1.getColumnName(service.getCCI(service.nc_word_en));
        Btn_info.setIcon(service.im12);
        Btn_info.setEnabled(false);
        if (list_questions.size() < service.ja_words.length()) {

            if (Btn_choice_answer.getModel().isEnabled()) {
                Btn_Next_question.setEnabled(false);
            }
            for (JRadioButton rb_option : rbs_options) {
                group_test.add(rb_option);
            }

            long seed = System.nanoTime();
            Lbl_Question_test.setText(n);
            list_answers.clear();
            JSONObject[] jo_words_examples_new_studied = new JSONObject[2];
            jo_words_examples_new_studied[0] = new JSONObject();
            try {
                service.dir_vocabulary_file(0);
                jo_words_examples_new_studied[0] = (JSONObject) new JSONObject(service.content_file(service.current_path[0])).
                        getJSONObject("words_example").get("words_new");
            } catch (IOException e) {
                e.printStackTrace();
            }


            for(int i=0; i<table1.getRowCount(); i++){
                String key_word = table1.getValueAt(i, service.getCCI(service.nc_word_en)).toString();
                String key_tr = table1.getValueAt(i, service.getCCI(service.nc_translate_en)).toString();
                if(Lbl_Question_test.getText().equals(key_word) || Lbl_Question_test.getText().equals(key_tr)){
                    for(int j=0; j<jo_words_examples_new_studied[0].length(); j++){
                        String key = jo_words_examples_new_studied[0].names().getString(j);
                        String val = jo_words_examples_new_studied[0].getString(key);
                        if(val.equals(table1.getValueAt(i, service.getCCI(service.nc_word_en)))||
                                val.equals(table1.getValueAt(i, service.getCCI(service.nc_translate_en)))){
                            Btn_info.setIcon(service.im11);
                            Btn_info.setEnabled(true);
                        }
                    }
                }
            }

            Collections.shuffle(service.list_questions_all, new Random(seed));
            Collections.shuffle(service.list_answers_all, new Random(seed));

//          addition 3 false_answer into the list
            for (int i = 0; i < service.ja_words.length(); i++) {
                String question = service.jo_number_pair[i].names().getString(0);
                String answer = service.jo_number_pair[i].getString(question);
                if (current_column_word.equals(service.nc_word_en) || current_column_word.equals(service.nc_word_ua)) {
                    if (question.equals(n)) {
                        list_answers.add(answer);
                        break;
                    }
                } else if (current_column_word.equals(service.nc_translate_en) || current_column_word.equals(service.nc_translate_ua)) {
                    if (answer.equals(n)) {
                        list_answers.add(question);
                        break;
                    }
                }
            }

            list_answers.add("Don't know");

//          copying elements from list to radiobuttons
            for (int i = 0; i < 2; i++) {
                rbs_options[i].setText(list_answers.get(i));
            }
        }
    }

    private void slider(JSlider c_slider, int c_size) {
        c_slider.setPaintTicks(true);
        c_slider.repaint();

        c_slider.setPaintLabels(true);
        c_slider.setLabelTable(null);

        if (c_size < 20) {
            c_slider.setMajorTickSpacing(1);
            c_slider.setMinorTickSpacing(1);
        } else if (c_size >= 20 && 50 > c_size) {
            c_slider.setMajorTickSpacing(2);
            c_slider.setMinorTickSpacing(2);
        } else if (c_size >= 50 && c_size < 300) {
            for (int i = c_size; i >= 50; i--) {
                if (i % 50 == 0) {
                    int mj = (int) Math.round(i * 0.1);
                    c_slider.setMajorTickSpacing(mj);
                }
                if (i % 25 == 0) {
                    int mn = (int) Math.round(i * 0.1);
                    c_slider.setMinorTickSpacing(mn);
                }
            }
        } else if (c_size >= 300 && c_size < 1000) {
            for (int i = c_size; i >= 300; i--) {
                if (i % 100 == 0) {
                    int mj = (int) Math.round(i * 0.1);
                    c_slider.setMajorTickSpacing(mj);
                }
                if (i % 50 == 0) {
                    int mn = (int) Math.round(i * 0.1);
                    c_slider.setMinorTickSpacing(mn);
                }
            }
        } else {
            for (int i = c_size; i >= 1000; i--) {
                if (i % 500 == 0) {
                    int pro = (int) Math.round(i * 0.1);
                    c_slider.setMajorTickSpacing(pro);
                }
                if (i % 250 == 0) {
                    int mn = (int) Math.round(i * 0.1);
                    c_slider.setMinorTickSpacing(mn);
                }
            }
        }

        if(c_slider.equals(Sld_Count_questions)) {
            Sld_Count_questions.setValue(number_question + 1);
            Sld_Count_questions.setToolTipText(number_question + "/" + c_size);
        }
        else{
            Sld_Checked_count_questions.setValue(5);
        }
        c_slider.setMinimum(0);
        c_slider.setMaximum(c_size);
    }

    private void all_checked_in_tab_tf(JTable c_table, boolean tf) {
        for (int i = 0; i < c_table.getRowCount(); i++) {
            c_table.setValueAt(tf, i, service.getCCI(service.nc_check));
        }
    }

    private void detail_result() {
        CB_fa_all_checking.setSelected(false);
        CB_ta_all_checking.setSelected(false);
        Lbl_true_answers.setText("true: " + jo_list_answer_true.length() + "");
        Lbl_true_answers.setForeground(Color.decode("#009926"));
        Lbl_false_answers.setText("false: " + jo_list_answer_false.length() + "");
        Lbl_false_answers.setForeground(Color.red);
        Btn_Words_right.setEnabled(false);
        Btn_Words_left.setEnabled(false);

        if (jo_list_answer_true.length() == 0) {
            Btn_Clear_answers.setEnabled(false);
            Btn_Words_transfer_to_studied_words.setEnabled(false);
        }

        dtm_true_false_answers_test(jo_list_answer_true.length(), jo_list_answer_true);
        Tab_True_answers.setModel(dtm_true_false_answers);
        dtm_true_false_answers_test(jo_list_answer_false.length(), jo_list_answer_false);
        Tab_False_answers.setModel(dtm_true_false_answers);
        int[] tab_true_false_answers_column_widths = {25, 35, 120, 120};
        for (int i = 0; i < tab_true_false_answers_column_widths.length; i++) {
            Tab_True_answers.getColumnModel().getColumn(i).setMaxWidth(tab_true_false_answers_column_widths[i]);
            Tab_False_answers.getColumnModel().getColumn(i).setMaxWidth(tab_true_false_answers_column_widths[i]);
        }
        panel_result.getTopLevelAncestor().setSize(720, 420);
    }

    private void export_elem_from_tab_to_tab(JTable table_from, JTable table_to, int n) {
        transferring_checked_elements_to_arrays(table_from, n);
        int ldr_st = 0;
        for (Integer al_indexe : table_list_delete_rows_indexes[n]) {
            int ldr_value = al_indexe - ldr_st;
            ((DefaultTableModel) table_from.getModel()).removeRow(ldr_value);
            ldr_st++;
        }

        for (int i = 0; i < table_from.getRowCount(); i++) {
            table_from.setValueAt(i + 1, i, 1);
        }

        for (int i = 0; i < table_list_delete_rows_indexes[n].size(); i++) {
            DefaultTableModel model = (DefaultTableModel) table_to.getModel();
            model.addRow(new Object[]{false, table_to.getRowCount() + 1, table_list_delete_rows_names[n].get(i),
                    table_list_delete_rows_translate[n].get(i)});
        }

        table_to.changeSelection(table_to.getRowCount() - 1, 0, false, false);

        table_list_delete_rows_names[n].clear();
        table_list_delete_rows_translate[n].clear();
        table_list_delete_rows_indexes[n].clear();
    }

    private void shape_for_tables_tf_answers(JTable c_tab) {
        for (int i = 0; i < c_tab.getRowCount(); i++) {
            Boolean checked = Boolean.valueOf(c_tab.getValueAt(i, 0).toString());
            if (!checked) {
                content_for_tables_tf_answers(false);
            } else {
                content_for_tables_tf_answers(true);
                break;
            }
        }
    }

    private void content_for_tables_tf_answers(Boolean tf) {
        if (t1ort2.equals(Tab_True_answers)) {
            Btn_Words_transfer_to_studied_words.setEnabled(tf);
            Btn_Words_right.setEnabled(tf);
            Tab_True_answers_checked = tf;
        } else if (t1ort2.equals(Tab_False_answers)) {
            Btn_Words_left.setEnabled(tf);
            Tab_False_answers_checked = tf;
        }
    }

    private void update_count_checked_elements() {
        if (t1ort2.equals(Tab_True_answers)) {
            shape_for_tables_tf_answers(Tab_True_answers);
        } else if (t1ort2.equals(Tab_False_answers)) {
            shape_for_tables_tf_answers(Tab_False_answers);
        }
        clear_checked_elements_from_tables_tf();
    }

    private void clear_checked_elements_from_tables_tf() {
        if (Tab_True_answers_checked || Tab_False_answers_checked) {
            Btn_Clear_answers.setEnabled(true);
        } else Btn_Clear_answers.setEnabled(false);
    }

    private void transferring_checked_elements_to_arrays(JTable t1, int n) {
        table_list_delete_rows_indexes[n] = new ArrayList();
        table_list_delete_rows_names[n] = new ArrayList();
        table_list_delete_rows_translate[n] = new ArrayList();

        for (int i = 0; i < t1.getRowCount(); i++) {
            Boolean checked = Boolean.valueOf(t1.getValueAt(i, 0).toString());
            int row = (int) t1.getValueAt(i, 1) - 1;
            if (checked) {
                table_list_delete_rows_indexes[n].add(row);
                table_list_delete_rows_names[n].add(t1.getValueAt(row, service.getCCI(service.nc_word_en)).toString());
                table_list_delete_rows_translate[n].add(t1.getValueAt(row, service.getCCI(service.nc_translate_en)).toString());
            }
        }
    }

    private void dtm_true_false_answers_test(int count_row, JSONObject c_data) {
        boolean[] canEdit2 = {true, false, false, false};
        dtm_true_false_answers = new DefaultTableModel((Object[]) new String[]{service.nc_check, service.nc_number, service.nc_word_en, service.nc_translate_en}, count_row) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Boolean.class;
                    case 1: return Integer.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    default: return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit2[columnIndex];
            }
        };

        for (int i = 0; i < count_row; i++) {
            dtm_true_false_answers.setValueAt(false, i, service.getCCI(service.nc_check));
            dtm_true_false_answers.setValueAt(i + 1, i, service.getCCI(service.nc_number));
            String key = c_data.names().getString(i);
            String val = c_data.getString(key);
            dtm_true_false_answers.setValueAt(key, i, service.getCCI(service.nc_word_en));
            dtm_true_false_answers.setValueAt(val, i, service.getCCI(service.nc_translate_en));
        }
    }

    private void spoken_test() {
        number_question = 0;
        Lbl_Result.setText("");
        panel_option_questions.setVisible(false);
        panel_test.setVisible(true);
        panel_result.setVisible(false);
        Btn_choice_answer.setEnabled(true);
        Btn_Next_question.setEnabled(false);
        Btn_go_to_written_test.setEnabled(true);
        JCB_Answers_show_hide.setSelected(true);

        Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());

        answers_show_hide();

        panel_test.getTopLevelAncestor().setSize(720, 340);

        if (list_questions.size() == table1.getRowCount()) {
            list_questions.remove(table1.getRowCount() - 1);
        }

        long seed = System.nanoTime();
        Collections.shuffle(list_questions, new Random(seed));
        jo_list_answer_false = new JSONObject();
        jo_list_answer_true = new JSONObject();
        creating_query(list_questions.get(number_question));
    }

    private void written_test() {
        list_current_answers = new ArrayList<>();
        panel_option_questions.setVisible(false);
        panel_test.setVisible(false);
        Btn_Additionally.setEnabled(false);
        Btn_go_to_spoken_test.setEnabled(true);
        panel_test2.setVisible(true);
        panel_test2.getTopLevelAncestor().setSize(720, 480);

        boolean[] canEdit2 = {false, false, true, false, false, false};
        DefaultTableModel dtm = new DefaultTableModel((Object[]) lang.SetLanguage("TC_name2"), list_questions.size()) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Integer.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return ImageIcon.class;
                    case 4: return String.class;
                    case 5: return ImageIcon.class;
                    default: return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit2[columnIndex];
            }
        };

        table2.setModel(dtm);

        for (int i = 0; i < list_questions.size(); i++) {
            dtm.setValueAt(i + 1, i, 0);
            dtm.setValueAt(list_questions.get(i), i, 1);
            dtm.setValueAt("", i, 2);
            dtm.setValueAt(null, i, 3);
            dtm.setValueAt("", i, 4);


            if (jo_list_answer_true.length() != 0) {
                for (int j = 0; j < jo_list_answer_true.length(); j++) {
                    if (list_questions.get(i).equals(jo_list_answer_true.names().get(j).toString())) {
                        dtm.setValueAt(i + 1, i, 0);
                        dtm.setValueAt(list_questions.get(i), i, 1);
                        dtm.setValueAt(jo_list_answer_true.getString(list_questions.get(i)), i, 2);
                        dtm.setValueAt(ii_true, i, 3);
                        dtm.setValueAt(jo_list_answer_true.getString(list_questions.get(i)), i, 4);
                    }
                }
                for (int j = 0; j < jo_list_answer_false.length(); j++) {
                    if (list_questions.get(i).equals(jo_list_answer_false.names().get(j).toString())) {
                        dtm.setValueAt(i + 1, i, 0);
                        dtm.setValueAt(list_questions.get(i), i, 1);
                        dtm.setValueAt("", i, 2);
                        dtm.setValueAt(ii_false, i, 3);
                        dtm.setValueAt(jo_list_answer_false.getString(list_questions.get(i)), i, 4);
                    }
                }
            }
        }



        table2.setSize(s_pane2.getSize().width, s_pane2.getSize().height);

        for (int i = 0; i < 6; i++) {
            table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
        }
    }

    private void load_data_table1(boolean[] current_canEdit, int[] current_table_column_widths) {
        service.table(0, current_canEdit, "words_new");
        table1.setModel(service.model[0]);
        for (int i = 0; i < current_table_column_widths.length; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(current_table_column_widths[i]);
        }
        table1.removeColumn(table1.getColumnModel().getColumn(5));
        table1.removeColumn(table1.getColumnModel().getColumn(5));
        table1.removeColumn(table1.getColumnModel().getColumn(5));
        table1.removeColumn(table1.getColumnModel().getColumn(5));
        spinner1.setModel(new SpinnerNumberModel(5, 1, service.ja_words.length(), 1));
        spinner2.setModel(new SpinnerNumberModel(1, 1, service.ja_words.length() - 1, 1));
        spinner3.setModel(new SpinnerNumberModel(2, 2, service.ja_words.length(), 1));
    }

    private void answers_show_hide() {
        if (JCB_Answers_show_hide.isSelected()) {
            rbs_options_color(panel_test.getBackground());
        } else {
            rbs_options_color(Color.BLACK);
        }
    }

    private void rbs_options_color(Color c_color){
        for (JRadioButton rbs_option : rbs_options) {
            rbs_option.setForeground(c_color);
        }
    }

    private void elements_name() {
        Service.Language lang = new Service.Language(service.current_path[1]);
        Lbl_title.setText(lang.SetLanguage("Lbl_Title_name").toString());
        RB_Count_questions.setText(lang.SetLanguage("RB_Count_questions_name").toString());
        RB_Choice_from_List.setText(lang.SetLanguage("RB_Choice_from_List_name").toString());
        CB_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
        Lbl_sp2_to_sp3.setText(lang.SetLanguage("Lbl_sp2_to_sp3_name").toString());
        Btn_Parts_of_speech.setText(lang.SetLanguage("Btn_Parts_of_speech").toString());
        Lbl_Type_test.setText(lang.SetLanguage("Lbl_Type_test").toString());
        Btn_choice_answer.setText(lang.SetLanguage("Btn_Ok_name").toString());
        Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());
        Btn_Start_test.setText(lang.SetLanguage("Btn_Start_test_name").toString());
        Lbl_true_answers.setText(lang.SetLanguage("Lbl_True_answers_name").toString());
        Lbl_false_answers.setText(lang.SetLanguage("Lbl_False_answers_name").toString());
        Btn_Cancel_test.setToolTipText(lang.SetLanguage("Btn_Cancel_test").toString());
        Btn_Cancel_written_test.setToolTipText(lang.SetLanguage("Btn_Cancel_test").toString());
        Btn_Cancel_test_variants.setToolTipText(lang.SetLanguage("Btn_Cancel_test").toString());
        Btn_Restart_spoken_test.setToolTipText(lang.SetLanguage("Btn_Reset_test").toString());
        Btn_Restart_written_test.setToolTipText(lang.SetLanguage("Btn_Reset_test").toString());
        Btn_Restart_tests.setToolTipText(lang.SetLanguage("Btn_Reset_test").toString());
        Btn_answer_for_written_test.setText(lang.SetLanguage("Lbl_Result_name").toString());
        Btn_Additionally.setToolTipText(lang.SetLanguage("Btn_Additionally").toString());
        Btn_go_to_spoken_test.setText(lang.SetLanguage("Btn_go_to_spoken_test").toString());
        Btn_go_to_written_test.setText(lang.SetLanguage("Btn_go_to_written_test").toString());
        Btn_Clear_answers.setToolTipText(lang.SetLanguage("OPM_Clear_answers").toString());
        Btn_Words_transfer_to_studied_words.setToolTipText(lang.SetLanguage("Btn_Transfer_to_studied_words").toString());
        CB_ta_all_checking.setText(lang.SetLanguage("CB_Checking_all").toString());
        CB_fa_all_checking.setText(lang.SetLanguage("CB_Checking_all").toString());
        Btn_All_answers_for_written_test.setText(lang.SetLanguage("Btn_Answers_all").toString());
        String[] str = (String[])lang.SetLanguage("CB_Words_type");

        for(int i=0;i< CB_WT1.length;i++){
            CB_WT1[i].setText(str[i]);
        }
    }

    private void color_elements() {
        panel_option_questions.setBackground(Color.decode(new Service.SetColor(service.current_path[1], "MainUI").val));
        panel_test2.setBackground(Color.decode(new Service.SetColor(service.current_path[1], "TestUI 2").val));
        panel_test.setBackground(Color.decode(new Service.SetColor(service.current_path[1], "TestUI 1(variants)").val));
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1], "MainUI Table").val));
        table2.setBackground(Color.decode(new Service.SetColor(service.current_path[1], "TestUI 2 Table").val));
        RB_Count_questions.setBackground(panel_option_questions.getBackground());
        RB_Choice_from_List.setBackground(panel_option_questions.getBackground());
        CB_Scope_questions.setBackground(panel_option_questions.getBackground());
        Lbl_sp2_to_sp3.setBackground(panel_option_questions.getBackground());

        for (JRadioButton rb_option : rbs_options) {
            rb_option.setBackground(panel_test.getBackground());
        }
    }


    private void spinner_choice_item_table() {
        if (CB_Scope_questions.isSelected()) {
            for (int i = 0; i < table1.getRowCount(); i++) {
                table1.setValueAt(false, i, service.getCCI(service.nc_check));
            }
            for (int i = (int) spinner2.getValue() - 1; i < (int) spinner3.getValue(); i++) {
                if (table1.getRowCount() > (int) spinner3.getValue() - 1) {
                    table1.setValueAt(true, i, service.getCCI(service.nc_check));
                }
            }
        } else {
            for (int i = (int) spinner2.getValue() - 1; i < (int) spinner3.getValue(); i++) {
                if (table1.getRowCount() > 0) {
                    table1.setValueAt(false, i, service.getCCI(service.nc_check));
                }
            }
        }
    }

    private void reset_start_test() {
        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);
        panel_option_questions.setVisible(true);
        panel_option_questions.getTopLevelAncestor().setSize(720, 540);
    }

    private void select_parameter_enable(boolean spinner1_enable, boolean spinner23_lbl_enable, boolean table1_enable) {
        spinner1.setEnabled(spinner1_enable);
        spinner2.setEnabled(spinner23_lbl_enable);
        Lbl_sp2_to_sp3.setEnabled(spinner23_lbl_enable);
        spinner3.setEnabled(spinner23_lbl_enable);
        table1.setEnabled(table1_enable);
    }


    class MenuUI extends JFrame {
        Service.Language lang = new Service.Language(service.current_path[1]);
        JMenuBar menuBar;
        JMenu m_settings, m_about, m_lang;
        JMenuItem mi_edit, mi_design;
        JMenuItem menuItem1, menuItem2;
        JMenuItem mi_description;

        private MenuUI() {
            menuBar = new JMenuBar();
            // Create Menu objects to add to the MenuBar
            m_settings = new JMenu(lang.SetLanguage("M_Settings_name").toString());
            m_about = new JMenu(lang.SetLanguage("M_About_name").toString());
            mi_edit = new JMenuItem(lang.SetLanguage("MI_Vocabulary_name").toString());
            m_lang = new JMenu(lang.SetLanguage("M_Lang_name").toString());
            mi_design = new JMenuItem(lang.SetLanguage("MI_Design_name").toString());

            menuItem1 = new JMenuItem("English");
            menuItem2 = new JMenuItem("Українська");

            mi_description = new JMenuItem(lang.SetLanguage("MI_Description").toString());

            mi_edit.addActionListener(actionEvent -> {
                ToolsUI toolsUI = new ToolsUI(new ArrayList<>());
                toolsUI.showFrame(new ArrayList<>());
            });

            mi_design.addActionListener(actionEvent -> {
                DesignUI desingUI = new DesignUI();
                desingUI.showFrame();
            });

            menuItem1.addActionListener(actionEvent -> {
                lang_turn_on("en");
            });

            menuItem2.addActionListener(actionEvent -> {
                lang_turn_on("ua");
            });

            mi_description.addActionListener(actionEvent -> {
                DescriptionUI descUI = new DescriptionUI();
                descUI.showFrame();
            });
        }

        private void lang_turn_on(String current_lang) {
            String content_file = service.content_file(service.current_path[1]);
            JSONArray ja = new JSONArray(content_file);
            JSONArray ja_all = new JSONArray();
            JSONObject jo = ja.getJSONObject(1);
            JSONObject jo2 = new JSONObject();
            jo2.put("lang", current_lang);

            ja_all.put(jo2);
            ja_all.put(jo);

            try {
                service.write_content_in_file(service.current_path[1], ja_all, "edit");
                JOptionPane.showMessageDialog(
                        null,
                        lang.SetLanguage("OPM_Restart_program").toString(),
                        lang.SetLanguage("OPM_Title").toString(),
                        JOptionPane.WARNING_MESSAGE);
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
        try {
            MainUI window = new MainUI();
            window.showFrame();
        }catch (Exception ex){
            ToolsUI toolsUI = new ToolsUI(new ArrayList<>());
            toolsUI.showFrame(new ArrayList<>());
        }
    }

    private void showFrame() {
        JFrame frame = new JFrame("MainUI");
        frame.setContentPane(new MainUI().main_panel);
        frame.setPreferredSize(new Dimension(720, 540));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        new MenuUI().showMenu(frame);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
