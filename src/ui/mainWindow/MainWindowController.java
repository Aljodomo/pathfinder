package ui.mainWindow;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import domain.Serie;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import main.MainTVSM;
import ui.createSerie.CreateSerieController;
import ui.createSerie.CreateSerieMain;
import ui.optionPane.OptionPanes;
import webaccess.constantClasses.StreamingSettings;
import webaccess.constantClasses.StreamingSitesEnum;

public class MainWindowController {

  // views
  @FXML
  private TextField searchField;
  @FXML
  public ListView<Serie> listPanel;
  @FXML
  private Button play;
  @FXML
  private Button neu;
  @FXML
  private Button edit;
  @FXML
  private Button delete;
  @FXML
  private Button details;

  // animations
  private ScaleTransition st;
  private RotateTransition rt = null;
  private int count = 0;

  // general
  private MainWindow main;

  // different
  private boolean workingMode;
  private boolean unselectedMode;
  private Service<String> playService;

  public void setMain(MainWindow main) {
    this.main = main;
  }

  public void ini() {
    MainTVSM.mwController = this;
    this.listPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    MainTVSM.refresh();

    // handle play. Ensures uninteruppted animations on the mainWindow while
    // starting the next episode
    class PlayService extends Service<String> {
      @Override
      protected Task<String> createTask() {
        return new Task<String>() {
          @Override
          protected String call() {
            Serie serie = MainTVSM.mwController.listPanel.getSelectionModel().getSelectedItem();
            // Werte werden gespeichert falls der benutzer nicht
            // speichern möchte.
            int seas = serie.getSeason();
            int epi = serie.getEpisode();
            if (serie.startNext()) {
              Platform.runLater(new Runnable() {
                @Override
                public void run() {
                  if (!OptionPanes.showOption("Fortschritt merken",
                      "Ich habe dir die nächste Folge geöffnet :)",
                      "Ich bin nun bereit die Folge als bereits gesehen zu vermerken, falls du das möchtest.")) {
                    serie.setSeason(seas);
                    serie.setEpisode(epi);
                  }
                  serie.refreshLatestUse();
                  MainTVSM.refresh();
                  // Update UI here
                }
              });
            }
            MainTVSM.mwController.setWorkingMode(false);
            return "true";
          }
        };
      }
    }
    this.playService = new PlayService();
    this.setUnselectedMode(true);

    this.listPanel.getSelectionModel().selectedItemProperty()
        .addListener((oberservable, newvalue, oldvalue) -> {
          this.setUnselectedMode(this.listPanel.getSelectionModel().isEmpty());
        });
  }

  // General

  public void fillList(TreeSet<Serie> serien) {
    this.listPanel.getItems().clear();
    this.listPanel.getItems().addAll(serien);
    this.listPanel.refresh();
  }

  public synchronized void setWorkingMode(boolean mode) {
    this.workingMode = mode;
    if (!this.unselectedMode) {
      this.play.setDisable(mode);
      this.edit.setDisable(mode);
      this.delete.setDisable(mode);
      this.details.setDisable(mode);
    }
    if (mode) {
      this.main.primaryStage.getScene().setCursor(Cursor.WAIT);
    } else {
      this.main.primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
    this.neu.setDisable(mode);
  }

  public synchronized void setUnselectedMode(boolean mode) {
    this.unselectedMode = mode;
    this.play.setDisable(mode);
    this.edit.setDisable(mode);
    this.delete.setDisable(mode);
    this.details.setDisable(mode);
  }

  // Handlers

  public void handlePlay() {
    this.setWorkingMode(true);
    this.playService.restart();
  }

  public void handleEnterPlay(KeyEvent e) {
    if (!this.workingMode) {
      if (e.getCode().equals(KeyCode.ENTER)) {
        this.handlePlay();
      }
    }
  }

  /*
   * workingMode auf false
   */
  public void handleManualPlay() {
    this.setWorkingMode(true);
    Serie serie = this.listPanel.getSelectionModel().getSelectedItem();

    ArrayList<String> labels = new ArrayList<>();
    labels.add("Staffel :");
    labels.add("Folge :");

    int oldst = serie.getSeason();
    int oldep = serie.getEpisode();

    ArrayList<String> fields = new ArrayList<>();
    fields.add(oldst + "");
    fields.add(oldep + "");

    ArrayList<String> ergs = OptionPanes.showCustomInput("Abzuspielende Folge",
        "Welche Folge soll ich für dich suchen? :)", labels, fields, 2, 1);

    if (ergs == null) {
      this.setWorkingMode(false);
      return;
    }

    int st = Integer.parseInt(ergs.get(0));
    int ep = Integer.parseInt(ergs.get(1));

    serie.setSeason(st);
    serie.setEpisode(ep);

    ArrayList<String> namen = new ArrayList<>();
    ArrayList<StreamingSitesEnum> sites = new ArrayList<>();

    // namen contains the names to be displayed on the buttons
    // sites contains all possible return values for further calculations
    for (StreamingSitesEnum e : StreamingSitesEnum.values()) {
      namen.add(e.toString());
      sites.add(e);
    }

    // returns the site to get the hosts from
    OptionPanes.showSiteSelection("Seiten Auswahl", "Seite auswählen", namen, sites, serie);

    serie.setSeason(oldst);
    serie.setEpisode(oldep);

    this.setWorkingMode(false);
  }

  public void handleNew() {
    this.setWorkingMode(true);
    CreateSerieController.alteserie = null;
    CreateSerieMain sFrame = new CreateSerieMain();
    sFrame.start();
  }

  public void handleHosts() {
    this.setWorkingMode(true);
    Serie serie = this.listPanel.getSelectionModel().getSelectedItem();
    try {
      String[] hoster = OptionPanes.showInput("Hoster Auswahl",
          "Hier kannst du deine liebslings Hoster für diese Serie eintragen :)",
          serie.getFavoritenHosts().favoriteHoster[0], serie.getFavoritenHosts().favoriteHoster[1],
          serie.getFavoritenHosts().favoriteHoster[2]);
      StreamingSettings setting = new StreamingSettings(hoster[0], hoster[1], hoster[2]);
      serie.setFavoritenHosts(setting);
    } catch (NoSuchElementException e) {

    }
    this.setWorkingMode(false);
  }

  public void handleEdit() {
    this.setWorkingMode(true);
    CreateSerieController.alteserie = this.listPanel.getSelectionModel().getSelectedItem();
    CreateSerieMain sFrame = new CreateSerieMain();
    sFrame.start();
  }

  public void handleDelete() {
    this.setWorkingMode(true);
    Boolean c = OptionPanes.showOption("Serie Löschen",
        this.listPanel.getSelectionModel().getSelectedItem().getName() + " Löschen?", "");

    if (c) {
      MainTVSM.serien.remove(this.listPanel.getSelectionModel().getSelectedItem());
      MainTVSM.refresh();
    }
    this.setWorkingMode(false);
  }

  public void handleEnterSearch(KeyEvent e) {
    String search = this.searchField.getText().toLowerCase();
    TreeSet<Serie> suchErgebnis = new TreeSet<>();
    for (Serie serie : MainTVSM.serien) {
      if (serie.getName().toLowerCase().contains(search)) {
        suchErgebnis.add(serie);
      }
    }
    this.fillList(suchErgebnis);
    if (e.getCode().equals(KeyCode.ENTER)) {
      this.searchField.setText("");
    }
  }

  // Animations

  public void handleMouseEnterted(MouseEvent e) {
    Button b = (Button) e.getSource();
    st = new ScaleTransition();
    st.setDuration(Duration.millis(25));
    st.setNode(b);
    st.setToX(1.05);
    st.setToY(1.05);
    st.play();
  }

  public void handleMouseExited(MouseEvent e) {
    st.setToX(1);
    st.setToY(1);
    st.play();
  }

  public void handleMouseClicked(MouseEvent e) {
    Button b = (Button) e.getSource();
    rt = new RotateTransition();
    rt.setDuration(Duration.millis(250));
    rt.setNode(b);
    rt.setToAngle(++count * 360);
    rt.play();
  }

  public void handleClipboardMode() {
    if (MainTVSM.copyallurlstoclipboard) {
      MainTVSM.copyallurlstoclipboard = false;
    } else {
      MainTVSM.copyallurlstoclipboard = true;
    }
  }

  public void handleEnglishSelected() {
    if (StreamingSettings.LanguageTag.equals("de")) {
      StreamingSettings.LanguageTag = "en";
    } else {
      StreamingSettings.LanguageTag = "de";
    }
  }

}
