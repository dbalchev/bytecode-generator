package boza;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    /**
     * @param args
     * @throws IOException 
     * @throws ClassNotFoundException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        BytecodeGenerator gen = new BytecodeGenerator ();
        short bozaClassId = gen.getConstantStream ().addConstantClass ("Boza");
        short objectClassId = gen.getConstantStream ().addConstantClass ("java/lang/Object");
        short fieldName = gen.getConstantStream ().addConstantUtf8 ("banica");
        short fieldDesc = gen.getConstantStream ().addConstantUtf8 ("D");
        short codeId    = gen.getConstantStream ().addConstantUtf8 ("Code");
        short methodDesc = gen.getConstantStream ().addConstantUtf8 ("(II)I");
        short methodName = gen.getConstantStream ().addConstantUtf8 ("kifla");
        short airanName = gen.getConstantStream ().addConstantUtf8 ("airan");
        short toSName = gen.getConstantStream ().addConstantUtf8 ("toS");
        short toSDescriptor = gen.getConstantStream ().addConstantUtf8 ("(Ljava/lang/Appendable;Ljava/lang/String;)V");
        
        gen.getFieldStream ().addFieldHeader ((short) 9, fieldName, fieldDesc, (short)0);
        gen.getMethodStream ().addMethodHeader ((short)9, methodName, methodDesc, (short)1, (short)16, (short)16, codeId)
            .iload (0)
            .iconst (0)
            .if_icmpeq ("arg0 is zero")
            .iload (0)
            .ireturn ()
            .label ("arg0 is zero")
            .iload(1)
            .ireturn()
            .flush ();
        gen.getMethodStream ().addMethodHeader ((short)9, airanName, methodDesc, (short) 1, (short)16, (short)16, codeId)
            .iconst (1)
            .istore(2)
            .iload(0)
            .iconst (0)
            .if_icmple ("skip loop")
            .label("loop begin")
            .iload (2)
            .iload (1)
            .imul()
            .istore (2)
            .iinc (0, (byte)-1)
            .iload(0)
            .iconst(0)
            .if_icmpgt ("loop begin")
            .label("skip loop")
            .iload (2)
            .ireturn ()
            .flush ();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        System.out.println ("constants " + gen.getConstantStream ().numConstants);
        gen.write (baos, (short)1, bozaClassId, (short)objectClassId, (short)0, (short)50);
        final Class<?>[] rez = new Class<?>[1]; 
        @SuppressWarnings("all")
        ClassLoader loader = new ClassLoader(ClassLoader.getSystemClassLoader ()) {
            {
                byte[] bytecode = baos.toByteArray ();
                rez[0] = defineClass ("Boza", bytecode, 0, bytecode.length);
            }
            @Override
            public Class<?> loadClass(String name)
                    throws ClassNotFoundException {
                // TODO Auto-generated method stub
                if (name.equals ("Ljava.lang.Object"))
                    return Object.class;
                return ClassLoader.getSystemClassLoader ().loadClass (name);
            }
        };
        Class<?> myClass = rez[0];
        for (Field f: myClass.getDeclaredFields ()) {
            System.out.println (f.getType ().toString () + " " + f.getName ());
        }
        for (Method m: myClass.getDeclaredMethods ()) {
            int a = 5;
            int b = 3;
            System.out.println (m.getName () + "(" + a + ", " + b + ") = " + m.invoke (null, a, b));
            a = 0;
            System.out.println (m.getName () + "(" + a + ", " + b + ") = " + m.invoke (null, a, b));
        }
        System.out.println (myClass.toString ());
    }
   
}
