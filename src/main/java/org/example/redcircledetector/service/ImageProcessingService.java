package org.example.redcircledetector.service;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class ImageProcessingService {

    public void processRedCircles(MultipartFile file) throws Exception {
        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        Mat image = bufferedImageToMat(inputImage);
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        Mat circlesMat = new Mat();
        Imgproc.HoughCircles(gray, circlesMat, Imgproc.HOUGH_GRADIENT_ALT, 1, 10, 1.5, 0.7, 10, 25);

        if (!circlesMat.empty()) {
            int count = 0;
            int cols = circlesMat.cols();
            for (int i = 0; i < cols; i++) {
                double[] circle = circlesMat.get(0, i);
                int xCent = (int) circle[0];
                int yCent = (int) circle[1];
                int radius = (int) circle[2];

                if (isRedCircle(image, xCent, yCent)) {
                    count++;
                    Imgproc.circle(image, new Point(xCent, yCent), radius, new Scalar(0, 255, 0), 2);
                }
            }
            Imgproc.putText(image, "Red circle count: " + count,
                    new Point(20, 20), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 1);
        }

        String projectRoot = System.getProperty("user.dir");
        String outputPath = projectRoot + "/processed_image.jpg";
        Imgcodecs.imwrite(outputPath, image);
    }

    private boolean isRedCircle(Mat image, int xCent, int yCent) {
        int startY = Math.max(0, yCent - 2);
        int endY = Math.min(image.rows(), yCent + 1);
        int startX = Math.max(0, xCent - 2);
        int endX = Math.min(image.cols(), xCent + 1);

        Mat littleImg = image.submat(startY, endY, startX, endX);
        Scalar mean = Core.mean(littleImg);
        double red = mean.val[2];
        double green = mean.val[1];
        double blue = mean.val[0];

        return red > 150 && green < 60 && blue < 60;
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((java.awt.image.DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

}