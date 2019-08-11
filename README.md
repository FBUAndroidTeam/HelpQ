<img src="https://github.com/FBUAndroidTeam/HelpQ/blob/master/app/src/main/res/drawable/helpq_logo.png" width="500">

# HelpQ

**HelpQ** is an app that revamps the CodePath training that FBU Engineering interns experience during the first three weeks of their internships by facilitating smoother communication between students and administrators.

## 1. User Stories (Required and Optional)

**Required Features**
 
- [x] Student/admin can login and logout of their account. 
- [x] Student can create a question indicating priority level. 
- [x] Student/admin can see questions on the queue, which is sorted based on priority and date.
- [x] Admin can delete questions from the queue.
- [x] Students can delete their own question from the queue.   

**Stretch Features**

- [x] Student can request the type of help needed when asking a question: written or in-person.
  - [x] Admin can submit answers to questions that have requested written help.
- [x] Pull down to refresh. 
- [x] Student can register if they don't have an account and pick an available admin. 
- [x] Facebook login. 
- [x] Admin can create workshops.
  - [x] Student can sign up for workshops.
- [x] Students have an inbox of all the questions they have previously asked, with their answers.
- [x] Student/admin can see a board of all students' previously answered questions.
  - [x] Admin can choose to make a question's answer private, so that it will only go to that student's inbox.
- [x] Admin can see a list of their own students.
  - [x] Admin can see a list of pending questions for each student.
- [x] Student/admin can see their profile page with their Facebook picture.
  - [x] Admin's profile displays the number of questions they have answered.
  - [x] Student's profile displays the number of answers that have been verified.
- [x] Login persists.
- [x] In-app notifications.
- [x] Students can like and reply to other student's questions on the queue.
  - If a student likes a question, it will also appear in their inbox when answered.
- [x] Admin can verify student replies to other student's questions.
- [x] Live estimated wait times.
  - [x] Displayed for each priority when asking a question.
  - [x] Displayed for each question on the queue.
- [x] Seachable queue, board, and inbox.
  - [x] Admin/student can enter a search query and only see results that have a significant match.
  - [x] Query results update with every character change.
- [x] Progress bars shown when loading. 
- [x] Queue, board, and inbox items animated.
- [x] New items are marked on queue, board, inbox, and workshops.
- [x] Student is reminded 15 minutes before a workshop they are signed up for begins.
  - [x] Students have a settings page where they can change the preferred time to 5 or 10 minutes.
- [x] Sound effects added.   
- [x] Orientation changes handled.
- [x] Our own logo (: 

## 2. Screen Archetypes

 * Login
   * Student/admin can login to their account.
 * Registration
   * Student can register if they do not have an account and pick an available admin. 
 * Queue
    * Student/admin can see questions on the queue that is sorted based on priority and date.
    * Admin can delete questions from the queue.
    * Students can delete their own question from the queue.
    * Student can create a question indicating priority level.
      * Student can choose a help type, written or in-person help. 
    * Students can like and reply to other student's questions on the queue.
      * Admin can answer student written help.
      * Admin can verify student replies to other student's questions.
    * Admin/student can search for specific queries.
 * Profile
    * Student/admin can see a profile page with their Facebook picture and stats.
    * Student/admin can logout of his/her account. 
 * Workshop
    * Student can sign up for workshops. 
    * Admin can create workshops.
 * Inbox
    * Student has an inbox for written help questions that the admin has answered.
    * Student can search for specific queries.
 * Board
    * Student/admin can see a board of all past questions admins choose to make public.
    * Admin/student can search for specific queries.
 * Enrolled Students
    * Admin can see a list of their own students.
    * Admin can navigate to a list of pending questions for each student.

## 3. Navigation

**Tab Navigation** (Tab to Screen)

 **Student**
 * Profile
 * Workshop
 * Queue
 * Inbox
 * Board

**Admin**
 * Profile
 * Enrolled students 
 * Queue
 * Workshop
 * Board

**Flow Navigation** (Screen to Screen)

 * Login
   * => Queue
   * => Registration (if haven't logged in before)
 * Registration
   * => Queue
* Queue
   * => **Student:** Create a question
   * => **Admin:** Reply (only for written help)
   * => Student-to-student help
* Profile
   * => Login (if logout button pressed)
* Workshop
   * => **Admin:** Create a workshop
* Enrolled Students List (Admin only)
   * => List of pending questions
      * => Student-to-student help
* Inbox (Student only)
   * => Student-to-student help
* Board
   * => Student-to-student help
