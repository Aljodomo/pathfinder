package ui.auswahlFrames;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import domain.Serie;
import webaccess.MainWebAccessController;
import webaccess.constantClasses.StreamingSitesEnum;

public class SeitenAuswahl extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new SeitenAuswahl(null);
	}

	private Container c;
	private JPanel buttons;
	private JScrollPane mainPanel;

	public SeitenAuswahl(Serie s) {
		this.setSize(300, 270);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final Dimension dimension = this.getToolkit().getScreenSize();
		this.setLocation((int) ((dimension.getWidth() - this.getWidth()) / 2),
				(int) ((dimension.getHeight() - this.getHeight()) / 2));
		this.c = this.getContentPane();
		this.buttons = new JPanel();
		this.buttons.setLayout(new GridLayout(0, 2));

		SeitenHoerer sh = new SeitenHoerer(this, s);

		for (StreamingSitesEnum site : StreamingSitesEnum.values()) {
			JButton b = new JButton(site.name());
			b.addActionListener(sh);
			this.buttons.add(b);
		}

		this.mainPanel = new JScrollPane(buttons);

		this.c.add(mainPanel);

		this.setVisible(true);
	}
}

class SeitenHoerer implements ActionListener {

	private MainWebAccessController webA;
	private SeitenAuswahl sa;

	public SeitenHoerer(SeitenAuswahl sa, Serie s) {
		this.webA = s.getWebCon();
		this.sa = sa;
	}

	// IN StreamingSite

	// IN MainWebAccessController

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton b = (JButton) e.getSource();

		StreamingSitesEnum en = StreamingSitesEnum.valueOf(b.getText());

		switch (en) {
		case DEDDL:
			new HosterAuswahl(this.webA.getDeddl().getHoster(), StreamingSitesEnum.DEDDL);
			break;
		case MOVIEK4K:
			new HosterAuswahl(this.webA.getMovie4k().getHoster(), StreamingSitesEnum.MOVIEK4K);
			break;
		case BS:
			new HosterAuswahl(this.webA.getBs().getHoster(), StreamingSitesEnum.BS);
			break;
		case SERIENSTREAM:
			new HosterAuswahl(this.webA.getSs().getHoster(), StreamingSitesEnum.SERIENSTREAM);
			break;

		default:
			break;

		}
		this.sa.dispose();
	}

}
