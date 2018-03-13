package de.cordulagloge.android.animalquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import de.cordulagloge.android.animalquiz.databinding.ActivityQuizBinding;
import de.cordulagloge.android.animalquiz.databinding.QuestionCheckboxLayoutBinding;
import de.cordulagloge.android.animalquiz.databinding.QuestionOpenLautBinding;
import de.cordulagloge.android.animalquiz.databinding.QuestionRadiobuttonLayoutBinding;

public class QuizActivity extends AppCompatActivity {

    private int questionNumber;
    private int[][] questionsArray;
    private boolean[] hasCorrectAnswered;
    private HashMap<Integer, int[][]> answerDictionary;
    private HashMap<Integer, String[]> correctAnswersDictionary;
    private ActivityQuizBinding quizBindings;
    private QuestionRadiobuttonLayoutBinding radioButtonViewBinding;
    private QuestionCheckboxLayoutBinding checkBoxViewBinding;
    private QuestionOpenLautBinding openQuestionViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizBindings = DataBindingUtil.setContentView(this, R.layout.activity_quiz);
        questionNumber = -1;

        // Array for the questions
        // QuestionType, Question, picture(1) or mp3(2)
        // questiontype: 1: RadioButtons, 2: CheckBoxes, 3: open questions
        questionsArray = new int[][]
                {{2, R.string.question_lion, 0, 0},
                        {1, R.string.question_hummingbird, 0, 0},
                        {3, R.string.question_ratite, 0, 0},
                        {1, R.string.question_marmoset, 2, R.raw.callithrix_call},
                        {1, R.string.question_elephant, 0, 0},
                        {2, R.string.question_tiger, 0, 0},
                        {3, R.string.question_leopard, 1, R.drawable.leopard},
                        {1, R.string.question_pelican, 0, 0}
                };

        hasCorrectAnswered = new boolean[questionsArray.length];

        // Hashmap for the answers
        // String of answer + correctness with 0 = false, 1= correct
        answerDictionary = new HashMap<>();
        answerDictionary.put(R.string.question_lion,
                new int[][]{{R.string.answer_lion_fighting, 1}, {R.string.answer_lion_temperature, 0},
                        {R.string.answer_lion_fitness, 1}, {R.string.answer_lion_protection, 0}});
        answerDictionary.put(R.string.question_hummingbird, new int[][]{{R.string.answer_hummingbird_eight_beat, 1},
                {R.string.answer_hummingbird_helicopter, 0}, {R.string.answer_hummingbird_myth, 0},
                {R.string.answer_hummingbird_speed, 0}});
        answerDictionary.put(R.string.question_marmoset, new int[][]{{R.string.answer_marmoset_nightingale, 0},
                {R.string.answer_marmoset_mouse, 0}, {R.string.answer_common_marmoset, 1}, {R.string.answer_marmoset_songbird, 0}});
        answerDictionary.put(R.string.question_pelican, new int[][]{{R.string.answer_pelican_2l, 0}, {R.string.answer_pelican_5l, 0},
                {R.string.answer_pelican_9l, 0}, {R.string.answer_pelican_13l, 1}});
        answerDictionary.put(R.string.question_elephant, new int[][]{{R.string.answer_elephant_communication, 0}, {R.string.answer_elephant_hearing, 0},
                {R.string.answer_elephant_attractive, 0}, {R.string.answer_elephant_temperature, 1}});
        answerDictionary.put(R.string.question_tiger, new int[][]{{R.string.answer_tiger_protection, 0}, {R.string.answer_tiger_camouflage, 1},
                {R.string.answer_tiger_sight, 1}, {R.string.answer_tiger_temperature, 0}});
        answerDictionary.put(R.string.question_ratite, new int[][]{{R.string.answer_ratite_ostrich, 2},
                {R.string.answer_ratite_emu, 2}, {R.string.answer_ratite_kiwi, 2},
                {R.string.answer_ratite_cassowary, 2}, {R.string.answer_ratite_rhea, 2}});
        answerDictionary.put(R.string.question_leopard, new int[][]{{R.string.answer_leopard, 1}});

        // get all layouts for the different question types
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        openQuestionViewBinding = QuestionOpenLautBinding.inflate(inflater);
        radioButtonViewBinding = QuestionRadiobuttonLayoutBinding.inflate(inflater);
        checkBoxViewBinding = QuestionCheckboxLayoutBinding.inflate(inflater);
        setNextQuestion();

        // set onclicklisteners for all buttons
        quizBindings.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextQuestion();
            }
        });

        quizBindings.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetQuiz();
            }
        });
    }

    /**
     * show single choice question with radio buttons
     *
     * @param questionId      string-id of the question
     * @param currentAnswers  answers of the questions
     * @param additionalMedia should a picture or mp3 player be added
     */
    private void showRadioButtons(int questionId, int[][] currentAnswers, int additionalMedia) {
        removeAdditionalViews("Additional View", radioButtonViewBinding.rootRadioButtons);
        radioButtonViewBinding.question.setText(questionId);
        radioButtonViewBinding.answerGroup.clearCheck();
        for (int index = 0; index < currentAnswers.length; index++) {
            RadioButton currentButton = (RadioButton) radioButtonViewBinding.answerGroup.getChildAt(index);
            currentButton.setText(getString(currentAnswers[index][0]));
            currentButton.setTag(currentAnswers[index][1]);
        }
        quizBindings.rootView.addView(radioButtonViewBinding.rootRadioButtons, 0);
    }

    /**
     * show mulstiple choice question with check boxes
     *
     * @param questionId      string-id of the question
     * @param currentAnswers  answers of the questions
     * @param additionalMedia should a picture or mp3 player be added
     */
    private void showCheckBoxes(int questionId, int[][] currentAnswers, int additionalMedia) {
        removeAdditionalViews("Additional View", checkBoxViewBinding.rootCheckBoxes);
        checkBoxViewBinding.question.setText(questionId);
        for (int index = 0; index < currentAnswers.length; index++) {
            CheckBox currentButton = (CheckBox) checkBoxViewBinding.groupCheckboxes.getChildAt(index);
            currentButton.setChecked(false);
            currentButton.setText(getString(currentAnswers[index][0]));
            currentButton.setTag(currentAnswers[index][1]);
        }
        quizBindings.rootView.addView(checkBoxViewBinding.rootCheckBoxes, 0);
    }

    /**
     * show open question with edit text
     *
     * @param questionId      string id of the question
     * @param additionalMedia should a picture or audio player be added to the view
     */
    private void showEditText(int questionId, int additionalMedia) {
        removeAdditionalViews("Additional View", openQuestionViewBinding.rootOpenQuestion);
        openQuestionViewBinding.question.setText(questionId);
        openQuestionViewBinding.answer.setText("");
        if (additionalMedia > 0) {
            setAdditionalViews(additionalMedia);
        }
        quizBindings.rootView.addView(openQuestionViewBinding.rootOpenQuestion, 0);
    }

    /**
     * set additional view for picure or audio player
     *
     * @param additionalMedia media type
     */
    private void setAdditionalViews(int additionalMedia) {
        if (additionalMedia == 1) {
            ImageView questionPicture = new ImageView(this);
            questionPicture.setImageResource(questionsArray[questionNumber][3]);
            questionPicture.setAdjustViewBounds(true);
            questionPicture.setTag("Additional View");
            questionPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            openQuestionViewBinding.rootOpenQuestion.addView(questionPicture, 1);
        }
        // TODO: add MediaPlayer: seekbar and play/pause button
    }

    /**
     * remove view for additional media from layout
     *
     * @param tag      of the additional view
     * @param rootView root layout of the view
     */
    private void removeAdditionalViews(Object tag, ViewGroup rootView) {
        View additionalView = rootView.findViewWithTag(tag);
        rootView.removeView(additionalView);
    }

    /**
     * get the next question and set new layout
     */
    private void setNextQuestion() {
        //TODO: get checked answer and compare if is correct
        if (questionNumber >= 0) {
            checkAnswers(questionsArray[questionNumber][1], questionsArray[questionNumber][0]);
            Log.i("QuizActivity", "Last question correct answered? " + hasCorrectAnswered[questionNumber]);
        }
        //TODO: set for last question
        questionNumber++;
        int currentQuestion = questionsArray[questionNumber][1];
        int media = questionsArray[questionNumber][2];
        if (questionNumber > 0) {
            quizBindings.rootView.removeViewAt(0);
        }
        switch (questionsArray[questionNumber][0]) {
            case (1):
                showRadioButtons(currentQuestion, answerDictionary.get(currentQuestion), media);
                break;
            case (2):
                showCheckBoxes(currentQuestion, answerDictionary.get(currentQuestion), media);
                break;
            case (3):
                showEditText(currentQuestion, media);
                break;
        }
    }

    private void checkAnswers(int answeredQuestionId, int questionType) {
        switch (questionType) {
            case (1):
                    int checkedButton = radioButtonViewBinding.answerGroup.getCheckedRadioButtonId();
                    int correctButton = radioButtonViewBinding.answerGroup.findViewWithTag(1).getId();
                    hasCorrectAnswered[questionNumber]= checkedButton == correctButton;
                break;
            case (2):
                int countChild = checkBoxViewBinding.groupCheckboxes.getChildCount();
                for (int index = 0; index < countChild; index++){
                    CheckBox currentCheckBox = (CheckBox) checkBoxViewBinding.groupCheckboxes.getChildAt(index);
                    int currentAnswer = currentCheckBox.isChecked()? 1:0;
                    int correctAnswer = (int) currentCheckBox.getTag();
                    if (currentAnswer != correctAnswer){
                        hasCorrectAnswered[questionNumber] = false;
                        break;
                    }
                    else {
                        hasCorrectAnswered[questionNumber] = true;
                    }
                }
                break;
            case (3):
                String answer = openQuestionViewBinding.answer.getText().toString().toLowerCase();
                int[][] currentAnswers = answerDictionary.get(answeredQuestionId);
                int neededCorrect = currentAnswers[0][1];
                int numberOfCorrectAnswers = 0;
                for (int index = 0; index < currentAnswers.length; index++) {
                    String correctAnswer = getString(currentAnswers[index][0]);
                    if (answer.contains(correctAnswer)){
                        numberOfCorrectAnswers++;
                    }
                    if (numberOfCorrectAnswers == neededCorrect){
                        hasCorrectAnswered[questionNumber] = true;
                        break;
                    }
                    else {
                        hasCorrectAnswered[questionNumber] = false;
                    }
                }
                break;
        }
    }

    /**
     * reset quiz to start screen
     */
    private void resetQuiz() {
        Intent startIntent = new Intent(this, MainActivity.class);
        startActivity(startIntent);
    }

}
