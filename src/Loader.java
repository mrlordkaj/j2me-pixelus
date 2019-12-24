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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import util.GraphicButton;
import util.IOHelper;
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class Loader extends Thread {
    private GamePage parent;
    
    public Loader(GamePage _parent) {
        parent = _parent;
    }
    
    public void run() {
        String parentClass = parent.getClass().getName();
        if(parentClass.equals(IslandMap.class.getName())) {
            loadIslandMapResource();
        } else if(parentClass.equals(Temple.class.getName())) {
            loadTempleResource();
        } else if(parentClass.equals(Play.class.getName())) {
            loadPlayResource();
        }
        parent.isLoading = false;
    }
    
    private void loadIslandMapResource() {
        ((IslandMap)parent).islandImage = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        Graphics g = ((IslandMap)parent).islandImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/islandmap.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        ImageHelper.medalSprite.setFrame(0);
        for(int i = 0; i < ((IslandMap)parent).totalOpenedTemple; i++) {
            if(((IslandMap)parent).getTempleCompleted(i)) {
                //vẽ đền phát sáng
                g.drawImage(ImageHelper.loadImage("/images/map" + Story.characterName[i].toLowerCase() + "b.png"), IslandMap.templeRectangle[i][0], IslandMap.templeRectangle[i][1], Graphics.LEFT | Graphics.TOP);
                //vẽ huy chương cho đền hoàn hảo
                if(((IslandMap)parent).templeIsPerfect(i)) {
                    ImageHelper.medalSprite.setPosition(IslandMap.templeRectangle[i][0] + IslandMap.templeRectangle[i][2] / 2, IslandMap.templeRectangle[i][1] + IslandMap.templeRectangle[i][3] - 12);
                    ImageHelper.medalSprite.paint(g);
                }
            } else if(i != 0) {
                //vẽ đền bình thường
                g.drawImage(ImageHelper.loadImage("/images/map" + Story.characterName[i].toLowerCase() + "a.png"), IslandMap.templeRectangle[i][0], IslandMap.templeRectangle[i][1], Graphics.LEFT | Graphics.TOP);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }
        
        ((IslandMap)parent).lightingImage = ImageHelper.loadImage("/images/lighting.png");
        ((IslandMap)parent).templeArrowImage = ImageHelper.loadImage("/images/templearrow.png");
        ((IslandMap)parent).starSprite = new Sprite(ImageHelper.loadImage("/images/star.png"), 100, 16);
        ((IslandMap)parent).starSprite.setPosition(25, 168);
        if(((IslandMap)parent).newTemple == 0) {
            ((IslandMap)parent).story = Story.getStory(Story.STORY_CYLOP_CYLOP, (IslandMap)parent);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
    }
    
    private void loadTempleResource() {
        Image lockImage = ImageHelper.loadImage("/images/lockpuzzleoverlay.png");
        Image pixelMask = ImageHelper.createPixelMask(4);
        
        //vẽ danh sách puzzle
        int templeId = ((Temple)parent).getTempleId();
        int numPuzzle = Puzzle.PUZZLE_FIRSTID[templeId + 1] - Puzzle.PUZZLE_FIRSTID[templeId];
        int width = 68 * 3 - 4;
        int height = ((Temple)parent).getPuzzleViewHeight();
        ((Temple)parent).puzzleViewImage = Image.createImage(width, height);
        Graphics g = ((Temple)parent).puzzleViewImage.getGraphics();
        if(((Temple)parent).getTempleId() == Temple.TEMPLE_CYLOP) {
            //nếu là tutorial
            g.setColor(0x585866);
            g.fillRect(0, 0, width, height);
            for(int i = 1; i <= numPuzzle; i++) {
                int row = (i - 1) / 3;
                int col = (i - 1) % 3;
                if(i <= ((Temple)parent).getSolvedPuzzle()) {
                    drawPuzzleImage(i, col * 68, row * 68, 4, g, pixelMask, Puzzle.MEDAL_NONE);
                } else {
                    drawPuzzleCover(i, col * 68, row * 68, g, pixelMask, Puzzle.MEDAL_NONE);
                }
                if(i > ((Temple)parent).getSolvedPuzzle() + 1) {
                    g.drawImage(lockImage, col * 68, row * 68, Graphics.LEFT | Graphics.TOP);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {}
            }
        } else {
            //nếu là các đền bình thường
            g.setColor(0xdda513);
            g.fillRect(0, 0, width, height);
            for(int i = 0; i < numPuzzle; i++) {
                int row = i / 3;
                int col = i % 3;
                if(((Temple)parent).bestAmountTurn(i) > 0) {
                    drawPuzzleImage(i + Puzzle.PUZZLE_FIRSTID[templeId], col * 68, row * 68, 4, g, pixelMask, ((Temple)parent).medal(i));
                } else {
                    drawPuzzleCover(i + Puzzle.PUZZLE_FIRSTID[templeId], col * 68, row * 68, g, pixelMask, ((Temple)parent).medal(i));
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {}
            }
            if(!((Temple)parent).lastPuzzleIsUnlocked()) {
                g.drawImage(lockImage, width, height, Graphics.RIGHT | Graphics.BOTTOM);
            }
        }
        //các resource còn lại
        ((Temple)parent).backgroundImage = ImageHelper.loadImage("/images/temple" + Story.characterName[((Temple)parent).getTempleId()].toLowerCase() + ".png");
        ((Temple)parent).buttonImage = ImageHelper.loadImage("/images/buttongold.png");
        ((Temple)parent).scrollerImage = ImageHelper.loadImage("/images/scroller.png");
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
        
        ((Temple)parent).schedule = 40;
    }
    
    private void loadPlayResource() {
        ((Play)parent).tileSprite = new Sprite(ImageHelper.loadImage("/images/tile.png"), 12, 12);
        ((Play)parent).viewpotImage = Image.createImage(252, 240);
        ((Play)parent).viewpotGraphic = ((Play)parent).viewpotImage.getGraphics();
        ((Play)parent).viewpotGraphic.drawImage(ImageHelper.loadImage("/images/playbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        int puzzleId = ((Play)parent).getPuzzleId();
        Puzzle myPuzzle = Puzzle.getPuzzle(puzzleId);
        ((Play)parent).puzzleTitle = myPuzzle.getTitle();
        String data = myPuzzle.getData();
        String name = myPuzzle.getName();
        String title = myPuzzle.getTitle();
        myPuzzle = null;
        int row, col;
        byte value, tileRemain = 0, totalTile = 0;
        for(short i = 0; i < 16*16; i++) {
            row = i / 16;
            col = i % 16;
            value = (byte)data.charAt(i);
            ((Play)parent).tileSprite.setFrame(value);
            ((Play)parent).tileSprite.setPosition(col * 12 + 24, row * 12 + 24);
            ((Play)parent).tileSprite.paint(((Play)parent).viewpotGraphic);
            ((Play)parent).cell[row][col] = ((Play)parent).defaultData[row][col] = value;
            switch(value) {
                case Play.TILE_WANT:
                case Play.TILE_STICKY:
                    tileRemain++;
                    totalTile++;
                    break;
                    
                case Play.TILE_BLUE:
                    totalTile++;
                    break;
                    
                case Play.TILE_RED:
                    tileRemain--;
                    break;
            }
        }
        data = null;
        //if(((Play)parent).getTempleId() != Temple.TEMPLE_CYLOP) ((Play)parent).worldRecord = IOHelper.getFileSize("/data/hints/" + name + ".dat");
        if(((Play)parent).getTempleId() != Temple.TEMPLE_CYLOP) ((Play)parent).hintData = IOHelper.read("/data/hints/" + name + ".dat");
        
        Image resourceImage = ImageHelper.loadImage("/data/images/" + name + ".gif");
        resourceImage.getRGB(((Play)parent).rgb, 0, 16, 0, 0, 16, 16);
        
        int stackHeight = 12 * totalTile;
        ((Play)parent).stackImage = Image.createImage(12, stackHeight);
        Graphics g = ((Play)parent).stackImage.getGraphics();
        ((Play)parent).tileSprite.setFrame(Play.TILE_BLUE);
        for(int i = stackHeight - 12; i >= 0; i -= 12) {
            ((Play)parent).tileSprite.setPosition(0, i);
            ((Play)parent).tileSprite.paint(g);
        }
        ((Play)parent).tileStackY = 240 - 12 * tileRemain;
        
        int[] rgb = new int[12*12];
        for (int i = 0; i < rgb.length; ++i) rgb[i] = 0x44ffffff;
        ((Play)parent).posibleMask = Image.createRGBImage(rgb, 12, 12, true);
        for (int i = 0; i < rgb.length; ++i) rgb[i] = 0x22ff0000;
        ((Play)parent).imposibleMask = Image.createRGBImage(rgb, 12, 12, true);
        rgb = null;
        
        ((Play)parent).puzzleCompleteImage = Image.createImage(Main.SCREENSIZE_WIDTH, Main.SCREENSIZE_HEIGHT);
        g = ((Play)parent).puzzleCompleteImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/puzzlecompleted.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        drawPuzzleImage(puzzleId, 161, 50, 5, g, ImageHelper.createPixelMask(5), 3);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.setColor(0x000000);
        g.drawString(title, Main.SCREENSIZE_WIDTH / 2 + 1, 148 + 1, Graphics.HCENTER | Graphics.BASELINE);
        g.setColor(0xffd800);
        g.drawString(title, Main.SCREENSIZE_WIDTH / 2, 148, Graphics.HCENTER | Graphics.BASELINE);
        
        Image gamepadImage = ImageHelper.loadImage("/images/navbutton.png");
        ((Play)parent).button = new GraphicButton[] {
            new GraphicButton(gamepadImage, Play.COMMAND_UP, 308, 131, 40, 30),
            new GraphicButton(gamepadImage, Play.COMMAND_RIGHT, 350, 164, 40, 30),
            new GraphicButton(gamepadImage, Play.COMMAND_DOWN, 308, 197, 40, 30),
            new GraphicButton(gamepadImage, Play.COMMAND_LEFT, 266, 164, 40, 30),
            new GraphicButton(gamepadImage, Play.COMMAND_FIRE, 308, 164, 40, 30)
        };
        gamepadImage = null;
        
        ((Play)parent).sidebarImage = Image.createImage(148, 240);
        g = ((Play)parent).sidebarImage.getGraphics();
        g.drawImage(ImageHelper.loadImage("/images/sidebarbackground.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        if(((Play)parent).getTempleId() == Temple.TEMPLE_CYLOP) {
            g.drawImage(ImageHelper.loadImage("/images/tutorialsidebar.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            String[] description = Tutorial.getDescription(((Play)parent).getPuzzleId());
            for(int i = 0; i < description.length; i++) {
                g.drawString(description[i], 75, 14*i + 24, Graphics.HCENTER | Graphics.BASELINE);
            }
        } else {
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0xff0000);
            g.drawString(Integer.toString(((Play)parent).hintData.length()), 126, 82, Graphics.RIGHT | Graphics.BASELINE);
        }
        
        ((Play)parent).characterSprite = new Sprite(ImageHelper.loadImage("/images/tilemaster.png"), 20, 26);
        ((Play)parent).characterSprite.setPosition(20, -2);
        
        ((Play)parent).aimImage = new Image[] {
            ImageHelper.loadImage("/images/aimup.png"),
            ImageHelper.loadImage("/images/aimright.png"),
            ImageHelper.loadImage("/images/aimdown.png"),
            ImageHelper.loadImage("/images/aimleft.png")
        };
        ((Play)parent).navImage = ImageHelper.loadImage("/images/navigator.png");
        ((Play)parent).shruggingSprite = new Sprite(ImageHelper.loadImage("/images/shrugging.png"), 20, 26);
        ((Play)parent).celebratingSprite = new Sprite(ImageHelper.loadImage("/images/celebrating.png"), 20, 35);
        ((Play)parent).cellMask = ImageHelper.loadImage("/images/cellmask.png");
        ((Play)parent).curtainImage = ImageHelper.loadImage("/images/curtain.png");
        ((Play)parent).quickMenuImage = ImageHelper.loadImage("/images/quickmenu.png");
        ((Play)parent).calcPosible();
        ((Play)parent).updateCharacterSprite();
        
        if(((Play)parent).getTempleId() == Temple.TEMPLE_CYLOP) {
            ((Play)parent).prepareTutorialStep();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
    }
    
    public static void drawPuzzleCover(int puzzleId, int x, int y, Graphics g, Image pixelMask, int medal) {
        Puzzle puzzle = Puzzle.getPuzzle(puzzleId);
        String data = puzzle.getData();
        for(short i = 0; i < 16*16; i++) {
            int row = i / 16;
            int col = i % 16;
            switch(data.charAt(i)) {
                case Play.TILE_NONE: //0
                    g.setColor(0xeabc6e);
                    break;
                    
                case Play.TILE_WANT: //?
                    g.setColor(0xffe0b1);
                    break;
                    
                case Play.TILE_STICKY: //S
                    g.setColor(0x69db76);
                    break;
                    
                case Play.TILE_BLUE: //+
                    g.setColor(0x5df0e8);
                    break;
                    
                case Play.TILE_RED: //-
                    g.setColor(0xf95d5d);
                    break;
                    
                case Play.TILE_DARKBLUE: //X
                    g.setColor(0x228aa2);
                    break;
                    
                default:
                    g.setColor(0x000000);
                    break;
            }
            g.fillRect(col * 4 + x, row * 4 + y, 4, 4);
        }
        data = null;
        g.drawImage(pixelMask, x, y, Graphics.LEFT | Graphics.TOP);
        if(medal < Puzzle.MEDAL_NONE) {
            ImageHelper.medalSprite.setFrame(medal);
            ImageHelper.medalSprite.setPosition(x, y + 40);
            ImageHelper.medalSprite.paint(g);
        }
    }
    
    public static void drawPuzzleImage(int puzzleId, int x, int y, int size, Graphics g, Image pixelMask, int medal) {
        String name = Puzzle.getPuzzle(puzzleId).getName();
        Image resourceImage = ImageHelper.loadImage("/data/images/" + name + ".gif");
        
        int[] rgb = new int[16*16];
        resourceImage.getRGB(rgb, 0, 16, 0, 0, 16, 16);
        for(int i = 0; i < rgb.length; ++i) {
            int row = i / 16;
            int col = i % 16;
            g.setColor(rgb[i]);
            g.fillRect(col * size + x, row * size + y, size, size);
        }
        g.drawImage(pixelMask, x, y, Graphics.LEFT | Graphics.TOP);
        if(medal < Puzzle.MEDAL_NONE) {
            ImageHelper.medalSprite.setFrame(medal);
            ImageHelper.medalSprite.setPosition(x, y + 40);
            ImageHelper.medalSprite.paint(g);
        }
    }
    
    public static Image confirmDialog(String[] message){
        final int WIDTH = 252;
        final int HEIGHT = 182;
        Image dialog = Image.createImage(WIDTH, HEIGHT);
        Graphics g = dialog.getGraphics();
        
        //vẽ bảng thông báo
        g.drawImage(ImageHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        
        //viết nội dung thông báo
        g.setColor(0, 0, 0);
        for(int i = 0; i < message.length; i++) {
            g.drawString(message[i], WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        
        //nút okie
        g.drawImage(ImageHelper.loadImage("/images/buttongold.png"), 84, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Okie", 84, 146, Graphics.HCENTER | Graphics.BASELINE);
        //nút cancel
        g.drawImage(ImageHelper.loadImage("/images/buttonsilver.png"), WIDTH - 84, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Cancel", WIDTH - 84, 146, Graphics.HCENTER | Graphics.BASELINE);
        
        //xóa nền rồi trả về hình ảnh
        int[] rgb = new int[WIDTH * HEIGHT];
        dialog.getRGB(rgb, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffff00ff) {
                rgb[i] &= 0x00ffffff;
            }
        }
        return Image.createRGBImage(rgb, WIDTH, HEIGHT, true);
    }
    
    public static Image messageDialog(String[] message){
        final int WIDTH = 252;
        final int HEIGHT = 182;
        Image dialog = Image.createImage(WIDTH, HEIGHT);
        Graphics g = dialog.getGraphics();
        
        //vẽ bảng thông báo
        g.drawImage(ImageHelper.loadImage("/images/dialog.png"), 0, 0, Graphics.LEFT | Graphics.TOP);
        
        //viết nội dung thông báo
        g.setColor(0, 0, 0);
        for(int i = 0; i < message.length; i++) {
            g.drawString(message[i], WIDTH / 2, 57 + i * 20, Graphics.HCENTER | Graphics.BASELINE);
        }
        
        //nút okie
        g.drawImage(ImageHelper.loadImage("/images/buttongold.png"), WIDTH / 2, 140, Graphics.HCENTER | Graphics.VCENTER);
        g.drawString("Okie", WIDTH / 2, 146, Graphics.HCENTER | Graphics.BASELINE);
        
        //xóa nền rồi trả về hình ảnh
        int[] rgb = new int[WIDTH * HEIGHT];
        dialog.getRGB(rgb, 0, WIDTH, 0, 0, WIDTH, HEIGHT);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffff00ff) {
                rgb[i] &= 0x00ffffff;
            }
        }
        return Image.createRGBImage(rgb, WIDTH, HEIGHT, true);
    }
}
