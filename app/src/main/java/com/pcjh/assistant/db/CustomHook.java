package com.pcjh.assistant.db;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

/**
 * CustomerServiceAssistant
 * Create   2017/2/21 15:03;
 * @author sz.hua
 */
public class CustomHook implements SQLiteDatabaseHook {
    @Override
    public void preKey(SQLiteDatabase sqLiteDatabase) {
    }
    @Override
    public void postKey(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.rawExecSQL("PRAGMA cipher_migrate;");
    }
}
