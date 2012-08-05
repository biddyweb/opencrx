package org.openmdx.compatibility.base.transport.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmdx.base.io.ByteArrayInputStream;
import org.openmdx.base.io.ByteArrayOutputStream;
import org.openmdx.base.io.NativeDataInputStream;
import org.openmdx.base.io.NativeDataOutputStream;
import org.openmdx.compatibility.base.collection.OffsetArrayList;
import org.openmdx.compatibility.base.collection.SparseList;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSelectors;
import org.openmdx.compatibility.base.dataprovider.cci.AttributeSpecifier;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderObject;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderOperations;
import org.openmdx.compatibility.base.dataprovider.cci.DataproviderRequest;
import org.openmdx.compatibility.base.dataprovider.cci.Directions;
import org.openmdx.compatibility.base.dataprovider.cci.Orders;
import org.openmdx.compatibility.base.dataprovider.cci.UnitOfWorkRequest;
import org.openmdx.compatibility.base.naming.Path;
import org.openmdx.compatibility.base.query.FilterOperators;
import org.openmdx.compatibility.base.query.FilterProperty;
import org.openmdx.compatibility.base.query.Quantors;
import org.openmdx.kernel.id.UUIDs;
import org.openmdx.kernel.id.cci.UUIDGenerator;

public class TestUnitOfWorkRequestMarshaller {

    public static class Timer {
        
        public Timer(
        ) {
            this.duration = 0L;
        }
    
        public void start(
        ) {
            this.startedAt = System.currentTimeMillis();
        }
        
        public void stop(
        ) {
            this.duration += (System.currentTimeMillis() - this.startedAt);
        }

        public void reset(
        ) {
            this.duration = 0L;
        }
        
        public long getDuration(
        ) {
            return this.duration;
        }
        
        private long duration = 0L;
        private long startedAt;
        
    }
        
    //-----------------------------------------------------------------------
    public static void main(
        String[] args
    ) throws IOException, ClassNotFoundException {
        UUIDGenerator uuids = UUIDs.getGenerator();
        DataproviderRequest[] requests = new DataproviderRequest[N_OBJECTS];
        DataproviderObject[] objects = new DataproviderObject[N_OBJECTS];
        
        // Performance array assignment with []
        timer.reset();
        timer.start();
        SparseList[] v = new SparseList[30];
        SparseList values = new OffsetArrayList();
        for(int n = 0; n < N_ASSIGNMENTS; n++) {        
            int i = n % 30;
            v[i] = values;
        }
        timer.stop();
        System.out.println("Assignment with []:");
        System.out.println("  assignments/s: " + (N_ASSIGNMENTS / timer.getDuration()));
        
        // Attribute map
        List<String> attributes = new ArrayList<String>(); 
        Map<String,Integer> attributeIndex = new HashMap<String,Integer>();  
        for(int i = 0; i < N_ATTRIBUTES; i++) {
            String name = "description[" + i + "]";
            attributes.add(name);
            attributeIndex.put(name, i);
        }        
        // Prepare uow
        for(int i = 0; i < requests.length; i++) {
            DataproviderObject objectMax = new DataproviderObject(
                new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/CRX/segment/Standard/account/" + uuids.next().toString())
            );
            for(int j = 0; j < N_ATTRIBUTES; j++) {
                objectMax.values(attributes.get(j)).add(uuids.next().toString());
                if(j % 5 == 0) {
                    objectMax.values(attributes.get(j)).add(uuids.next().toString());                    
                }
            }
            objects[i] = objectMax;
            DataproviderObject objectMin = new DataproviderObject(
                new Path("xri:@openmdx:org.opencrx.kernel.account1/provider/CRX/segment/Standard/account/" + uuids.next().toString())
            );
            requests[i] = new DataproviderRequest(
               objectMin,
               DataproviderOperations.OBJECT_CREATION,
               true
                   ? null
                   : new FilterProperty[]{
                       new FilterProperty(Quantors.THERE_EXISTS, "a[0]", FilterOperators.IS_IN, new String[]{"aaaaaaaa"})
                     },
               0,
               Integer.MAX_VALUE,
               Directions.ASCENDING,
               AttributeSelectors.ALL_ATTRIBUTES,
               true
                   ? null
                   : new AttributeSpecifier[]{
                       new AttributeSpecifier("a[1]", 0, Orders.ASCENDING)
                     }               
            );
        }
        UnitOfWorkRequest[] uow = new UnitOfWorkRequest[1];
        uow[0] = new UnitOfWorkRequest(
            false,
            requests
        );
        
        // Measure speed of loop with byte[]
        timer.reset();
        timer.start();
        byte[] buf = new byte[10000000];       
        for(int i = 0; i < buf.length; i++) {
            buf[i] = (byte)(i % 256);
        }
        timer.stop();
        System.out.println("Write 10M to byte[]:");
        System.out.println("  duration(ms): " + timer.getDuration());
        System.out.println("  kbytes/s: " + (10000000 / timer.getDuration()));

        // Measure speed of loop with ByteBuffer
        timer.reset();
        timer.start();
        ByteBuffer bb = ByteBuffer.allocateDirect(10000000);       
        for(int i = 0; i < 10000000; i++) {
            bb.put((byte)37);
        }
        timer.stop();
        System.out.println("Write 10M to ByteBuffer:");
        System.out.println("  duration(ms): " + timer.getDuration());
        System.out.println("  kbytes/s: " + (10000000 / timer.getDuration()));

        // Measure speed of loop with ByteArrayOutputStream
        timer.reset();
        timer.start();
        ByteArrayOutputStream os = new ByteArrayOutputStream(10000000);        
        for(int i = 0; i < 10000000; i++) {
            os.write((byte)37);
        }
        os.close();
        timer.stop();
        System.out.println("Write 10M to ByteArrayOutputStream:");
        System.out.println("  duration(ms): " + timer.getDuration());
        System.out.println("  kbytes/s: " + (10000000 / timer.getDuration()));

        // Measure Serialize
        timer.reset();
        timer.start();
        int size = 0;
        for(int n = 0; n < RUNS; n++) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(100000);
            ObjectOutputStream oout = new ObjectOutputStream(bout); 
            oout.writeObject(uow);
            oout.flush();
            NativeDataOutputStream nout = new NativeDataOutputStream(bout);
            for(int i = 0; i < N_OBJECTS; i++) {
                objects[i].writeExternal(nout);
            }
            nout.flush();
            bout.close();
            size += bout.size();
            timer.stop();
            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            timer.start();
            ObjectInputStream oin = new ObjectInputStream(bin);
            UnitOfWorkRequest[] uowReply = (UnitOfWorkRequest[])oin.readObject();
            NativeDataInputStream nin = new NativeDataInputStream(bin);
            for(int i = 0; i < N_OBJECTS; i++) {
                objects[i] = new DataproviderObject();
                objects[i].readExternal(nin);
            }            
        }
        timer.stop();
        System.out.println("Serialize:");
        System.out.println("  #runs: " + RUNS);
        System.out.println("  #objects: " + (RUNS*N_OBJECTS));
        System.out.println("  duration(ms): " + timer.getDuration());
        System.out.println("  total size: " + size);
        System.out.println("  kbytes/s: " + (size / timer.getDuration()));
        System.out.println("  obj/s: " + ((1000*RUNS*N_OBJECTS) / timer.getDuration()));
                
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final int N_ATTRIBUTES = 30;
    private static final int N_OBJECTS = 100;
    private static final int N_ASSIGNMENTS = 1000000;
    private static final int RUNS = 100;
    static final Timer timer = new Timer();
    
}
