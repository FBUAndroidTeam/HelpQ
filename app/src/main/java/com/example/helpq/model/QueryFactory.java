package com.example.helpq.model;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.example.helpq.model.Question.KEY_ASKER;

public class QueryFactory {

    private static String KEY_WRITTEN = "written";

    public static class Questions {

        // Query for all archived questions.
        public static ParseQuery<Question> getArchivedQuestions() {
            ParseQuery<Question> query = new ParseQuery<>(Question.class);
            query.whereEqualTo(Question.KEY_ARCHIVED, true)
                    .include(KEY_ASKER);
            return query;
        }

        // Query for all questions that are not archived.
        public static ParseQuery<Question> getQuestionsForQueue() {
            ParseQuery<Question> query = new ParseQuery<>(Question.class);
            query.whereEqualTo(Question.KEY_ARCHIVED, false)
                    .include(KEY_ASKER);
            return query;
        }

        // Query for public answered questions asked by all students.
        public static ParseQuery<Question> getStudentBoardMessages() {
            ParseQuery<Question> query = new ParseQuery<>(Question.class);
            query.include(KEY_ASKER)
                    .whereEqualTo(Question.KEY_ARCHIVED, true)
                    .whereEqualTo(Question.KEY_IS_PRIVATE, false)
                    .whereEqualTo(Question.KEY_HELP_TYPE, KEY_WRITTEN)
                    .orderByDescending(Question.KEY_ANSWERED_AT);
            return query;
        }

        // Query for answered questions asked by the current user only.
        public static ParseQuery<Question> getStudentInboxMessages() {
            ParseQuery<Question> query = new ParseQuery<>(Question.class);
            query.include(KEY_ASKER)
                    .whereEqualTo(Question.KEY_HELP_TYPE, KEY_WRITTEN)
                    .whereEqualTo(Question.KEY_ARCHIVED, true)
                    .orderByDescending(Question.KEY_ANSWERED_AT);
            return query;
        }

        // Query for all questions answered by this admin (the current user).
        public static ParseQuery<Question> getAdminBoardMessages() {
            ParseQuery<Question> query = new ParseQuery<Question>(Question.class);
            query.include(KEY_ASKER)
                    .whereEqualTo(Question.KEY_ARCHIVED, true)
                    .whereEqualTo(Question.KEY_HELP_TYPE, KEY_WRITTEN)
                    .orderByDescending(Question.KEY_ANSWERED_AT);
            return query;
        }

        public static ParseQuery<Question> getNumberOfPendingQuestions(List<ParseUser> students) {
            ParseQuery<Question> query = new ParseQuery<Question>(Question.class);
            query.whereEqualTo(Question.KEY_ARCHIVED, false)
                    .whereContainedIn(KEY_ASKER, Arrays.asList(students))
                    .include(KEY_ASKER);
            return query;
        }
    }

    public static class Users {

        // Query for all students whose admin is the current user.
        public static ParseQuery<ParseUser> getStudentList() {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(User.KEY_ADMIN_NAME, ParseUser.getCurrentUser().getUsername());
            return query;
        }

        // Query for the current user's admin.
        public static ParseQuery<ParseUser> getAdmin() {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(User.KEY_USERNAME, User.getAdminName(ParseUser.getCurrentUser()));
            return query;
        }

        // Query for users that have the given username.
        public static ParseQuery<ParseUser> getUserByUsername(String username) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", username);
            return query;
        }

        public static ParseQuery<ParseUser> getUserByObjectId(String objectId) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", objectId);
            return query;
        }

        // Query for all admins.
        public static ParseQuery<ParseUser> getAllAdmins() {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(User.KEY_IS_ADMIN, true);
            return query;
        }
    }

    public static class Workshops {

        // Query for all workshops created by the current user.
        public static ParseQuery<Workshop> getWorkshopsForAdmin() {
            ParseQuery<Workshop> query = ParseQuery.getQuery("Workshop");
            query.whereEqualTo("creator", ParseUser.getCurrentUser());
            query.whereGreaterThan("startTime", new Date(System.currentTimeMillis()));
            query.addAscendingOrder("startTime");
            return query;
        }

        // Query for all workshops.
        public static ParseQuery<Workshop> getWorkshopsForStudent() {
            ParseQuery<Workshop> query = ParseQuery.getQuery("Workshop");
            query.include("creator");
            query.addAscendingOrder("startTime");
            query.whereGreaterThan("startTime", new Date(System.currentTimeMillis()));
            return query;
        }
    }

    public static class Notifications {

        // Query for all of the current user's notifications.
        public static ParseQuery<Notification> getNotifications() {
            ParseQuery<Notification> query = new ParseQuery<>(Notification.class);
            query.whereEqualTo(Notification.KEY_USER, ParseUser.getCurrentUser());
            return query;
        }

        // Find all notifications related to the question with the given object id.
        public static ParseQuery<Notification> getNotificationsByQuestion(String questionId) {
            ParseQuery<Notification> query = new ParseQuery<>(Notification.class);
            query.whereEqualTo(Notification.KEY_QUESTION_ID, questionId);
            return query;
        }

        // Find all notifications related to the workshop with the given object id.
        public static ParseQuery<Notification> getNotificationsByWorkshop(String workshopId) {
            ParseQuery<Notification> query = new ParseQuery<>(Notification.class);
            query.whereEqualTo(Notification.KEY_WORKSHOP_ID, workshopId);
            return query;
        }
    }

    public static class WaitTimes {

        // Get the wait times that correspond to the current user's admin,
        // or, get the current user's wait times, if they are an admin.
        public static ParseQuery<WaitTime> getAdminWaitTimes() {
            ParseUser user = ParseUser.getCurrentUser();
            String adminName;
            if (User.isAdmin(user)) adminName = user.getUsername();
            else adminName = User.getAdminName(user);
            ParseQuery<WaitTime> query = new ParseQuery<WaitTime>(WaitTime.class);
            query.whereEqualTo(WaitTime.KEY_ADMIN_NAME, adminName);
            return query;
        }
    }

    public static class Replies {

        public static ParseQuery<Reply> getVerifiedReplies() {
            ParseQuery<Reply> query = ParseQuery.getQuery("Reply");
            query.whereEqualTo(Reply.KEY_VERIFY, true);
            query.include(Reply.KEY_USER);
            return query;
        }

        public static ParseQuery<Reply> deleteAssociatedReplies(Question q) {
            ParseQuery<Reply> query = ParseQuery.getQuery("Reply");
            query.whereEqualTo(Reply.KEY_QUESTION, q);
            return query;
        }
    }
}
