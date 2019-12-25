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
//import java.util.Timer;
//import java.util.TimerTask;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.ImageHelper;
import util.MathHelper;

/**
 * @author Thinh Pham
 */
public class Main extends MIDlet {
    
//#if ScreenWidth == 400
//#     public static final int SCREEN_WIDTH = 400;
//#     public static final int SCREEN_HEIGHT = 240;
//#elif ScreenWidth == 320
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 240;
//#endif
    
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
    
    private int templeMarginTop;
    public String playerName = "";
    public boolean displayAds = true;
//    private Timer timer;
    private GameScene child;
    
    protected void startApp() {
        // change device orientation to landspace
        try {
            Class.forName("com.nokia.mid.ui.orientation.Orientation");
            Orientation.setAppOrientation(Orientation.ORIENTATION_LANDSCAPE);
        }  catch (ClassNotFoundException ex) { }
        // generate default setting if needed, then check time after ads clicked
        try {
            RecordStore rs = RecordStore.openRecordStore(RMS_SETTING, true);
            if (rs.getNumRecords() != 2) {
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(RMS_SETTING);
                rs = RecordStore.openRecordStore(RMS_SETTING, true);
                byte[] data = MathHelper.randomDeviceId();
                rs.addRecord(data, 0, data.length); // line 1: deviceid
                data = "0".getBytes();
                rs.addRecord(data, 0, data.length); // line 2: ads click time
            } else {
                long adsTime = Long.parseLong(new String(rs.getRecord(RMS_SETTING_ADSTIME)));
                long currentTime = Calendar.getInstance().getTime().getTime();
                displayAds = (currentTime - adsTime > 86400000);
            }
            rs.closeRecordStore();
        } catch (RecordStoreException ex) { }
        // load user name from record store
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            playerName = new String(rs.getRecord(RMS_USER_NAME));
            rs.closeRecordStore();
        } catch (RecordStoreException ex) { }
        // set splash scene as start point
        child = new SplashScene(this);
        Display.getDisplay(this).setCurrent(child);
        
//        // runs gc each 0.5 secs
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                System.gc();
//            }
//        }, 0, 500);
    }
    
    protected void pauseApp() { }
    
    protected void destroyApp(boolean unconditional) { }
    
    public void bannerPressed() {
        displayAds = false;
        long curTime = Calendar.getInstance().getTime().getTime();
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_SETTING, false);
            byte[] data = Long.toString(curTime).getBytes();
            rs.setRecord(RMS_SETTING_ADSTIME, data, 0, data.length); // line 2: ads click time
            rs.closeRecordStore();
        }
        catch (RecordStoreException ex) { }
    }
    
    public void gotoMainMenu() {
        child.dispose();
        child = new MenuScene(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoIslandMap() {
        child.dispose();
        child = new IslandScene(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoHelp() {
        child.dispose();
        child = new HelpScene(this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoTemple(int templeId, boolean isMarginTop) {
        child.dispose();
        child = isMarginTop ?
                new TempleScene(this, templeId, templeMarginTop) :
                new TempleScene(this, templeId);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoPlay(int puzzleId, int templeId, int templeMarginTop) {
        this.templeMarginTop = templeMarginTop;
        child.dispose();
        child = new PlayScene(puzzleId, templeId, this);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void openTemple(int templeId) {
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, false);
            int openedTemple = Integer.parseInt(new String(rs.getRecord(RMS_USER_OPENEDTEMPLE))) + 1;
            byte[] data = Integer.toString(templeId).getBytes();
            rs.setRecord(RMS_USER_NEWTEMPLE, data, 0, data.length); // update new opened templeId
            data = Integer.toString(openedTemple).getBytes();
            rs.setRecord(RMS_USER_OPENEDTEMPLE, data, 0, data.length); // update number of opened temples
            rs.closeRecordStore();
        }
        catch (RecordStoreException ex) { }
    }
}
