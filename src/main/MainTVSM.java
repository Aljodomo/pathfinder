package main;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import Database.SerieWR;
import domain.Serie;
import javafx.application.Application;
import ui.mainWindow.MainWindow;
import ui.mainWindow.MainWindowController;
import webaccess.constantClasses.StreamingSettings;

/**
 * Startet das Hauptprogramm
 */
public class MainTVSM {

	// public static DatabaseHandler database;
	public static SerieWR databaseNeu;
	public static MainWindowController mwController;
	public static StreamingSettings settings;
	public static TreeSet<Serie> serien;
	public static boolean copyallurlstoclipboard;

	public static void main(String[] args) {
		MainTVSM.copyallurlstoclipboard = false;
		String path = null;
		try {
			path = new File(".").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		path = path + "\\resources\\SerienDatabase";
		MainTVSM.databaseNeu = new SerieWR(path);
		MainTVSM.load();
		Application.launch(MainWindow.class);
	}

	public static void save() {
		databaseNeu.writeSerien(serien);
	}

	public static void load() {
		serien = databaseNeu.readSerien();
	}

	private static void resortList() {
		TreeSet<Serie> newList = new TreeSet<Serie>();
		for (Serie serie : MainTVSM.serien) {
			newList.add(serie);
		}
		MainTVSM.serien = newList;
	}

	public static void refresh() {
		MainTVSM.resortList();
		MainTVSM.save();
		mwController.fillList(serien);
	}
}
