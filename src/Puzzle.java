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

/**
 *
 * @author Thinh Pham
 */
public class Puzzle {
    
    public static final int[] PUZZLE_FIRSTID = new int[] { 1, 10, 22, 37, 55, 76, 100, 127, 154, 181, 211 };
    public static final int MEDAL_NONE = 3;
    public static final int MEDAL_BRONZE = 2;
    public static final int MEDAL_SILVER = 1;
    public static final int MEDAL_GOLDEN = 0;
    
    private final String name;
    private final String title;
    private final String data;
    
    public static Puzzle getPuzzle(int id) {
        String[] puzzleData = GameHelper.readPuzzleData(id);
        return new Puzzle(puzzleData[0], puzzleData[1], puzzleData[2]);
    }
    
    private Puzzle(String name, String title, String data) {
        this.name = name;
        this.title = title;
        this.data = data;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getData() {
        return data;
    }
}
