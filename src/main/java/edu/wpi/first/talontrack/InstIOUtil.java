package edu.wpi.first.talontrack;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import edu.wpi.first.talontrack.global.CurrentSelections;
import edu.wpi.first.talontrack.path.Path;
import edu.wpi.first.talontrack.path.wpilib.WpilibPath;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.geometry.Point2D;

public final class InstIOUtil {
  private static final Logger LOGGER = Logger.getLogger(PathIOUtil.class.getName());

  private InstIOUtil() {
  }

  /**
   * Exports path object to csv file.
   *
   * @param fileLocation the directory and filename to write to
   * @param path         Path object to save
   *
   * @return true if successful file write was preformed
   */
  public static boolean export(String fileLocation, CommandInstance inst) {
    try (
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileLocation + inst.getName()));

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader(getKeysFromHash(inst.getMap())))) {
      double start = inst.getStart();
      double finish = inst.getFinish();
      String name = inst.getName();
      String parentName = inst.getParent().getName();
      Object[] hash = getObjFromHash(inst);
      csvPrinter.printRecord(start, finish, name, parentName, hash[0], hash[1], hash[2], hash[3], hash[4], hash[5]);
      csvPrinter.flush();
    } catch (IOException except) {
      LOGGER.log(Level.WARNING, "Could not save Inst file", except);
      return false;
    }
    return true;
  }

  /**
   * Imports Path object from disk.
   *
   * @param fileLocation Folder with path file
   * @param fileName     Name of path file
   *
   * @return Path object saved in Path file
   */
  public static CommandInstance importInstance(String fileLocation, String fileName) {
    try (Reader reader = Files.newBufferedReader(java.nio.file.Path.of(fileLocation, fileName));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
            .withFirstRecordAsHeader()
            .withIgnoreHeaderCase()
            .withTrim())) {
      CSVRecord rec = csvParser.getRecords().get(0);
      double start = Double.parseDouble(rec.get("Start Time"));
      double finish = Double.parseDouble(rec.get("Finish Time"));
      String name = rec.get("Name");
      String parentName = rec.get("Parent Name");
      CommandTemplate parent = null;
      for (CommandTemplate comTemp : CurrentSelections.getCurCommandTemplateArr()) {
        if (parentName == comTemp.getName()) {
          parent = comTemp;
        }
      }
      List<Object> objs = new ArrayList<>();
      for (int i = 0; i < 6; i++) {
        objs.add(rec.get(i + 4));
      }

      Map<String, Object> valMap = new HashMap<>();

      parent.getParameterMap().forEach((a, b) -> {
        int i = 0;
        if (b == "String") {
          valMap.put(a, objs.get(i).toString());
        } else if (b == "double") {
          valMap.put(a, Double.parseDouble(objs.get(i).toString()));
        } else if (b == "int") {
          valMap.put(a, Integer.parseInt(objs.get(i).toString()));
        }
        i++;
      });

      return new CommandInstance(parent, name, valMap, start, finish);
    } catch (IOException except) {
      LOGGER.log(Level.WARNING, "Could not read Path file", except);
      return null;
    }
  }

  private static String[] getKeysFromHash(Map<String, Object> e) {
    List<String> s = new ArrayList<>(10);
    s.add("Start Time");
    s.add("Finish Time");
    s.add("Name");
    s.add("Parent Name");
    e.forEach((a, b) -> {
      s.add(a);
    });
    for (int i = 0; s.size() < 10; i++) {
      s.add("null" + "[" + i + "]");
    }

    return s.toArray(new String[10]);
  }

  private static Object[] getObjFromHash(CommandInstance inst) {
    List<Object> s = new ArrayList<>();
    inst.getMap().forEach((a, b) -> {
      s.add(b);
    });

    return s.toArray(new Object[6]);
  }
}

// MY BRAIN HURTS SO MUCH I DONT ENJOY THIS
