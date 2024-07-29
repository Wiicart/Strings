package com.pedestriamc.strings;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;


public class StringsLogger implements Listener{

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

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        this.write("[JOIN] " + event.getPlayer() + " joined the server from IP " + event.getPlayer().getAddress());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        this.write("[QUIT] " + event.getPlayer() + " left the server.");
    }



}
