# LiveWaves_Android

   First screen in livewaves is splash screen or launcher screen , in which app logo appear, if our app current version
is greater then live version then the app BASEURL set to test mode, and if app current version is equal to live version
then the app BASEURL set to Development mode.

   After the splash screen the next screen is login screen.In Login Screen the user enter credential and if credential is correct the
next screen is home screen,the user credential is checked through API. In Login Screen you can
also move to the register account screen and register you account.

In HomeScreen the user can move 5 different screen from bottom navigation
1. Home Screen

   the user can see his/her profile, posts, option to edit the profile, option to share the posts , goes to messages screen, search option,
his follower and followings and create events.

   when user click on followers option the new screen open and get all followers from API and same for followings.

   when user click on search option the search screen opens, the three list show here 1.USERS  2.EVENTS  3.HASHTAGS, the all lists filter when
every character entered.

   when user click on EditProfile option the edit profile screen open, In this screen edit detail about his account and user can also have option
to change the password

   When user click on chat option, the screen is opens, in which all chat show, we used technology for chat is firebase firestore, this is
third party library. the user can create chat group with multiple users, can add users in group, can remove users from group. The user can delete message
or in group or individual chat, and can delete whole chat with individual user. The user can send text messages and images in group and individual chat.
The Firebase Storage is used for store images.

   when user click on event option, the events screen is opens, In which user sees MyEVENTS and GOING events, MyEVENTS is created by me and GOING is created
by others. In this screen i have a option to create the EVENTS. when i clicks the Create Event option then new screen is open in this screen we enter detail
about event and click on Create Event Button.

   user have a option to create posts, the user can add video, images and texts in their post. The Storage used for post images and videos
is Firebase Storage. The list of post shows below that created by current user. The current user can is edit/delete his/her post. see comments
and list of users who like post.

2. NewsFeed Screen
   The user can see other users posts, and can give likes and comments, and can share the post. In This screen user have a option to see Paid Posts
and Events.When the current user clicks on user name in post then user profile screen is opens.

3. Live Screen
   In this screen the user can see the list of live streams that can occur and never finished. In Global streams list gets all streams that are created by anyone
and Following streams list get all streams that are created by followings. The use can join the stream by clicking the join button in list item. When user
click the join button. The live stream screen opens and current user see the streaming and total persons that's join the stream.

   The user can also create the stream, and can send request to the user to join the stream as publisher. The User has ables to pause, finish and resume the stream
that created by his/her.

4. Alert/Notification screen
   In this screen the user can sees the incoming notification and perform action by clicks on this. This screen also contain the search option

5. Setting Screen
   The setting screen has contains the option of Edit Profile, Wallet, Analytics, Invite, Contact us, Help, Term and Condition, Getting Started,
and logout.
   In Edit Profile Screen the user can update the profile. Wallet screen show the user his Received, Transferred and WithDrawl balance. using Invite Option the user send request to other person
to join the livewaves application. when we clicks the help button then the help web page opens, same for when we click the term and condition then also web page is opens.