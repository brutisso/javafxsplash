package com.looklet.sysmenu;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;

import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.tk.TKSystemMenu;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.stage.Stage;


public class Main extends Application {

    private static Process splashProcess;

    private static String getStandardJavaLauncher() {
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    private static String getCodeLocation() throws Exception {
        URL location = SplashScreenMain.class.getProtectionDomain().getCodeSource().getLocation();
        return Paths.get(location.toURI()).toFile().getCanonicalPath();
    }

    private static void launchSplash() throws Exception {
        String javaBinary = getStandardJavaLauncher();

        String codeLocation = getCodeLocation();

        boolean isJar = codeLocation.endsWith(".jar"); // If we were started from a jar or exploded directory
        String splashCommand = "-splash:" + (isJar ? "" : codeLocation + File.separator) + "splash.jpg";

        String className = SplashScreenMain.class.getCanonicalName();
        ProcessBuilder builder = new ProcessBuilder(javaBinary, splashCommand, "-cp", codeLocation, className);

        if (isJar) {
            // Tell Java which jar to search to find the splash resource
            builder.environment().put("_JAVA_SPLASH_JAR", codeLocation);
        }

        splashProcess = builder.start();
    }

    private static void closeSplash() {
        splashProcess.destroyForcibly();
    }

    public static void main(String[] args) throws Exception{
        launchSplash();
        launch(args);
    }


    private void doSomeInitializationThatTakesALongTime() {
        // Simulate startup work that a real app might have to do
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void start(Stage primaryStage) {
        doSomeInitializationThatTakesALongTime();

        primaryStage.setTitle("Hello World!");
        primaryStage.show();
        closeSplash();

        TKSystemMenu sysMenu = Toolkit.getToolkit().getSystemMenu();
        if (!sysMenu.isSupported()) {
            throw new RuntimeException("System menu not supported");
        }

        MenuBase splashMenu = GlobalMenuAdapter.adapt(new Menu("Splash"));
        sysMenu.setMenus(Collections.singletonList(splashMenu));
    }
}