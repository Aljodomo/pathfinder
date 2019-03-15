package ui.optionPane;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import domain.Serie;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import webaccess.MainWebAccessController;
import webaccess.constantClasses.StreamingSettings;
import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.ErrorTyp;
import webaccess.testHandeling.TVShowException;
import webaccess.testHandeling.TestThread;

public class OptionPanes {

  public static int index;

  public static boolean showOption(String oben, String mitte, String unten) {

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(oben);
    alert.setHeaderText(mitte);
    alert.setContentText(unten);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      return true;
    } else {
      return false;
    }
  }

  public static void showConfirm(String oben, String mitte, String unten) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle(oben);
    alert.setHeaderText(mitte);
    alert.setContentText(unten);
    alert.showAndWait();
  }

  public static ArrayList<String> showCustomInput(String oben, String mitte,
      ArrayList<String> labels, ArrayList<String> fields, int rows, int lines) {

    // Create the custom dialog.
    Dialog<ArrayList<String>> dialog = new Dialog<>();
    dialog.setTitle(oben);
    dialog.setHeaderText(mitte);

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Anwenden", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    ArrayList<TextField> textFields = new ArrayList<TextField>();

    int i = 0;
    for (int l = 0; l < lines; l++) {
      for (int r = 0; r < 2 * rows; r++) {
        grid.add(new Label(labels.get(i)), r, l);
        TextField textField = new TextField(fields.get(i));
        textFields.add(textField);
        grid.add(textField, ++r, l);
        i++;
      }
    }

    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setOnKeyReleased(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        ArrayList<String> erg = new ArrayList<String>();
        for (TextField t : textFields) {
          erg.add(t.getText());
        }
        dialog.setResult(erg);
      }
      if (e.getCode() == KeyCode.ESCAPE) {
        dialog.close();
      }
    });

    loginButton.setDisable(false);

    dialog.getDialogPane().setContent(grid);
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == loginButtonType) {
        ArrayList<String> erg = new ArrayList<String>();
        for (TextField t : textFields) {
          erg.add(t.getText());
        }
        return erg;
      }
      return null;
    });

    Optional<ArrayList<String>> result = dialog.showAndWait();

    if (result.isPresent()) {
      return result.get();
    } else {
      return null;
    }
  }

  public static String[] showInput(String oben, String mitte, String feld1, String feld2,
      String feld3) {
    // Create the custom dialog.
    Dialog<String[]> dialog = new Dialog<>();
    dialog.setTitle(oben);
    dialog.setHeaderText(mitte);

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Anwenden", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField fav1 = new TextField();
    fav1.setText(feld1);
    TextField fav2 = new TextField();
    fav2.setText(feld2);
    TextField fav3 = new TextField();
    fav3.setText(feld3);

    grid.add(new Label("Favorite:"), 0, 0);
    grid.add(fav1, 1, 0);
    grid.add(new Label("Favorite:"), 0, 1);
    grid.add(fav2, 1, 1);
    grid.add(new Label("Favorite:"), 0, 2);
    grid.add(fav3, 1, 2);

    // Enable/Disable login button depending on whether a username was
    // entered.
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(false);

    // Do some validation (using the Java 8 lambda syntax).
    // username.textProperty().addListener((observable, oldValue, newValue)
    // -> {
    // loginButton.setDisable(newValue.trim().isEmpty());
    // });

    dialog.getDialogPane().setContent(grid);

    // Request focus on the username field by default.
    // Platform.runLater(() -> fav1.requestFocus());

    // Convert the result to a username-password-pair when the login button
    // is clicked.
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == loginButtonType) {
        String[] favs = new String[] {fav1.getText(), fav2.getText(), fav3.getText()};
        return favs;
      }
      return null;
    });

    Optional<String[]> result = dialog.showAndWait();
    //
    // result.ifPresent(usernamePassword -> {
    // System.out.println("Username=" + usernamePassword.getKey() + ",
    // Password=" + usernamePassword.getValue());
    // });
    return result.get();
  }

  public static <T> T showButtonSelection(String oben, String mitte, ArrayList<String> namen,
      ArrayList<T> objekte) {
    // Create the custom dialog.
    Dialog<T> dialog = new Dialog<>();
    dialog.setTitle(oben);
    dialog.setHeaderText(mitte);

    // Set the button types.
    ButtonType loginButtonType = new ButtonType("Anwenden", ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    OptionPanes.index = -1;

    for (int i = 0; i < objekte.size(); i++) {
      Button b = new Button(i + ": " + namen.get(i));
      b.setOnAction(e -> {
        Button k = (Button) e.getSource();
        String text = k.getText();
        text = text.substring(0, text.indexOf(":"));
        OptionPanes.index = Integer.parseInt(text);
      });
      grid.add(b, i, 0);
    }
    // Enable/Disable login button depending on whether a username was
    // entered.
    Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    loginButton.setDisable(false);

    // Do some validation (using the Java 8 lambda syntax).
    // username.textProperty().addListener((observable, oldValue, newValue)
    // -> {
    // loginButton.setDisable(newValue.trim().isEmpty());
    // });

    dialog.getDialogPane().setContent(grid);

    // Request focus on the username field by default.
    // Platform.runLater(() -> fav1.requestFocus());

    // Convert the result to a username-password-pair when the login button
    // is clicked.
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == loginButtonType) {
        T erg = objekte.get(index);
        return erg;
      }
      return null;
    });

    Optional<T> result = dialog.showAndWait();
    //
    // result.ifPresent(usernamePassword -> {
    // System.out.println("Username=" + usernamePassword.getKey() + ",
    // Password=" + usernamePassword.getValue());
    // });
    try {
      return result.get();
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  public static void showSiteSelection(String oben, String mitte, ArrayList<String> namen,
      ArrayList<StreamingSitesEnum> objekte, Serie serie) {
    // Create the custom dialog.
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(oben);
    dialog.setHeaderText(mitte);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // index management

    // initiate buttons
    Button[] buttons = new Button[objekte.size()];

    for (int i = 0; i < objekte.size(); i++) {
      buttons[i] = new Button(i + ": " + namen.get(i));
      buttons[i].setOnAction(e -> {
        // calculate/get indexes
        Button k = (Button) e.getSource();
        String text = k.getText();
        text = text.substring(0, text.indexOf(":"));
        int siteIndex = Integer.parseInt(text);

        StreamingSitesEnum site = objekte.get(siteIndex);

        ArrayList<ArrayList<String>> hosts = null;
        try {
          switch (site) {
            case BS:
              // serie.getWebCon().getBs().initialize();
              hosts = serie.getWebCon().getBs().getHoster();
              if (hosts.size() == 0) {
                throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
              }
              break;
            case DEDDL:
              // serie.getWebCon().getDeddl().initialize();
              hosts = serie.getWebCon().getDeddl().getHoster();
              if (hosts.size() == 0 || !StreamingSettings.LanguageTag.equals("de")) { // XXX
                                                                                      // Language
                                                                                      // Setting
                throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
              }
              break;
            case MOVIEK4K:
              serie.getWebCon().getMovie4k().initialize();
              hosts = serie.getWebCon().getMovie4k().getHoster();
              if (hosts.size() == 0 || !StreamingSettings.LanguageTag.equals("de")) { // XXX
                                                                                      // Language
                                                                                      // Setting
                throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
              }
              break;
            case SERIENSTREAM:
              serie.getWebCon().getSs().initialize();
              hosts = serie.getWebCon().getSs().getHoster();
              if (hosts.size() == 0 || !StreamingSettings.LanguageTag.equals("de")) { // XXX
                                                                                      // Language
                                                                                      // Setting
                throw new TVShowException(ErrorTyp.EPISODENOTFOUND);
              }
              break;

            default:
              break;
          }

          // prepare names list like before
          namen.clear();

          for (ArrayList<String> host : hosts) {
            namen.add(host.get(0));
          }

          // start host selection

          // opens a stream from a selected host until the window is
          dialog.close();
          OptionPanes.showHostSelection("Host Auswahl", "Host auswählen", namen, hosts, site);
        } catch (TVShowException e1) {
          Platform.runLater(() -> {
            OptionPanes.showConfirm("Nicht gefunden",
                "Ich konnte diese Folge leider nicht finden...",
                "Versuche es mit einer anderen Seite oder einer an sich anderen Folge :)");
          });
        }
      });
      grid.add(buttons[i], i, 0);
    }

    dialog.getDialogPane().setContent(grid);

    // invisible button to close the dialog
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
    closeButton.managedProperty().bind(closeButton.visibleProperty());
    closeButton.setVisible(false);

    dialog.showAndWait();
  }

  static ArrayList<ArrayList<String>> hoster;
  // contains the current stream index for every host by index in hoster
  static int[] indexe;

  public static void showHostSelection(String oben, String mitte, ArrayList<String> namen,
      ArrayList<ArrayList<String>> objekte, StreamingSitesEnum enu) {
    // Create the custom dialog.
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(oben);
    dialog.setHeaderText(mitte);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    // index management
    OptionPanes.hoster = objekte;
    OptionPanes.indexe = new int[OptionPanes.hoster.size()];
    for (int i = 0; i < OptionPanes.indexe.length; i++) {
      OptionPanes.indexe[i] = 1;
    }

    // initiate buttons
    Button[] buttons = new Button[objekte.size()];

    for (int i = 0; i < objekte.size(); i++) {
      buttons[i] = new Button(i + ": " + namen.get(i));
      buttons[i].setOnAction(e -> {
        // calculate/get indexes
        Button k = (Button) e.getSource();
        String text = k.getText();
        text = text.substring(0, text.indexOf(":"));
        int hostIndex = Integer.parseInt(text);
        int streamIndex = OptionPanes.indexe[hostIndex];
        TestThread thread = new TestThread(OptionPanes.hoster.get(hostIndex).get(0),
            OptionPanes.hoster.get(hostIndex).get(streamIndex), enu);
        thread.start();
        try {
          thread.join();
        } catch (InterruptedException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        if (thread.testState) {
          MainWebAccessController.openURL(thread.url);
        }
        OptionPanes.indexe[hostIndex]++;

        // disable button if no streams remain
        if (streamIndex >= hoster.get(hostIndex).size() - 1) {
          k.setDisable(true);
        }
      });
      grid.add(buttons[i], i, 0);
    }

    dialog.getDialogPane().setContent(grid);

    // invisible button to close the dialog
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
    closeButton.managedProperty().bind(closeButton.visibleProperty());
    closeButton.setVisible(false);

    dialog.showAndWait();
  }

}
