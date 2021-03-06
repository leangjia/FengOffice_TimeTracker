package ru.vat78.fotimetracker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ru.vat78.fotimetracker.FOTT_App;
import ru.vat78.fotimetracker.R;
import ru.vat78.fotimetracker.model.FOTT_Member;
import ru.vat78.fotimetracker.views.FOTT_ErrorsHandler;

/**
 * Created by vat on 21.12.2015.
 */
public class FOTT_DBMembers extends FOTT_DBContract {
    private static final String CLASS_NAME = "FOTT_DBMembers";

    private static final String TABLE_NAME = "members";
    private static final String COLUMN_NAME_COLOR = "color";
    private static final String COLUMN_NAME_TYPE = "type";
    private static final String COLUMN_NAME_PATH = "path";
    private static final String COLUMN_NAME_PARENT = "parentid";
    private static final String COLUMN_NAME_LEVEL = "level";
    private static final String COLUMN_NAME_TASKS = "tasks_cnt";

    public static final String SQL_CREATE_ENTRIES =
            CREATE_TABLE + TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    COLUMN_NAME_FO_ID + INTEGER_TYPE + UNIQUE_FIELD + COMMA_SEP +
                    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_COLOR + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PATH + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_PARENT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LEVEL + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_TASKS + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_CHANGED + NUMERIC_TYPE +
                    " );";
    public static final String SQL_DELETE_ENTRIES =
            DROP_TABLE + TABLE_NAME +";";

    public static void rebuild(FOTT_App app){
        app.getDatabase().execSQL(SQL_DELETE_ENTRIES);
        app.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }

    public static void save(FOTT_App app, ArrayList<FOTT_Member> members) {

        if (app.getCurMember() > 0){
            //TODO: if has selected member
        }

        try {
            rebuild(app);

            members.add(generateAnyMember(app));

            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getId() != 0) insert(app, members.get(i));
            }
        }
        catch (Error e){
            app.getError().error_handler(FOTT_ErrorsHandler.ERROR_SAVE_ERROR,CLASS_NAME,e.getMessage());
        }

    }

    private static void insert(FOTT_App app, FOTT_Member member) {
        ContentValues ts = convertToDB(member);
        app.getDatabase().insertOrUpdate(TABLE_NAME, ts);
    }

    private static ContentValues convertToDB(FOTT_Member member) {
        ContentValues res = new ContentValues();
        res.put(COLUMN_NAME_FO_ID, member.getId());
        res.put(COLUMN_NAME_TITLE,member.getName());

        res.put(COLUMN_NAME_PATH,member.getPath());
        res.put(COLUMN_NAME_LEVEL, member.getLevel());
        res.put(COLUMN_NAME_COLOR, member.getColorIndex());

        res.put(COLUMN_NAME_TASKS, member.getTasksCnt());
        //res.put(COLUMN_NAME_CHANGED,member.getChanged().getTime());

        return res;
    }

    private static FOTT_Member generateAnyMember(FOTT_App app) {
        FOTT_Member any = new FOTT_Member(0,app.getString(R.string.any_category));
        any.setPath("");
        any.setColorIndex(Color.TRANSPARENT);
        return any;
    }

    public static ArrayList<FOTT_Member> load (FOTT_App app){
        ArrayList<FOTT_Member> members = new ArrayList<>();

        Cursor memberCursor = app.getDatabase().query(TABLE_NAME + " m",
                new String[]{"m." + COLUMN_NAME_FO_ID,
                        "m." + COLUMN_NAME_TITLE,
                        "m." + COLUMN_NAME_PATH,
                        "m." + COLUMN_NAME_LEVEL,
                        "m." + COLUMN_NAME_COLOR,
                        "(" + FOTT_DBMembers_Objects.getSQLObectsCnt("m." +
                                COLUMN_NAME_FO_ID) + ") AS TaskCnt"},
                null,
                COLUMN_NAME_PATH + " ASC");

        memberCursor.moveToFirst();
        FOTT_Member m;
        FOTT_Member prev = new FOTT_Member(0,"");
        int shownLevel = 1;

        if (!memberCursor.isAfterLast()){
            do {
                long id = memberCursor.getLong(0);
                String name = memberCursor.getString(1);
                String path = memberCursor.getString(2);
                int color = memberCursor.getInt(4);

                m = new FOTT_Member(id, name);
                m.setPath(path);
                m.setColorIndex(color);
                m.setTasksCnt(memberCursor.getInt(5));
                int curLevel = m.getLevel();
                if (curLevel > prev.getLevel()) {prev.setNode(1);}
                m.setVisible(curLevel <= shownLevel);
                if (curLevel < shownLevel) shownLevel = curLevel;

                if (id == app.getCurMember()){
                    //Make visible branch with selected member
                    shownLevel = curLevel;
                    m.setVisible(true);
                    for (int i = members.size() - 1; i>=0 && shownLevel > 1;i--) {
                        FOTT_Member el = members.get(i);
                        el.setVisible(el.getLevel() <= shownLevel);
                        if (el.getLevel() <shownLevel) {
                            el.setNode(2);
                            shownLevel = el.getLevel();
                        }
                    }
                    shownLevel = curLevel;
                }
                members.add(m);

                prev = m;
            } while (memberCursor.moveToNext());
        }
        return members;
    }

    public static FOTT_Member getMemberById(FOTT_App app, long id) {

        FOTT_Member res = new FOTT_Member(0, "");

        if (id > 0) {
            String filter = " " + COLUMN_NAME_FO_ID + " = " + id;
            Cursor memberCursor = app.getDatabase().query(TABLE_NAME + " m",
                    new String[]{"m." + COLUMN_NAME_FO_ID,
                            "m." + COLUMN_NAME_TITLE,
                            "m." + COLUMN_NAME_PATH,
                            "m." + COLUMN_NAME_LEVEL,
                            "m." + COLUMN_NAME_COLOR,
                            "(" + FOTT_DBMembers_Objects.getSQLObectsCnt(String.valueOf(id)) +
                                    ") AS TaskCnt"},
                    filter,
                    COLUMN_NAME_PATH);
            memberCursor.moveToFirst();
            if (!memberCursor.isAfterLast()) {
                res.setId(memberCursor.getLong(0));
                res.setName(memberCursor.getString(1));
                res.setColorIndex(memberCursor.getInt(4));
                res.setTasksCnt(memberCursor.getInt(5));
            }
        }
        return res;
    }
}
