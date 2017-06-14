import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

class Service{
    ArrayList<String> list_questions_all = new ArrayList<>();
    ArrayList<String> list_answers_all = new ArrayList<>();
    DefaultTableModel[] model = new DefaultTableModel[4];
    JSONArray ja_words;
    JSONObject[] jo_number_pair;
    String[] current_path = new String[2];

    String nc_word_en = "Word";
    String nc_translate_en = "Translate";
    String nc_type_en = "Type";

    String nc_word_ua = "Слово";
    String nc_translate_ua = "Переклад";
//    String nc_type_ua = "Тип";

    String[] name_cols =  {"✓", "№", nc_word_en, nc_translate_en, nc_type_en, "W*", "T*"};
    String[] word_types = {"nn", "vr", "aj", "av", "pn", "pp", "oth"};

    void table(int number, boolean[] canEdit, String lib){
        try {
            dir_vocabulary_file(0);
            dir_vocabulary_file(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String str_words = content_file(current_path[0]);
        JSONObject ja_words2 = new JSONObject(str_words).getJSONObject("words_type");

        if(number == 2){
            str_words = "";
            ja_words = new JSONArray(str_words);
        }
        else {
            JSONObject jo_words = new JSONObject(str_words);
            ja_words = new JSONArray(jo_words.getJSONArray(lib).toString());
        }

        jo_number_pair = new JSONObject[ja_words.length()];

        JSONObject jo_wtt = new JSONObject();
        if(Objects.equals(lib, "words_new")){
            jo_wtt = (JSONObject) ja_words2.get("words_new");

        }
        else if(Objects.equals(lib, "words_studied")){
            jo_wtt = (JSONObject) ja_words2.get("words_studied");
        }

        model[number] = new DefaultTableModel((Object[]) new Language(current_path[1]).SetLanguage("TC_name"), ja_words.length())
        {
            public Class<?> getColumnClass(int column){
                switch(column){
                    case 0:
                        return Boolean.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return String.class;
                    case 5:
                        return ImageIcon.class;
                    case 6:
                        return ImageIcon.class;
                    default:
                        return String.class;
                }
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };

        ImageIcon im = new ImageIcon("");

        JCheckBoxMenuItem[] jcbmi = new JCheckBoxMenuItem[word_types.length];

        for(int i=0; i<word_types.length;i++){
            jcbmi[i] = new JCheckBoxMenuItem(word_types[i]);
        }

        for (int i = 0; i < ja_words.length(); i++) {
            jo_number_pair[i] = (JSONObject)ja_words.get(i);
            String key = jo_number_pair[i].names().getString(0);
            String val = jo_number_pair[i].getString(key);
            list_questions_all.add(key);
            list_answers_all.add(val);
            model[number].setValueAt(false, i, getCCI(""));
            model[number].setValueAt(i+1, i, getCCI("№"));
            model[number].setValueAt(key, i, getCCI("Word"));
            model[number].setValueAt(val, i, getCCI("Translate"));
            String wt_buffer = "";
            for (JCheckBoxMenuItem aJcbmi : jcbmi) {
                JSONArray ja_wtt = (JSONArray) jo_wtt.get(aJcbmi.getText());
                for (int k = 0; k < ja_wtt.length(); k++) {
                    if ((int) ja_wtt.get(k) == i) {
                        wt_buffer += aJcbmi.getText() + " ";
                        model[number].setValueAt(wt_buffer, i, getCCI("Type"));
                    }
                }
            }
            model[number].setValueAt(im, i, getCCI("W*"));
            model[number].setValueAt(im, i, getCCI("T*"));
        }
    }

    void dir_vocabulary_file(int file_type) throws IOException{
        String[] lists = new String[2];
        lists[0] = "Es-t_vocabulary.json";
        lists[1] = "Es-t_settings.json";
        String OS = System.getProperty("os.name").toLowerCase();
        String win_path_D = "D:\\"+ lists[file_type] +"";
        String win_path_C = "C:\\"+ lists[file_type] +"";
        String ubuntu_path = lists[file_type] +"";

        File win_file_D = new File(win_path_D);
        File win_file_C = new File(win_path_C);
        File ubuntu_file = new File(ubuntu_path);

        if(OS.contains("windows")){
            if(win_file_D.canExecute()){
                current_path[file_type] = win_path_D;
            }
            else if(win_file_C.canExecute()){
                current_path[file_type] = win_path_C;
            }
            else{
                if(!win_file_D.canExecute()){
                    array_content_words(win_path_D, file_type);
                    current_path[file_type] = win_path_D;
                }
                else if(!win_file_C.canExecute()){
                    array_content_words(win_path_C, file_type);
                    current_path[file_type] = win_path_C;
                }
            }
        }

        else if(OS.contains("nix") ||
                OS.contains("nux") ||
                OS.indexOf("aix") > 0){
            if(ubuntu_file.canExecute()){
                current_path[file_type] = ubuntu_path;
            }
            else if(!ubuntu_file.canExecute()){
                array_content_words(ubuntu_path, file_type);
                current_path[file_type] = ubuntu_path;
            }
        }
    }

    final static class Language{
        JSONObject jo_i18n = new JSONObject();


        String strLang;

        Language(String pth){
            JSONArray ja = new JSONArray(new Service().content_file(pth));
            JSONObject jo = ja.getJSONObject(0);
            String j1 = jo.names().getString(0);
            String j2 = jo.getString(j1);
            if(j2.equals("ua")){strLang = "ua";}
            else if(j2.equals("en")){strLang = "en";}
            else strLang = "en";

            jo_i18n = new JSONObject().put("uaen", new JSONObject().
                put("TC_name", new JSONObject().put("ua", new Object[] {"✔", "№", "Слово", "Переклад", "Тип", "С*", "П*"}).
                                                put("en", new Service().name_cols)).
                put("TC_name2",new JSONObject().put("ua", new String[] {"№", "Слово", "Переклад", "", "Відповідь"}).
                                                put("en", new String[] {"№", "Word", "Translate", "", "True answer"})).
                put("Lbl_Title_name", new JSONObject().put("ua","Вибір запитань").put("en", "Choice questions")).
                put("RB_Count_questions_name", new JSONObject().put("ua","Кількість запитань").
                                                                put("en", "Count questions")).
                put("RB_Choice_from_List_name", new JSONObject().put("ua","Вибір з таблиці: ").
                                                                 put("en", "Choice from table")).
                put("RB_Scope_questions_name", new JSONObject().put("ua","В межах з:").put("en", "Scope from:")).
                put("Lbl_sp2_to_sp3_name", new JSONObject().put("ua","до").put("en", "to")).
                put("Lbl_Type_test", new JSONObject().put("ua","Тип тесту: ").put("en", "Test type")).
                put("CB_Elements_name", new JSONObject().put("en", new String[] {"Written", "Spoken"}).
                                                         put("ua", new String[] {"Письмовий", "Усний"})).
                put("Btn_Start_test_name", new JSONObject().put("ua","Старт").put("en", "Start")).
                put("Btn_Ok_name", new JSONObject().put("ua","Підтвердити").put("en", "Ok")).
                put("Btn_Next_question_name", new JSONObject().put("ua","Наступне запитання").put("en",  "Next")).
                put("Lbl_Result_name", new JSONObject().put("ua","Результат").put("en", "Result")).
                put("Btn_Cancel_test", new JSONObject().put("ua","Відмінити тест").put("en", "Cancel test")).
                put("Lbl_True_answers_name", new JSONObject().put("ua","Правильно").put("en", "True answer")).
                put("Lbl_False_answers_name", new JSONObject().put("ua","Помилки").put("en", "False answer")).
                put("Btn_Reset_test", new JSONObject().put("ua","Запустити знову").put("en", "Restart test")).
                put("OPM_Clear_true_answers", new JSONObject().put("ua","Прибрати правильні?").put("en", "Clear true?")).
                put("Btn_go_to_written_test", new JSONObject().put("ua","Перейти до письмого тесту").
                                                               put("en", "Go to written test")).
                put("Btn_go_to_spoken_test", new JSONObject().put("ua","Перейти до усного тесту").
                                                                put("en", "Go to spoken test")).
                put("Btn_Choose_test", new JSONObject().put("ua","Виберіть тест").put("en", "Choose test")).
                put("Btn_Additionally", new JSONObject().put("ua","Додатково").put("en", "Additionally")).
                put("M_Settings_name", new JSONObject().put("ua","Налаштування").put("en", "Settings")).
                put("M_About_name", new JSONObject().put("ua","Про програму").put("en", "About")).
                put("M_Lang_name", new JSONObject().put("ua","Мова інтерфейсу").put("en", "Language")).
                put("OPM_Restart_program", new JSONObject().put("ua","Перезапустіть програму для змін").
                                                            put("en", "Restart program for changes")).
                put("MI_Vocabulary_name", new JSONObject().put("ua","Словник").put("en", "Vocabulary")).
                put("MI_Design_name", new JSONObject().put("ua","Дизайн").put("en", "Design")).
                put("MI_Description", new JSONObject().put("ua","Детальніше про програму").put("en", "Description")).
                put("Btn_Duplicates_name", new JSONObject().put("ua","Перевірити наявність повторень").
                                                            put("en", "Check for duplicates")).
                put("Btn_Add_name", new JSONObject().put("ua","Додати").put("en", "Add")).
                put("Btn_Del_name", new JSONObject().put("ua","Видалити").put("en", "Del")).
                put("Btn_Save_name", new JSONObject().put("ua","Зберегти").put("en", "Save")).
                put("Btn_Moving_to_studied_words", new JSONObject().put("ua","Перемістити до вивчених слів").
                                                                    put("en", "Move to the studied words")).
                put("OPM_Title", new JSONObject().put("ua","Повідомлення").put("en", "Message")).
                put("OPM_Text_question_null", new JSONObject().put("ua","Запитань не вибрано!").
                                                               put("en", "Question is not selected!")).
                put("OPM_Line_not_completed", new JSONObject().put("ua","Рядок ще не заповнено!").
                                                               put("en", "The line is not yet completed")).
                put("OPM_Are_you_sure", new JSONObject().put("ua","Ви впевнені?").put("en", "Are you sure?")).

                put("OPM_Title_removing", new JSONObject().put("ua","Видалення ").put("en", "Deleting ")).
                put("OPM_Title_moving", new JSONObject().put("ua","Переміщення ").put("en", "Moving ")).
                put("OPM_Words_repositioned", new JSONObject().put("ua"," було переміщено!").
                                                               put("en", " Words have been repositioned")).
                put("OPM_Clear_answers", new JSONObject().put("ua","Прибрати відмічені").put("en", "Clear marked")).

                put("CB_Checking_all", new JSONObject().put("ua","Позначити всі").put("en", "Сhecking all")).
                put("OPM_Clear_answers", new JSONObject().put("ua","Видалити відмічені").put("en", "Clear marked")).
                put("OPM_Items", new JSONObject().put("ua"," ел. ").put("en", " item(s)")).
                put("OPM_Words_removed", new JSONObject().put("ua","було видалено!").
                                                          put("en", "have been removed")).
                put("OPM_Words_not_removed", new JSONObject().put("ua","Слова не видалено!").
                                                              put("en", "Words are not removed")).
                put("OPM_Words_success_saved", new JSONObject().put("ua","Записи успішно збережено").
                                                                put("en","Words success saved")).
                put("OPM_Words_not_saved", new JSONObject().put("ua","Записи не збережено!").
                                                            put("en", "Words not saved")).
                put("Btn_Answers_all", new JSONObject().put("ua","Показати всі відповіді").
                                                        put("en", "Show answers all")).
                put("OPM_Minimum_5_words",
                        new JSONObject().put("ua","Ви повинні мати мінімум 5 нових слів для запуску тестів").
                                         put("en", "You must have minimum 5 new words for run tests")).
                put("OPM_Already_exist", new JSONObject().put("ua","вже існує в таблиці").
                                                          put("en", "Word already exists")));
        }

        Object SetLanguage(String str){
           return jo_i18n.getJSONObject("uaen").getJSONObject(str).get(strLang);
        }
    }


    void wt_search(JTable tab, JTextField tf1, JTextField tf2, JTextField tf3){
        for(int i=0; i< tab.getRowCount(); i++){
            String s_name = tab.getValueAt(i,getCCI("Word")).toString().toLowerCase();
            String s_trans = tab.getValueAt(i,getCCI("Translate")).toString().toLowerCase();
            String s_type;
            if(tab.getValueAt(i,getCCI("Type"))==null) {
                s_type = null;
            }
            else{
                s_type = tab.getValueAt(i, getCCI("Type")).toString().toLowerCase();
            }

            if(!tf1.getText().equals("")) {
                if (s_name.contains(tf1.getText().toLowerCase())) {
                    tab.changeSelection(i, getCCI("Word"), true, false);
                }
            }
            else if(!tf2.getText().equals("")) {
                if (s_trans.contains(tf2.getText().toLowerCase())) {
                    tab.changeSelection(i, getCCI("Translate"), true, false);
                }
            }
            else if(!tf3.getText().equals("")) {
                if(s_type != null) {
                    if (s_type.contains(tf3.getText().toLowerCase())) {
                        tab.changeSelection(i, getCCI("Type"), true, false);
                    }
                }
            }
        }
    }

//  getCCI
    int getCCI(String col_name) {
        int current_col_index = 0;
        for(int i=0; i<name_cols.length; i++) {
            if(col_name.toLowerCase().equals(name_cols[i].toLowerCase())) {
                current_col_index = i;
            }
        }
        return current_col_index;
    }


    final static class SetColor{
        Service service = new Service();
        String val;
        ArrayList<String> list_colors = new ArrayList<>();
        JSONArray ja_content_all;

        JSONObject jo_colors;
        JSONArray ja_colors;
        JSONObject jo_colors2;

        SetColor(String pth, String key) {
            String content_file = new Service().content_file(pth);
            ja_content_all = new JSONArray(content_file);
            jo_colors = ja_content_all.getJSONObject(1);
//            ja_colors = jo_colors.getJSONObject(jo_colors.names().getString(0));
            ja_colors = jo_colors.getJSONArray("color");
            for(int i=0; i<ja_colors.length(); i++){
                if(Objects.equals(ja_colors.getJSONObject(i).names().get(0).toString(), key)){
                    val = ja_colors.getJSONObject(i).get(key).toString();
                }
            }
//            val = ja_colors.getString(key);
        }
    }

    String content_file(String pth){
        String thisLine;
        String str_words = "";
//        String val = "";
//        if(new File(pth).canExecute()){
        try{
            // open input stream test.txt for reading purpose.
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pth), "windows-1251"));
            while ((thisLine = br.readLine()) != null) {
                str_words += thisLine;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return str_words;
    }

    private void array_content_words(String path, int list_numb) throws IOException {
        if(list_numb == 0){
            JSONObject start_list = new JSONObject();
            JSONArray words_studied = new JSONArray();
            JSONArray words_new = new JSONArray();
            JSONObject words_type = new JSONObject();

            String[] words =
                    {"quiet", "broke", "mistakes", "turn", "stay",
                            "mind", "explain", "calm", "still", "become"};
            String[] trans =
                    {"тихо", "зламати", "помилки", "поворот", "залишитись",
                            "дбати", "пояснювати", "спокійний", "до цих пір", "стати/відповідати"};

            for (int i = 0; i < 10; i++) {
                words_new.put(new JSONObject().put(words[i], trans[i]));
            }

            int[][] num1 = new int[word_types.length][];
            int[][] num2 = new int[word_types.length][];

            num1[0] = new int[]{0,2,3,4,5,7,8};
            num1[1] = new int[]{2,3,4,5,6,7,9};
            num1[2] = new int[]{1,7};
            num1[3] = new int[]{0,1,8};
            num1[4] = new int[]{};
            num1[5] = new int[]{};
            num1[6] = new int[]{8};

            for(int i=0; i<word_types.length; i++){
                num2[i] = new int[]{};
            }

            JSONObject jo_wt1 = new JSONObject();
            JSONObject jo_wt2 = new JSONObject();
            for(int i=0; i<word_types.length;i++) {
                jo_wt1.put(word_types[i], num1[i]);
                jo_wt2.put(word_types[i], num2[i]);
            }
            words_type.put("words_new", jo_wt1);
            words_type.put("words_studied", jo_wt2);

            start_list.put("words_studied", words_studied);
            start_list.put("words_new", words_new);
            start_list.put("words_type", words_type);

            write_content_in_file(path, start_list, "create");
        }
        else{
            JSONArray ob = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put("lang","ua");

            JSONArray ja_color_item = new JSONArray();
            JSONObject ja2 = new JSONObject();
            JSONObject jo_colors_all = new JSONObject();

            ja_color_item.put(0, new JSONObject().put("MainUI","#EEEEEE"));
            ja_color_item.put(1, new JSONObject().put("TestUI 1(variants)","#EEEEEE"));
            ja_color_item.put(2, new JSONObject().put("TestUI 2","#EEEEEE"));
            ja_color_item.put(3, new JSONObject().put("ToolsUI","#EEEEEE"));
            ja_color_item.put(4, new JSONObject().put("MainUI Table","#CFB685"));
            ja_color_item.put(5, new JSONObject().put("TestUI 2 Table","#CFB685"));
            ja_color_item.put(6, new JSONObject().put("ToolsUI Table","#CFB685"));

            jo_colors_all.put("color", ja_color_item);

            ob.put(jo);
            ob.put(jo_colors_all);
            write_content_in_file(path, ob, "create");
        }
    }

    void write_content_in_file(String path, Object obj, String code_write) throws IOException {
        File file =new File(path);
        if (!file.exists()) {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "windows-1251"));
            write_content_in_file_details(obj, out);
            out.close();
        }
        else
            if(Objects.equals(code_write, "edit")){
                Writer out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(path), "windows-1251"));
                write_content_in_file_details(obj, out);
                out.close();
            }
    }

    private void write_content_in_file_details(Object obj, Writer out){
        try {
            if (Objects.equals(obj.getClass().getName(), "org.json.JSONObject")) {
                JSONObject obj1 = (JSONObject) obj;
                out.write(obj1.toString(2));
            } else if (Objects.equals(obj.getClass().getName(), "org.json.JSONArray")) {
                JSONArray obj1 = (JSONArray) obj;
                out.write(obj1.toString(2));
            }
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(), "Message",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
