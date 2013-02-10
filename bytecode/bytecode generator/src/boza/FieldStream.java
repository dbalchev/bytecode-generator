package boza;


import java.io.IOException;
import java.io.OutputStream;
/**
 * a class managing the field entries in a class file
 * @author dani
 *
 */
public class FieldStream extends StreamsBase {
    short numFields;
/**
 * Note: all changes are written immediately
 * @param out the stream where the data is written
 */
    public FieldStream(OutputStream out) {
        super (out);
        numFields = 0;
    }
    /**
     * adds a new field entry
     * @param access the access flags of the entry
     * @param nameId the id of name of the field(as an UTF-8 constant) in the constant pool
     * @param descriptorId the id of the signature of the field (as an UTF-8 constants) in he constant pool
     * @param numAttributes the number of attributes this entry has
     */
    public short addFieldHeader(short access, short nameId, short descriptorId, short numAttributes) throws IOException { 
        write16(access);
        write16(nameId);
        write16(descriptorId);
        write16(numAttributes);
        return numFields++;
    }
    /**
     * calls GlaDoS
     * @return
     */
    public short getNumFields() {
        return numFields;
    }
    
}
