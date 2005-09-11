/*
 * PrivacySelect.java
 *
 * Created on 26 ������ 2005 �., 23:04
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package PrivacyLists;
import Client.Roster;
import Client.StaticData;
import javax.microedition.lcdui.*;
import ui.*;
import java.util.*;
import com.alsutton.jabber.*;

/**
 *
 * @author EvgS
 */
public class PrivacySelect 
        extends VirtualList 
        implements CommandListener,
        JabberBlockListener,
        MIDPTextBox.TextBoxNotify
{
    private Vector list=new Vector();
    
    private Command cmdCancel=new Command ("Back", Command.BACK, 99);
    private Command cmdActivate=new Command ("Activate", Command.SCREEN, 10);
    private Command cmdDefault=new Command ("Set default", Command.SCREEN, 11);
    private Command cmdNewList=new Command ("New list", Command.SCREEN, 12);
    private Command cmdDelete=new Command ("Delete list", Command.SCREEN, 13);
    //private Command cmdEdit=new Command ("Edit list", Command.SCREEN, 14);
    private Command cmdIL=new Command ("Make Ignore-List", Command.SCREEN, 16);
    
    JabberStream stream=StaticData.getInstance().roster.theStream;
    
    /** Creates a new instance of PrivacySelect */
    public PrivacySelect(Display display) {
        super(display);
        createTitleItem(1, "Privacy Lists", null);
        addCommand(cmdActivate);
        addCommand(cmdDefault);
        addCommand(cmdCancel);
        addCommand(cmdNewList);
        addCommand(cmdDelete);
        //addCommand(cmdEdit);
        addCommand(cmdIL);
        setCommandListener(this);
        list.addElement(new PrivacyList(null));//none
        
        getLists();
    }

    private void getLists(){
        stream.addBlockListener(this);
        PrivacyList.privacyListRq(false, null, "getplists");
    }
    
    protected int getItemCount() { return list.size(); }
    protected VirtualElement getItemRef(int index) { return (VirtualElement) list.elementAt(index); }
    public void commandAction(Command c, Displayable d) {
        if (c==cmdCancel) {
            destroyView();
            stream.cancelBlockListener(this);
        }
        if (c==cmdActivate || c==cmdDefault) {
            PrivacyList active=((PrivacyList)getFocusedObject());
            for (Enumeration e=list.elements(); e.hasMoreElements(); ) {
                PrivacyList pl=(PrivacyList)e.nextElement();
                boolean state=(pl==active);
                if (c==cmdActivate) pl.isActive=state; else pl.isDefault=state;
            }
            ((PrivacyList)getFocusedObject()).activate( (c==cmdActivate)? "active":"default" ); 
            getLists();
        }
        if (c==cmdIL) {
            generateIgnoreList();
            //new PrivacyForm(display, PrivacyItem.itemIgnoreList());
            getLists();
        }
        if (c==cmdDelete) {
            if (atCursor!=null) {
                PrivacyList pl=(PrivacyList) atCursor;
                if (pl.name!=null) pl.deleteList();
                getLists();
            }
        }
        if (c==cmdNewList) new MIDPTextBox(display, "New list name", "", this, TextField.URL);
    }
    
    // MIDPTextBox interface
    public void OkNotify(String listName) {
        if (listName.length()>0) (new PrivacyModifyList(display, new PrivacyList(listName))).setParentView(parentView);
    }
    
    public int blockArrived(JabberDataBlock data){
        if (data.getTypeAttribute().equals("result"))
        if (data.getAttribute("id").equals("getplists")) {
            data=data.findNamespace("jabber:iq:privacy");
            if (data!=null) {
                list=new Vector();                
                String activeList="";
                String defaultList="";
                for (Enumeration e=data.getChildBlocks().elements(); e.hasMoreElements();) {
                    JabberDataBlock pe=(JabberDataBlock) e.nextElement();
                    String tag=pe.getTagName();
                    String name=pe.getAttribute("name");
                    if (tag.equals("active")) activeList=name;
                    if (tag.equals("default")) defaultList=name;
                    if (tag.equals("list")) {
                        PrivacyList pl=new PrivacyList(name);
                        pl.isActive=(name.equals(activeList));
                        pl.isDefault=(name.equals(defaultList));
                        list.addElement(pl);
                    }
                }
                PrivacyList nullList=new PrivacyList(null);
                nullList.isActive=activeList.length()==0;
                nullList.isDefault=defaultList.length()==0;
                list.addElement(nullList);//none
            }
            redraw();
            return JabberBlockListener.NO_MORE_BLOCKS;
        }
        return JabberBlockListener.BLOCK_REJECTED;
    }

    public void eventOk(){
        if (atCursor!=null) {
            PrivacyList pl=(PrivacyList) atCursor;
            if (pl.name!=null) new PrivacyModifyList(display, pl);
        }
    }
    private void generateIgnoreList(){
        JabberDataBlock ignoreList=new JabberDataBlock("list", null, null);
        ignoreList.setAttribute("name", Roster.IGNORE_GROUP);
        JabberDataBlock item=PrivacyItem.itemIgnoreList().constructBlock();
        ignoreList.addChild(item);
        PrivacyList.privacyListRq(true, ignoreList, "ignlst");
    }
}