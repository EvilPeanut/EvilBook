package com.amentrix.evilbook.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.amentrix.evilbook.main.EvilBook;

/**
 * @author cnaude
 */
class Render extends MapRenderer {
    private Image img;
    private final String type;
    private final List<String> rendered = new ArrayList<>();
     
    Render(String file, String type) {
        this.type = type;
        readImage(file);
    }
 
    @Override
    public void render(MapView mv, MapCanvas mc, Player p) {
        // We only render once per player. Without this we lag like crazy.
        if (this.img != null && (!this.rendered.contains(p.getName()))) {
            mc.drawImage(0, 0, this.img); 
            this.rendered.add(p.getName());
            MapListener.mapViews.add(mv);
        } else {
            mv.getRenderers().clear();
        }
    }
    
    private static BufferedImage flipH(BufferedImage img) {        
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }
    
    private void readImage(String file) {
        try {
            File f = new File(file);            
            if (f.exists()) {
                // Load from file, crop and then resize
                BufferedImage bi = ImageIO.read(f);
                if (bi != null) {
	                if (this.type.equals("body")) {
	                    BufferedImage face = bi.getSubimage(8, 8, 8, 8);
	                    BufferedImage faceAcc = bi.getSubimage(40, 8, 8, 8);
	                    BufferedImage leftLeg = bi.getSubimage(4, 20, 4, 12);
	                    BufferedImage rightLeg = flipH(leftLeg);
	                    BufferedImage leftArm = bi.getSubimage(44, 20, 4, 12);
	                    BufferedImage rightArm = flipH(leftArm);
	                    BufferedImage body = bi.getSubimage(20, 20, 8, 12);
	                    BufferedImage combined = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);                    
	                    Graphics2D g = combined.createGraphics(); 
	                    int adj = 8;
	                    g.drawImage(face, 4 + adj, 0, null);
	                    g.drawImage(faceAcc, 4 + adj, 0, null);
	                    g.drawImage(body, 4 + adj, 8, null);
	                    g.drawImage(leftArm, 0 + adj, 8, null);
	                    g.drawImage(rightArm, 12 + adj, 8, null);
	                    g.drawImage(leftLeg, 4 + adj, 20, null);
	                    g.drawImage(rightLeg, 8 + adj, 20, null);  
	                    this.img = combined.getScaledInstance(128, 128, 0);
	                } else if (this.type.equals("url")) {
	                    this.img = bi.getScaledInstance(128, 128, 0);
	                } else {             
	                    BufferedImage combined = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);  
	                    Graphics2D g = combined.createGraphics(); 
	                    BufferedImage face = bi.getSubimage(8, 8, 8, 8);
	                    BufferedImage faceAcc = bi.getSubimage(40, 8, 8, 8);
	                    g.drawImage(face, 0, 0, null);
	                    g.drawImage(faceAcc, 0, 0, null);
	                    this.img = combined.getScaledInstance(128, 128, 0);
	                }
                } else {
                	f.delete();
                	EvilBook.logSevere("Removed invalid map image " + f.getName());
                }
            } 
        } catch (IOException exception) {  
        	//Problem reading file
        }        
    }
    
    void removePlayer(String pName) {
        for (int x = this.rendered.size()-1; x > 0; x--) {
            if (this.rendered.get(x).equals(pName)) {
                this.rendered.remove(x);
            }                
        }
    }
}