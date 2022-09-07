package edu.wpi.first.talontrack.global;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import edu.wpi.first.talontrack.CommandInstance;

public class CommandExporter {
  public static void exportCommands(String name, CommandInstance... insts) {
    CompilationUnit mainUnit = new CompilationUnit();
    ClassOrInterfaceType c = new ClassOrInterfaceType().setName("ParallelCommandGroup");
    ClassOrInterfaceDeclaration mainClass = mainUnit.addClass(name).setExtendedType(0, c);
    mainClass.addConstructor(Keyword.PUBLIC);
    // mainClass.getConstructors().get(0).metho
  }
}
