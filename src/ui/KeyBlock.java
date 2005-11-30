/*
 * KeyBlock.java
 *
 * Created on 15 ��� 2005 �., 3:08
 *
 * Copyright (c) 2005, Eugene Stahov (evgs), http://bombus.jrudevels.org
 * All rights reserved.
 */

package ui;

import Client.Config;
import javax.microedition.lcdui.*;
import java.util.*;

/**
 *
 * @author Eugene Stahov
 */
public class KeyBlock extends Canvas implements Runnable{
    
    public final static int COLOR_BLK_BORDER=  0x808080;
    public final static int COLOR_BLK_TEXT=    0xffffff;
    public final static int COLOR_BLK_BGND=    0x000000;
    
    private int width;
    private int height;
    
    private Display display;
    private Displayable parentView;
    
    private Image img;
    
    private ComplexString status;
    
    private char exitKey;
    private int kHold;
    
    private TimerTaskClock tc;
    
    boolean motorola_backlight;
    
    /** Creates a new instance */
    public KeyBlock(
            Display display, 
            ComplexString status, 
            char exitKey, 
            boolean motorola_backlight) 
    {
        this.status=status;
        this.display=display;
        kHold=this.exitKey=exitKey;
        this.motorola_backlight=motorola_backlight;
        
        parentView=display.getCurrent();
        status.setElementAt(new Integer(ImageList.ICON_KEYBLOCK_INDEX),6);
        repaint();

        new Thread(this).start();
        
        tc=new TimerTaskClock();
        
//#if !(MIDP1)
        setFullScreenMode(Config.getInstance().fullscreen);
//#endif
        //System.gc();   // heap cleanup
    }
    
    public void run(){
        try {
            img=Image.createImage("/images/splash.png");
        } catch (Exception e) {};
        
        display.setCurrent(this);
//#if !(MIDP1)
        if (motorola_backlight) display.flashBacklight(0);
//#endif
    }
    
    public void paint(Graphics g){
        width=getWidth();
        height=getHeight();
        Font f=Font.getDefaultFont();
        
        g.setColor(COLOR_BLK_BGND);
        g.fillRect(0,0, width, height);
        
        if (img!=null) g.drawImage(img, width/2, 0, Graphics.TOP|Graphics.HCENTER);
        
        int h=f.getHeight()+1;
//#if ALCATEL_FONT
//--        int y=height-h-4;
//#else
        int y=height-h;
//#endif
        g.setColor(COLOR_BLK_TEXT);
        g.translate(0, y);
        status.drawItem(g, 0, false);
        
        String time=Time.timeString(Time.localTime());
        int tw=f.stringWidth(time);
        g.translate(width/2, -h);
        g.setColor(COLOR_BLK_BGND);
        g.fillRect(-tw/2-5, -h, tw+10, h);
        g.setColor(COLOR_BLK_TEXT);
        g.drawString(time, 0, 0, Graphics.BOTTOM | Graphics.HCENTER);
    }
    
    public void keyPressed(int keyCode) { 
        //System.out.println("blocked press"+(char) keyCode);
        kHold=0; 
    }
    public void keyReleased(int keyCode) { 
        //System.out.println("blocked released"+(char) keyCode); kHold=0; 
    }
    protected void keyRepeated(int keyCode) { 
        //System.out.println("blocked repeat"+(char) keyCode);
        if (kHold==0)
        if (keyCode==exitKey) destroyView(); 
    }

    private void destroyView(){
        status.setElementAt(null,6);
//#if !(MIDP1)
        if (motorola_backlight) display.flashBacklight(Integer.MAX_VALUE);
//#endif
        if (display!=null)   display.setCurrent(parentView);
        img=null;
        tc.stop();
        System.gc();
    }
    
    private class TimerTaskClock extends TimerTask {
        private Timer t;
        public TimerTaskClock(){
            t=new Timer();
            t.schedule(this, 10, 20000);
        }
        public void run() {
            repaint();
        }
        public void stop(){
            cancel();
            t.cancel();
        }
    }
}
