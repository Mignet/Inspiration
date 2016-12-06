package com.v5ent.game.desktop;

/**
 * Created by Mignet on 2016/11/10.
 */

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RolePacker {
    public static void main(String[] args) throws IOException {
//        splitImage("F:\\backend\\Inspiration\\android\\assets\\heros",4,3);
        splitImage("F:\\backend\\Inspiration\\android\\assets\\items\\items.png",5,9);
    }

    private static void splitImage(String originalImg,int rows,int cols) throws IOException {

        File file = new File(originalImg);
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);

//	    int rows = 4;
//	    int cols = 4;
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0,
                        chunkWidth, chunkHeight,
                        chunkWidth* y, chunkHeight * x,
                        chunkWidth * y + chunkWidth,
                        chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        String out = file.getParent()+"\\temp";
        File dir = new File(out);
        if(!dir.exists()){
            dir.mkdirs();
        }
        for (int i = 0; i < imgs.length; i++) {
            ImageIO.write(imgs[i], "png", new File(out+"\\" + i + ".png"));
        }

        System.out.println("Completed!");
    }

    private static void mergeImage(String dir,int rows,int cols) throws IOException {

//	    int rows = 2;
//	    int cols = 2;
        int chunks = rows * cols;

        int chunkWidth, chunkHeight;
        int type;

        //读入小图
        File[] imgFiles = new File(dir).listFiles();
        //创建BufferedImage
        BufferedImage[] buffImages = new BufferedImage[chunks];
        for (int i = 0; i < chunks; i++) {
            buffImages[i] = ImageIO.read(imgFiles[i]);
        }
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();

        //设置拼接后图的大小和类型
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

        //写入图像内容
        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }

        //输出拼接后的图像
        ImageIO.write(finalImg, "png", new File(dir+"\\gjl-move-"+chunkWidth+"-"+chunkHeight+".png"));

        System.out.println("Merged!");
    }
}
