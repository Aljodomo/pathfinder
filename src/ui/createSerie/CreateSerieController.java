package ui.createSerie;

import java.net.MalformedURLException;

import domain.Links;
import domain.Serie;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.MainTVSM;
import ui.optionPane.OptionPanes;
import webaccess.MainWebAccessController;

public class CreateSerieController {
	CreateSerieMain createSerieMain;

	// views
	@FXML
	private TextField nameField;
	@FXML
	private TextField staffelField;
	@FXML
	private TextField folgeField;
	@FXML

	// links
	private TextField bsField;
	@FXML
	private TextField deddlField;
	@FXML
	private TextField movie4kField;
	@FXML
	private TextField serienstreamField;

	public static Serie alteserie;

	public void setMain(CreateSerieMain createSerieMain) {
		this.createSerieMain = createSerieMain;
		this.ini();
	}

	public void ini() {

		if (alteserie != null) {
			nameField.setText(alteserie.getName());
			staffelField.setText(alteserie.getSeason() + "");
			folgeField.setText(alteserie.getEpisode() + "");
			movie4kField.setText(alteserie.getLinks().getMovie4kString());
			deddlField.setText(alteserie.getLinks().getDeddlString());
			bsField.setText(alteserie.getLinks().getBsString());
			serienstreamField.setText(alteserie.getLinks().getSerienstreamString());
		}

		if (alteserie == null) {
			this.createSerieMain.window.setTitle("Serie anlegen");
		} else {
			this.createSerieMain.window.setTitle("Serie bearbeiten");
		}

	}

	public void handleSave() {

		String name, bs, deddl, movie4k, ss;
		int season, episode;

		name = nameField.getText();
		if (name == null || name.equals("")) {
			OptionPanes.showConfirm("Falsches Fomrat", "Ein Name ist unabdinglich :)",
					"Denke aber dran den offizellen Namen und keine Abkürzung zu verwenden.");
			return;
		}

		try {
			season = Integer.parseInt(staffelField.getText());

			episode = Integer.parseInt(folgeField.getText());

		} catch (NumberFormatException nfe) {

			if (!staffelField.getText().equals("") && !folgeField.getText().equals("")) {
				OptionPanes.showConfirm("Falsches Fomrat", "Überprüfe doch bitte nochmal deine Eingaben :)",
						"Bei Staffel und Folge dürfen nur Zahlen stehen.");
				return;
			}

			season = 1;
			episode = 0;
		}

		bs = bsField.getText();
		deddl = deddlField.getText();
		movie4k = movie4kField.getText();
		ss = serienstreamField.getText();

		if (alteserie == null) {

			if (movie4k.equals("") && deddl.equals("") && bs.equals("") && ss.equals("")) {

				Links links = MainWebAccessController.searchLinks(name);

				if (links != null) {

					String bst = "", ddlt = "", m4t = "", sst = "";
					int linksfound = 0;

					if (links.getBs() != null) {
						bst = " bs.to\n   -" + links.getBsString().substring(19) + "\n";
						linksfound++;
					}
					if (links.getDeddl() != null) {
						ddlt = " de.ddl.me\n   -" + links.getDeddlString().substring(16) + "\n";
						linksfound++;
					}
					if (links.getMovie4k() != null) {
						m4t = " movie4k.to\n   -" + links.getMovie4kString().substring(18) + "\n";
						linksfound++;
					}
					if (links.getSerienstream() != null) {
						sst = " serienstreams.to\n   -" + links.getSerienstreamString().substring(36) + "\n";
						linksfound++;
					}

					if (linksfound == 0) {
						OptionPanes.showConfirm("Nicht Gefunden",
								"Ich konnte die von dir angegebene Serie nicht in meinen Quellen finden :/",
								"Du kannst die Links aber manuellen über Edit einfügen.");
					}

					OptionPanes.showConfirm("Vervollständigt zu " + linksfound / 4 * 100 + "%",
							"Ich habe Links zu " + name + " gefunden auf\n" + bst + ddlt + m4t + sst,
							"Du kannst die Links auch manuellen über Edit einfügen und bearbeiten.");

					MainTVSM.serien.add(new Serie(name, season, episode, links));

				} else {

					OptionPanes.showConfirm("Nicht Gefunden",
							"Ich konnte die von dir angegebene Serie nicht in meinen Quellen finden :/",
							"Du kannst die Links aber manuellen über Edit einfügen.");

					MainTVSM.serien.add(new Serie(name, season, episode, new Links()));

				}
			} else {
				try {
					MainTVSM.serien.add(new Serie(name, season, episode, new Links(movie4k, deddl, bs, ss)));
				} catch (MalformedURLException e) {
					OptionPanes.showConfirm("Falsches Fomrat", "Überprüfe doch bitte nochmal deine Eingaben :)",
							"Du musst dich bei den Links irgendwo verschrieben haben.");
					return;
				}
			}

		} else {
			try {
				alteserie.edit(name, season, episode, movie4k, deddl, bs, ss);
				MainTVSM.serien.remove(alteserie);
				MainTVSM.serien.add(alteserie);
			} catch (MalformedURLException e) {
				OptionPanes.showConfirm("Falsches Fomrat", "Überprüfe doch bitte nochmal deine Eingaben :)",
						"Du musst dich bei den Links irgendwo verschrieben haben.");
				return;
			}
		}

		alteserie = null;

		MainTVSM.refresh();

		this.createSerieMain.window.close();

		MainTVSM.mwController.setWorkingMode(false);

	}

	public void handleEnterSave(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			this.handleSave();
		}
		if (e.getCode() == KeyCode.ESCAPE) {
			this.handleCancel();
		}
	}

	public void handleCancel() {
		this.createSerieMain.window.close();
		MainTVSM.mwController.setWorkingMode(false);
	}

}
