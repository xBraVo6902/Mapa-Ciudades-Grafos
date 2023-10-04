package ventanas;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CruzMilenioPanel extends JPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CruzMilenioPanel() {
		setLayout(null);
		
		JButton btnNewButton = new JButton("Volver");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Principal page= new Principal();
				page.setSize(470,536);
		    	page.setLocation(0,0);
		    	removeAll();
		    	add(page, BorderLayout.CENTER);
		    	revalidate();
		    	repaint();
			}
		});
		btnNewButton.setBounds(371, 502, 89, 23);
		add(btnNewButton);
        // Configura el panel si es necesario
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        File arch = new File("cordenadas.txt");
        Point actual = null;
        try (Scanner read = new Scanner(arch)) {
            while (read.hasNextLine()) {
                String line = read.nextLine();
                String[] parts = line.split(",");
                int x2 = Integer.valueOf(parts[0]);
                int y2 = Integer.valueOf(parts[1]);
                Point next = new Point(x2, y2);

                if (actual == null) {
                    actual = next;
                } else {
                    g.drawLine((int) (actual.getX()) + 10, (int) (actual.getY() + 40), x2 + 10, y2 + 40);
                    actual = next;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
