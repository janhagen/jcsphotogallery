/*	
 * 	File    : PopUpImgShow.java
 * 
 * 	Copyright (C) 2010 Daniel Cioi <dan@dancioi.net>
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

package net.dancioi.jcsphotogallery.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * This class shows the selected image 
 * (from the center panel) on a PopUp panel.
 *  
 * @version 1.2 
 * @author Daniel Cioi <dan@dancioi.net>
 */

public class PopUpImgShow extends PopupGeneric{

//TODO set the autoplay time from a xml file.
	AbsolutePanel ap;
	int popUpSize;
	AbsolutePanel imgPanel;
	int imgPanelSize;
	AbsolutePanel bottomPanel;

	String []imgName;
	String []imgFile;
	String []imgComment;
	int imgStart;				// image id from to start.
	String imgPath; 			// images path
	ImagePopUp img; 			// image which will be shown.
	
	Image next;
	Image previous;
	Image close;
	
	
	Label lImgName;				// image name
	Label lImgComment;			// image comment

	int currentImg;
	
	/* add a loading message		*/
	String loading;
	Label loadingLabel;
	int ind = 1;
	Timer t;
	
	/* cache the next and previous image		*/
	Image imgCacheN;
	Image imgCacheP;
	
	
	//Button play;				// button AUTO PLAY
	Image play;
	boolean playFlag = true;
	Timer tPlay;
	Label playMode;
	

	public PopUpImgShow(int imgStart, String imgPath, String []imgFile, String []imgName,  String []imgComment){
		super();
		this.imgStart = imgStart;
		this.imgPath = imgPath;
		this.imgName = imgName;
		this.imgFile = imgFile;
		this.imgComment = imgComment;
		currentImg = imgStart;
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize(){
		setGlassStyleName("gwt-PopupPanelGlass");
		setGlassEnabled(true); 

		getMaximSquareSize();
		
		setPosition();

		ap = new AbsolutePanel();
		ap.setPixelSize(popUpSize, popUpSize);	
		ap.setStyleName("popUpPanel");

		imgPanel = new AbsolutePanel();
		imgPanelSize = popUpSize-40;
		imgPanel.setPixelSize(imgPanelSize, imgPanelSize);
		ap.add(imgPanel, 20,1);
		
		bottomPanel = new AbsolutePanel();
		bottomPanel.setPixelSize(popUpSize, 50);
		
		next = new Image("ext/next.gif");
		next.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(!playFlag){autoPlay(false);play.setUrl("ext/play.gif");playFlag = true;}
				nextImg();}} );
		bottomPanel.add(next, popUpSize-110, 5);
		
		previous = new Image("ext/previous.gif");
		previous.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(!playFlag){autoPlay(false);play.setUrl("ext/play.gif");playFlag = true;}
				previousImg();}} );
		bottomPanel.add(previous, popUpSize-160, 5);
		
		close = new Image("ext/close.gif");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeImg();}} );
		bottomPanel.add(close, popUpSize-40, 5);	
		
		play = new Image("ext/play.gif");
		play.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(playFlag){
					autoPlay(true);
					play.setUrl("ext/pause.gif");
					playFlag = false;
				}
				else{
					autoPlay(false);
					play.setUrl("ext/play.gif");
					playFlag = true;
				}
				}} );
		bottomPanel.add(play, popUpSize-240, 5);

		lImgName = new Label();
		bottomPanel.add(lImgName, 20,0);

		lImgComment = new Label();
		bottomPanel.add(lImgComment, 20,20);


		loading = "Loading...";
		loadingLabel = new Label();
		loadingLabel.setText(loading);
		bottomPanel.add(loadingLabel, popUpSize/2,2);
		
		playMode = new Label();
		bottomPanel.add(playMode, popUpSize/2,22);
		
		ap.add(bottomPanel, 1, popUpSize-40);

		setWidget(ap);

		addImage(imgPath+imgFile[imgStart]);

		checkStartImg();
	}

	/**
	 * Method to show the next image.
	 */
	public void nextImg(){
		showLoadingProcess(false);
		if(currentImg<(imgFile.length-1)){
			currentImg++;
			checkButtons(currentImg);
			addImage(imgPath+imgFile[currentImg]);
		}
	}

	/**
	 * Method to show the previous image.
	 */
	public void previousImg(){
		showLoadingProcess(false);
		if(currentImg>0){
			currentImg--;
			checkButtons(currentImg);
			addImage(imgPath+imgFile[currentImg]);
		}
	}

	/**
	 * Method to check if the selected image is first or the last from album;
	 * disable the buttons according.
	 * @param id image id
	 */
	private void checkButtons(int id){
		next.setVisible(true);
		previous.setVisible(true);
		if(id==(imgFile.length-1))next.setVisible(false);
		if(id==0) previous.setVisible(false);
	}


	
	
	
	/**
	 * check if the image is first or the last.
	 * disable the button according.
	 */
	private void checkStartImg(){
		checkButtons(currentImg);
	}

	/**
	 * Method to close the popup.
	 */
	public void closeImg(){
		this.hide();
	}

	/**
	 * Method to add image to the panel.
	 * Because the image size method from the image class can't return the values
	 * until the image is fully loaded by browser, this method add the image
	 * on the popup panel's bottom right corner (making the image invisible and
	 * creating the background loading effect). When the image is complete downloaded, 
	 * get the image's size and the it's added to the popup panel.
	 */
	private void addImage(String imagePath){
		showLoadingProcess(true);
		img = new ImagePopUp(imagePath, this);
		imgPanel.add(img, imgPanelSize, imgPanelSize);
	}

	/**
	 * scale the image to fit the panel
	 */
	public void scaleImg(Image im){
		int iox = im.getWidth();
		int ioy = im.getHeight();
		float scaleX = (float)imgPanelSize/iox;
		float scaleY = (float)imgPanelSize/ioy;
		float scalef = scaleX<=scaleY ? scaleX:scaleY;
		int ix = (int)(iox*scalef);
		int iy = (int)(ioy*scalef);
		img.setPixelSize(ix, iy);

		lImgName.setText(imgName[currentImg]);
		lImgComment.setText(imgComment[currentImg]);
		showLoadingProcess(false);
		clearImg();
		imgPanel.add(img, (imgPanelSize-ix)/2, (imgPanelSize-iy)/2);
		
		getNextAndPrevious(currentImg);	// cache the next and previous pictures;
	}


	/**
	 * Method to get the maxim popup size. The dimension should be the same (square).
	 */
	private void getMaximSquareSize(){
		getWindowSize();
		popUpSize = getBrowserWindowWidth() <= getBrowserWindowHeight() ? getBrowserWindowWidth():getBrowserWindowHeight();
		popUpSize = popUpSize -40;	// let some margin.
		setSizeX(popUpSize);
		setSizeY(popUpSize);
	}
	
	/**
	 * Method to clear the old image before add the new one.
	 */
	private void clearImg(){
		imgPanel.clear();
	}
	
	/**
	 * This method shows the picture's loading process.
	 */
	private void showLoadingProcess(boolean show){
		if(show){
			// show the loading bar

			ind = 1;
			loadingLabel.setText(loading.substring(0, ind));
			
			t = new Timer() {
			      @Override
			      public void run() {
			    	  if(ind<loading.length()){
							ind++;
						}
						else{
							ind=1;
						}
						loadingLabel.setText(loading.substring(0, ind));
			      }
			    };
			    //t.schedule(500);
			    t.scheduleRepeating(500);
		}
		else{
			// hide the loading bar
			t.cancel();
			loadingLabel.setText("");
		}
	}
	
	/**
	 * Cache the next and previous pictures to reduce the loading time. 
	 * @param i picture id
	 */
	private void getNextAndPrevious(int i){
		if(i<imgFile.length-1)imgCacheN = new Image(imgPath+imgFile[i+1]);
		if(i>0)imgCacheP = new Image(imgPath+imgFile[i-1]);
	}

	/**
	 * AutoPlay method.
	 * @param flag 
	 */
	private void autoPlay(boolean flag){
		if(flag){	
			playMode.setText("Auto Play Mode");
			tPlay = new Timer() {
			      @Override
			      public void run() {
			    	  nextImg();
			      }
			    };
			    tPlay.scheduleRepeating(10000);	// wait 10 seconds between pictures. 
		}
		else{
			playMode.setText("");
			tPlay.cancel();
		}
	}
	
}
