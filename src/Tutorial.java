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
import util.ImageHelper;

/**
 *
 * @author Thinh Pham
 */
public class Tutorial {
    public int[] step;
    public String[][] description;
    private Sprite ballonSprite;
    
    public Tutorial(int[] _step, String[][] _description) {
        step = _step;
        description = _description;
        ballonSprite = new Sprite(ImageHelper.loadImage("/images/ballon.png"), 127, 110);
    }
    
    public Image getBallon(int nStep) {
        Image ballon = Image.createImage(127, 110);
        Graphics g = ballon.getGraphics();
        if(nStep > step.length - 1) nStep--;
        int cellIndex = step[nStep];
        int row = cellIndex / 16;
        int col = cellIndex % 16;
        int marginTop;
        ballonSprite.setPosition(0, 0);
        if(row <= 7 && col <= 7) { //góc trên bên trái
            ballonSprite.setFrame(3);
            marginTop = 50;
        } else if(row <= 7 && col >= 8) { //góc trên bên phải
            ballonSprite.setFrame(2);
            marginTop = 50;
        } else if(row >= 8 && col <= 7) { //góc dưới bên trái
            ballonSprite.setFrame(1);
            marginTop = 16;
        } else {
            ballonSprite.setFrame(0);
            marginTop = 16;
        }
        ballonSprite.paint(g);
        String[] desc = description[nStep];
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        for(byte i = 0; i < desc.length; i++) {
            g.drawString(desc[i], 64, i * 16 + marginTop, Graphics.HCENTER | Graphics.BASELINE);
        }
        int[] rgb = new int[127*110];
        ballon.getRGB(rgb, 0, 127, 0, 0, 127, 110);
        for (int i = 0; i < rgb.length; ++i) {
            if (rgb[i] == 0xffff00ff) rgb[i] &= 0x00ffffff;
        }
        ballon = Image.createRGBImage(rgb, 127, 110, true);
        return ballon;
    }
    
    public int getCellIndex(int nStep) {
        if(nStep > step.length - 1) nStep--;
        return step[nStep];
    }
    
    public static Tutorial getTutorial(int puzzleId) {
        int[] _step = new int[0];
        String[][] _description = new String[0][0];
        switch(puzzleId) {
            case 1:
                _step = new int[] { 152, 153 };
                _description = new String[][] {
                    {
                        "Tap on this",
                        "bright spot to",
                        "slide in a tile."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 2:
                _step = new int[] { 89, 92 };
                _description = new String[][] {
                    {
                        "Tap on this",
                        "bright spot to",
                        "slide in a tile."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 3:
                _step = new int[] { 36, 37, 36, 43 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 4:
                _step = new int[] { 59, 43, 59, 60, 61, 60, 147 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 5:
                _step = new int[] { 215, 214, 215, 213, 214, 219 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 6:
                _step = new int[] { 90, 91, 90, 92, 108 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on this",
                        "bright spot to",
                        "slide in a tile."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 7:
                _step = new int[] { 167, 168, 169, 167, 168, 184, 71 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on this",
                        "bright spot to",
                        "slide in a tile."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 8:
                _step = new int[] { 101, 102, 101, 103, 104, 103, 86, 87, 86, 167 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
                
            case 9:
                _step = new int[] { 120, 119, 120, 168, 184, 168, 172 };
                _description = new String[][] {
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap here to add",
                        "a temporary tile."
                    },
                    {
                        "Tap on this spot",
                        "to slide a tile",
                        "against the",
                        "temporary tile."
                    },
                    {
                        "Tap on this",
                        "temporary tile to",
                        "remove it again."
                    },
                    {
                        "Tap on the last",
                        "bright spot to",
                        "complete the",
                        "puzzle."
                    }
                };
                break;
        }
        return new Tutorial(_step, _description);
    }
    
    public static String[] getDescription(int puzzleId) {
        String[] rs = new String[0];
        switch(puzzleId) {
            case 1:
                rs = new String[] {
                    "Tap on the bright",
                    "spots to slide in tiles.",
                    "You can only place a",
                    "tile if there's one",
                    "behind the empty",
                    "spot to stop it."
                };
                break;
                
            case 2:
                rs = new String[] {
                    "In this puzzle it's",
                    "important to place",
                    "the tile from the left",
                    "to right. Go ahead",
                    "and try the other",
                    "way around!"
                };
                break;
                
            case 3:
                rs = new String[] {
                    "When there's no way",
                    "to reach a bright",
                    "spot directly, you can",
                    "place a temporary",
                    "tile to act as",
                    "a stopper."
                };
                break;
                
            case 4:
                rs = new String[] {
                    "In this puzzle, you",
                    "have to work with",
                    "temporary tiles AND",
                    "think about the",
                    "order of placement."
                };
                break;
                
            case 5:
                rs = new String[] {
                    "Here you need to",
                    "place a temporary",
                    "tile, to act as a",
                    "stopper for another",
                    "temporary tile!"
                };
                break;
                
            case 6:
                rs = new String[] {
                    "At first you mind",
                    "think you can place",
                    "these tiles from",
                    "either side, but",
                    "think again!"
                };
                break;
                
            case 7:
                rs = new String[] {
                    "Placing the easy",
                    "tiles first can often",
                    "block the only",
                    "posible solution!"
                };
                break;
                
            case 8:
                rs = new String[] {
                    "This puzzle requires a",
                    "'layered' approach."
                };
                break;
                
            case 9:
                rs = new String[] {
                    "Keep an eye on the",
                    "workbench at the",
                    "left of the sidebar",
                    "It shows you how",
                    "many tiles you have",
                    "left."
                };
                break;
        }
        return rs;
    }
}
