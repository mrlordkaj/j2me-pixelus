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
import util.GraphicButton;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class MainMenu extends GamePage implements CommandListener {
    private static final byte COMMAND_NONE = 0;
    private static final byte COMMAND_PLAY = 1;
    private static final byte COMMAND_EXTRAS = 2;
    private static final byte COMMAND_ABOUT = 3;
    private static final byte COMMAND_QUIT = 4;
    
    private boolean touching = false;
    private Image backgroundTexture, confirmDialogImage, messageDialogImage;
    private GraphicButton[] menuItem;
    private byte activeCommand = COMMAND_NONE;
    private String tempName = "Claudius";
    private TextBox txtPlayerName;
    private Command okCommand = new Command("OK", Command.OK, 1);
//    private Vector ads;
    
    private Main parent;
    
    public MainMenu(Main _parent) {
        super();
        parent = _parent;
        
        prepareResource();
        
        schedule = 40;
        new Thread(this).start();
    }
    
    private void prepareResource() {
        backgroundTexture = ImageHelper.loadImage("/images/mainmenubackground.png");
//        if(parent.displayAds) {
//            ads = IADView.getBannerAdData(parent, Main.NAX_CODE);
//        }
        menuItem = new GraphicButton[] {
            new GraphicButton("/images/menuitemplay.png", COMMAND_PLAY, 264, 34, 54, 58),
            new GraphicButton("/images/menuitemextras.png", COMMAND_EXTRAS, 296, 126, 54, 34),
            new GraphicButton("/images/menuitemabout.png", COMMAND_ABOUT, 280, 177, 69, 20),
            new GraphicButton("/images/menuitemquit.png", COMMAND_QUIT, 280, 206, 53, 21)
        };
        isLoading = false;
    }
    
    protected void update(){}
    
    public void paint(Graphics g) {
        g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        if(isLoading) {
            g.fillRect(0, 0, Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
            return;
        }
        
        //vẽ nền
        g.drawImage(backgroundTexture, 0, 0, Graphics.LEFT | Graphics.TOP);
        for(int i = 0; i < 4; i++) {
            menuItem[i].paint(g);
        }
        
        //lời chào mừng
        String welcomeText;
        if(!parent.playerName.equals("")) {
            welcomeText = "Welcome back, " + parent.playerName + "!";
        } else {
            welcomeText = "Hey mortal! Who are you?";
        }
        g.drawString(welcomeText, 16, 84, Graphics.LEFT | Graphics.TOP);
        g.setColor(0xffffff);
        g.drawString(welcomeText, 14, 82, Graphics.LEFT | Graphics.TOP);
        
        //hộp thoại xác nhận
        if(confirmDialogImage != null) {
            g.drawImage(confirmDialogImage, Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
            if(activeCommand == COMMAND_PLAY) {
                g.setColor(0x000000);
                g.fillRect(130, 100, 140, 28);
                g.setColor(0xffffff);
                g.drawString(tempName, Main.SCREENSIZE_WIDTH / 2, 118, Graphics.HCENTER | Graphics.BASELINE);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.setColor(0xff0000);
                g.drawString("must have 3-14 chars", Main.SCREENSIZE_WIDTH / 2, 144, Graphics.HCENTER | Graphics.BASELINE);
            }
        } else if(messageDialogImage != null) {
            g.drawImage(messageDialogImage, Main.SCREENSIZE_WIDTH / 2, Main.SCREENSIZE_HEIGHT / 2, Graphics.HCENTER | Graphics.VCENTER);
        }
        
        //quảng cáo
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
            
        if(confirmDialogImage == null && messageDialogImage == null) {
            touching = true;
            setActiveMenu(x, y);
        }
    }
    
    protected void pointerReleased(int x, int y) {
        if(confirmDialogImage != null) {
            if(x > 128 && x < 194 && y > 154 && y < 182) {
                //yes
                confirmCommand();
            } else if(x > 210 && x < 276 && y > 154 && y < 182) {
                //no
                cancelCommand();
            } else if(activeCommand == COMMAND_PLAY && x > 130 && x < 130 + 140 && y > 100 && y < 100 + 28) {
                //nếu đang hiện hộp thoại nhập tên
                if(!tempName.equals("Claudis")) {
                    txtPlayerName = new TextBox("Enter your name:", tempName, 14, 0);
                } else {
                    txtPlayerName = new TextBox("Enter your name:", "", 14, 0);
                }
                txtPlayerName.addCommand(okCommand);
                txtPlayerName.setCommandListener(this);
                Display.getDisplay(parent).setCurrent(txtPlayerName);
            }
        } else if(messageDialogImage != null) {
            //nút ok của hộp thoại message
            if(x > 165 && x < 235 && y > 149 && y < 189) {
                messageDialogImage = null;
            }
        } else {
            touching = false;
            for(int i = 0; i < 4; i++) {
                menuItem[i].active = 0;
            }

            //thực thi lệnh
            switch(activeCommand) {
                case COMMAND_PLAY:
                    if(parent.playerName.equals("")) {
                        confirmDialogImage = Loader.confirmDialog(new String[] {
                            "Enter your name:"
                        });
                    } else {
                        parent.gotoIslandMap();
                    }
                    break;

                case COMMAND_EXTRAS:
                    messageDialogImage = Loader.messageDialog(new String[] {
                        "This feature is not",
                        "available by now!"
                    });
                    break;

                case COMMAND_ABOUT:
                    parent.gotoHelp();
                    break;

                case COMMAND_QUIT:
                    confirmDialogImage = Loader.confirmDialog(new String[] {
                        "Are you sure you",
                        "want to quit the",
                        "Pixelus Mobile?"
                    });
                    break;
            }
            
            try {
                //nút phụ: facebook và rate me
                if(x > 14 && x < 126 && y > 136 && y < 168) {
                    //rate me
                    parent.platformRequest("http://store.ovi.mobi/content/375376/comments/add");
                } else if(x > 14 && x < 126 && y > 200 && y < 232) {
                    //facebook
                    parent.platformRequest("http://m.facebook.com/openitvn");
                }
            } catch (ConnectionNotFoundException ex) {}
        }
    }
    
    protected void pointerDragged(int x, int y) {
        if(confirmDialogImage == null && messageDialogImage == null) {
            if(touching) setActiveMenu(x, y);
        }
    }
    
    public void dispose() {
        isLoading = true;
        pageLooping = false;
        backgroundTexture = null;
        confirmDialogImage = null;
        menuItem = null;
        tempName = null;
        txtPlayerName = null;
//        ads = null;
    }
    
    //kiểm tra xem menu nào đang được active
    private void setActiveMenu(int x, int y) {
        for(int i = 0; i < 4; i++) {
            menuItem[i].active = 0;
        }
        
        for(int i = 0; i < 4; i++) {
            if(menuItem[i].contains(x, y)) {
                menuItem[i].active = 1;
                activeCommand = menuItem[i].getCommand();
                return;
            }
        }
        
        activeCommand = COMMAND_NONE;
    }
    
    private void confirmCommand() {
        switch(activeCommand) {
            case COMMAND_PLAY:
                createPlayerProfile();
                parent.gotoIslandMap();
                break;
                
            case COMMAND_QUIT:
                parent.notifyDestroyed();
                break;
        }
        activeCommand = COMMAND_NONE;
    }
    
    private void cancelCommand() {
        switch(activeCommand) {
            case COMMAND_PLAY:
            case COMMAND_QUIT:
                confirmDialogImage = null;
                break;
        }
        activeCommand = COMMAND_NONE;
    }

    public void commandAction(Command c, Displayable d) {
        if (c.getCommandType() == Command.OK) {
            if(txtPlayerName.getString().length() > 2 && txtPlayerName.getString().length() < 15) {
                tempName = txtPlayerName.getString();
            }
            txtPlayerName = null;
            Display.getDisplay(parent).setCurrent(this);
        }
    }
    
    private void createPlayerProfile() {
        parent.playerName = tempName;
        try {
            RecordStore.deleteRecordStore(Main.RMS_USER);
        } catch (RecordStoreException ex) {}
        
        try {
            RecordStore rs = RecordStore.openRecordStore(Main.RMS_USER, true);
            byte[] writer;
            for(int i = 1; i <= 201; i++) { //201 dong dau ghi thong tin puzzle
                writer = "0#0#3#0".getBytes();
                rs.addRecord(writer, 0, writer.length);
            }
            writer = parent.playerName.getBytes(); //dong 202 ghi ten nguoi choi
            rs.addRecord(writer, 0, writer.length);
            writer = "1".getBytes(); //dong 203 ghi so temple da mo
            rs.addRecord(writer, 0, writer.length);
            writer = "0".getBytes(); //dong 204 ghi so luong cau do da giai duoc
            rs.addRecord(writer, 0, writer.length);
            writer = "0".getBytes(); //dong 205 ghi temple moi duoc mo
            rs.addRecord(writer, 0, writer.length);
            for(int i = 0; i < 10; i++) { //dong 206 den 215 ghi thong tin temple
                writer = "0#0#0".getBytes();
                rs.addRecord(writer, 0, writer.length);
            }
            rs.closeRecordStore();
        } catch (RecordStoreException ex) {}
    }
}
