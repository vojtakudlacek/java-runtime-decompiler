package org.jrd.backend.data;

import org.jrd.backend.core.AgentRequestAction;
import org.jrd.backend.core.VmDecompilerStatus;
import org.jrd.backend.decompiling.DecompilerWrapperInformation;
import org.jrd.backend.decompiling.PluginManager;
import org.jrd.frontend.MainFrame.VmDecompilerInformationController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import static org.jrd.frontend.MainFrame.VmDecompilerInformationController.createRequest; //TODO This needs to be moved
import static org.jrd.frontend.MainFrame.VmDecompilerInformationController.submitRequest;

public class Cli {

    private final VmManager vmManager;
    private final PluginManager pluginManager;

    public Cli(Model model) {
        this.vmManager = model.getVmManager();
        this.pluginManager = model.getPluginManager();
    }

    public void decompile(String pid, String className, String decompiler, String[] args) throws Exception{

        DecompilerWrapperInformation wrapperInformation = null;
        for (DecompilerWrapperInformation wrapper : pluginManager.getWrappers()){
            if (wrapper.getName().equals(decompiler)){
                wrapperInformation = wrapper;
            }
        }
        if (wrapperInformation == null){
            return;
        }

        VmInfo vmInfo = null;
        vmInfo = vmManager.findVmFromPID(pid);
        if (wrapperInformation == null){
            return;
        }

        AgentRequestAction request = createRequest(vmInfo, className, AgentRequestAction.RequestAction.BYTES);
        String response = submitRequest(vmManager, request);
        if (response.equals("error")) {
            return;
        }
        VmDecompilerStatus vmStatus = vmInfo.getVmDecompilerStatus();
        String bytesInString = vmStatus.getLoadedClassBytes();
        byte[] bytes = Base64.getDecoder().decode(bytesInString);
        String decompiledClass = pluginManager.decompile(wrapperInformation, bytes, args);
        System.out.println(decompiledClass);
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
        AgentRequestAction request = createRequest(vmInfo, clazz, AgentRequestAction.RequestAction.BYTES);
        String response = submitRequest(manager, request);
        if (response.equals("ok")) {
            return vmInfo.getVmDecompilerStatus();
        } else {
            throw new RuntimeException(VmDecompilerInformationController.CLASSES_NOPE);

        }
    }
}
