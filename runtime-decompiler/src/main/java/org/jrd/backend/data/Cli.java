package org.jrd.backend.data;

import org.jrd.backend.core.AgentRequestAction;
import org.jrd.backend.core.VmDecompilerStatus;
import org.jrd.backend.decompiling.DecompilerWrapperInformation;
import org.jrd.backend.decompiling.PluginManager;
import org.jrd.frontend.MainFrame.VmDecompilerInformationController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

public class Cli {

    private final VmManager vmManager;
    private final PluginManager pluginManager;

    public Cli(Model model) {
        this.vmManager = model.getVmManager();
        this.pluginManager = model.getPluginManager();
    }

    public  void decompile(List<String> args, int i) throws Exception {
        if (args.size() != 4) {
//            throw new RuntimeException(DECOMPILE + " expects exactly three arguments - pid or url of JVM, fully classified class name and decompiler name (as set-up) or decompiler json file, or javap(see help)");
        }
        String jvmStr = args.get(i + 1);
        String classStr = args.get(i + 2);
        String decompilerName = args.get(i + 3);
        try {
            VmInfo vmInfo = vmManager.findVmFromPID(jvmStr);
            VmDecompilerStatus result = obtainClass(vmInfo, classStr, vmManager);
            byte[] bytes = Base64.getDecoder().decode(result.getLoadedClassBytes());
            if (new File(decompilerName).exists() && decompilerName.toLowerCase().endsWith(".json")) {
                throw new RuntimeException("Plugins loading directly from file is not implemented yet.");
            }
            if (decompilerName.startsWith("javap")) {
                String[] split_name = decompilerName.split("-");
                String[] options = new String[split_name.length-1];
                for (int x = 1; x < split_name.length; x++) {
                    options[x - 1] = "-" + split_name[x];
                }
                String decompile_output = pluginManager.decompile(findDecompiler("javap", pluginManager), bytes, options);
                System.out.println(decompile_output);
            } else {
                DecompilerWrapperInformation decompiler = findDecompiler(decompilerName, pluginManager);
                if (decompiler != null) {
                    String decompiledClass = pluginManager.decompile(decompiler, bytes);
                    System.out.println(decompiledClass);
                } else {
                    throw new RuntimeException("Decompiler " + decompilerName + " not found");
                }
            }
        } catch (NumberFormatException e) {
            try {
                URL u = new URL(jvmStr);
                throw new RuntimeException("Remote VM not yet implemented");
            } catch (MalformedURLException ee) {
                throw new RuntimeException("Second param was supposed to be URL or PID", ee);
            }
        }
    }

    private DecompilerWrapperInformation findDecompiler(String decompilerName, PluginManager pluginManager) {
        List<DecompilerWrapperInformation> wrappers = pluginManager.getWrappers();
        DecompilerWrapperInformation decompiler = null;
        for (DecompilerWrapperInformation dw : wrappers) {
            if (!dw.getScope().equals(DecompilerWrapperInformation.LOCAL_SCOPE) && dw.getName().equals(decompilerName)) {
                decompiler = dw;
            }
        }
        //LOCAL is preferred one
        for (DecompilerWrapperInformation dw : wrappers) {
            if (dw.getScope().equals(DecompilerWrapperInformation.LOCAL_SCOPE) && dw.getName().equals(decompilerName)) {
                decompiler = dw;
            }
        }
        return decompiler;
    }

    private void printBytes(List<String> args, int i, boolean bytes) throws IOException {
        if (args.size() != 3) {
//            throw new RuntimeException(BYTES + " and " + BASE64 + " expect exactly two arguments - pid or url of JVM and fully classified class name");
        }
        String jvmStr = args.get(i + 1);
        String classStr = args.get(i + 2);
        try {
            VmInfo vmInfo = vmManager.findVmFromPID(jvmStr);
            VmDecompilerStatus result = obtainClass(vmInfo, classStr, vmManager);
            if (bytes) {
                byte[] ba = Base64.getDecoder().decode(result.getLoadedClassBytes());
                System.out.write(ba);
            } else {
                System.out.println(result.getLoadedClassBytes());
            }
        } catch (NumberFormatException e) {
            try {
                URL u = new URL(jvmStr);
                throw new RuntimeException("Remote VM not yet implemented");
            } catch (MalformedURLException ee) {
                throw new RuntimeException("Second param was supposed to be URL or PID", ee);
            }
        }
    }

//    public void listClasses(String VMPIDorURL) {
////        String param = args.get(i + 1);
//        try {
//            VmInfo vmInfo = vmManager.findVmFromPID(param);
//            AgentRequestAction request = VmDecompilerInformationController.createRequest(vmInfo, null, AgentRequestAction.RequestAction.CLASSES);
//            String response = VmDecompilerInformationController.submitRequest(vmManager, request);
//            if (response.equals("ok")) {
//                String[] classes = vmInfo.getVmDecompilerStatus().getLoadedClassNames();
//                for (String clazz : classes) {
//                    System.out.println(clazz);
//                }
//            }
//            if (response.equals("error")) {
//                throw new RuntimeException(VmDecompilerInformationController.CLASSES_NOPE);
//
//            }
//        } catch (NumberFormatException e) {
//            try {
//                URL u = new URL(param);
//                throw new RuntimeException("Remote VM not yet implemented");
//            } catch (MalformedURLException ee) {
//                throw new RuntimeException("Second param was supposed to be URL or PID", ee);
//            }
//        }
//    }

    public void listPlugins() {
        List<DecompilerWrapperInformation> wrappers = pluginManager.getWrappers();
        for (DecompilerWrapperInformation wrapper : wrappers) {
            System.out.println(wrapper.getName() + " " + wrapper.getScope() +
                    "/" + invalidityToString(wrapper.isInvalidWrapper()) + " - " + wrapper.getFileLocation());
        }
    }

    public void listJvms() {
        for (VmInfo vmInfo : vmManager.getVmInfoSet()) {
            System.out.println(vmInfo.getVmPid() + " " + vmInfo.getVmName());
        }
    }

    private static String invalidityToString(boolean invalidWrapper) {
        if (invalidWrapper) {
            return "invalid";
        } else {
            return "valid";
        }
    }

    private static VmDecompilerStatus obtainClass(VmInfo vmInfo, String clazz, VmManager manager) {
        AgentRequestAction request = VmDecompilerInformationController.createRequest(vmInfo, clazz, AgentRequestAction.RequestAction.BYTES);
        String response = VmDecompilerInformationController.submitRequest(manager, request);
        if (response.equals("ok")) {
            return vmInfo.getVmDecompilerStatus();
        } else {
            throw new RuntimeException(VmDecompilerInformationController.CLASSES_NOPE);

        }
    }
}
