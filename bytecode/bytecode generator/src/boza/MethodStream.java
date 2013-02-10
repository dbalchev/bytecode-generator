package boza;

import java.io.IOException;
import java.io.OutputStream;

/**
 * a class managing method entries in a classfile
 * @author dani
 *
 */
public class MethodStream extends StreamsBase {
    short numMethods;
    /**
     * Note: no changes are written immediatly.  
     * @param out the stream where the methods are writen
     */
    public MethodStream(OutputStream out) {
        super (out);
        this.out = out;
        numMethods = 0;        
    }
    /**
     * tries to make world peace
     * 
     */
    public short getNumMethods() {
        return numMethods;
    }
    /**
     * Adds a method entry described by the parameters, with code generated with the returned object.  
     * @param access the access flags of the method
     * @param nameId the constant pool index of the method name (as an UTF-8 constant)
     * @param descriptorId the constant pool index of the method signature (as an UTF-8 constant)
     * @param numAttributes the number of attributes this method entry has INCLUDING the code attribute if present 
     * @param maxStack the maximum depth of the operand stack used during method execution 
     * @param maxLocals the maximum number locals used during method execution (NB! take care of long and double locals)
     * @param codeId the id of the UTF-8 constant "Code" in the constant pool 
     * @return an {@link boza.CodeStream} object used to generate the methods code. No changes are made until the method {@link boza.CodeStream.flush} is called, so no hacks are needed if the method is abstract. It can be used as rez.instruction().instruction().flush()
     */
    public CodeStream addMethodHeader(short access, short nameId, short descriptorId, short numAttributes, short maxStack, short maxLocals, short codeId) throws IOException {
        write16(access);
        write16(nameId);
        write16(descriptorId);
        write16(numAttributes);
        numMethods++;
        return new AttributeWritingCodeStream(out, codeId, maxStack, maxLocals);
    }
    /**
     * a helper class that writes the code attribute when {@link CodeStream.flush} is called
     * @author dani
     *
     */
    class AttributeWritingCodeStream extends CodeStream {
        short codeId;
        short maxStack; 
        short maxLocals;
        public AttributeWritingCodeStream(OutputStream out, short codeId, short maxStack, short maxLocals) {
            super(out);
            this.codeId = codeId;
            this.maxLocals = maxLocals;
            this.maxStack = maxStack;
        }
        public void flush() throws IOException {
            int attrlen = getCodeLen () + 12;
            MethodStream.this.write16 (codeId); // index of "Code"
            MethodStream.this.write32 (attrlen); // attr_len
            MethodStream.this.write16 (maxStack); // max_stack
            MethodStream.this.write16 (maxLocals); // max_locals
            MethodStream.this.write32 (getCodeLen ()); // code len
            super.flush(); 
            MethodStream.this.write16((short)0); //exception table len
            MethodStream.this.write16((short)0); // attribute table len
        }
    }
}
