/*
 * Baloon.java
 *
 * Created on 6.02.2006, 23:09
 *
 * Copyright (c) 2005-2007, Eugene Stahov (evgs), http://bombus-im.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ui.controls;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.Colors;
import ui.FontCache;

/**
 *
 * @author Evg_S
 */
public class Balloon {
    
    public static int getHeight(){
        Font f=FontCache.getBalloonFont();
        return f.getHeight()+3;
    }
    
    public static void draw(Graphics g, String text) {
        Font f=FontCache.getBalloonFont();
        g.setFont(f);
        int height=getHeight();
        int width=f.stringWidth(text)+6;
        
        int y=height-g.getTranslateY();
        if (y<0) y=0;
        y-=height-1;
        g.translate(0, y);
        
        g.setColor(Colors.BALLOON_INK);
        g.fillRect(2, 0, width, height);

        g.setColor(Colors.BALLOON_BGND);
        g.fillRect(3, 1, width-2, height-2);
       
        g.setColor(Colors.BALLOON_INK);
        g.drawString(text, 5, 2, Graphics.TOP | Graphics.LEFT);
    }
}
