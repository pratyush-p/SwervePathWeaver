package edu.wpi.first.talontrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.utils.SourceRoot;

import javafx.scene.control.TreeItem;

public class CommandTemplate {

  private String pathString;
  private String name;
  private Path path;
  private File file;
  private FileReader reader;

  private int i = 0;

  public CommandTemplate(String pathString, String name, TreeItem<String> associatedTreeItem) {
    this.pathString = pathString;
    this.name = name;
    initialize();
  }

  public void initialize() {
    path = Path.of(pathString);
    file = new File(pathString, name);
  }

  public String getFullPath() {
    return file.getPath();
  }

  public String getName() {
    return name;
  }

  private CompilationUnit parse() {
    CompilationUnit blank = new CompilationUnit();
    SourceRoot sourceRoot = new SourceRoot(path);
    sourceRoot.setParserConfiguration(new ParserConfiguration());
    try {
      ParseResult<CompilationUnit> parseResult = sourceRoot.tryToParse("", name);
      CompilationUnit cUnit = parseResult.getResult().get();
      return cUnit;
    } catch (IOException e) {
      System.out.println("bru parse broke");
      return blank;
    }
  }

  public HashMap<String, String> getParameterMap() {
    CompilationUnit c = parse();
    int size = c.getType(0).getConstructors().get(0).getParameters().size();
    HashMap<String, String> map = new HashMap<>(6);

    c.getType(0).getConstructors().get(0).getParameters()
        .forEach(p -> {
          if (((p.getTypeAsString().equalsIgnoreCase("double")) || (p.getTypeAsString().equalsIgnoreCase("int")) || (p
              .getTypeAsString().equalsIgnoreCase("String"))) && !(p.getNameAsString().equalsIgnoreCase("length"))) {
            map.put(p.getNameAsString(), p.getTypeAsString());
          }
        });

    List<String> badStrings = new ArrayList<>();
    if (!(map.size() <= 6)) {
      map.forEach((a, b) -> {
        if (i >= 6) {
          badStrings.add(a);
        }
        i++;
      });
      i = 0;
      badStrings.forEach(p -> {
        map.remove(p);
      });
    }

    return map;
  }

  public HashMap<String, String> getRawParameterMap() {
    CompilationUnit c = parse();
    HashMap<String, String> map = new HashMap<>();
    c.getType(0).getConstructors().get(0).getParameters()
        .forEach(p -> {
          map.put(p.getNameAsString(), p.getTypeAsString());
        });

    return map;
  }

  public String getCommandPreview() {
    CompilationUnit c = parse();
    return c.toString();
  }

  public static List<Node> getNodes(List<CompilationUnit> cus, Class nodeClass) {
    List<Node> res = new LinkedList<Node>();
    cus.forEach(cu -> res.addAll(cu.findAll(nodeClass)));
    return res;
  }

}
