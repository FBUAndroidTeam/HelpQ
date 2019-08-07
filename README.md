
# HelpQ

**HelpQ** is an app that revamps the CodePath training that FBU Engineering interns experience during the first three weeks of their internships by facilitating smoother communication between students and instructors.

## 1. User Stories (Required and Optional)

**Required Features**
 
- [x] Student/admin can login and out of his/her account. 
- [x] Student can create a question indicating priority level. 
- [x] Student/admin can see questions on the queue that is sorted based on priority.
- [x] Admin can delete questions from the queue.
- [x] Students can delete their own question from the queue.   

**Stretch Features**

- [x] Student can choose a help type, written or in-person help. 
- [x] Admin can answer student written help.
- [x] Swipe up to refresh. 
- [x] Student can register if he/she doesn't have an account and pick an available admin. 
- [x] Facebook login. 
- [x] Admin can create workshops.
- [x] Student can sign up for workshops.    
- [x] Student/admin can see a board of all past questions admins choose to make public.
- [x] Admin can see a list of students enrolled with them.
- [x] Student/admin have a profile page with their Facebook picture.
- [x] Admin can navigate to a list of pending questions of a student from the list of enrolled student page.
- [x] Student has an inbox for written help questions admin answer.
- [x] Login persists.
- [x] In-app notifications.
- [x] Students can like and reply to other student's questions on the queue.
- [x] Admin can verify student replies to other student's questions.
- [x] Live estimated wait time displayed on the queue.
- [x] Seachable queue, board, and inbox.    
- [x] Progress bars shown when loading. 
- [x] Queue, board, and inbox items animated.
- [x] Student is reminded 15 minutes before a workshop their signed up for begins
- [x] Sound effects added.   
- [x] Orientation changes handled.
- [x] Our own logo (: 

## 2. Screen Archetypes

 * Login
   * Student/admin can login his/her account.
 * Registration
   * Student can register if he/she doesn't have an account and pick an available admin. 
 * Queue
    * Student/admin can see questions on the queue that is sorted based on priority.
    * Admin can delete questions from the queue.
    * Students can delete their own question from the queue.
    * Student can create a question indicating priority level.
      * Student can choose a help type, written or in-person help. 
    * Students can like and reply to other student's questions on the queue.
      * Admin can answer student written help.
      * Admin can verify student replies to other student's questions.
    * Live estimated wait time displayed on the queue.
    * Searchable
 * Profile
    * Student/admin have a profile page with their Facebook picture.
    * Student/admin can out of his/her account. 
 * Workshop
    * Student can sign up for workshops. 
    * Admin can create workshops.
 * Inbox
    * Student has an inbox for written help questions admin answer.
    * Searchable
 * Board
    * Student/admin can see a board of all past questions admins choose to make public.
    * Searchable
 * Enrolled Students
    * Admin can see a list of students enrolled with them.
    * Admin can navigate to a list of pending questions of a student from the list of enrolled student page.

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
