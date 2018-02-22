package br.com.cursomc.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.cursomc.services.exceptions.FileException;

@Service
public class ImageService {
	
	public BufferedImage getJpgImageFromFile(MultipartFile uploadedFile)
	{
		String ext = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
		
		if(!"png".equals(ext) && !"jpg".equals(ext))
		{
			throw new FileException("Somente imagens PNG e JPG são permitidas");
		}
		
		try {
			BufferedImage image = ImageIO.read(uploadedFile.getInputStream());
			
			if("png".equals(ext))
			{
				image = pngToJpg(image);
			}
			
			return image;
			
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}

	public BufferedImage pngToJpg(BufferedImage image) {
		
		BufferedImage jpgImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		jpgImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
		
		return jpgImage;
	}
	
	public InputStream getInputStream(BufferedImage image, String extension)
	{
		try {
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, extension, stream);
			return new ByteArrayInputStream(stream.toByteArray());
			
		} catch (IOException e) {
			throw new FileException("Erro ao ler arquivo");
		}
	}
	
	public BufferedImage cropSquare(BufferedImage image)
	{
		int min = (image.getHeight() <= image.getWidth()) ? image.getHeight() : image.getWidth();
		
		return Scalr.crop(
				image, 
				(image.getWidth()/2) - (min/2), 
				(image.getHeight()/2) - (min/2),
				min,
				min);
	}
	
	public BufferedImage resize(BufferedImage image, int size)
	{
		return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, size);
	}
}
