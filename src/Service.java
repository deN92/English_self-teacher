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
        JSONArray ja_words2 = new JSONArray(new JSONObject(str_words).getJSONArray("words_type").toString());

        if(number == 2){
            str_words = "";
            ja_words = new JSONArray(str_words);
        }
        else {
            JSONObject jo_words = new JSONObject(str_words);
            ja_words = new JSONArray(jo_words.getJSONArray(lib).toString());
        }

        jo_number_pair = new JSONObject[ja_words.length()];

        JSONObject jo_wt1 = (JSONObject) ja_words2.get(0);
        JSONObject jo_wt2 = (JSONObject) ja_words2.get(1);

        JSONObject jo_wtt = new JSONObject();
        if(Objects.equals(lib, "words_new")){
            jo_wtt = (JSONObject) jo_wt1.get("words_new");

        }
        else if(Objects.equals(lib, "words_studied")){
            jo_wtt = (JSONObject) jo_wt2.get("words_studied");
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

        Language(String pth){
            JSONArray ja = new JSONArray(new Service().content_file(pth));
            JSONObject jo = ja.getJSONObject(0);
            String j1 = jo.names().getString(0);
            String j2 = jo.getString(j1);

            int i;
            if(j2.equals("ua")){i = 0;}
            else{ i = 1;}

            Object[][] list = new Object[2][];

            list[0] = new Object[]{
                new Object[] {"✔", "№", "Слово", "Переклад","Тип", "С*", "П*"},
                new String[] {"№", "Слово", "Переклад","", "Відповідь"},
                "Вибір запитань", "Кількість запитань", "Вибір з таблиці: ", "В межах з:", "до", "Варіанти відповідей: ",
                new String[] {"Без варіантів", "Так"}, "Старт",

                "Підтвердити", "Наступне запитання",  "Результат", "Відмінити тест", "Правильно", "Помилки",
                "Відмінити тест", "Запустити знову", "Прибрати правильні",

                "Налаштування", "Про програму", "Мова інтерфейсу", "Перезапустіть програму для змін", "Словник", "Дизайн",
                "Детальніше про програму",

                "Перевірити наявність повторень", "Додати", "Видалити", "Зберегти", "Повідомлення",
                "Запитань не вибрано!", "Рядок ще не заповнено!", " було переміщено!", "Ви впевнені?", "пункт(и)",
                "було видалено!", "Слова не видалено!", "Записи успішно збережено",
                "Записи не збережено!", " вже існує в таблиці "};

            list[1] = new Object[]{
                new Service().name_cols,
                new String[] {"№", "Word", "Translate","", "True answer"},
                "Choice questions", "Count questions", "Choice from table", "Scope from:", "to", "Answers options: ",
                new String[] {"No", "Yes"}, "Start",

                "Ok", "Next",  "Result", "Cancel test", "True answer", "False answer",
                "Cancel test", "Restart test", "Clear true",

                "Settings","About", "Language", "Restart program for changes", "Vocabulary", "Design",
                "Description",

                "Check for duplicates", "Add", "Del", "Save", "Message",
                "Question is not selected!", "The line is not yet completed", "Words have been repositioned",
                "Are you sure?", "item(s)", "have been removed", "Words are not removed", "Words success saved",
                "Words not saved", " Word already exists "};

            String[] str = {
                "TC_name",
                "TC_name2",
                "Lbl_Title_name", "RB_Count_questions_name", "RB_Choice_from_List_name", "RB_Scope_questions_name",
                "Lbl_sp2_to_sp3_name", "Lbl_Order_question_name", "CB_Elements_name", "Btn_Start_test_name",

                "Btn_Ok_name", "Btn_Next_question_name",  "Lbl_Result_name", "Btn_Cancel_test", "Lbl_True_answers_name",
                "Lbl_False_answers_name", "Btn_Cancel_test",  "Btn_Reset_test", "CB_Clear_true_answers",

                "M_Settings_name", "M_About_name", "M_Lang_name", "OPM_Restart_program", "MI_Vocabulary_name",
                "MI_Design_name", "MI_Description",

                "Btn_Duplicates_name", "Btn_Add_name", "Btn_Del_name", "Btn_Save_name", "OPM_Title",
                "OPM_Text_question_null", "OPM_Line_not_completed", "OPM_Words_repositioned", "OPM_Question_delete",
                "OPM_Count_words", "OPM_Words_removed", "OPM_Words_not_removed", "OPM_Words_success_saved",
                "OPM_Words_not_saved", "OPM_Already_exists"
            };

            for(int w=0; w< str.length; w++) {
                jo_i18n.put(str[w], list[i][w]);
            }
        }

        Object SetLanguage(String str){
           return jo_i18n.get(str);
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
//        }
        return str_words;
    }

    private void array_content_words(String path, int list_numb) throws IOException {
        if(list_numb == 0){
            JSONObject start_list = new JSONObject();
            JSONArray words_studied = new JSONArray();
            JSONArray words_new = new JSONArray();
            JSONArray words_type = new JSONArray();

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
            words_type.put(new JSONObject().put("words_new", jo_wt1));
            words_type.put(new JSONObject().put("words_studied", jo_wt2));

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
