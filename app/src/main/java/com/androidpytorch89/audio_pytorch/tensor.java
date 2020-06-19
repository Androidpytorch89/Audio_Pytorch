package com.androidpytorch89.audio_pytorch;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

class label_names {
    public static String[] arrayLables= new String[]{"Coughing","Sneezing","Neither"};
}

public class tensor {

    Module module = null;
    public int cou=0, sne=0, nee=0;
    EditText co, sn, ne;
    ArrayList<Integer> num= new ArrayList<>();

    public static String audClassifier(PyObject pyf, Module module){
        float[] array = pyf.callAttr("test").toJava(float[].class);
        int ln = Array.getLength(array);
        array = (float[]) resizeArray(array, 220500);
        final long[] shape = new long[]{1, 1, array.length};
        final Tensor inputTensor = Tensor.fromBlob(array, shape);
        /*if(inputTensor!=null)
            t.setText(inputTensor.toString());
            //t.setText(pyf.callAttr("locate").toString());
        else
            t.setText("null");*/

        //co.setText(p.toString());

        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();


        final float[] scores = outputTensor.getDataAsFloatArray();


        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {

            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        label_names obj= new label_names();
        String className = obj.arrayLables[maxScoreIdx];

        return className;

        /*if (className == "Coughing")
        {
            cou+=1;
        }
        if (className== "Sneezing")
        {
            sne+=1;

        }
        if (className == "Neither")
        {
            nee=+1;
        }

        co.setText("Coughing : "+ cou);
        sn.setText("Sneezing : "+ sne);
        ne.setText("Neither : "+nee);*/
    }

    private class  updateDatabase extends AsyncTask<Void,Void,Void>
    {

        private DatabaseHelper helper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            helper = new DatabaseHelper(tensor.this);

        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            ContentValues values = new ContentValues();
            int fc = 1, fina=0;
            int i = 0;
            SQLiteDatabase database = helper.getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT PROBLEM, COUNT FROM RECORD", new String[]{});
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {

                String name = cursor.getString(0);
                Integer count = cursor.getInt(1);
                Log.i("column", name);

                fina=num.get(i);
                count=count+fina;

                if (name=="Cough")
                {
                    database.update("RECORD", values, "_id= ?", new String[]{"1"});
                    co.setText("Coughing" + count);
                }
                if (name=="Sneeze")
                {
                    database.update("RECORD", values, "_id= ?", new String[]{"2"});
                    sn.setText("Sneezing" + count);
                }
                if (name=="Other")
                {
                    database.update("RECORD", values, "_id= ?", new String[]{"3"});
                    ne.setText("Other" + count);
                }
                i++;



            }while(cursor.moveToNext());

            return null;
        }
    }

    private static Object resizeArray (Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0)
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        return newArray;
    }
}