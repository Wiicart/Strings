package com.pedestriamc.strings;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class StringsLogger extends Handler{

    private FileWriter writer;

    public StringsLogger(Strings strings, File file){
        try{
            this.writer = new FileWriter(file);
        }catch(IOException e){
            e.printStackTrace();
            Bukkit.getLogger().info("[Strings] Unable to enable logger.");
        }
    }

    public void write(String str){
        try{
            writer.write("[" + LocalDateTime.now() + "] " + str);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
