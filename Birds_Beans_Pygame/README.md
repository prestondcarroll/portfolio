# Birds & Beans
Birds & Beans

Click for Video
[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/IJ9c73GBglM/0.jpg)](https://www.youtube.com/watch?v=IJ9c73GBglM)

![alt text](https://raw.githubusercontent.com/prestondcarroll/projects/master/Birds_Beans_Pygame/birds_beans.png)

Please download the ![zip file](https://raw.githubusercontent.com/prestondcarroll/projects/master/Birds_Beans/Birds_Beans_Windows_exe.zip) to play the game. Extract the contents and click the Birds.exe file to play. It should work on windows environments without the need for a python installation.

Back Story: This is a fast-paced game from the Nintendo DSi that I have rebuilt from scratch in python using the pygame libraries. The game is called Birds & Beans, and you play as a bird called Pyoro who spits seeds at beans that constantly drop from the sky. Spitting beans contribute to your score while missing beans will block the path for Pyoro to walk on. Getting hit on the head by a bean means game over.

How to play: <br />
* Left ← and right →arrows: Move Left & Right <br />
* Z:	will spit in a diagonal line that matches up with the direction pyoro looks like he is spitting in <br />
* F1:  brings up the info/help screen (meant to be pressed once) <br />
* Esc: exits the game


Objective: Avoid getting hit by the beans and and get the highest score you can before dieing. If they hit you on the head then game over. 
	Scoring: The more beans you can hit in one shot, the higher the score! One bean is worth 50 points. Two beans is 100 each, or 200. Three beans is 300 each, or 900. And 4 beans is 1000 each, or 4000! 
	Modes: There is one mode is this game and it is the one you play.
	Help: Press F1 to bring up the info/help screen.
	 
Modules:<br />	
* main.py – this contains the main function and a handful of helper functions
it also takes care of the sounds because there is not too many sounds. It imports everthing from images
	It is broken up into Events, Collision, Updates, and Drawing. After all of that is done then it displays everything and the clock ticks to advance it to the next frame. 

* images.py – this contains all the classes for the objects & images that are used in this game

Acknowledgement: 
	All artwork, sprites, sounds, are directly from Nintendo DSi ware game Birds & Beans. The gameplay concepts presented here are also from the game but the implementation and programming of it is my own. I give credit to http://programarcadegames.com/python_examples/en/sprite_sheets/ for their tutorial that I used to help me get started.
  
Miscellaneous Info:
  This is my first game that I developed outside of a basic drag and drop environment called Alice. It is also my first experience coding in python. The game took around 10 days to make, and was a fun yet challenging experience. For a simple game, there was a lot to consider and many things to get right. It is still missing some effects and scene transitions game but still represents the gameplay of the original very closely, which was my goal.
