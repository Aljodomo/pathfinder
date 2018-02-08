package ui.auswahlFrames;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import webaccess.constantClasses.StreamingSitesEnum;
import webaccess.testHandeling.TestThread;

public class HosterAuswahl extends JFrame {

	private static final long serialVersionUID = 1L;

	private Container c;
	private JScrollPane listenPanel;
	private StreamingSitesEnum site;
	private ArrayList<ArrayList<String>> list;
	private JPanel buttonPanel;

	public HosterAuswahl(ArrayList<ArrayList<String>> list, StreamingSitesEnum site) {
		super("Hoster Auswahl");
		this.site = site;
		this.list = list;

		this.setSize(300, 270);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final Dimension dimension = this.getToolkit().getScreenSize();
		this.setLocation((int) ((dimension.getWidth() - this.getWidth()) / 2),
				(int) ((dimension.getHeight() - this.getHeight()) / 2));

		createComponents();

		this.setVisible(true);
	}

	private void createComponents() {

		c = this.getContentPane();

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 2));

		for (ArrayList<String> hoster : list) {
			Host h = new Host(hoster);
			StartKnopf k = new StartKnopf(h, this.site);
			buttonPanel.add(k);
		}

		listenPanel = new JScrollPane(buttonPanel);
		c.add(listenPanel);

	}
}

class StartKnopf extends JButton {
	private static final long serialVersionUID = 1L;

	Host host;
	StreamingSitesEnum site;
	public int index;
	final int maxI;

	public StartKnopf(Host h, StreamingSitesEnum site) {
		super(h.name);
		this.site = site;
		this.host = h;
		this.index = 0;
		this.maxI = this.host.links.size() - 1;
		this.addActionListener(new Hoerer());
		this.setEnabled(true);
	}

	public int getIndex() throws Exception {
		if (this.index <= this.maxI) {
			this.index++;
			return this.index - 1;
		} else {
			this.setBackground(Color.BLACK);
			this.setEnabled(false);
			throw new Exception();
		}
	}

	public void setIndex(int i) {
		this.index = i;
	}
}

class Hoerer implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		StartKnopf sk = (StartKnopf) e.getSource();

		while (true) {
			TestThread t = null;
			try {
				t = new TestThread(sk.host.name, sk.host.links.get(sk.getIndex()), sk.site);
			} catch (Exception e1) {
				break;
			}

			try {
				sk.getIndex();
				sk.setIndex(--sk.index);
			} catch (Exception e2) {
				sk.setBackground(Color.BLACK);
				sk.setEnabled(false);
			}

			try {
				t.start();
				t.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (t.testState) {
				this.openURL(t.url);
				break;
			} else {
				continue;
			}
		}

	}

	private void openURL(String urlS) {
		try {
			URL url = new URL(urlS);
			Desktop.getDesktop().browse(url.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class Host {
	String name;
	ArrayList<String> links;

	public Host(ArrayList<String> list) {
		this.name = list.get(0);
		list.remove(0);
		this.links = list;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
