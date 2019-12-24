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

import util.IOHelper;

/**
 *
 * @author Thinh Pham
 */
public class Puzzle {
    public static final int[] PUZZLE_FIRSTID = new int[] {1, 10, 22, 37, 55, 76, 100, 127, 154, 181, 211};
    public static final int MEDAL_NONE = 3;
    public static final int MEDAL_BRONZE = 2;
    public static final int MEDAL_SILVER = 1;
    public static final int MEDAL_GOLDEN = 0;
    
    private String name;
    private String title;
    private String data;
    
    public static Puzzle getPuzzle(int id) {
        String[] puzzleData = IOHelper.readPuzzleData(id);
        return new Puzzle(puzzleData[0], puzzleData[1], puzzleData[2]);
    }
    
    private Puzzle(String _name, String _title, String _data) {
        name = _name;
        title = _title;
        data = _data;
    }
    
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getData() { return data; }
}
