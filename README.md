
# MCShare

Never navigate the .minecraft folder to import worlds again!

## 📝Overview

**MCShare** is a Minecraft mod that simplifies the process of exporting and importing worlds. With this mod, you can say goodbye to the hassle of navigating your `.minecraft` folder to manage your worlds. Whether you're a seasoned player looking to share your creations with friends or a modpack developer in need of a quick way to import custom worlds, this mod has got you covered.

## 🚀How do I export my Minecraft world?

Exporting your Minecraft world using MCShare is a straightforward process that eliminates the need to navigate the .minecraft folder and create a zip file manually. Follow these simple steps to export your world:

![exporting screen](https://boxadactle.dev/img/mcshare/export_screen.png)

1. **Navigate to the World Edit Screen:** Locate the world you wish to export on the World Selection screen and click on it to enter the world edit screen.

2. **Click the Export Button:** On the world edit screen, you will find a new button labeled "Export." Click on this button to initiate the exporting process.

3. **Export Options Screen:** After clicking the "Export" button, you will be taken to the Export World screen. Here, you can configure the export settings according to your preferences.

4. **Select a Path:** On the Export Options screen, you can specify the destination path for the exported world. Choose a folder location on your computer where you want to save the exported world file.

5. **Export Your World:** Once you've configured the export settings and selected a path, click the "Export" button on the Export Options screen. The WorldExportImport Mod will package your world into a convenient zip file and save it to the specified location.

That's it! Your Minecraft world is now exported and ready for sharing, backup, or any other use you have in mind!

## 📥How do I import a Minecraft world?

Importing a world in MCShare is just as easy as exporting. Follow these simple steps to import a world using the Minecraft World Export/Import Mod:

![exporting screen](https://boxadactle.dev/img/mcshare/import_screen.png)

1. **Access World Selection:** Head to the main world selection screen.

2. **Locate the "Import World" Button:** Look for the "Import World" button, which has been added to the World Selection screen by the mod. If you haven't created any worlds yet, and can't get to the World Selection screen, a tab has been added to the Create World screen.

3. **Select Path:** After clicking the “Import” button, you will be taken to the Import World screen. Here, click "Select Path..." navigate to the world file. Once you've reached the folder containing your exported world, select the file and exit the file gui.

4. **Import the World:** After selecting the world folder, click the "Import" button. The mod will process the world data and integrate it into your Minecraft game.

That's all! Your world file has been converted into a Minecraft world that you can now play!

## 🔗 Mod Dependencies?
This mod does not have any external dependencies. However, it does embed [this external library mod](https://github.com/Boxadactle/BoxLib) made by me.

## 🛠 Building

* Download the source code from [GitHub](https://github.com/Boxadactle/MCShare/) (Code -> Download zip)
*  Extract the zip file onto your local machine and open the folder.
* Open a terminal prompt in said folder
* Run the command "gradlew build"
   	* The fabric build will be in "fabric/build/libs"
	* The forge build will be in "forge/build/libs"
