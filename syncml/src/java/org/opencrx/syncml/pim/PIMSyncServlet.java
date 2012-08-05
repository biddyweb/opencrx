package org.opencrx.syncml.pim;

import java.io.OutputStream;
import java.util.Date;

import org.openmdx.syncml.SmlException_t;
import org.openmdx.syncml.SmlGenericCmd_t;
import org.openmdx.syncml.SmlItemList_t;
import org.openmdx.syncml.SmlSyncHdr_t;
import org.openmdx.syncml.SmlSync_t;
import org.openmdx.syncml.SmlUtil;
import org.openmdx.syncml.engine.SyncEngine;
import org.openmdx.syncml.engine.SyncOptions;

public class PIMSyncServlet extends SyncEngine {
    
    public PIMSyncServlet(
        SyncOptions options
    ) throws SmlException_t {
        super(options);
    }
    
    /**
     * Callback handling <SyncHdr> tag
     *
     * @param id instance ID
     * @param pSync pointer containing infomation about <SyncHdr> block
     * @return SML_ERR_OK(=0)
     * @see smlInitInstance
     */
    @Override
    public void startMessage(
        SmlSyncHdr_t pSyncHdr
    ) throws SmlException_t {
        System.out.println(new Date() + " INFO   > startMessage()");
        System.out.println(new Date() + " INFO     SyncHdr.VerDTD = " + SmlUtil.smlPcdata2String(pSyncHdr.version));
        System.out.println(new Date() + " INFO     SyncHdr.VerProto = " + SmlUtil.smlPcdata2String(pSyncHdr.proto));
        System.out.println(new Date() + " INFO     SyncHdr.SessionID = " + SmlUtil.smlPcdata2String(pSyncHdr.sessionID));
        System.out.println(new Date() + " INFO     SyncHdr.MsgID = " + SmlUtil.smlPcdata2String(pSyncHdr.msgID));
        System.out.println(new Date() + " INFO     SyncHdr.Flags = " + pSyncHdr.flags);
        System.out.println(new Date() + " INFO     SyncHdr.Target.locURI = " + SmlUtil.smlPcdata2String(pSyncHdr.target.locURI));
        System.out.println(new Date() + " INFO     SyncHdr.Source.locURI = " + SmlUtil.smlPcdata2String(pSyncHdr.source.locURI));
        System.out.println(new Date() + " INFO     SyncHdr.RespURI = " + SmlUtil.smlPcdata2String(pSyncHdr.respURI));
        System.out.println(new Date() + " INFO     SyncHdr.Meta = " + SmlUtil.smlPcdata2String(pSyncHdr.meta));
        System.out.println(new Date() + " INFO   < startMessage()");
    }
    
    /**
     * Callback handling <Sync> command
     *
     * @param id instance ID
     * @param pSync pointer containing infomation about <Sync> block
     * @return SML_ERR_OK(=0)
     * @see smlInitInstance
     */
    @Override
    public void startSync(
        SmlSync_t pSync
    ) throws SmlException_t {
        System.out.println(new Date() + " INFO   > startSync()");
        System.out.println(new Date() + " INFO     Sync.CmdID = " + SmlUtil.smlPcdata2String(pSync.cmdID));
        System.out.println(new Date() + " INFO     Sync.Target.LocURI = " + SmlUtil.smlPcdata2String(pSync.target.locURI));
        System.out.println(new Date() + " INFO     Sync.Source.LocURI = " + SmlUtil.smlPcdata2String(pSync.source.locURI));
        System.out.println(new Date() + " INFO     Sync.NumberOfChange = " + SmlUtil.smlPcdata2String(pSync.noc));
        System.out.println(new Date() + " INFO   < startSync()");
    }
    
    /**
     * Callback handling <Add> command
     *
     * @param id instance ID
     * @param pSync pointer containing infomation about <Add> command
     * @return SML_ERR_OK(=0)
     * @see smlInitInstance
     */
    @Override
    public void addCmd(
        SmlGenericCmd_t pAdd
    ) throws SmlException_t {
        SmlItemList_t ele;
    
        System.out.println(new Date() + " INFO   > addCmd()");
        System.out.println(new Date() + " INFO     Add.CmdID = " + SmlUtil.smlPcdata2String(pAdd.cmdID));
        System.out.println(new Date() + " INFO     Add.Flags = " + pAdd.flags);
        System.out.println(new Date() + " INFO     Add.Meta = " + SmlUtil.smlPcdata2String(pAdd.meta));
    
        ele = pAdd.itemList;
        while (ele != null) {
            System.out.println(new Date() + " INFO     Add.ItemList.Item.Source.LocURI = " + SmlUtil.smlPcdata2String(ele.item.source.locURI));
            System.out.println(new Date() + " INFO     Add.ItemList.Item.Data = " + SmlUtil.smlPcdata2String(ele.item.data));
            /* putInDB(ele.item); */
            ele = ele.next;
        }
        System.out.println(new Date() + " INFO   < addCmd()");
    }
    
    @Override
    public void endSync(
    ) throws SmlException_t {
        System.out.println(new Date() + " INFO   > endSync()");
        System.out.println(new Date() + " INFO   < endSync()");
    }
    
    @Override
    public void endMessage(
        boolean isFinal
    ) throws SmlException_t {
        System.out.println(new Date() + " INFO   > endMessage()");
        System.out.println(new Date() + " INFO   < endMessage()");
    }

    @Override
    public void getResponse(
        OutputStream response
    ) throws SmlException_t {
    }
    
}
