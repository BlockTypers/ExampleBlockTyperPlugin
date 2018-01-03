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
import java.util.Arrays;

public class Builder
{

    public static final String OS = System.getProperty( "os.name" );
    public static final boolean IS_WINDOWS = OS.startsWith( "Windows" );
    public static final File CWD = new File(".");

    public static void main(String[] args) throws Exception
    {


        OptionParser parser = new OptionParser();
        OptionSpec<Void> flag = parser.accepts( "example-flag" );
        //OptionSpec<String> root = parser.accepts( "root" ).withRequiredArg().defaultsTo( "." );

        OptionSpec<File> inputDir = parser.acceptsAll( Arrays.asList( "i", "input-dir" ) ).withRequiredArg().ofType( File.class ).defaultsTo( CWD );
        OptionSpec<File> outputDir = parser.acceptsAll( Arrays.asList( "o", "output-dir" ) ).withRequiredArg().ofType( File.class ).defaultsTo( CWD );

        OptionSet options = parser.parse( args );

        System.out.println("Example Plugin Builder...");
        System.out.println("OS: " + OS);
        System.out.println("Windows: " + IS_WINDOWS);
        System.out.println("Flag: " + options.has(flag));

        printFileMeta(inputDir.value(options));

    }

    private static void printFileMeta(File input) throws Exception{
        for(File file : input.listFiles()){
            System.out.println("file: " + file.getName());
            if(file.getName().equals("pom.xml")) {
                processPOM(file);
            }
        }
    }

    private static void processPOM(File pom) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(pom);
        System.out.println("get pom");

        Element table = document.getDocumentElement();

        NodeList dependencies = table.getElementsByTagName("dependency");
        for(int i = 0; i < dependencies.getLength(); i++) {
            Node dependency = dependencies.item(i);
            NodeList elements = dependency.getChildNodes();
            for(int j = 0; j < elements.getLength(); j++) {
                if("groupId".equals(elements.item(j).getNodeName())) {
                    String groupId = elements.item(j).getChildNodes().item(0).getNodeValue();
                    if("net.sf.jopt-simple".equals(groupId)){
                        dependency.getParentNode().removeChild(dependency);
                    }
                }
            }
        }

//------------------------------------------------------------------------

// -------------- printing the resulting tree to the console -------------
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(new DOMSource(document), new StreamResult(pom));
    }


}
