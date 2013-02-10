package boza;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * a class used for generating instructions of the Code attribute of a method entry
 * most(all?) undocumented methods emit instruction opcode. check http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html
 * Note: changes are not written until flush
 * Note2: all instruction emiters and the label generator return this to allow chaining
 * @author dani
 * 
 */
public class CodeStream extends StreamsBase {
    /**
     * a map of already defined labels to their addresses
     */
    Map<String, Integer> labels;
    /**
     * a map of needed labels to lists of the events fixing the offsets of the already defined instructions with targets undefined labels
     */
    Map<String, List<LabelAddEvent>> futureLabels;
    /**
     * the stream where the data is written when flush() is called
     */
    OutputStream realOut;
    /**
     * @param out the stream the data is written to
     */
    public CodeStream(OutputStream out) {
        super (new RandomByteArrayStream ());
        labels = new HashMap<String, Integer> ();
        futureLabels = new HashMap<String, List<LabelAddEvent>>();
        realOut = out;
        
    }
    public void clearLabels() {
        labels.clear ();
    }
    /**
     * adds a label with the specified name pointing to the current position
     * @param name the name of the label
     * @return this object to allow chaining
     */
    public CodeStream label(String name) {
        labels.put (name, getCodeLen());
        List<LabelAddEvent> forThisLabel = futureLabels.remove (name);
        if (forThisLabel != null) {
            for (LabelAddEvent event: forThisLabel) {
                event.labelAddedAtAddress (getCodeLen());
            }
        }
        return this;
    }
    /**
     * emits iadd instruction (pops the top 2 integer values of the operand stack and pushes their sum) 
     */
    public CodeStream iadd () throws IOException{
        write8((byte) 0x60);
        return this;
    }
    /**
     * emits imul instruction (pops the top 2 integer values of the operand stack and pushes their product) 
     */
    public CodeStream imul() throws IOException {
        write8((byte)0x68);
        return this;
    }
    /**
     * emits isub (pops the top 2 integer values of the operand stack and pushes their difference)
     */
    public CodeStream isub() throws IOException {
        write8((byte)0x64);
        return this;
    }
    /**
     * emits a iconst  (pushes an integer constant on the operand stack) 
     * @param c the value of the constant (must be in [-1, 5])
     * @throws IllegalArgumentException if the constant is < -1 or > 5
     */
    public CodeStream iconst(int c) throws IOException {
        if (c < -1 || c > 5)
            throw new IllegalArgumentException ("constants must be in [-1, 5]");
        byte opcode = (byte) (3 + c);
        write8(opcode);
        return this;
    }
    /**
     * emits a iload (pushes an integer representing the specified local slot on the operand stack)
     * uses 1-byte instruction if posible
     * @param refId the index of the local slot
     */
    public CodeStream iload(int refId) throws IOException {
        if (refId < 0 || refId > 255)
            throw new IllegalArgumentException ("reference index must be >= 0");
        if (refId < 4) {
            byte opcode = (byte)(0x1a + refId);
            write8(opcode);
        } else {
            write8((byte) 0x15);
            write8((byte) (refId & 0xff));
        }
        return this;
    }
    /**
     *  emits ireturn (the current method returns the top of the stack integer value)
     */
    public CodeStream ireturn() throws IOException {
        write8((byte) 0xac);
        return this;
    }
    /**
     * generalized method for the if_icmp* instructions
     * @param opcode the opcode of the instruction
     * @param offset the offset of the branch target 
     */
    public CodeStream if_xxx(byte opcode, short offset) throws IOException {
        write8(opcode);
        write16(offset);
        return this;
    }
    /**
     * generalized method for the if_icmp* instructions, handling label management
     * @param opcode the opcode of the instruction
     * @param label the label of the branch target (might be specified later)
     */

    public CodeStream if_xxx(byte opcode, String label) throws IOException {
        Integer targetAddres = labels.get (label);
        short offset;
        if (targetAddres == null) {
            List<LabelAddEvent> forThisLabel = futureLabels.get (label);
            if (forThisLabel == null) {
                forThisLabel = new Vector<LabelAddEvent> ();
                futureLabels.put (label, forThisLabel);
            }
            final int instructionAddr = getCodeLen (); 
            forThisLabel.add (new LabelAddEvent() {
                
                @Override
                public void labelAddedAtAddress(int address) {
                    typedOut ().write16at (instructionAddr + 1, (short)(address - instructionAddr));
                }
            });
            offset = Short.MIN_VALUE;
        } else
            offset = (short)(targetAddres - getCodeLen ());
       
        return if_xxx (opcode, offset);
    }
    /**
     * emits if_icmpeq(pops 2 integer values and branches if they are EQUAL) 
     * @param label the label of the branch target
     */
    public CodeStream if_icmpeq(String label) throws IOException {
        return if_xxx((byte)0x9f, label);
    }
    /**
     * emits if_icmpgt(pops 2 integer values and branches if the first-pushed is GREATER THAN the top of the stack) 
     * @param label the label of the branch target
     */
    public CodeStream if_icmpgt(String label) throws IOException {
        return if_xxx((byte)0xa3, label);
    }
    /**
     * emits if_icmpgt(pops 2 integer values and branches if the first-pushed is LESS or EQUAL the top of the stack) 
     * @param label the label of the branch target
     */
    public CodeStream if_icmple(String label) throws IOException {
        return if_xxx((byte)0xa4, label);
    } 
    /**
     * emits iinc (increments as integer a local slot) 
     * @param refId the index of the local slot
     * @param val the amount of increase(as a signed byte)
     */
    public CodeStream iinc(int refId, byte val) throws IOException {
        if (refId < 0 || refId > 255)
            throw new IllegalArgumentException ("refId must be between [0, 255]");
        write8((byte)0x84);
        write8((byte)refId);
        write8(val);
        return this;
    }
    /**
     * emits istore (pops the integer at the top of the stack and stores it in a local slot)
     * (emits 1-byte instruction if possible)
     * @param refId the index of the local slot
     */
    public CodeStream istore(int refId) throws IOException {
        if (refId < 0 || refId > 255)
            throw new IllegalArgumentException ("istore refId must be in [0, 255]");
        if (refId < 4) {
            byte opcode = (byte)(0x3b + refId);
            write8(opcode);
        } else {
            write8((byte)0x36);
            write8((byte)(refId & 0xff));
        }
        return this;
    }
    public CodeStream invokeinterface(short methodId, byte count) throws IOException {
    	write8((byte)0xb9);
    	write16(methodId);
    	write8(count);
    	write8((byte)0);
    	return this;
    }
    /**
     * emits dwinm(reads the developer's thoughts and does the opposite)
     * @return
     */
    public int getCodeLen() {
        return typedOut ().size ();
    }
    /**
     * @return {@link StreamsBase.out as its correct type}
     */
    protected RandomByteArrayStream typedOut() {
        return (RandomByteArrayStream)out;
    }
    /**
     * writes the emited instructions to the output stream 
     * @throws {@link IllegalStateException} if there are used undefined labels 
     */
    public void flush() throws IOException {
        if (!futureLabels.isEmpty ()) {
            StringBuilder sb = new StringBuilder ("the following labels are undefined ");
            for(String label: futureLabels.keySet ()) {
                sb.append ('"');
                sb.append (label);
                sb.append ('"');
                sb.append(", ");
            }
            throw new IllegalStateException (sb.toString ());
        }
        typedOut ().writeTo (realOut);
        typedOut ().reset (); // in case of second flush()
    }
    /**
     * subclass of {@link ByteArrayOutputStream} with methods for random writes(used when a label is added)
     * @author dani
     *
     */
    static class RandomByteArrayStream extends ByteArrayOutputStream {
        public void write8at(int offset, byte value) {
            buf[offset] = value;
        }
        public void write16at(int offset, short value) {
            write8at(offset, (byte)(value >>>8));
            write8at(offset + 1, (byte)(value & 0xff));
        }
    }
    /**
     * type of the handler witch fixes the offset of branch targets, when the label is defined 
     */
    static interface LabelAddEvent {
        /**
         * the long-waited label is defined
         * @param address the address of the label
         */
        void labelAddedAtAddress(int address);
    }
}
