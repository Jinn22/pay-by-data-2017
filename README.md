# README #
PBD model which was introduced by Guo and Wu (2013) aims to provide a platform which
regulates types of data and limits of authority applications can have to access mobile users’
data.
This project adds PBD AppStore and Marketplace into the PBD Model. 

User guide:

I. Prepare an Android device for PBD services
(The following guide was based on: https://github.com/Guanjie-Liu/Pay-By-Data-Android/wiki/Compiling-&-Installing-PBD-OS } by Liu (2016).
The official guide of building a system from AOSP is on:
https://source.android.com/source/)

a. Set up a Linux build environment with Ubuntu 12.04  and allocate enough space for built. However, it is suggested to use more up-to-date version of Ubuntu (such as 14.04 as suggested on the official website) because most of the build tools are available within that system.

b. Install packages based on the Ubuntu version you chose.

c. Configure USB Access by downloading 51-android.txt.

d. Install JDK.

e. Set up ccache, which can help make builds faster. Ccache can cache the compiling results to reduce build time. 

f. Download the source using Repo tool. Run repo init and check out a branch using repo init -u https://android.googlesource.com/platform/manifest -b android-5.0.2_r1
, if Android 5.0.2 was chosen.

g. Download the source by repo sync. (Note this might take a while because the code is about 40 GB). Add PBD service source code. 

h. Obtain proprietary binaries, and extract it to the root of the AOSP, as shown in Figure 2.12. Note that the proprietary binaries should match the device that you are going to flash in. Then clean up the output of any previous build.
 
i. set up environment and choose target.
 
j. Build the code by make -j6. Also, it would take a long time to make for the first time.

k. Install the system into the device. Note android sdk should be installed properly to use fastboot as the tool to falsh the device. lsusb and adb devices can be used to test whether the device is well connected to the computer by USB and ready to fastboot. Then navigate to the folder which contains the image files, and use fastboot -w flashall (-w will erase all the user data) to flash the device. (Note the device needs to be backed up because all the user data would be erased). 

l. Use the same method to make PBD SDK and import the SDK into Android Studio. 

II. Install the PBD AppStore

a. Download the code from Git. Open the file in Android Studio.

b. Import the PBD SDK into Android Studio by open Project Structure window (under file/project structure). Change the path of the SDK to the path of the PBD SDK you place. 

c. Connect your device with computer by USB debugging mode, and run the app on your device. 

III. Set up the Sever

a. Download the source code from the repository. Make sure MongoDB, npm was installed properly.
 
b. Navigate to the root of the source code in the terminal. Then use npm init and npm install to initialize and install all the required packages. 

c. nodemon app.js to run the server. Additionally, type localhost:3000/api/app_infos in a web browser to check whether the sever can respond to the request successfully. 

d. To upload an apk, put the apk file under folder apk_fils, then run nodemon file_driver.js, then the apk would be uploaded into the database.

e. To upload a DPA file, start mongo shell by mongo, then switch to a pre-defined database by use database-name. Insert a new document by db.collection-name.insert{JSON-here}. 
