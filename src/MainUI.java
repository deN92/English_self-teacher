import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainUI{
    private JPanel main_panel;
    private JPanel panel_test, panel_option_questions, panel_result;
    private JRadioButton RB_Option1, RB_Option2, RB_Option3, RB_Option4,
                         RB_Count_questions, RB_Scope_questions, RB_Choice_from_List;
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

    private Service service;
    private Service.Language lang;
    private Service.SetColor color1;

    private ArrayList<String> list_questions;
    private ArrayList<String> list_answers;
    private DefaultListModel model_answer_true;
    private DefaultListModel model_answer_false;

    private int result_count = 0;
    private int n;
    private int answer_true;
    private int answer_false;
    private int sum2;
    private int[] table2_column_widths = {25,200,200,23,200};

    private void elements_name(){
        lang = new Service.Language(service.current_path[1]);
        Lbl_title.setText(lang.SetLanguage("Lbl_Title_name").toString());
        RB_Count_questions.setText(lang.SetLanguage("RB_Count_questions_name").toString());
        RB_Choice_from_List.setText(lang.SetLanguage("RB_Choice_from_List_name").toString());
        RB_Scope_questions.setText(lang.SetLanguage("RB_Scope_questions_name").toString());
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
    }

    private void color_elements(){
        table1.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"MainUI_Table_bg" ).val));
        panel_option_questions.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"MainUI_bg" ).val));
        panel_test.setBackground(Color.decode(new Service.SetColor(service.current_path[1],"TestUI_bg" ).val));
        RB_Count_questions.setBackground(panel_option_questions.getBackground());
        RB_Choice_from_List.setBackground(panel_option_questions.getBackground());
        RB_Scope_questions.setBackground(panel_option_questions.getBackground());
        Lbl_sp2_to_sp3.setBackground(panel_option_questions.getBackground());

        JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
        for(int i=0; i< rb.length; i++){
            rb[i].setBackground(panel_test.getBackground());
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

    public MainUI() {
        service = new Service();
        start_current_test();

        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);

        boolean[] canEdit = {true, false, false, false, false, false};
        service.table(0, canEdit, "", "words_new");
        elements_name();
        table1.setModel(service.model[0]);
        table1.setSize(s_pane1.getSize().width, s_pane1.getSize().height);
        int[] table_column_widths = {25,35,230,230,0,0};
        for (int i = 0; i < 6; i++) {
            table1.getColumnModel().getColumn(i).setMaxWidth(table_column_widths[i]);
        }
        table1.removeColumn(table1.getColumnModel().getColumn(4));
        table1.removeColumn(table1.getColumnModel().getColumn(4));
//        table1.removeColumn(table1.getColumnModel().getColumn(5));

        RB_Count_questions.setSelected(true);
        RB_Option1.setSelected(true);

        spinner1.setModel(new SpinnerNumberModel(5, 1, service.ja_words.length(), 1));
        spinner2.setModel(new SpinnerNumberModel(1, 1, service.ja_words.length() - 1, 1));
        spinner3.setModel(new SpinnerNumberModel(2, 2, service.ja_words.length(), 1));

        parameter_enable(new boolean[]{true, false, false, false});
        Service.Language lang = new Service.Language(service.current_path[1]);
        color_elements();
        comboBox1.setModel(new DefaultComboBoxModel((String[])lang.SetLanguage("CB_Elements_name")));

        Btn_Start_test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                long seed = System.nanoTime();

//              check count random questions
                if (RB_Count_questions.getModel().isSelected()) {
                    list_questions.clear();
                    int qc = (int) spinner1.getValue();
                    Collections.shuffle(service.list_questions_all, new Random(seed));
                    for (int i = 0; i < qc; i++) {
                        list_questions.add(i, service.list_questions_all.get(i));
                    }
                }

//              Check scope number questions from table
                if (RB_Scope_questions.getModel().isSelected()) {
                    list_questions.clear();
                    Integer iS = (Integer) spinner2.getValue();
                    Integer iL = (Integer) spinner3.getValue();
                    for (int i = 0; i < iL - iS + 1; i++) {
                        list_questions.add(service.jo_number_pair[iS + i - 1].names().getString(0));
                    }
                    Collections.shuffle(list_questions, new Random(seed));
                }

//              Check questions from table
                if (RB_Choice_from_List.getModel().isSelected()) {
                    list_questions.clear();
                    for (int i = 0; i < service.ja_words.length(); i++) {
                        Boolean checked = Boolean.valueOf(table1.getValueAt(i, 0).toString());
                        int col = (int) table1.getValueAt(i, 1) - 1;
                        if (checked) {
                            list_questions.add(service.jo_number_pair[col].names().getString(0));
                        }
                    }
                    Collections.shuffle(list_questions, new Random(seed));
                }

//              Order questions
                if(comboBox1.getSelectedIndex() == 0){
                    panel_option_questions.setVisible(false);
                    panel_test.setVisible(false);
                    panel_test2.setVisible(true);
                    panel_test2.getTopLevelAncestor().setSize(720,400);

                    DefaultTableModel dtm = new DefaultTableModel() {
                        String[] employee = (String[]) lang.SetLanguage("TC_name2");
                        boolean[] canEdit2 = {false, false, true, false, false};
                        @Override
                        public int getColumnCount() {
                            return 5;
                        }

                        @Override
                        public int getRowCount(){
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
                        public Class<?> getColumnClass(int column){
                            switch(column){
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

                    for(int i=0; i<list_questions.size(); i++) {
                        dtm.setValueAt(i+1, i, 0);
                        dtm.setValueAt(list_questions.get(i), i, 1);
                        dtm.setValueAt("", i, 2);
                    }

                    table2.removeColumn(table2.getColumnModel().getColumn(4));
                    table2.removeColumn(table2.getColumnModel().getColumn(3));

                    table2.setSize(s_pane2.getSize().width, s_pane2.getSize().height);

                    for (int i = 0; i < 3; i++) {
                        table2.getColumnModel().getColumn(i).setMaxWidth(table2_column_widths[i]);
                    }
                }
                else{
                    panel_option_questions.setVisible(false);
                    panel_test.setVisible(true);
                    panel_result.setVisible(false);
                    Btn_choice_answer.setEnabled(true);
                    Btn_Next_question.setEnabled(false);
                    Btn_Next_question.setText(lang.SetLanguage("Btn_Next_question_name").toString());

                    JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
                    for (int i = 0; i < 4; i++) {
                        rb[i].setForeground(Color.decode("#000"));
                    }

                    panel_test.getTopLevelAncestor().setSize(720, 300);
                    if(list_questions.size() == table1.getRowCount()) {
                        list_questions.remove(table1.getRowCount()-1);
                    }
                    creating_query(list_questions.get(n));
                }

//              check list_questions is not empty
                if(list_questions.size() == 0){
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Text_question_null").toString(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.WARNING_MESSAGE);
                }


            }

        });



        Btn_choice_answer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};

//              Find true answer to the question
                String answer = "";
                for (int i = 0; i < service.ja_words.length(); i++) {
                    String question = service.jo_number_pair[i].names().getString(0);
                    if (question.equals(Lbl_Question_test.getText())) {
                        answer = service.jo_number_pair[i].getString(question);
                    }
                }

//              Check true/false answer
                for (int i = 0; i < 4; i++) {
                    group_test.add(rb[i]);
                    if (group_test.isSelected(rb[i].getModel())) {
                        if (rb[i].getText().equals(answer)) {
                            Lbl_Result.setForeground(Color.decode("#009926"));
                            answer_true++;
                            Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name")+ ": "+answer_true+"/"+answer_false);
                            model_answer_true.addElement(Lbl_Question_test.getText() + " - " + answer);
                        } else {
                            Lbl_Result.setForeground(Color.RED);
                            rb[i].setForeground(Color.RED);
                            answer_false++;
                            Lbl_Result.setText(lang.SetLanguage("Lbl_Result_name")+ ": "+answer_true+"/"+answer_false);
                            model_answer_false.addElement(Lbl_Question_test.getText() + " - " + answer);
                        }
                    }
                    if (rb[i].getText().equals(answer)) {
                        rb[i].setForeground(Color.decode("#009926"));
                    }
                }
                Btn_choice_answer.setEnabled(false);
                Btn_Next_question.setEnabled(true);

//              +1 completed question
                sum2++;

//              check list_questions size == current number question from list
                if (list_questions.size() == sum2) {
//                  next_question rename to result
                    Btn_Next_question.setText(lang.SetLanguage("Lbl_Result_name").toString());
                }
            }
        });

        Btn_Next_question.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Btn_Next_question.getText().equals(lang.SetLanguage("Btn_Next_question_name"))) {
                    n++;
                    Btn_choice_answer.setEnabled(true);
                    Lbl_Result.setText("");
                    JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
                    for (int i = 0; i < 4; i++) {
                        rb[i].setForeground(Color.BLACK);
                    }
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
                    panel_result.getTopLevelAncestor().setSize(720,400);
                }
            }
        });

        RB_Count_questions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                parameter_enable(new boolean[] {true, false, false, false});
            }
        });
        RB_Scope_questions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                parameter_enable(new boolean[] {false, true, true, false});
            }
        });
        RB_Choice_from_List.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                parameter_enable(new boolean[] {false, false, false, true});
            }
        });

        spinner2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int jS2 = (int) spinner2.getValue();
                int jS3 = (int) spinner3.getValue();
                if (jS2 == jS3) {
                    spinner3.setValue(jS2 + 1);
                }
            }
        });
        spinner3.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                int jS2 = (int) spinner2.getValue();
                int jS3 = (int) spinner3.getValue();
                if (jS2 == jS3) {
                    spinner2.setValue(jS3 - 1);
                }
            }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                start_current_test();
                reset_start_test();
            }
        });
        Btn_Reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                start_current_test();
                reset_start_test();
            }
        });
        Btn_answer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ArrayList<String> ast = new ArrayList<>();

                if(table2.getColumnCount() == 3){
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
                for(int j=0; j< list_questions.size(); j++) {
                    for (int i = 0; i < table1.getRowCount(); i++) {
                        if (table1.getValueAt(i, 2).toString().toLowerCase().equals(list_questions.get(j).toLowerCase())) {
                            ast.add(table1.getValueAt(i, 3).toString());
                        }
                    }

                    String value_table = table2.getValueAt(j,2).toString().toLowerCase();
                    String value_current = ast.get(j).toLowerCase();


                    if((!value_table.equals("")) && (value_table.contains(value_current)|| value_current.contains(value_table))){
                        table2.setValueAt(ii_true, j, 3);
                    }
                    else table2.setValueAt(ii_false, j, 3);
//                    table2.

                    table2.setValueAt(value_current, j, 4);
                }


            }
        });
        Btn_Reset2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                start_current_test();
                reset_start_test();
            }
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
        Search_Ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                service.wt_search(table1, field_search1, field_search2);
            }
        });
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

    private void reset_start_test(){
        panel_result.setVisible(false);
        panel_test.setVisible(false);
        panel_test2.setVisible(false);
        panel_option_questions.setVisible(true);
        panel_option_questions.getTopLevelAncestor().setSize(720,500);
    }

    private void parameter_enable(boolean[] s){
        spinner1.setEnabled(s[0]);
        spinner2.setEnabled(s[1]);
        spinner3.setEnabled(s[2]);
        table1.setEnabled(s[3]);
    }

    private void creating_query(String n) {
        if (list_questions.size() < service.ja_words.length()) {

            if (Btn_choice_answer.getModel().isEnabled()) {
                Btn_Next_question.setEnabled(false);
            }
            JRadioButton[] rb = {RB_Option1, RB_Option2, RB_Option3, RB_Option4};
            for (int i = 0; i < 4; i++) {
                group_test.add(rb[i]);
            }

//          select default answer
//            group_test.setSelected(jRB_Count_questions.getModel(), true);
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
                if (question.equals(n)) {
                    list_answers.add(answer);
                }
            }
            for (int i = 0; i < service.ja_words.length(); i++) {
                if (!(list_answers.get(0).equals(service.list_answers_all.get(i)))) {
                    list_answers.add(service.list_answers_all.get(i));
                }
                if (list_answers.size() == 4) {
                    break;
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

        public MenuUI(){
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

            mi_edit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ToolsUI toolsUI = new ToolsUI();
                    toolsUI.showFrame();
                }
            });

            mi_design.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    DesignUI desingUI = new DesignUI();
                    desingUI.showFrame();
                }
            });

            menuItem1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    lang_turn_on("en");
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Restart_program").toString(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            menuItem2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    lang_turn_on("ua");
                    JOptionPane.showMessageDialog(
                            null,
                            lang.SetLanguage("OPM_Restart_program").toString(),
                            lang.SetLanguage("OPM_Title").toString(),
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            mi_description.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    DescriptionUI descUI = new DescriptionUI();
                    descUI.showFrame();
                }
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

//             Service.SetLanguage l = new Service.SetLanguage(service.current_path[1]);

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

    public void showFrame() {
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
