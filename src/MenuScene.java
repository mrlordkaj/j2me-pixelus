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

//import InneractiveSDK.IADView;
import util.GameScene;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import util.ButtonSprite;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class MenuScene extends GameScene implements CommandListener {
    
    private static final byte COMMAND_NONE = 0;
    private static final byte COMMAND_PLAY = 1;
    private static final byte COMMAND_EXTRAS = 2;
    private static final byte COMMAND_ABOUT = 3;
    private static final byte COMMAND_QUIT = 4;
    
    private boolean touching = false;
    private Image backgroundImage, confirmDialogImage, messageDialogImage;
    private ButtonSprite[] menuItems;
    private byte activeCommand = COMMAND_NONE;
    private String tempName = "Claudius";
    private TextBox txtPlayerName;
    private String welcomeText;
//    private Vector ads;
    
    public MenuScene() {
        super();
        load();
        play(40);
    }
    
    protected final void load() {
        backgroundImage = ImageHelper.loadImage("/images/mainmenubackground.png");
//        if(parent.displayAds) {
//            ads = IADView.getBannerAdData(parent, Main.NAX_CODE);
//        }
//#if ScreenWidth == 400
//#         menuItems = new ButtonSprite[] {
//#             new ButtonSprite("/images/menuitemplay.png", COMMAND_PLAY, 264, 34, 54, 58),
//#             new ButtonSprite("/images/menuitemextras.png", COMMAND_EXTRAS, 296, 126, 54, 34),
//#             new ButtonSprite("/images/menuitemabout.png", COMMAND_ABOUT, 280, 177, 69, 20),
//#             new ButtonSprite("/images/menuitemquit.png", COMMAND_QUIT, 280, 206, 53, 21)
//#         };
//#elif ScreenWidth == 320
        menuItems = new ButtonSprite[] {
            new ButtonSprite("/images/menuitemplay.png", COMMAND_PLAY, 188, 34, 54, 58),
            new ButtonSprite("/images/menuitemextras.png", COMMAND_EXTRAS, 218, 122, 54, 34),
            new ButtonSprite("/images/menuitemabout.png", COMMAND_ABOUT, 202, 170, 69, 20),
            new ButtonSprite("/images/menuitemquit.png", COMMAND_QUIT, 202, 200, 53, 21)
        };
//#endif
        // welcome string
        welcomeText = !Main.getInstance().playerName.equals("") ?
                "Welcome back, " + Main.getInstance().playerName + "!" :
                "Hey mortal! Who are you?";
        isLoading = false;
    }
    
    protected void unload() {
        backgroundImage = null;
        confirmDialogImage = null;
        menuItems = null;
        tempName = null;
        txtPlayerName = null;
//        ads = null;
    }
    
    protected void update() { }
    
    public void paint(Graphics g) {
        g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        if (isLoading) {
            g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            return;
        }
        // draw background
        g.drawImage(backgroundImage, 0, 0, Graphics.LEFT | Graphics.TOP);
        for(int i = 0; i < 4; i++) {
            menuItems[i].paint(g);
        }
        // draw welcome string
//#if ScreenWidth == 400
//#         g.drawString(welcomeText, 16, 84, Graphics.LEFT | Graphics.TOP);
//#         g.setColor(0xffffff);
//#         g.drawString(welcomeText, 14, 82, Graphics.LEFT | Graphics.TOP);
//#elif ScreenWidth == 320
        g.drawString(welcomeText, 10, 84, Graphics.LEFT | Graphics.TOP);
        g.setColor(0xffffff);
        g.drawString(welcomeText, 8, 82, Graphics.LEFT | Graphics.TOP);
//#endif
        
        // draw confirm dialog
        if (confirmDialogImage != null) {
            g.drawImage(confirmDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
            if (activeCommand == COMMAND_PLAY) {
                g.setColor(0x000000);
//#if ScreenWidth == 400
//#                 g.fillRect(130, 100, 140, 28);
//#elif ScreenWidth == 320
                g.fillRect(90, 100, 140, 28);
//#endif
                g.setColor(0xffffff);
                g.drawString(tempName, Main.SCREEN_WIDTH / 2, 118, Graphics.HCENTER | Graphics.BASELINE);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.setColor(0xff0000);
                g.drawString("must have 3-14 chars", Main.SCREEN_WIDTH / 2, 144, Graphics.HCENTER | Graphics.BASELINE);
            }
        }
        else if (messageDialogImage != null) {
            g.drawImage(messageDialogImage, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
        }
        
        // ads
//        if(parent.displayAds && ads != null) {
//            g.drawImage((Image)ads.elementAt(0), 10, 10, Graphics.LEFT | Graphics.TOP);
//        }
    }
    
    protected void pointerPressed(int x, int y) {
//        if(parent.displayAds && ads != null) {
//            int imgX1 = 10;
//            int imgY1 = 10;
//            int imgX2 = imgX1 + ((Image)ads.elementAt(0)).getWidth();
//            int imgY2 = imgY1 + ((Image)ads.elementAt(0)).getHeight();
//            if(x > imgX1 && x < imgX2 && y > imgY1 && y < imgY2) {
//                //nếu bấm vào quảng cáo
//                try {
//                    parent.bannerPressed();
//                    parent.platformRequest((String)ads.elementAt(1));
//                    ads = null;
//                } catch (ConnectionNotFoundException ex) {}
//            }
//        }
            
        if (confirmDialogImage == null && messageDialogImage == null) {
            touching = true;
            setActiveMenu(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if (confirmDialogImage != null) {
//#if ScreenWidth == 400
//#             if (x > 128 && x < 194 && y > 154 && y < 182) {
//#                 // yes
//#                 confirmCommand();
//#             }
//#             else if (x > 210 && x < 276 && y > 154 && y < 182) {
//#                 // no
//#                 cancelCommand();
//#             }
//#elif ScreenWidth == 320
            if (x > 94 && x < 154 && y > 154 && y < 182) {
                // yes
                confirmCommand();
            }
            else if(x > 170 && x < 240 && y > 154 && y < 182) {
                // no
                cancelCommand();
            }
//#endif
            else if (activeCommand == COMMAND_PLAY && x > 130 && x < 130 + 140 && y > 100 && y < 100 + 28) {
                // if name input dialog is shown
                txtPlayerName = tempName.equals("Claudis") ?
                        new TextBox("Enter your name:", "", 14, 0) :
                        new TextBox("Enter your name:", tempName, 14, 0);
                txtPlayerName.addCommand(new Command("OK", Command.OK, 1));
                txtPlayerName.setCommandListener(this);
                Display.getDisplay(Main.getInstance()).setCurrent(txtPlayerName);
            }
        }
        else if (messageDialogImage != null) {
            // ok button on message dialog
//#if ScreenWidth == 400
//#             if (x > 165 && x < 235 && y > 149 && y < 189)
//#                 messageDialogImage = null;
//#elif ScreenWidth == 320
            if (x > 130 && x < 200 && y > 149 && y < 189)
                messageDialogImage = null;
//#endif
        }
        else {
            touching = false;
            for (int i = 0; i < 4; i++) {
                menuItems[i].active = false;
            }
            // excute command
            switch (activeCommand) {
                case COMMAND_PLAY:
                    if (Main.getInstance().playerName.equals("")) {
                        confirmDialogImage = GameHelper.confirmDialog(new String[] {
                            "Enter your name:"
                        });
                    }
                    else {
                        Main.getInstance().gotoIslandMap();
                    }
                    break;

                case COMMAND_EXTRAS:
                    messageDialogImage = GameHelper.messageDialog(new String[] {
                        "This feature is not",
                        "available by now!"
                    });
                    break;

                case COMMAND_ABOUT:
                    Main.getInstance().gotoHelp();
                    break;

                case COMMAND_QUIT:
                    confirmDialogImage = GameHelper.confirmDialog(new String[] {
                        "Are you sure you",
                        "want to quit the",
                        "Pixelus Mobile?"
                    });
                    break;
            }
            
            try {
                // buttons: facebook and rate me
//#if ScreenWidth == 400
//#                 // rate me
//#                 if (x > 14 && x < 126 && y > 136 && y < 168)
//#                     parent.platformRequest("http://store.ovi.mobi/content/375376/comments/add");
//#                 // facebook
//#                 else if (x > 14 && x < 126 && y > 200 && y < 232)
//#                     parent.platformRequest("http://m.facebook.com/openitvn");
//#elif ScreenWidth == 320
                // rate me
                if (x > 10 && x < 80 && y > 150 && y < 214)
                    Main.getInstance().platformRequest("http://store.ovi.mobi/content/375376/comments/add");
//#endif
            }
            catch (ConnectionNotFoundException ex) { }
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if (confirmDialogImage == null && messageDialogImage == null) {
            if (touching)
                setActiveMenu(x, y);
        }
    }
    
    private void setActiveMenu(int x, int y) {
        for (int i = 0; i < 4; i++) {
            menuItems[i].active = false;
        }
        for (int i = 0; i < 4; i++) {
            if (menuItems[i].contains(x, y)) {
                menuItems[i].active = true;
                activeCommand = menuItems[i].getCommand();
                return;
            }
        }
        activeCommand = COMMAND_NONE;
    }
    
    private void confirmCommand() {
        switch (activeCommand) {
            case COMMAND_PLAY:
                createPlayerProfile();
                Main.getInstance().gotoIslandMap();
                break;
                
            case COMMAND_QUIT:
                Main.getInstance().notifyDestroyed();
                break;
        }
        activeCommand = COMMAND_NONE;
    }
    
    private void cancelCommand() {
        switch (activeCommand) {
            case COMMAND_PLAY:
            case COMMAND_QUIT:
                confirmDialogImage = null;
                break;
        }
        activeCommand = COMMAND_NONE;
    }

    public void commandAction(Command c, Displayable d) {
        if (c.getCommandType() == Command.OK) {
            if (txtPlayerName.getString().length() > 2 && txtPlayerName.getString().length() < 15)
                tempName = txtPlayerName.getString();
            txtPlayerName = null;
            Display.getDisplay(Main.getInstance()).setCurrent(this);
        }
    }
    
    private void createPlayerProfile() {
        Main.getInstance().playerName = tempName;
        try {
            RecordStore.deleteRecordStore(Main.RMS_USER);
        }
        catch (RecordStoreException ex) { }
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, true);
            byte[] data;
            for (int i = 1; i <= 201; i++) { // first 201 lines store puzzles information
                data = "0#0#3#0".getBytes();
                rs.addRecord(data, 0, data.length);
            }
            data = Main.getInstance().playerName.getBytes(); // line 202 for player name
            rs.addRecord(data, 0, data.length);
            data = "1".getBytes(); // line 203 for number of opened temples
            rs.addRecord(data, 0, data.length);
            data = "0".getBytes(); // line 204 for number of solved puzzles
            rs.addRecord(data, 0, data.length);
            data = "0".getBytes(); // line 205 for the newest opened temple
            rs.addRecord(data, 0, data.length);
            for (int i = 0; i < 10; i++) { // lines 206-215 store temples information
                data = "0#0#0".getBytes();
                rs.addRecord(data, 0, data.length);
            }
            rs.closeRecordStore();
        }
        catch (RecordStoreException ex) { }
    }
}
