/*	
 * 	File    : RightClickPopUpInterface.java
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
package net.dancioi.jcsphotogallery.app.controller;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Interface for right click popUp menu.
 * 
 * @author Daniel Cioi <dan@dancioi.net>
 * @version $Revision$ Last modified: $Date$, by: $Author$
 */
public interface RightClickPopUpInterface {

	void addNewAlbum();

	void addNewImage(DefaultMutableTreeNode treeNode);

	void setAlbumImage(DefaultMutableTreeNode treeNode);

	void deleteImage(DefaultMutableTreeNode treeNode);

	void deleteAlbum(DefaultMutableTreeNode treeNode);

	boolean isGalleryCreated();

}
