package boza;

import java.io.IOException;
import java.io.OutputStream;
/**
 * a class managing the constant pool of a classfile
 * @author dani
 *
 */
public class ConstantStream extends StreamsBase {
    short numConstants;
    /**
     * Note: all changes are written in out immediatly!
     * @param out the stream where the data is writen to
     * 
     */
    public ConstantStream(OutputStream out) {
        super(out);
        numConstants = 1; //0 is invalid
    }
    /**
     * adds a class constant
     * @param utf8bytes the UTF-8 representation of the name of the class
     * @return returns the index of the newlycreated constant in this constant pool
     */
    public short addConstantClass(byte [] utf8bytes) throws IOException {
        short nameId = addConstantUtf8(utf8bytes);
        short classId = numConstants++;
        write8 (CONSTANT_Class);
        write16(nameId);
        return classId;
        
    }
    /**
     * adds a class constant
     * @param name the name of the class
     * @return returns the index of the newlycreated constant in this constant pool
     */
    public short addConstantClass(String name) throws IOException {
        return addConstantClass (name.getBytes ("Utf-8"));
        
    }
    /**
     * adds a string constant 
     * @param value the value of the string constant
     * @return the constant index in the constant pool
     */
    public short addConstantString(String value) throws IOException {
        short utf8Id = addConstantUtf8 (value.getBytes ("Utf-8"));
        short stringId = numConstants++;
        write8(CONSTANT_String);
        write16(utf8Id);

        return stringId;
    }
    /**
     * adds an UTF-8 constant
     * @param value the value of the constant
     * @return returns the index of the newlycreated constant in this constant pool
     */
    public short addConstantUtf8(String value) throws IOException {
        return addConstantUtf8 (value.getBytes ("Utf-8"));
    }
    /**
     * adds an UTF-8 constant 
     * @param bytes an array holding the data for this constant
     * @return returns the index of the newlycreated constant in this constant pool
     */
    public short addConstantUtf8(byte[] bytes) throws IOException {
        short utf8Id   = numConstants++;
        write8(CONSTANT_Utf8);
        byte[] utf8 = bytes;
        write16((short)utf8.length);
        out.write (utf8);
        return utf8Id;
    }
    /**
     * adds an integer constant 
     * @param c the value of the constant
     * @return returns the index of the newlycreated constant in this constant pool
     */
    public short addConstantInt(int c) throws IOException {
        short intId = numConstants++;
        write8(CONSTANT_Integer);
        write32(c);
        return intId;
    }
    /**
     * crashes the host machine
     * @return the number of constants added + 1, so this value could be used unmodified in the corresponding field of the classfile 
     */
    public short getNumConstants() {
        return numConstants;
    }
    
    final static byte CONSTANT_Class = 7;
    final static byte CONSTANT_Fieldref =  9;
    final static byte CONSTANT_Methodref = 10;
    final static byte CONSTANT_InterfaceMethodref =    11;
    final static byte CONSTANT_String  =   8;
    final static byte CONSTANT_Integer =   3;
    final static byte CONSTANT_Float=  4;
    final static byte CONSTANT_Long  = 5;
    final static byte CONSTANT_Double  =   6;
    final static byte CONSTANT_NameAndType =   12;
    final static byte CONSTANT_Utf8  = 1;
    final static byte CONSTANT_MethodHandle =  15;
    final static byte CONSTANT_MethodType  =   16;
    final static byte CONSTANT_InvokeDynamic = 18;
}
