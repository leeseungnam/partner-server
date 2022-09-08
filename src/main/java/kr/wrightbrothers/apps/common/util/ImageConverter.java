package kr.wrightbrothers.apps.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class ImageConverter {
    
	/**
	 * 전체적으로 리사이징
	 * 정사각형으로 잘라낼지 여부
	 * @param file
	 * @param newSize
	 * @param cropYn
	 * @return
	 */
    public static InputStream imageConverter(MultipartFile file, int newSize, boolean cropYn) {
        
        // 리사이즈 
        // String imgOriginalPath= "C:/test/test.jpg";           // 원본 이미지 파일명
        // String imgTargetPath= "C:/test/test_resize.jpg";    // 새 이미지 파일명
        // String imgFormat = "jpg";                             // 새 이미지 포맷. jpg, gif 등
    	// int newWidth = 600;                                  // 변경 할 넓이
    	// int newHeight = 700;                                 // 변경 할 높이
    	// String mainPosition = "W";                             // W:넓이중심, H:높이중심, X:설정한 수치로(비율무시)
    	// int newWidth = 1024;
        
        Image image;
        int imageWidth;
        int imageHeight;
        double ratio;
        int w;
        int h;

        try{
            // 이미지 리사이즈
            InputStream imgOriginal = file.getInputStream();
        
            // 원본 이미지 가져오기
            //image = ImageIO.read(new File(imgOriginalPath));
            image = ImageIO.read(imgOriginal);
 
            // 원본 이미지 사이즈 가져오기
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);

            ratio = (double)newSize/(double)imageWidth;
            w = (int)(imageWidth * ratio);
            h = (int)(imageHeight * ratio);
            
            // 이미지 컷
            if(cropYn){
                int x1 = 0;
                int y1 = 0;
                int x2 = newSize;
                int y2 = newSize;
                if(w > h) {
                    // 작은 값으로 리 사이징 하고 컷  
                    // 높이 기준
                    ratio = (double)newSize/(double)imageHeight;
                    w = (int)(imageWidth * ratio) + 1;
                    h = (int)(imageHeight * ratio) + 1; 
                    // 가로가 길면
                    x1 = (w - newSize) / 2;
                    x2 = x1 + newSize;
                } else if(h > w) {
                    // 작은 값으로 리 사이징 하고 컷
                    // 넓이 기준
                    ratio = (double)newSize/(double)imageWidth;
                    w = (int)(imageWidth * ratio) + 1;      // 1픽셀정도 모자라서 테두리에 검은선이 생기는 경우가 있다! + 1을 해주자
                    h = (int)(imageHeight * ratio) + 1;
                    // 높이가 길면
                    y1 = (h - newSize) / 2;
                    y2 = y1 + newSize;
                } else  {
                    // 동일하면 아무기준이나 리사이징 하고 컷
                    // 넓이 기준
                    // ratio = (double)newSize/(double)imageWidth;
                    // w = (int)(imageWidth * ratio);
                    // h = (int)(imageHeight * ratio);
                    w = newSize;
                    h = newSize;
                }

                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                //g.drawImage(resizeImage, 0, 0, null);
                g.drawImage(resizeImage, 0, 0, newSize, newSize, x1, y1, x2, y2, null);

                g.dispose();
            // ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            } else {

                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                g.drawImage(resizeImage, 0, 0, null);
                g.dispose();
            // ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            }
 
        }catch (Exception e){
           log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 정사각형으로 잘라낼지 여부
     * @param image
     * @param newSize
     * @param cropYn
     * @return
     */
    public static InputStream imageConverter2(Image image, int newSize, boolean cropYn){
        
        // 리사이즈 
        // String imgOriginalPath= "C:/test/test.jpg";           // 원본 이미지 파일명
        // String imgTargetPath= "C:/test/test_resize.jpg";    // 새 이미지 파일명
        // String imgFormat = "jpg";                             // 새 이미지 포맷. jpg, gif 등
    	// int newWidth = 600;                                  // 변경 할 넓이
    	// int newHeight = 700;                                 // 변경 할 높이
    	// String mainPosition = "W";                             // W:넓이중심, H:높이중심, X:설정한 수치로(비율무시)
    	// int newWidth = 1024;
        
        //Image image;
        int imageWidth;
        int imageHeight;
        double ratio;
        int w;
        int h;

        try{
 
            // 원본 이미지 사이즈 가져오기
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);

            ratio = (double)newSize/(double)imageWidth;
            w = (int)(imageWidth * ratio);
            h = (int)(imageHeight * ratio);

            // 이미지 컷
            if(cropYn) {
                int x1 = 0;
                int y1 = 0;
                int x2 = newSize;
                int y2 = newSize;
                if(w > h) {
                    // 작은 값으로 리 사이징 하고 컷  
                    // 높이 기준
                    ratio = (double)newSize/(double)imageHeight;
                    w = (int)(imageWidth * ratio);
                    h = (int)(imageHeight * ratio); 
                    // 가로가 길면
                    x1 = (w - newSize) / 2;
                    x2 = x1 + newSize;
                } else if(h > w) {
                    // 작은 값으로 리 사이징 하고 컷
                    // 넓이 기준
                    ratio = (double)newSize/(double)imageWidth;
                    w = (int)(imageWidth * ratio);
                    h = (int)(imageHeight * ratio);

                    // 높이가 길면
                    y1 = (h - newSize) / 2;
                    y2 = y1 + newSize;
                } else {
                    // 동일하면 아무기준이나 리사이징 하고 컷
                    // 넓이 기준
                    ratio = (double)newSize/(double)imageWidth;
                    w = (int)(imageWidth * ratio);
                    h = (int)(imageHeight * ratio);
                }

                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                //g.drawImage(resizeImage, 0, 0, null);
                g.drawImage(resizeImage, 0, 0, newSize, newSize, x1, y1, x2, y2, null);

                g.dispose();
                // ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            } else {

                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                g.drawImage(resizeImage, 0, 0, null);
                g.dispose();
            // ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

	/**
	 * 
	 * @param text
	 * @param newWidth
	 * @param newHeight
	 * @param file
	 * @return
	 */
    public static InputStream imageConverter(String text, int newWidth, int newHeight, MultipartFile file){
        
        // 리사이즈 
        // String imgOriginalPath= "C:/test/test.jpg";           // 원본 이미지 파일명
        // String imgTargetPath= "C:/test/test_resize.jpg";    // 새 이미지 파일명
        // String imgFormat = "jpg";                             // 새 이미지 포맷. jpg, gif 등
    	// int newWidth = 600;                                  // 변경 할 넓이
    	// int newHeight = 700;                                 // 변경 할 높이
        String mainPosition = "X";                             // W:넓이중심, H:높이중심, X:설정한 수치로(비율무시)
        
        if(newWidth > 0 && newHeight > 0) // 두 값에 맞출것
            mainPosition = "X";
        else if(newWidth > 0)
            mainPosition = "W"; // 넓이에 맞출것
        else if(newHeight > 0)
            mainPosition = "H"; // 높이에 맞출것

        Image image;
        int imageWidth;
        int imageHeight;
        double ratio;
        int w;
        int h;

        try{
            // 이미지 리사이즈
            InputStream imgOriginal = file.getInputStream();
        
            // 원본 이미지 가져오기
            //image = ImageIO.read(new File(imgOriginalPath));
            image = ImageIO.read(imgOriginal);
 
            // 원본 이미지 사이즈 가져오기
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);
 
            if(mainPosition.equals("W")){    // 넓이기준
                ratio = (double)newWidth/(double)imageWidth;
                w = (int)(imageWidth * ratio);
                h = (int)(imageHeight * ratio);
            } else if(mainPosition.equals("H")) { // 높이기준
                ratio = (double)newHeight/(double)imageHeight;
                w = (int)(imageWidth * ratio);
                h = (int)(imageHeight * ratio);
            } else { //설정값 (비율무시)
                w = newWidth;
                h = newHeight;
            }
 
            // 이미지 리사이즈
            // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
            // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
            // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
            // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
            // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
            Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
 
            // 새 이미지  저장하기
            BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = newImage.getGraphics();
            g.drawImage(resizeImage, 0, 0, null);
            g.dispose();
           // ImageIO.write(newImage, imgFormat, new File(imgTargetPath));

           // 이미지 워터 마킹 
            return addTextWatermark(text, newWidth, newImage);
 
        }catch (Exception e){
        	log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Embeds a textual watermark over a source image to produce
     * a watermarked one.
     * @param text The text to be embedded as watermark.
     * @param sourceImageFile The source image file.
     * @param destImageFile The output image file.
     */
    static public InputStream addTextWatermark(String text, int newWidth, BufferedImage sourceImage) {
        try {
            if(!text.equals("")) {
         //   BufferedImage sourceImage = ImageIO.read(sourceImageFile);
                Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
        
                // initializes necessary graphic properties
                AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
                g2d.setComposite(alphaChannel);
                g2d.setColor(Color.BLUE);
                
                int font = 20;

                // font 사이즈는 width에 따라 다르게 한다.
                if(newWidth <= 600)       font = 30;
                else if(newWidth >= 1024)  font = 50;
                else if(newWidth >= 2048)  font = 150;

                g2d.setFont(new Font("Arial", Font.BOLD, font));
                FontMetrics fontMetrics = g2d.getFontMetrics();
                Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);
        
                // calculates the coordinate where the String is painted
                int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
                int centerY = sourceImage.getHeight() / 2;
        
                // paints the textual watermark
                g2d.drawString(text, centerX, centerY);
        
                //ImageIO.write(sourceImage, "png", destImageFile);
                g2d.dispose();
        
                System.out.println("The tex watermark is added to the image.");

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(sourceImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            } else {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(sourceImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                return is;
            }
    
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        return null;
    }
    
	/**
	 * Embeds an image watermark over a source image to produce 
	 * a watermarked one. 
	 * @param watermarkImageFile The image file used as the watermark.
	 * @param sourceImageFile The source image file.
	 * @param destImageFile The output image file.
	 */
	static void addImageWatermark(File watermarkImageFile, File sourceImageFile, File destImageFile) {
		try {
			BufferedImage sourceImage = ImageIO.read(sourceImageFile);
			BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
			
			// initializes necessary graphic properties
			Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
			AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
			g2d.setComposite(alphaChannel);
			
			// calculates the coordinate where the image is painted
			int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 2;
			int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 2;
			
			// paints the image watermark
			g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
			
			ImageIO.write(sourceImage, "png", destImageFile);
			g2d.dispose();
			
			System.out.println("The image watermark is added to the image.");
			
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}
	}

	/**
	 * 
	 * @param file
	 * @param newSize
	 * @param cropYn
	 * @return
	 */
    public static InputStream imageConverter(File file, int newSize, boolean cropYn){                
        Image image;
        int imageWidth;
        int imageHeight;
        double ratio;
        int w;
        int h;

        try{
            // 원본 이미지 가져오기
            image = ImageIO.read(file);

            // 원본 이미지 사이즈 가져오기
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);

            ratio = (double)newSize/(double)imageWidth;
            w = (int)(imageWidth * ratio);
            h = (int)(imageHeight * ratio);
            
            // 이미지 컷
            if(cropYn) {                    
                int x1 = 0;
                int y1 = 0;
                int x2 = newSize;
                int y2 = newSize;
                
                if(w > h) {                    
                    // 작은 값으로 리 사이징 하고 컷  
                    // 높이 기준
                    ratio = (double)newSize/(double)imageHeight;
                    w = (int)(imageWidth * ratio) + 1;
                    h = (int)(imageHeight * ratio) + 1; 

                    // 가로가 길면
                    x1 = (w - newSize) / 2;
                    x2 = x1 + newSize;
                } else if(h > w) {                    
                    // 작은 값으로 리 사이징 하고 컷
                    // 넓이 기준
                    ratio = (double)newSize/(double)imageWidth;
                    w = (int)(imageWidth * ratio) + 1;      // 1픽셀정도 모자라서 테두리에 검은선이 생기는 경우가 있다! + 1을 해주자
                    h = (int)(imageHeight * ratio) + 1;

                    // 높이가 길면
                    y1 = (h - newSize) / 2;
                    y2 = y1 + newSize;
                } else {
                    // 동일하면 아무기준이나 리사이징 하고 컷
                    // 넓이 기준
                    w = newSize;
                    h = newSize;
                }

                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                //g.drawImage(resizeImage, 0, 0, null);
                g.drawImage(resizeImage, 0, 0, newSize, newSize, x1, y1, x2, y2, null);
                g.dispose();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            } else {
                // 이미지 리사이즈
                // Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
                // Image.SCALE_FAST    : 이미지 부드러움보다 속도 우선
                // Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
                // Image.SCALE_SMOOTH  : 속도보다 이미지 부드러움을 우선
                // Image.SCALE_AREA_AVERAGING  : 평균 알고리즘 사용
                Image resizeImage = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    
                // 새 이미지  저장하기
                BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = newImage.getGraphics();
                g.drawImage(resizeImage, 0, 0, null);
                g.dispose();
   
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                return is;
            }
        }catch (Exception e){
        	log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 
     * @param tiffFile
     * @param filePath
     * @param fileName
     * @return
     */
    public static File convertTiff(MultipartFile tiffFile, String filePath, String fileName) {
        String ext = getFileExtension(fileName);
        BufferedImage tiff;
        File output = null;
		try {
			tiff = ImageIO.read(tiffFile.getInputStream());
			output = new File(filePath + fileName.replace(ext, ".jpg"));
	        ImageIO.write(tiff, "jpg", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return output;
    }
    
    /**
     * 
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(lastIndexOf).toLowerCase();
    }
    
}