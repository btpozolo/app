package com.example.blake.nounsonaphone.data;

import android.provider.BaseColumns;

/**
 * Created by Blake on 6/14/2017.
 */

public class WordContract {

    private WordContract(){}

    public static final class  WordEntry implements BaseColumns{
        public static final String TABLE_NAME = "words";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_WORD = "word";
    }
}
