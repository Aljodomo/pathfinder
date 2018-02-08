package Database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import domain.Serie;
import main.MainTVSM;
import webaccess.MainWebAccessController;
import webaccess.constantClasses.StreamingSettings;

public class SerieWR {
	ObjectOutputStream zumFile;
	ObjectInputStream vomFile;
	File file;

	public SerieWR(String path) {
		this.file = new File(path);
		if (!this.file.exists()) {
			try {
				MainTVSM.settings = new StreamingSettings("streamango", "openload", "streamcloud");
				this.writeSerien(new TreeSet<Serie>());
				this.file.createNewFile();
				String s = this.createStartMessage();
				JOptionPane.showMessageDialog(null, s.substring(0, s.indexOf("1.")));

				JOptionPane.showMessageDialog(null, s.substring(s.indexOf("1."), s.indexOf("2.")));

				JOptionPane.showMessageDialog(null, s.substring(s.indexOf("2."), s.indexOf("3.")));

				JOptionPane.showMessageDialog(null, s.substring(s.indexOf("3."), s.indexOf("4.")));

				JOptionPane.showMessageDialog(null, s.substring(s.indexOf("4."), s.length()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String createStartMessage() {
		InputStreamReader fr;
		try {
			fr = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("StartText.txt"));
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			String s;
			while ((s = br.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
			br.close();

			return sb.toString();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void writeSerien(TreeSet<Serie> list) {
		try {
			zumFile = new ObjectOutputStream(new FileOutputStream(this.file));
			zumFile.writeObject(MainTVSM.settings);
			zumFile.writeInt(list.size());
			for (Serie s : list) {
				zumFile.writeObject(s);
			}
			this.zumFile.flush();

		} catch (

		IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public TreeSet<Serie> readSerien() {
		try {
			vomFile = new ObjectInputStream(new FileInputStream(this.file));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			MainTVSM.settings = (StreamingSettings) vomFile.readObject();
		} catch (ClassNotFoundException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TreeSet<Serie> list = null;
		try {
			list = new TreeSet<Serie>();
			int size = vomFile.readInt();
			for (int i = 0; i < size; i++) {
				Serie s = (Serie) vomFile.readObject();
				s.setWebCon(new MainWebAccessController(s));
				list.add(s);
			}
			return list;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

}
