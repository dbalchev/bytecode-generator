package boza;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * a class used for classfile generation
 * @author dani
 *
 */
public class BytecodeGenerator {
    ConstantStream constantStream;
    MethodStream methodStream;
    FieldStream fieldStream;
    public BytecodeGenerator() {
        super ();

        constantStream = new ConstantStream(new ByteArrayOutputStream ());
        fieldStream = new FieldStream (new ByteArrayOutputStream ());
        methodStream   = new MethodStream (new ByteArrayOutputStream ());
    }
    /**
     * @return an object with constant management methods
     */
    public ConstantStream getConstantStream() {
        return constantStream;
    }
    /**
     * @return an object with method management methods
     */
    public MethodStream getMethodStream() {
        return methodStream;
    }

    /**
     * @return an object with field management methods
     */
    public FieldStream getFieldStream() {
        return fieldStream;
    }
    /**
     * writes the generated classfile to out 
     * @param out the stream where the classfile is writen
     * @param access access flags for the current class
     * @param this_class id of the class constant of this in the constant pool
     * @param base_class id of the class constant of the base class in the constant pool
     * @param minor minimal minor JVM version required
     * @param major minimal major JVM version required (50 works...i think)
     */
    public void write(OutputStream out, short access, short this_class, short base_class, short minor, short major) throws IOException {
        StreamsBase base = new StreamsBase (out);
        base.write32 (0xCAFEBABE); //magic
        base.write16 (minor);
        base.write16(major);
        base.write16 (constantStream.getNumConstants ());
        ((ByteArrayOutputStream)constantStream.getStream ()).writeTo (out);
        base.write16 (access);
        base.write16(this_class);
        base.write16 (base_class);
        base.write16((short)0);//no interfaces
        base.write16 (fieldStream.getNumFields ());
        ((ByteArrayOutputStream)fieldStream.getStream ()).writeTo (out);
        base.write16(methodStream.getNumMethods ());
        ((ByteArrayOutputStream)methodStream.getStream ()).writeTo (out);
        base.write16((short)0);//no attributes
        
    }
}
