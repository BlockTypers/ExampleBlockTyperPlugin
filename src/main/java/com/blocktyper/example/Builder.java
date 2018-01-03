package com.blocktyper.example;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Builder {

    private final String name;
    private final String group;
    private final String noSpacesName;
    private final String pkg;
    private final String tag;
    private final String caps;
    private final List<File> files;
    private final File projectDir;
    private final File codeDir;
    private final File resourcesDir;

    Builder(String inputName, String group) throws Exception {
        this.name = cap(splitCamelCase(inputName.replace(" ", "")));
        this.group = group;
        this.files = Arrays.asList(new File(".").listFiles());

        this.noSpacesName = name.replace(" ", "");
        this.pkg = name.replace(" ", "").toLowerCase();
        this.tag = name.toLowerCase().replace(" ", "-");
        this.caps = name.toUpperCase().replace(" ", "_");

        File target = getRootFile("target");
        File projectsDir = Files.createDirectories(Paths.get(target.getAbsolutePath(), "projects")).toFile();
        this.projectDir = Files.createDirectories(Paths.get(projectsDir.getAbsolutePath(), tag)).toFile();
        File srcDir = Files.createDirectories(Paths.get(projectDir.getAbsolutePath(), "src")).toFile();
        File mainDir = Files.createDirectories(Paths.get(srcDir.getAbsolutePath(), "main")).toFile();
        File javaDir = Files.createDirectories(Paths.get(mainDir.getAbsolutePath(), "java")).toFile();
        this.resourcesDir = Files.createDirectories(Paths.get(mainDir.getAbsolutePath(), "resources")).toFile();

        File codeDir = javaDir;
        for(String part : group.split("\\.")) {
            codeDir = Files.createDirectories(Paths.get(codeDir.getAbsolutePath(), part)).toFile();
        }
        codeDir = Files.createDirectories(Paths.get(codeDir.getAbsolutePath(), pkg)).toFile();
        this.codeDir = codeDir;
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<String> groupName = parser.acceptsAll(Arrays.asList("g", "group")).withRequiredArg().defaultsTo("com.blocktyper");
        OptionSpec<String> pluginName = parser.acceptsAll(Arrays.asList("n", "name")).withRequiredArg().defaultsTo("My Plugin");
        OptionSet options = parser.parse(args);


        Builder builder = new Builder(options.valueOf(pluginName), options.valueOf(groupName));
        builder.createPlugin();

        System.out.println("Example Plugin Builder...");
        System.out.println("name: " + builder.name);
        System.out.println("group: " + builder.group);

    }


    private void createPlugin() throws Exception {
        processPOM();
        copyRootFile(".gitignore");
        copyRootFile("LICENSE");
        copyRootFile("README.md");
        copyRootFile("upgradeVersion");
        copyRootFile("install.sh");
        //copyRootFile("src");

        File src = getRootFile("src");
        File code = getSubFile(src, new ArrayList(Arrays.asList("main","java", "com", "blocktyper", "example")));
        copyFiles(code, codeDir);

        File resources = getSubFile(src, new ArrayList(Arrays.asList("main","resources")));
        copyFiles(resources, resourcesDir);

    }

    private void copyRootFile(String name) throws Exception {
        File source = getRootFile(name);
        copyFiles(source, new File(projectDir, source.getName()));

    }

    private void processPOM() throws Exception {
        File pom = getRootFile("pom.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(pom);

        Element table = document.getDocumentElement();

        for (int i = 0; i < table.getChildNodes().getLength(); i++) {
            Node node = table.getChildNodes().item(i);
            if ("groupId".equals(node.getNodeName())) {
                node.getChildNodes().item(0).setNodeValue(group);
            }
            if ("artifactId".equals(node.getNodeName())) {
                node.getChildNodes().item(0).setNodeValue(tag);
            }
            if ("name".equals(node.getNodeName())) {
                node.getChildNodes().item(0).setNodeValue(name);
            }
        }


        NodeList dependencies = table.getElementsByTagName("dependency");
        for (int i = 0; i < dependencies.getLength(); i++) {
            Node dependency = dependencies.item(i);
            NodeList elements = dependency.getChildNodes();
            for (int j = 0; j < elements.getLength(); j++) {
                if ("groupId".equals(elements.item(j).getNodeName())) {
                    String groupId = elements.item(j).getChildNodes().item(0).getNodeValue();
                    if ("net.sf.jopt-simple".equals(groupId)) {
                        dependency.getParentNode().removeChild(dependency);
                    }
                }
            }
        }

        Node targetPath = table.getElementsByTagName("targetPath").item(0);
        targetPath.getChildNodes().item(0).setNodeValue(group.replace(".", "/") + "/" + pkg + "/resources");

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(new DOMSource(document), new StreamResult(new File(projectDir, pom.getName())));
    }

    private File getRootFile(String name) {
        return files.stream().filter(f -> name.equals(f.getName())).findFirst().get();
    }

    private File getSubFile(File file, List<String> names) {
        File subFile = (getSubFile(file, names.remove(0)));
        if(names.isEmpty()) {
            return subFile;
        }
        return getSubFile(subFile, names);
    }

    private File getSubFile(File file, String name) {
        return Arrays.asList(file.listFiles()).stream().filter(f -> name.equals(f.getName())).findFirst().get();
    }


    private void copyFiles(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String files[] = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file.replace("Example", noSpacesName));
                copyFiles(srcFile, destFile);
            }
        } else {
            if(!source.getName().endsWith(this.getClass().getSimpleName() + ".java")) {
                Path path = Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Charset charset = StandardCharsets.UTF_8;

                String content = new String(Files.readAllBytes(path), charset);
                content = alterContent(source.getName(), content);
                Files.write(path, content.getBytes(charset));
            }
        }
    }

    private String alterContent(String fileName, String content) {
        content = content.replaceAll("package com.blocktyper", "package " + group);
        content = content.replaceAll("main: com.blocktyper", "main: " + group);
        content = content.replaceAll("\"com.blocktyper", "\"" + group);


        content = content.replaceAll("example", pkg);
        content = content.replaceAll("Example", noSpacesName);
        content = content.replaceAll("EXAMPLE", caps);
        return content;
    }

    private static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    private static String cap(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
