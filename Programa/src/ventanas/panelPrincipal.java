package ventanas;

import aplicacion.App;
import paneles.*;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class panelPrincipal extends JPanel {
    private static final long serialVersionUID = 1L;

    public panelPrincipal() {
        // Configurar el layout del JPanel
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);
        
        JButton btnLocal = new JButton("Archivo Local");
        springLayout.putConstraint(SpringLayout.NORTH, btnLocal, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, btnLocal, 98, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, btnLocal, -480, SpringLayout.SOUTH, this);
        add(btnLocal);
        
        JButton btnOnline = new JButton("Online");
        springLayout.putConstraint(SpringLayout.EAST, btnLocal, -36, SpringLayout.WEST, btnOnline);
        springLayout.putConstraint(SpringLayout.EAST, btnOnline, -76, SpringLayout.EAST, this);
        springLayout.putConstraint(SpringLayout.NORTH, btnOnline, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, btnOnline, 264, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.SOUTH, btnOnline, 0, SpringLayout.SOUTH, btnLocal);
        add(btnOnline);
        
        JPanel content = new JPanel();
        springLayout.putConstraint(SpringLayout.NORTH, content, 6, SpringLayout.SOUTH, btnLocal);
        springLayout.putConstraint(SpringLayout.SOUTH, content, 0, SpringLayout.SOUTH, this);
        springLayout.putConstraint(SpringLayout.WEST, content, 0, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, content, 470, SpringLayout.WEST, this);
        add(content);
        content.setLayout(null);
        
        panelLocal p1 = new panelLocal();
        p1.setSize(470, 536);
        p1.setLocation(0, 0);
        content.removeAll();
        content.add(p1, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
        
        btnOnline.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		panelOnline p2 = new panelOnline();
                p2.setSize(470, 474);
                p2.setLocation(0, 0);
                content.removeAll();
                content.add(p2, BorderLayout.CENTER);
                content.revalidate();
                content.repaint();
        	}
        });
        
        btnLocal.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		panelLocal p1 = new panelLocal();
        		p1.setSize(470, 474);
        		p1.setLocation(0, 0);
        		content.removeAll();
        		content.add(p1, BorderLayout.CENTER);
        		content.revalidate();
        		content.repaint();
        	}
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Seleccionar Archivo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 200);

                // Crear una instancia del JPanel y agregarlo al JFrame
                panelPrincipal fileChooserPanel = new panelPrincipal();
                frame.getContentPane().add(fileChooserPanel);

                // Hacer visible el JFrame
                frame.setVisible(true);
            }
        });
    }
}
