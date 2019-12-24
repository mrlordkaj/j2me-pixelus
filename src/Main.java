/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.nokia.mid.ui.orientation.Orientation;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.ImageHelper;
import util.MathHelper;

/**
 * @author Thinh Pham
 */
public class Main extends MIDlet {
    public static final int SCREENSIZE_WIDTH = 400;
    public static final int SCREENSIZE_HEIGHT = 240;
    public static final String RMS_SETTING = "setting";
    public static final int RMS_SETTING_DEVICEID = 1;
    public static final int RMS_SETTING_ADSTIME = 2;
    public static final String RMS_USER = "user";
    public static final int RMS_USER_NAME = 202;
    public static final int RMS_USER_OPENEDTEMPLE = 203;
    public static final int RMS_USER_SOLVEDPUZZLE = 204;
    public static final int RMS_USER_NEWTEMPLE = 205;
    public static final int RMS_USER_TEMPLESTATISTIC = 206;
    public static final String NAX_CODE = "Openitvn_Forum_Nokia";
    public static final Sprite loadingSprite = new Sprite(ImageHelper.loadImage("/images/juggling.png"), 20, 26);
    private int templeMarginTop;
    
    public String playerName = "";
    public boolean displayAds = true;
    private Timer timer;
    private GamePage child;
    
    public void startApp() {
        try {
            Class.forName("com.nokia.mid.ui.orientation.Orientation");
            Orientation.setAppOrientation(Orientation.ORIENTATION_LANDSCAPE);
        }
        catch (ClassNotFoundException e) {}
        
        //neu chua co setting thi sinh ra mac dinh, dong thoi kiem tra thoi gian bam quang cao
        defaultRecordStore();
        loadUserName();
        loadingSprite.setPosition(154, Main.SCREENSIZE_HEIGHT / 2);
        
        child = new Splash(this);
        Display.getDisplay(this).setCurrent(child);
        
        //0.5 giay don rac mot lan
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.gc();
            }
        }, 0, 500);
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
    
    private void defaultRecordStore() {
        try {
            RecordStore rs = RecordStore.openRecordStore(RMS_SETTING, true);
            if (rs.getNumRecords() != 2) {
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(RMS_SETTING);

                rs = RecordStore.openRecordStore(RMS_SETTING, true);
                byte[] writer = MathHelper.RandomDeviceId().getBytes();
                rs.addRecord(writer, 0, writer.length); //dong 1 la deviceid
                writer = "0".getBytes();
                rs.addRecord(writer, 0, writer.length); //dong 2 la thoi gian bam quang cao
            } else {
                long adsTime = Long.parseLong(new String(rs.getRecord(RMS_SETTING_ADSTIME)));
                long currentTime = Calendar.getInstance().getTime().getTime();
                if(currentTime - adsTime > 86400000) displayAds = true;
                else displayAds = false;
            }
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {}
    }
    
    public void bannerPressed() {
        displayAds = false;
        long currentTime = Calendar.getInstance().getTime().getTime();
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_SETTING, false);
            byte[] writer = Long.toString(currentTime).getBytes();
            rs.setRecord(RMS_SETTING_ADSTIME, writer, 0, writer.length); //dong 2 la thoi gian bam quang cao
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {}
    }
    
    public void gotoMainMenu() {
        child.dispose();
        child = new MainMenu(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoIslandMap() {
        child.dispose();
        child = new IslandMap(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoHelp() {
        child.dispose();
        child = new Help(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoTemple(int templeId, boolean isMarginTop) {
        child.dispose();
        if(isMarginTop) child = new Temple(templeId, templeMarginTop, this);
        else child = new Temple(templeId, this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoPlay(int puzzleId, int templeId, int _templeMarginTop) {
        templeMarginTop = _templeMarginTop;
        child.dispose();
        child = new Play(puzzleId, templeId, this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    private void loadUserName() {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            playerName = new String(rs.getRecord(RMS_USER_NAME));
        } catch (RecordStoreException ex) {}
    }
    
    public void openTemple(int templeId) {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            int openedTemple = Integer.parseInt(new String(rs.getRecord(RMS_USER_OPENEDTEMPLE))) + 1;
            byte[] writer = Integer.toString(templeId).getBytes();
            rs.setRecord(RMS_USER_NEWTEMPLE, writer, 0, writer.length); //cập nhật id của đền mới mở
            writer = Integer.toString(openedTemple).getBytes();
            rs.setRecord(RMS_USER_OPENEDTEMPLE, writer, 0, writer.length); //cập nhật số lượng temple đã mở
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {}
    }
}
