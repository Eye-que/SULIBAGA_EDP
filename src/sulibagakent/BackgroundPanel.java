/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sulibagakent;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 *
 * @author Sulibaga-Ke
 */
public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel() {
        backgroundImage = new ImageIcon(
            getClass().getResource("/sulibagakent/Icons/bg.jpg")
        ).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}