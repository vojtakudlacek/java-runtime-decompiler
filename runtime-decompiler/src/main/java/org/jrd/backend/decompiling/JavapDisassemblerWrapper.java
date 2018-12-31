package org.jrd.backend.decompiling;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class JavapDisassemblerWrapper {

    public String decompile(byte[] bytecode, String[] options){
        File tempByteFile = null;
        File tempOutputFile = null;
        try {
            tempByteFile = bytesToFile(bytecode);
            tempOutputFile = File.createTempFile("decompile-output", ".java");
            PrintWriter printWriter = new PrintWriter(tempOutputFile);
            StringBuilder OptionsString = new StringBuilder();
            System.out.println(Arrays.toString(options));
            if (options != null){
                for (String option: options){
                    OptionsString.append(option);
                }
            }
            com.sun.tools.javap.Main.run(new String[]{OptionsString.toString(), tempByteFile.getAbsolutePath()}, printWriter);
            return readStringFromFile(tempOutputFile.getAbsolutePath());
        } catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return "Exception while decompiling" + errors.toString();
        } finally {
            if (tempByteFile != null){
                tempByteFile.delete();
            }
            if (tempByteFile != null){
                tempOutputFile.delete();
            }
        }
    }

    private File bytesToFile(byte[] bytes) throws IOException {
        File tempFile = File.createTempFile("temporary-byte-file", ".class");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile.getCanonicalPath());
        fos.write(bytes);
        fos.close();
        return tempFile;
    }

    private String readStringFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
