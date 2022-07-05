package dev._3000IQPlay.experium.features.command.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev._3000IQPlay.experium.features.command.Command;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class QueueCommand
        extends Command {
    public QueueCommand() {
        super("queue", new String[]{"priority, regular"});
    }

    @Override
    public void execute(final String[] commands) {
        if (commands.length == 1 || commands.length == 0) {
            QueueCommand.sendMessage("ayo, specify the type! (priority/regular)");
            return;
        }
        String sURL = "https://2bqueue.info/*"; //just a string

        // Connect to the URL using java's native library
        String adjsaofj = commands[0];
        if (adjsaofj.equalsIgnoreCase("regular")) {
            try {
                URL url = new URL(sURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                String aaaaaa = rootobj.get("regular").getAsString(); //just grab the zipcode
                QueueCommand.sendMessage("Regular queue currently have: " + aaaaaa);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (adjsaofj.equalsIgnoreCase("priority")) {
            try {
                URL url = new URL(sURL);
                URLConnection request = url.openConnection();
                request.connect();
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                String aaaaaa = rootobj.get("prio").getAsString(); //just grab the zipcode
                QueueCommand.sendMessage("Priority queue currently have: " + aaaaaa);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}