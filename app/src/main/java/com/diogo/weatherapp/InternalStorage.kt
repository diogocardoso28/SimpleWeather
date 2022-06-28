package com.diogo.weatherapp

import android.content.Context
import java.io.*

class InternalStorage {

    companion object {
        @Throws(IOException::class)
        fun writeObject(context: Context, key: String?, `object`: Any?) {
            val fos: FileOutputStream = context.openFileOutput(key, Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(`object`)
            oos.close()
            fos.close()
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        fun readObject(context: Context, key: String?): Any? {
            val fis: FileInputStream = context.openFileInput(key)
            val ois = ObjectInputStream(fis)
            return ois.readObject()
        }
    }
}