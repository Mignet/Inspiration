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
//		mergeImage("D:/快盘/libgdx-1.2.0/test/core/assets/gjl/move", 8,8);
//		mergeImage("D:/快盘/libgdx-1.2.0/test/core/assets/gjl/stand", 8,16);
        splitImage("F:\\backend\\Inspiration\\android\\assets\\heros",4,3);
    }

    private static void splitImage(String src,int rows,int cols) throws IOException {

        String originalImg = src+"\\ASL.png";

        // 读入大图
        File file = new File(originalImg);
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);

        // 分割成4*4(16)个小图
//	    int rows = 4;
//	    int cols = 4;
        int chunks = rows * cols;

        // 计算每个小图的宽度和高度
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //设置小图的大小和类型
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());

                //写入图像内容
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0,
                        chunkWidth, chunkHeight,
                        chunkWidth* y, chunkHeight * x,
                        chunkWidth * y + chunkWidth,
                        chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }

        // 输出小图
        for (int i = 0; i < imgs.length; i++) {
            ImageIO.write(imgs[i], "png", new File(src+"\\001\\" + i + ".png"));
        }

        System.out.println("完成分割！");
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

        System.out.println("完成拼接！");
    }
}
