package com.example.helpq.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class Search {

    private static final int MAX_RESULTS = 10;

    public static List<Question> mSearch(List<Question> objects, String query) {
        List<Question> result = new ArrayList<>();
        List<String> boardQuestionText = new ArrayList<>();
        Hashtable<String, Question> map = new Hashtable<>();
        for(Question question : objects) {
            boardQuestionText.add(question.getText());
            map.put(question.getText(), question);
        }
        List<ExtractedResult> extractList =
                FuzzySearch.extractTop(query, boardQuestionText, MAX_RESULTS);
        for(ExtractedResult extracted : extractList) {
            result.add(map.get(extracted.getString()));
        }
        return result;
    }
}
