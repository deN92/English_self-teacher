import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Service{
//    SetLanguage lang = new SetLanguage();
    ArrayList<String> list_questions_all = new ArrayList<>();
    ArrayList<String> list_answers_all = new ArrayList<>();
    DefaultTableModel[] model = new DefaultTableModel[4];
    JSONArray ja_words;
    JSONObject jo_words;
    JSONObject[] jo_number_pair;
    String[] current_path = new String[2];


    public void table(int number, boolean[] canEdit, String ja_words_temp, String lib){
        try {
            dir_vocabulary_file(0);
            dir_vocabulary_file(1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        String str_words = content_file(current_path[0]);

        if(number == 2){
            str_words = ja_words_temp;
            ja_words = new JSONArray(str_words);
        }
        else {
            jo_words = new JSONObject(str_words);
            ja_words = new JSONArray(jo_words.getJSONArray(lib).toString());
        }
//        ja_words = ja_words.
        jo_number_pair = new JSONObject[ja_words.length()];


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
                        return ImageIcon.class;
                    case 5:
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
        for (int i = 0; i < ja_words.length(); i++) {
            jo_number_pair[i] = (JSONObject)ja_words.get(i);
            String key = jo_number_pair[i].names().getString(0);
            String val = jo_number_pair[i].getString(key);
            list_questions_all.add(key);
            list_answers_all.add(val);
            model[number].setValueAt(false, i, 0);
            model[number].setValueAt(i+1, i, 1);
            model[number].setValueAt(key, i, 2);
            model[number].setValueAt(val, i, 3);
            model[number].setValueAt(im, i, 4);
            model[number].setValueAt(im, i, 5);
        }
    }

    public void dir_vocabulary_file(int file_type) throws IOException{
        String[] lsts = new String[2];
        lsts[0] = "Es-t_vocabulary.json";
        lsts[1] = "Es-t_settings.json";
        String OS = System.getProperty("os.name").toLowerCase();
        String win_path_D = "D:\\"+ lsts[file_type] +"";
        String win_path_C = "C:\\"+ lsts[file_type] +"";
        String ubuntu_path = "/home/"+ lsts[file_type] +"";

        File win_file_D = new File(win_path_D);
        File win_file_C = new File(win_path_C);
        File ubuntu_file = new File(ubuntu_path);

        if(OS.indexOf("windows") >= 0){
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

        else if(OS.indexOf("nix") >= 0 ||
                OS.indexOf("nux") >= 0 ||
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

        public Language(String pth){
            JSONArray ja = new JSONArray(new Service().content_file(pth));
            JSONObject jo = ja.getJSONObject(0);
            String j1 = jo.names().getString(0);
            String j2 = jo.getString(j1);

            int i;
            if(j2.equals("ua")){i = 0;}
            else{ i = 1;}

            Object[][] list = new Object[2][];

            list[0] = new Object[]{
                    new Object[] {"", "№", "Слово", "Переклад","С*", "П*"},
                    new String[] {"№", "Слово", "Переклад","", "Відповідь"},
                    "Вибір запитань", "Кількість запитань", "Вибір з таблиці: ", "В межах з:", "до", "Варіанти відповідей: ",
                    new String[] {"Без варіантів", "Так"},
                    "Підтвердити", "Наступне запитання", "Старт", "Результат",  "Відмінити тест",   "Правильно", "Помилки",
                    "Повернутись до початку", "Налаштування", "Про програму", "Мова інтерфейсу", "Зміни", "Дизайн",
                    "Додати", "Видалити", "Зберегти", "Крок назад", "Крок вперед", "Початк. стан",
                    "Повідомлення", "Запитань не вибрано!", "Рядок ще не заповнено!", " було переміщено!",  " було видалено!",
                    "Слова не видалено!", "Записи успішно збережено", "Записи не збережено!", " вже існує в таблиці ",
                    "Детальніше про програму", "Перезапустіть програму будь-ласка"};

            list[1] = new Object[]{
                    new Object[] {"", "№", "Word", "Translate","W*", "T*"},
                    new String[] {"№", "Word", "Translate","", "True answer"},
                    "Choice questions", "Count questions", "Choice from table", "Scope with:", "to", "Answers options: ",
                    new String[] {"No", "Yes"},
                    "Ok", "Next", "Start", "Result", "Cancel test", "True answer", "False answer",
                    "Return to start", "Settings","About", "Language", "Edit", "Design",
                    "Add", "Del", "Save", "Back", "Next", "Reset",
                    "Message", "Question is not selected!",
                    "The line is not yet completed",  "Words have been repositioned", "Words have been removed",
                    "Words are not removed", "Words success saved", "Words not saved", " Word already exists ",
                    "Description", "Restart program please"};

            String[] str = {
                    "TC_name", "TC_name2", "Lbl_Title_name",
                    "RB_Count_questions_name", "RB_Choice_from_List_name", "RB_Scope_questions_name", "Lbl_sp2_to_sp3_name",
                    "Lbl_Order_question_name", "CB_Elements_name",
                    "Btn_Ok_name", "Btn_Next_question_name", "Btn_Start_test_name", "Lbl_Result_name",
                    "Btn_Cancel_test", "Lbl_True_answers_name", "Lbl_False_answers_name",
                    "Btn_Reset_start_test_name", "M_Settings_name", "M_About_name", "M_Lang_name", "MI_Edit_name", "MI_Design_name",
                    "Btn_Add_name", "Btn_Del_name", "Btn_Save_name", "Btn_Back_step_name", "Btn_Next_step_name", "Btn_Reset_name",
                    "OPM_Title", "OPM_Text_question_null",
                    "OPM_Line_not_completed", "OPM_Words_repositioned", "OPM_Words_removed", "OPM_Words_not_removed", "OPM_Words_success_saved",
                    "OPM_Words_not_saved", "OPM_Already_exists", "MI_Description", "OPM_Restart_program"
            };


            for(int w=0; w< str.length; w++) {
                jo_i18n.put(str[w], list[i][w]);
            }
        }

        public Object SetLanguage(String str){
           return jo_i18n.get(str);
        }
    }


    public void wt_search(JTable tab, JTextField tf1, JTextField tf2){
        for(int i=0; i< tab.getRowCount(); i++){
            String w = tab.getValueAt(i,2).toString().toLowerCase();
            String wt = tab.getValueAt(i,3).toString().toLowerCase();

            if(!tf1.getText().equals("")) {
                if (w.contains(tf1.getText().toLowerCase())) {
                    tab.changeSelection(i, 2, false, false);
                }
            }
            if(!tf2.getText().equals("")) {
                if (wt.contains(tf2.getText().toLowerCase())) {
                    tab.changeSelection(i, 3, false, false);
                }
            }
        }
    }


    final static class SetColor{
        Service service = new Service();
        String val;
        ArrayList<String> list_colors = new ArrayList<>();
        JSONArray ja_content_all;
        JSONObject jo_colors;
        JSONObject ja_colors;

        public SetColor(String pth, String key) {
            String content_file = new Service().content_file(pth);
            ja_content_all = new JSONArray(content_file);
            jo_colors = ja_content_all.getJSONObject(1);
            ja_colors = jo_colors.getJSONObject(jo_colors.names().getString(0));
            val = ja_colors.getString(key);
        }
    }


    public String content_file(String pth){
        String thisLine = null;
        String str_words = "";
        String val = "";
        if(new File(pth).canExecute()){
            try{
                // open input stream test.txt for reading purpose.
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pth), "windows-1251"));
                while ((thisLine = br.readLine()) != null) {
                    str_words +=thisLine;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return str_words;
    }


    private void array_content_words(String path, int list_numb) throws IOException {



        if(list_numb == 0){
            JSONObject start_list = new JSONObject();
            JSONArray words_studied = new JSONArray();
            JSONArray words_new = new JSONArray();
            JSONObject wt = new JSONObject();
            String[] words =
                    {"quiet", "broke", "mistakes", "turn", "stay",
                            "mind", "explain", "calm", "still", "become"};
            String[] trans =
                    {"тихо", "зламати", "помилки", "поворот", "залишитись",
                            "дбати", "пояснювати", "спокійний", "до цих пір", "стати/відповідати"};

            words_studied.put(new JSONObject().put("hello","привіт"));
            words_studied.put(new JSONObject().put("this","цей"));
            for (int i = 0; i < 10; i++) {
                words_new.put(new JSONObject().put(words[i], trans[i]));
            }

            start_list.put("words_studied", words_studied);
            start_list.put("words_new", words_new);
            write_content_in_file(path, start_list);
        }
        else{
            JSONArray ob = new JSONArray();
            JSONObject jo = new JSONObject();
            jo.put("lang","ua");

            JSONObject jo2 = new JSONObject();
            JSONObject ja2 = new JSONObject();
            ja2.put("MainUI_Table_bg","#CFB685");
            ja2.put("ToolsUI_Table_bg","#CFB685");
            ja2.put("MainUI_bg","#EEEEEE");
            ja2.put("TestUI_bg","#EEEEEE");
            ja2.put("ToolsUI_bg","#EEEEEE");
            ja2.put("ColorsUI_bg","#EEEEEE");

            jo2.put("color", ja2);

            ob.put(jo);
            ob.put(jo2);
            write_content_in_file(path, ob);
        }
    }

    public void write_content_in_file(String path, Object obj) throws IOException {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path), "windows-1251"));
            try {
                if(obj.getClass().getName() == "org.json.JSONObject") {
                    JSONObject obj1 = (JSONObject) obj;
                    out.write(obj1.toString(2));
                }
                else if(obj.getClass().getName() == "org.json.JSONArray") {
                    JSONArray obj1 = (JSONArray) obj;
                    out.write(obj1.toString(2));
                }
            } finally {
                out.close();
            }
            System.out.println("File saved!");
        }
                    catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        new Language(current_path[1]).SetLanguage("OPM_Words_success_saved") + "| "+e.getMessage(), "Message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
    }
}
