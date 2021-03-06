/*	
 * 	File    : GalleryFiles.java
 * 
 * 	Copyright (C) 2012 Daniel Cioi <dan@dancioi.net>
 *                              
 *	www.dancioi.net/projects/Jcsphotogallery
 *
 *	This file is part of Jcsphotogallery.
 *
 *  Jcsphotogallery is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jcsphotogallery is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jcsphotogallery.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.dancioi.jcsphotogallery.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.Queue;

import net.dancioi.jcsphotogallery.shared.AlbumBean;
import net.dancioi.jcsphotogallery.shared.PictureBean;

/**
 * Files manipulation.
 * 
 * @author Daniel Cioi <dan@dancioi.net>
 * @version $Revision$ Last modified: $Date$, by: $Author$
 */
public class GalleryFiles {

	private JcsPhotoGalleryModel model;
	private Queue<File> deleteQueue = new LinkedList<File>();

	public GalleryFiles(JcsPhotoGalleryModel model) {
		this.model = model;
	}

	private void addToDelete(File file) {
		deleteQueue.add(file);
	}

	public void executeQueuedDeleteOperations() {
		System.gc(); // gc is called but is not guaranteed to execute before deleteFile method. Thus, delete will run in a thread trying 10 times to delete the file. This is required just on Windows
		StringBuilder result = new StringBuilder();
		int queSize = deleteQueue.size();
		for (int i = 0; i < queSize; i++) {
			deleteFileAndReturnResult(deleteQueue.remove(), result);
		}
		checkDeleteReport(result);
	}

	private StringBuilder deleteFileAndReturnResult(File file, StringBuilder result) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteFileAndReturnResult(child, result);
			}
		}

		boolean deleteFileResult = false;
		if (file.exists()) {
			deleteFileResult = file.delete();
			if (!deleteFileResult) {
				new Thread(new DeleteLater(file)).start();
			}
		}
		return result.append(file.getAbsolutePath() + " : " + deleteFileResult + "\n");
	}

	private void checkDeleteReport(StringBuilder result) {
		/*
		 * if (result.indexOf(": false") != -1) { new DeleteReport(result); }
		 */
		// System.out.println(result.toString());
	}

	public void deleteAlbum(AlbumBean albumToDelete) {
		addToDelete(new File(model.getAppGalleryPath() + File.separator + albumToDelete.getFolderName()));
	}

	public void deletePicture(PictureBean picture) {
		String pictureBeanPath = model.getAppGalleryPath() + File.separator + picture.getParent().getFolderName() + File.separator;
		addToDelete(new File(pictureBeanPath + picture.getFileName()));
		addToDelete(new File(pictureBeanPath + picture.getImgThumbnail()));
	}

	public void copyPicture(PictureBean picture, AlbumBean albumSource, AlbumBean albumDestination) {
		if (!albumSource.getFolderName().equals(albumDestination.getFolderName())) { // if the picture is moved in another album
			String pictureSource = model.getAppGalleryPath() + File.separator + albumSource.getFolderName() + File.separator + picture.getFileName();
			String pictureDestination = model.getAppGalleryPath() + File.separator + albumDestination.getFolderName() + File.separator + picture.getFileName();
			copyFile(pictureSource, pictureDestination);

			String pictureThumbnailSource = model.getAppGalleryPath() + File.separator + albumSource.getFolderName() + File.separator + picture.getImgThumbnail();
			String pictureThumbnailDestination = model.getAppGalleryPath() + File.separator + albumDestination.getFolderName() + File.separator + picture.getImgThumbnail();
			copyFile(pictureThumbnailSource, pictureThumbnailDestination);

			albumSource.setEdited(true);
			albumDestination.setEdited(true);
		} else { // if the picture is moved in the same album
			albumSource.setEdited(true);
		}
	}

	private void copyFile(String pictureSource, String pictureDestination) {
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(pictureSource).getChannel();
			out = new FileOutputStream(pictureDestination).getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if(in!=null)
				in.close();
				if(out!=null)
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void deleteFiles(File[] filePaths) {
		for (File filePath : filePaths) {
			addToDelete(filePath);
		}
	}
}

class DeleteLater implements Runnable {
	private File fileToDelete;
	private boolean result;

	DeleteLater(File fileToDelete) {
		this.fileToDelete = fileToDelete;
	}

	@Override
	public void run() {
		for (int count = 0; count < 10; count++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// System.out.println("tring to delete file: " + fileToDelete.getAbsolutePath());
			if (fileToDelete.exists()) {
				result = fileToDelete.delete();
				if (result)
					break;
			}
		}

	}

}
