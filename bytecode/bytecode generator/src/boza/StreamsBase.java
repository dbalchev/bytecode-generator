package boza;

import java.io.IOException;
import java.io.OutputStream;

/**
 * a class defining constants and basic writing operations
 */
public class StreamsBase {
    protected OutputStream out;
    /**
     * 
     * @param out the stream where the data is written
     */
    public StreamsBase(OutputStream out) {
        this.out = out;
    }
    /**
     * 
     * @return the stream where the data is written
     */
    public OutputStream getStream() {
        return out;
    }
    /**
     * writes a byte to the stream
     * @param value the value of the byte
     * @return this object
     */
    StreamsBase write8(byte value) throws IOException {
        out.write(value);
        return this;
    }
    /**
     * writes 2 bytes in Big Endian to the stream
     * @param value the value of the bytes
     * @return this object
     */
    StreamsBase write16(short value) throws IOException {
        write8((byte)(value >>> 8));
        write8((byte)(value & 0xff));
        return this;
    }
    /**
     * writes 4 bytes in Big Endian to the stream
     * @param value the value of the bytes
     * @return this object
     */
    StreamsBase write32(int value) throws IOException {
        write16((short)(value >>> 16));
        write16((short)(value & 0xffff));
        return this;
    }

    public static final short ACC_PUBLIC   = 0x0001;  
    public static final short ACC_PRIVATE  =   0x0002 ; 
    public static final short ACC_PROTECTED =  0x0004  ;
    public static final short ACC_STATIC =  0x0008  ;
    public static final short ACC_FINAL  = 0x0010  ;
    public static final short ACC_VOLATILE  =  0x0040;  
    public static final short ACC_TRANSIENT =  0x0080 ; 
    public static final short ACC_SYNTHETIC = 0x1000  ;
    public static final short ACC_ENUM  =  0x4000   ;
}
