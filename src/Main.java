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

import util.GameScene;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.StringHelper;

/**
 * @author Thinh Pham
 */
public final class Main extends MIDlet {
    
//#if ScreenWidth == 400
//#     public static final int SCREEN_WIDTH = 400;
//#     public static final int SCREEN_HEIGHT = 240;
//#elif ScreenWidth == 320
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 240;
//#endif
    
    public static final String RMS_SETTING = "setting";
    public static final int RMS_SETTING_DEVICEID = 1;
    public static final String RMS_USER = "user";
    public static final int RMS_USER_NAME = 202;
    public static final int RMS_USER_OPENEDTEMPLE = 203;
    public static final int RMS_USER_SOLVEDPUZZLE = 204;
    public static final int RMS_USER_NEWTEMPLE = 205;
    public static final int RMS_USER_TEMPLESTATISTIC = 206;
    public static final String NAX_CODE = "Openitvn_Forum_Nokia";
    
    private int templeMarginTop;
    public String playerName = "";
//    private Timer timer;
    private GameScene child;
    
    private static Main instance;
    
    public static Main getInstance() {
        return instance;
    }
    
    public Main() {
        instance = Main.this;
    }
    
    protected void startApp() {
        // generate default setting if needed, then check time after ads clicked
        try {
            RecordStore rs = RecordStore.openRecordStore(RMS_SETTING, true);
            if (rs.getNumRecords() != 2) {
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(RMS_SETTING);
                rs = RecordStore.openRecordStore(RMS_SETTING, true);
                byte[] data = StringHelper.randomDeviceId();
                rs.addRecord(data, 0, data.length); // line 1: deviceid
                data = "0".getBytes();
                rs.addRecord(data, 0, data.length); // line 2: ads click time
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
        child = new SplashScene();
        Display.getDisplay(this).setCurrent(child);
    }
    
    protected void pauseApp() {
    
    }
    
    protected void destroyApp(boolean unconditional) {
    
    }
    
    public void gotoMainMenu() {
        child.destroy();
        child = new MenuScene();
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoIslandMap() {
        child.destroy();
        child = new IslandScene();
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoHelp() {
        child.destroy();
        child = new HelpScene();
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoTemple(int templeId, boolean isMarginTop) {
        child.destroy();
        child = isMarginTop ?
                new TempleScene(templeId, templeMarginTop) :
                new TempleScene(templeId);
        Display.getDisplay(this).setCurrent(child);
    }
    
    public void gotoPlay(int puzzleId, int templeId, int templeMarginTop) {
        this.templeMarginTop = templeMarginTop;
        child.destroy();
        child = new PlayScene(templeId, puzzleId);
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
