/*
 * IqLast.java
 *
 * Created on 25.07.2006, 19:14
 *
 * Copyright (c) 2005-2008, Eugene Stahov (evgs), http://bombus-im.org
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

package xmpp.extensions;

import Client.StaticData;
import com.alsutton.jabber.JabberBlockListener;
import com.alsutton.jabber.JabberDataBlock;
import com.alsutton.jabber.datablocks.*;
import ui.Time;

/**
 *
 * @author EvgS
 */
public class IqLast implements JabberBlockListener {
    
    public IqLast(){ };

    public int blockArrived(JabberDataBlock data) {
        if (!(data instanceof Iq)) return BLOCK_REJECTED;
        if (!data.getAttribute("type").equals("get")) return BLOCK_REJECTED;
        
        JabberDataBlock query=data.findNamespace("query", "jabber:iq:last");
        if (query==null) return BLOCK_REJECTED;
        
        long last=(Time.utcTimeMillis() - StaticData.getInstance().roster.lastMessageTime)/1000;

        Iq reply=new Iq(data.getAttribute("from"), Iq.TYPE_RESULT, data.getAttribute("id"));
        //reply.addChildNs("query", "jabber:iq:last")
        reply.addChild(query);
        query.setAttribute("seconds", String.valueOf(last));
        
        StaticData.getInstance().roster.theStream.send(reply);
        
        return JabberBlockListener.BLOCK_PROCESSED;

    }
}
