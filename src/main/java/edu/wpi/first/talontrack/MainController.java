package edu.wpi.first.talontrack;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.wpi.first.talontrack.global.CurrentSelections;
import edu.wpi.first.talontrack.global.PathExports;
import edu.wpi.first.talontrack.path.Path;
import edu.wpi.first.talontrack.path.wpilib.WpilibPath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//With the creation of a project many of these functions should be moved out of here
//Anything to do with the directory should be part of a Project object

@SuppressWarnings({ "PMD.UnusedPrivateMethod", "PMD.AvoidFieldNameMatchingMethodName",
    "PMD.GodClass", "PMD.TooManyFields" })
public class MainController {
  private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

  @FXML
  private TreeView<String> autons;
  @FXML
  private TreeView<String> paths;
  @FXML
  private TreeView<String> commandTemplates;
  @FXML
  private TreeView<String> commandInstances;

  @FXML
  private Pane fieldDisplay;
  @FXML
  private FieldDisplayController fieldDisplayController;

  @FXML
  private GridPane editWaypoint;
  @FXML
  private EditWaypointController editWaypointController;

  @FXML
  private GridPane editCommand;
  @FXML
  private EditCommandController editCommandController;

  @FXML
  private TitledPane commandPropertiesTab;
  @FXML
  private TitledPane waypointPropertiesTab;
  @FXML
  private TitledPane autonPane;
  @FXML
  private TitledPane pathPane;
  @FXML
  private TitledPane commandTempPane;
  @FXML
  private TitledPane commandInstPane;
  @FXML
  private Button buildBtn;

  private String directory = ProjectPreferences.getInstance().getDirectory();
  private final String pathDirectory = directory + "/Paths/";
  private final String autonDirectory = directory + "/Autos/";
  private final String commandDirectory = directory + "/"
      + ProjectPreferences.getInstance().getValues().getCommandDir();
  private final String commandInstDirectory = directory + "/Instances/";
  private final String groupDirectory = directory + "/Groups/"; // Legacy dir for backwards compatibility
  private final TreeItem<String> autonRoot = new TreeItem<>("Autons");
  private final TreeItem<String> pathRoot = new TreeItem<>("Paths");
  // private final TreeItem<String> commandRoot = new TreeItem<>("Commands");
  private final TreeItem<String> tempRoot = new TreeItem<>("Templates");
  private static final TreeItem<String> instRoot = new TreeItem<>("Instances");

  private String specialAlertString;
  private boolean autonSelected;

  private TreeItem<String> selected = null;
  private TreeItem<String> selectedCommandTemp = null;
  private TreeItem<String> selectedInstance = null;
  private TreeItem<String> mostRecentCommand = null;

  private Field field;

  private List<CommandTemplate> commandTemplatesArr = new ArrayList<CommandTemplate>();
  private List<CommandInstance> commandInstancesArr = new ArrayList<CommandInstance>();

  private static boolean pathBuilt = false;

  @FXML
  private void initialize() {
    setPaneExpansions();

    commandPropertiesTab.setOnMouseClicked(event -> setPaneExpansions());
    waypointPropertiesTab.setOnMouseClicked(event -> setPaneExpansions());
    autonPane.setOnMouseClicked(event -> setPaneExpansions());
    pathPane.setOnMouseClicked(event -> setPaneExpansions());
    commandTempPane.setOnMouseClicked(event -> setPaneExpansions());
    commandInstPane.setOnMouseClicked(event -> setPaneExpansions());

    field = ProjectPreferences.getInstance().getField();
    setupDrag();

    setupTreeView(autons, autonRoot, FxUtils.menuItem("New Autonomous...", event -> createAuton()));
    setupTreeView(paths, pathRoot, FxUtils.menuItem("New Path...", event -> createPath()));
    setupCommandTreeView(commandTemplates, commandInstances, tempRoot, instRoot,
        FxUtils.menuItem("New Template...", event -> {
          System.out.println("asd");
        }), FxUtils.menuItem("New Instance...", event -> {
          createInstance();
        }));

    // Copying files from the old directory name to the new one to maintain
    // backwards compatibility
    try {
      MainIOUtil.copyGroupFiles(autonDirectory, groupDirectory);
    } catch (IOException e) {
      e.printStackTrace();
    }

    MainIOUtil.setupItemsInDirectory(pathDirectory, pathRoot);
    MainIOUtil.setupItemsInDirectory(autonDirectory, autonRoot);
    MainIOUtil.setupItemsInDirectory(commandDirectory, tempRoot);
    MainIOUtil.setupItemsInDirectory(commandInstDirectory, instRoot);

    setupClickablePaths();
    setupClickableAutons();
    setupClickableCommandTemplates();
    setupClickableCommandInstances();
    loadAllAutons();

    autons.setEditable(true);
    paths.setEditable(true);
    commandTemplates.setEditable(false);
    commandInstances.setEditable(true);
    setupEditable();

    if (pathBuilt) {
      fieldDisplayController.setPathList();
      autonSelected = true;
    }

    importCommands();
    importInstances();
    CurrentSelections.setCurCommandTemplate(commandTemplatesArr.get(0));

    editWaypointController.bindToWaypoint(CurrentSelections.curWaypointProperty(), fieldDisplayController);
    editCommandController.bindToCommand(CurrentSelections.curCommandTemplateProperty(), fieldDisplayController);
    buildBtn.setText(pathBuilt ? "Build Commands" : "Build Paths");
    setPathBuilt(pathBuilt);
  }

  private void setupTreeView(TreeView<String> treeView, TreeItem<String> treeRoot, MenuItem newItem) {
    treeView.setRoot(treeRoot);
    treeView.setContextMenu(new ContextMenu());
    treeView.getContextMenu().getItems().addAll(newItem, FxUtils.menuItem("Delete", event -> delete()));
    treeRoot.setExpanded(true);
    treeView.setShowRoot(false); // Don't show the roots "Paths" and "Autons" - cleaner appearance
  }

  private void setupCommandTreeView(TreeView<String> treeView,
      TreeView<String> treeView2, TreeItem<String> treeRoot, TreeItem<String> treeRoot2, MenuItem newItem,
      MenuItem newItem2) {
    treeView.setRoot(treeRoot);
    treeView.setContextMenu(new ContextMenu());
    treeView.getContextMenu().getItems().addAll(newItem, FxUtils.menuItem("Delete", event -> {
      System.out.println("balls, yes, balls indeed.");
    }));
    treeRoot.setExpanded(true);
    treeView.setShowRoot(false);

    treeView2.setRoot(treeRoot2);
    treeView2.setContextMenu(new ContextMenu());
    treeView2.getContextMenu().getItems().addAll(newItem2, FxUtils.menuItem("Delete", event -> removeInst()));
    treeRoot2.setExpanded(true);
    treeView2.setShowRoot(false);
  }

  private void clearSelectionTreeView(TreeView<String> t) {
    t.getSelectionModel().clearSelection();
  }

  private void select(TreeView<String> t, TreeItem<String> s) {
    t.getSelectionModel().select(s);
  }

  private void setupEditable() {
    autons.setOnEditStart(event -> {
      if (event.getTreeItem().getParent() != autonRoot) {
        SaveManager.getInstance().promptSaveAll(false);
      }
    });
    autons.setOnEditCommit(event -> {
      if (event.getTreeItem().getParent() == autonRoot) {
        MainIOUtil.rename(autonDirectory, event.getTreeItem(), event.getNewValue());
        event.getTreeItem().setValue(event.getNewValue());
      } else {
        MainIOUtil.rename(pathDirectory, event.getTreeItem(), event.getNewValue());
        renameAllPathInstances(event.getTreeItem(), event.getNewValue());
      }
      saveAllAutons();
      loadAllAutons();
    });
    paths.setOnEditStart(event -> {
      SaveManager.getInstance().promptSaveAll(false);
    });
    paths.setOnEditCommit(event -> {
      MainIOUtil.rename(pathDirectory, event.getTreeItem(), event.getNewValue());
      renameAllPathInstances(event.getTreeItem(), event.getNewValue());

      saveAllAutons();
      loadAllAutons();
      fieldDisplayController.removeAllPath();
      fieldDisplayController.addPath(pathDirectory, event.getTreeItem());
    });

    commandInstances.setOnEditStart(event -> {
      SaveManager.getInstance().promptSaveAll(false);
    });
    commandInstances.setOnEditCommit(event -> {
      MainIOUtil.rename(commandInstDirectory, event.getTreeItem(), event.getNewValue());
      event.getTreeItem().setValue(event.getNewValue());
    });
  }

  private void renameAllPathInstances(TreeItem<String> path, String newName) {
    String oldName = path.getValue();

    for (TreeItem<String> instance : getAllInstances(path)) {
      instance.setValue(newName);
    }
    for (TreeItem<String> potential : pathRoot.getChildren()) {
      if (oldName.equals(potential.getValue())) {
        potential.setValue(newName);
      }
    }
  }

  private void loadAllAutons() {
    for (TreeItem<String> item : autonRoot.getChildren()) {
      MainIOUtil.loadAuton(autonDirectory, item.getValue(), item);
    }
  }

  private void saveAllAutons() {
    for (TreeItem<String> item : autonRoot.getChildren()) {
      MainIOUtil.saveAuton(autonDirectory, item.getValue(), item);
    }
  }

  @FXML

  private void delete() {
    if (selected == null) {
      // have nothing selected
      return;
    }
    TreeItem<String> root = getRoot(selected);
    if (selected == root) {
      // clicked impossible thing to delete
      return;
    }
    if (autonRoot == root) {
      return;
    } else if (pathRoot == root && FxUtils.promptDelete(selected.getValue())) {
      fieldDisplayController.removeAllPath();
      SaveManager.getInstance().removeChange(CurrentSelections.curPathProperty().get());
      MainIOUtil.deleteItem(pathDirectory, selected);
      for (TreeItem<String> path : getAllInstances(selected)) {
        removePath(path);
      }
      saveAllAutons();
      loadAllAutons();
    }
  }

  @FXML

  private void removeInst() {
    TreeItem<String> inst = selectedInstance;
    TreeItem<String> root = getRoot(inst);
    if (instRoot == root && FxUtils.promptDelete(inst.getValue())) {
      clearSelectionTreeView(commandInstances);
      select(commandTemplates, inst.getParent());
      SaveManager.getInstance().removeChangeInst(CurrentSelections.curInstProperty().get());
      MainIOUtil.deleteItem(commandInstDirectory, inst);
      // editCommandController.unbindToInstance(CurrentSelections.curInstProperty(),
      // fieldDisplayController);
      editCommandController.bindToCommand(CurrentSelections.curCommandTemplateProperty(), fieldDisplayController);
    }
  }

  @FXML
  private void deleteAuton() {
    if (selected == null) {
      // have nothing selected
      return;
    }
    TreeItem<String> root = getRoot(selected);
    if (selected == root) {
      // clicked impossible thing to delete
      return;
    }
    if (autonRoot == root) {
      if (selected.getParent() == autonRoot) {
        if (FxUtils.promptDelete(selected.getValue())) {
          MainIOUtil.deleteItem(autonDirectory, selected);
        }
      } else {
        removePath(selected);
      }
    } else {
      return;
    }
  }

  @FXML
  private void keyPressed(KeyEvent event) {
    if (event.getCode() == KeyCode.DELETE
        || event.getCode() == KeyCode.BACK_SPACE) {
      delete();
    }
  }

  private List<TreeItem<String>> getAllInstances(TreeItem<String> chosenPath) {
    List<TreeItem<String>> list = new ArrayList<>();
    for (TreeItem<String> auton : autonRoot.getChildren()) {
      for (TreeItem<String> path : auton.getChildren()) {
        if (path.getValue().equals(chosenPath.getValue())) {
          list.add(path);
        }
      }
    }
    return list;
  }

  private void removePath(TreeItem<String> path) {
    TreeItem<String> auton = path.getParent();
    auton.getChildren().remove(path);
    MainIOUtil.saveAuton(autonDirectory, auton.getValue(), auton);
  }

  private TreeItem<String> getRoot(TreeItem<String> item) {
    TreeItem<String> root = item;
    while (root.getParent() != null) {
      root = root.getParent();
    }
    return root;
  }

  private void setupClickablePaths() {
    ChangeListener<TreeItem<String>> selectionListener = new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
          TreeItem<String> newValue) {
        if (!SaveManager.getInstance().promptSaveAll()) {
          paths.getSelectionModel().selectedItemProperty().removeListener(this);
          paths.getSelectionModel().select(oldValue);
          paths.getSelectionModel().selectedItemProperty().addListener(this);
          return;
        }
        selected = newValue;
        autonSelected = false;
        if (newValue != pathRoot && newValue != null) {
          fieldDisplayController.removeAllPath();
          fieldDisplayController.addPath(pathDirectory, newValue);
          CurrentSelections.getCurPath().selectWaypoint(CurrentSelections.getCurPath().getStart());
        }
      }
    };
    paths.getSelectionModel().selectedItemProperty().addListener(selectionListener);
  }

  private void setupClickableCommandTemplates() {
    ChangeListener<TreeItem<String>> selectionListener = new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
          TreeItem<String> newValue) {
        selectedCommandTemp = newValue;
        if (newValue != tempRoot && newValue != null) {
          for (CommandTemplate comTemp : commandTemplatesArr) {
            if (selectedCommandTemp.getValue().equals(comTemp.getName())) {
              CurrentSelections.setCurCommandTemplate(comTemp);
            }
          }

          // if (selectedInstance != null) {
          // editCommandController.unbindToInstance(CurrentSelections.curInstProperty(),
          // fieldDisplayController);
          // }
          editCommandController.bindToCommand(CurrentSelections.curCommandTemplateProperty(), fieldDisplayController);
        }
        clearSelectionTreeView(commandInstances);
        select(commandTemplates, newValue);
      }
    };
    commandTemplates.getSelectionModel().selectedItemProperty().addListener(selectionListener);
  }

  private void setupClickableCommandInstances() {
    ChangeListener<TreeItem<String>> selectionListener = new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
          TreeItem<String> newValue) {
        selectedInstance = newValue;
        if (newValue != instRoot && newValue != null) {
          for (CommandInstance comInst : commandInstancesArr) {
            if (comInst.getName().equals(selectedInstance.getValue())) {
              CurrentSelections.setCurInst(comInst);
            }
          }
          // if (selectedCommandTemp != null) {
          // editCommandController.unbindToCommand(CurrentSelections.curCommandTemplateProperty(),
          // fieldDisplayController);
          // }
          editCommandController.bindToInstance(CurrentSelections.curInstProperty(), fieldDisplayController);
        }
        clearSelectionTreeView(commandTemplates);
        select(commandInstances, newValue);
      }
    };
    commandInstances.getSelectionModel().selectedItemProperty().addListener(selectionListener);
  }

  @FXML
  private void previewJavaFile() {
    for (CommandTemplate comTemp : commandTemplatesArr) {
      if (selectedCommandTemp.getValue() == comTemp.getName()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        FxUtils.applyDarkMode(alert);
        alert.setResizable(true);
        alert.setHeaderText("Command Preview");
        alert.setTitle(comTemp.getName());
        alert.setContentText(comTemp.getCommandPreview());
        alert.show();
      }
    }
  }

  @FXML
  private void info() {
    for (CommandTemplate comTemp : commandTemplatesArr) {
      if (selectedCommandTemp.getValue() == comTemp.getName()) {
        HashMap<String, String> map = comTemp.getParameterMap();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        specialAlertString = new String("Parameters (Name - Type) :\n");
        map.forEach((a, b) -> {
          specialAlertString = specialAlertString.concat(a + " - " + b + "\n");
        });
        FxUtils.applyDarkMode(alert);
        alert.setResizable(true);
        alert.setHeaderText("Command Info");
        alert.setTitle(comTemp.getName());
        alert.setContentText(specialAlertString);
        alert.show();
      }
    }
  }

  private void setupClickableAutons() {
    ChangeListener<TreeItem<String>> selectionListener = new ChangeListener<>() {
      @Override
      public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
          TreeItem<String> newValue) {
        if (newValue == null) {
          return;
        }
        selected = newValue;
        autonSelected = newValue.getParent() == autonRoot ? true : false;
        // autonSelected = true;
        fieldDisplayController.removeAllPath();
        if (newValue != autonRoot) {
          if (newValue.getParent() == autonRoot) { // is an auton with children
            for (TreeItem<String> it : selected.getChildren()) {
              fieldDisplayController.addPath(pathDirectory, it).enableSubchildSelector(FxUtils.getItemIndex(it));
            }
          } else { // has no children so try to display path
            Path path = fieldDisplayController.addPath(pathDirectory, newValue);
            if (FxUtils.isSubChild(autons, newValue)) {
              path.enableSubchildSelector(FxUtils.getItemIndex(newValue));
            }
          }
        }
      }
    };
    autons.getSelectionModel().selectedItemProperty().addListener(selectionListener);
  }

  private boolean validPathName(String oldName, String newName) {
    return MainIOUtil.isValidRename(pathDirectory, oldName, newName);
  }

  private boolean validAutonName(String oldName, String newName) {
    return MainIOUtil.isValidRename(autonDirectory, oldName, newName);
  }

  private void setupDrag() {
    paths.setCellFactory(param -> new PathCell(false, this::validPathName));
    autons.setCellFactory(param -> new PathCell(true, this::validAutonName));

    autons.setOnDragDropped(event -> {
      // simpler than communicating which was updated from the cells
      saveAllAutons();
      loadAllAutons();
    });
  }

  @FXML
  private void flipHorizontal() {
    fieldDisplayController.flip(true);
  }

  @FXML
  private void flipVertical() {
    fieldDisplayController.flip(false);
  }

  @FXML
  private void duplicate() {
    Path newPath = fieldDisplayController.duplicate(pathDirectory);
    TreeItem<String> stringTreeItem = MainIOUtil.addChild(pathRoot, newPath.getPathName());
    SaveManager.getInstance().saveChange(newPath);
    paths.getSelectionModel().select(stringTreeItem);
  }

  @FXML
  private void dupInst() {
    String name = MainIOUtil.getValidFileName(commandInstDirectory,
        selectedInstance.getValue().substring(0, selectedInstance
            .getValue().length() - 5),
        ".inst");
    TreeItem<String> stringTreeItem = MainIOUtil.addChild(instRoot, name);
    CommandInstance newInstance = new CommandInstance(CurrentSelections.getCurInst(), name);
    commandInstancesArr.add(newInstance);
    SaveManager.getInstance().saveInst(newInstance);
    commandInstances.getSelectionModel().select(stringTreeItem);
  }

  @FXML
  private void createPath() {
    String name = MainIOUtil.getValidFileName(pathDirectory, "Unnamed", ".path");
    MainIOUtil.addChild(pathRoot, name);
    Path newPath = new WpilibPath(name);
    // The default path defaults to FEET
    newPath.convertUnit(PathUnits.FOOT, ProjectPreferences.getInstance().getValues().getLengthUnit());
    SaveManager.getInstance().saveChange(newPath);
  }

  @FXML
  private void createAuton() {
    String name = MainIOUtil.getValidFileName(autonDirectory, "Unnamed", "");
    TreeItem<String> auton = MainIOUtil.addChild(autonRoot, name);
    MainIOUtil.saveAuton(autonDirectory, auton.getValue(), auton);
  }

  @FXML
  private void createInstance() {
    String name = MainIOUtil.getValidFileName(commandInstDirectory,
        (CurrentSelections.getCurCommandTemplate().getName().substring(0,
            CurrentSelections.getCurCommandTemplate().getName().length() - 5) + "Inst"),
        ".inst");
    MainIOUtil.addChild(instRoot, name);
    CommandInstance newInstance = new CommandInstance(CurrentSelections.getCurCommandTemplate(), name);
    commandInstancesArr.add(newInstance);
    SaveManager.getInstance().saveInst(newInstance);
  }

  @FXML

  private void buildPaths() {
    if (autonSelected) {
      if (!pathBuilt) {
        if (!SaveManager.getInstance().promptSaveAll()) {
          return;
        }

        java.nio.file.Path output = ProjectPreferences.getInstance().getOutputDir().toPath();
        try {
          Files.createDirectories(output);
        } catch (IOException e) {
          LOGGER.log(Level.WARNING, "Could not export to " + output, e);
        }
        for (TreeItem<String> pathName : pathRoot.getChildren()) {
          Path path = PathIOUtil.importPath(pathDirectory, pathName.getValue());

          java.nio.file.Path pathNameFile = output.resolve(path.getPathNameNoExtension());

          if (!path.getSpline().writeToFile(pathNameFile)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            FxUtils.applyDarkMode(alert);
            alert.setTitle("Path export failure!");
            alert.setContentText("Could not export to: " + output.toAbsolutePath());
          }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        FxUtils.applyDarkMode(alert);
        alert.setTitle("Paths exported!");
        alert.setHeaderText("Path Built");
        alert.setContentText("Paths exported to: " + output.toAbsolutePath());
        alert.show();
        PathExports.initialize();
      }

      pathBuilt = !pathBuilt;
      setPaneExpansions();
      buildBtn.setText(pathBuilt ? "Build Commands" : "Build Paths");
      fieldDisplayController.getPathList().forEach(pl -> pl.getWaypoints().forEach(w -> w.setLineVisible(!pathBuilt)));
      CurrentSelections.setCurPathlist(fieldDisplayController.getPathList());
    } else if (!autonSelected && !pathBuilt) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      FxUtils.applyDarkMode(alert);
      alert.setTitle("Auton not selected.");
      alert.setHeaderText("Build Failed");
      alert.setContentText("Select an Autonomous Routine.");
      alert.show();
    }

  }

  @FXML
  private void editProject() {
    try {
      Pane root = FXMLLoader.load(getClass().getResource("createProject.fxml"));
      Scene scene = fieldDisplay.getScene();
      Stage primaryStage = (Stage) scene.getWindow();
      primaryStage.setHeight(700);
      primaryStage.setMaximized(false);
      primaryStage.setResizable(true);
      scene.setRoot(root);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Couldn't load create project screen", e);
    }
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  private void importCommands() {
    for (TreeItem<String> child : tempRoot.getChildren()) {
      commandTemplatesArr.add(new CommandTemplate(commandDirectory, child.getValue(), child));
    }
    CurrentSelections.setCurCommandTemplateArr(commandTemplatesArr);
  }

  private void importInstances() {
    for (TreeItem<String> child : instRoot.getChildren()) {
      commandInstancesArr.add(InstIOUtil.importInstance(commandInstDirectory, child.getValue()));
      commandInstancesArr.forEach((a) -> {
      });
    }
  }

  private void setPaneExpansions() {
    commandPropertiesTab.setExpanded(pathBuilt);
    waypointPropertiesTab.setExpanded(!pathBuilt);
    autonPane.setExpanded(!pathBuilt);
    pathPane.setExpanded(!pathBuilt);
    commandTempPane.setExpanded(pathBuilt);
    commandInstPane.setExpanded(pathBuilt);
  }

  public static boolean getPathBuilt() {
    return pathBuilt;
  }

  public static void setPathBuilt(boolean b) {
    pathBuilt = b;
  }

  public static TreeItem<String> getInstRoot() {
    return instRoot;
  }
}
