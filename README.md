# Note-Sharing-App
### After installing this app you need to give storage, camera and microphone permission from your Apps settings 
## Login credentials
[Email,Password] = [test10@gmail.com,test10],[test11@gmail.com,test11], [test12@gmail.com,test12] , [test13@gmail.com,test13], [test14@gmail.com,test14],[test15@gmail.com,test15]
#### Use any of the pair to test this app

## Features already implemented 
### Create group
#### One user can create group by giving group name and group descriptions. After creating group he can be able to see his group by swipping down to the screen. Then from menu, he can add new user that must be a real user of this app, remove user, leave this group and see members list. 
### Announcements
#### User can post a new announcement. New announcement can be seen by coming back to the group list page and clicking that group name again.
### Share notes
#### From file fragment, user can upload new files. User can see shared files list of this group by clicking see files button. And can download files.
### Create notes
#### There are several options to create new file. From menu option user can scan any image from camera or gallery, user can use his micro phone to import speech to text, can import images from camera or gallery and also can create a table and import it. While scaning image or speech recognition he can edit text if required. Each time importing something, user need to add this to pdf and start again importing something from menu. And finally he can save and share this file to this group. He can see this file in both app and local Document storage. To see this file in app. He needs to go back to group list and click group name again.

## Feature need to implement in future
### Discussion
#### Members will be able to discuss within a group
### Notification
#### Each new announcement and sharing file will notify each group member

## Improvements or or need to care while running this app
#### Since we insisted on functionalities, we only cared about whether a functionality was working or not. So there are few things we need to improve. We used swipe down refresh in group list page but we didn't use it in announcements and files lists. So what we actually need to see new files or new announcement is we need to go back to group list and entering that group again. See files list need sometimes to load file from firebase storage to app, here we need to use a progress dialog. So it would be good if user click see files button after some time[2 second is enough]. 
