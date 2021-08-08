package com.hellionbots.Plugins;

import java.util.concurrent.ExecutionException;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.user.User.ProfilePic;
import com.hellionbots.InstaX;
import com.hellionbots.Master;
import com.hellionbots.Helpers.credentials;
import java.io.IOException;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class info extends InstaX implements Master {
    Message m;

    @Override
    public void handleRequests(Update update, String cmd) {
        String t = update.getMessage().getText().replace(getHandler() + "info ", "");
        if (cmd.equalsIgnoreCase(getHandler() + "info " + t)) {
            String username = new credentials().getUsername(update);
            String password = new credentials().getPass(update);
            m = sendMessage(update, "Getting User Info...");
            if (username != null && password != null) {
                getInfo(update, t, username, password);
            } else {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId(update));
                editMessageText.setMessageId(m.getMessageId());
                editMessageText.setText(
                        "Please set your Username and Password in order to use the Bot.\nType /setcred to enter your credential's");
            }
        }
    }

    public void getInfo(Update update, String t, String username, String password) {
        String info = "";
        IGClient client;
        try {
            long start = System.nanoTime();
            client = IGClient.builder().username(username).password(password).login();

            ProfilePic pic = client.actions().users().findByUsername(t).get().getUser().getHd_profile_pic_url_info();
            /*
            URL url = new URL(pic.url);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            */

            SendPhoto photo = new SendPhoto(chatId(update), new InputFile(pic.url));

            info += "Name : " + client.actions().users().findByUsername(t).get().getUser().getFull_name() + "\n";
            info += "Username : " + client.actions().users().findByUsername(t).get().getUser().getUsername()
                    + "\n";
            info += "Bio : " + client.actions().users().findByUsername(t).get().getUser().getBiography() + "\n";
            info += "Followers : "
                    + client.actions().users().findByUsername(t).get().getUser().getFollower_count() + "\n";
            info += "Following : "
                    + client.actions().users().findByUsername(t).get().getUser().getFollowing_count() + "\n";
            /*info += "Extrenal Link : [Link]("
                    + client.actions().users().findByUsername(username).get().getUser().getExternal_url() + ")\n";*/
            long end = System.nanoTime();
            photo.setCaption("Information Fetched in "+((end-start)*Math.pow(10, -9)+"").substring(0, 3)+"s\n\n"+info);
            DeleteMessage deleteMessage = new DeleteMessage(chatId(update), m.getMessageId());
            
            execute(deleteMessage);
            execute(photo);
            
        } catch (InterruptedException | ExecutionException | TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }

}
