package org.jrd.backend.data;

import org.apache.commons.cli.*;
import org.jrd.backend.core.OutputController;
import org.jrd.frontend.MainFrame.MainFrameView;
import org.jrd.frontend.MainFrame.VmDecompilerInformationController;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws Exception{

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        // Boolean
        Option verboseOption = new Option("v", "verbose", false, "Be verbose");
        verboseOption.setOptionalArg(true);
        options.addOption(verboseOption);

        // Main
        OptionGroup mainOptions = new OptionGroup();
        Option helpOption = new Option("h", "help", false, "Print this message");
        mainOptions.addOption(helpOption);
        Option versionOption = Option.builder()
                .longOpt("version")
                .desc("Print version information")
                .build();
        mainOptions.addOption(versionOption);
        Option guiOption = new Option("g", "gui", false, "Launch GUI");
        mainOptions.addOption(guiOption);
        Option listjvmsOption = new Option("l", "listjvms", false, "List JVMs");
        mainOptions.addOption(listjvmsOption);
        Option listpluginsOption = new Option("p", "listplugins", false, "List plugins");
        mainOptions.addOption(listpluginsOption);
        Option listclassesOption = Option.builder("c")
                .longOpt("listclasses")
                .hasArg()
                .argName("PID")
                .desc("List classes")
                .build();
        mainOptions.addOption(listclassesOption);
        Option decompileOption = Option.builder("d") // <PID> <CLASSNAME> <BASE64|BYTES|DecompilerName> [ARGS for decompiler]
                .longOpt("decompile")
                .numberOfArgs(3)
                .argName("PID> <Classname> <decompiler> [args]") //this is dumb
                .desc("Decompile class")
                .build();
        mainOptions.addOption(decompileOption);
        options.addOptionGroup(mainOptions);

        //Parse
        CommandLine commandline;
        try {
            commandline = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("There was an error when parsing arguments: " + e.getMessage());
            System.exit(1);
            return;
        }
        // Verbose
        if (commandline.hasOption("verbose")){
            OutputController.getLogger().setVerbose();
        }
        //mainOptions
        if (commandline.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("java-runtime-decompiler","",  options,
                    "Please report issues at https://github.com/pmikova/java-runtime-decompiler/issues", true);
        }
        Model model = new Model();
        if (commandline.hasOption("gui")){
            setLookAndFeel();
            MainFrameView mainView = new MainFrameView();
            VmDecompilerInformationController controller = new VmDecompilerInformationController(mainView, model);
        }
        Cli Cli = new Cli(model);
        if (commandline.hasOption("listjvms")){
            Cli.listJvms();
        }
        if (commandline.hasOption("listplugins")){
            Cli.listPlugins();
        }
        if (commandline.hasOption("listclasses")){
            System.out.println("Listing classes for JVM with PID " + commandline.getOptionValue("listclasses") + ".");
            System.out.println(Arrays.toString(commandline.getArgs()));
//            Cli.listClasses();
        }
        if (commandline.hasOption("decompile")){
            String[] optionValues = commandline.getOptionValues("decompile");
            String[] decompilerArgs = commandline.getArgs();
            if (decompilerArgs.length > 1){
                System.out.println("Too many arguments for decompile. Use single quotes to pass arguments to decompiler. eg. '-v -c'");
                return;
            } else {
                if (decompilerArgs.length == 0){
                } else {
                    decompilerArgs = new String[]{decompilerArgs[0].replaceAll("'", "")};
                }
            }
            Cli.decompile(optionValues[0], optionValues[1], optionValues[2], decompilerArgs);
        }
    }

    public static void setLookAndFeel(){
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    OutputController.getLogger().log(OutputController.Level.MESSAGE_DEBUG, e);
                }
                break;
            }
        }
    }

}
