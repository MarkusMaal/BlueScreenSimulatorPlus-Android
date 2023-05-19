# Blue Screen Simulator Plus for Android
Welcome to Blue Screen Simulator Plus for Android! This is a simple utility for Android based devices, which lets you simulate various error screens from different Windows versions.

## Presets
This app uses presets, which contain settings about themselves (User configurations) and what operating system they should use and thus what options should be visible for the user (OS template).
These presets are stored in a list, which can be saved inside a configuration file.

![Preset structure diagram](file:///android_asset/drawable/structure)

The default presets are the following:

* Windows 11 (Native, ClearType) [Windows 11]
* Windows 10 (Native, ClearType) [Windows 10]
* Windows 8/8.1 (Native, ClearType) [Windows 8/8.1]
* Windows 7 (640x480, ClearType) [Windows 7]
* Windows Vista (640x480, Standard) [Windows Vista]
* Windows XP (640x480, Standard) [Windows XP]
* Windows 2000 Professional/Server Family (640x480, Standard) [Windows 2000]
* Windows NT 4.0/3.x (Text mode, Standard) [Windows NT 3.x/4.0]
* Windows CE 3.0 and later (750x400, Standard) [Windows CE]
* Windows 9x/Millennium Edition (Text mode, Standard) [Windows 9x/Me]
* Windows 3.1 (Text mode, Standard) [Windows 3.1x]
* Windows 1.x/2.x (Text mode, Standard) [Windows 1.x/2.x]

Each one of these presets can be seen in a drop down list at the main interface.

Selecting each one, will display a different set of configurable blue screen options.
As mentioned before, all of these options are stored inside the preset, meaning any
changes made in one preset, will NOT carry over to another preset
(e.g. if you disable watermark in one preset, it will be enabled in other presets).

## User interface
After launching the app, you see a simple user interface, that allows you to choose a blue screen that suits your needs!

![User interface](file:///android_asset/drawable/help_ui)

First, you can choose an operating system from the first dropdown. As mentioned before, 
they are actually configurable templates that have operating systems associated with them.

After selecting one, you may be able to adjust the following settings, depending on, which
one you chose:

### Error code
This is option determines what error code is displayed on the error screen. If you want to use a custom code, check the custom error code switch, and you'll see a popup window, which looks like this:

![Custom error code window](file:///android_asset/drawable/help_custom_error)

Here you can set a custom error description and a hex code. A hex code can only consist of numbers (0-9) and letters A-F. Tap "OK" to confirm a custom error code. If you do this, you'll not be able to choose an error code until you uncheck "Custom error code".

### Auto close
If enabled, this will exit the simulation after the dumping progress on the error screen finishes. This option is not available for all operating systems.

### Watermark
By default, Blue Screen Simulator Plus displays a watermark in the top middle section of the error screen to let you know that this is just a simulation, not a real error. If you don't want to see the watermark, just uncheck "Display watermark" option.

### Windows 10/11 specific options
In Windows 10 and 11, you can see the following options:

* Preview build - Displays a green screen instead of blue and replaces PC/device with "Windows Insider Build"
* Server blue screen - Hides the emoticon ":(" from the blue screen
* Black screen (W11 only) - Displays a black screen instead of blue (found in some Windows 11 builds)
* Replace "PC" with "device" (W10 only) - In newer versions of Windows 10, the bugcheck says "device" instead of "PC". This option allows you to toggle between the two.
* Show parameters - Displays error parameters in top left corner
* Culprit file - Displays a culprit file below the error code (e.g. What failed: wdfilter.sys). You can set it by tapping the "SET CULPRIT FILE" button. This opens a dialog, where you can either manually enter a file that presumably caused the error or choose the file from a list.
* Display error details - If checked, displays the error description, if unchecked, displays the actual error code

Some of these options also exist in Windows 8/8.1.

### Windows XP/Vista/7 specific options
Specific options for the three operating systems. The only difference between Vista and 7 by default is the font used.

* Display error details - If checked, shows the error description after the first paragraph. If unchecked, the error description will not be displayed.
* Culprit file - If checked, shows what file may have caused the error below the error description/first paragraph
* Show additional info about culprit file - If checked, displays more information about the file below the stop error code
* Custom error code

### Windows NT 4.0/3.x specific options
Windows NT has a complex error screen, with several options that can be adjusted.

* Blinking cursor - Blinks a caret symbol at the top left if checked
* AMD processor - Shows "AuthenticAMD" instead of "GenuineIntel" on the error screen
* Show stack trace - Toggles whether or not to display list of files and error codes associated with them. This can be customized using in the "NT CODE EDITOR" menu. 
* Culprit file
* Custom error code