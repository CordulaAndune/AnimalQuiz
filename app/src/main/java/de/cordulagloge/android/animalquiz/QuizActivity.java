package de.cordulagloge.android.animalquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

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
    private HashMap<Integer, int[]> answerDictionary;
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
                {{2, R.string.question_lion,0,0},
                        {1, R.string.question_hummingbird,0,0},
                        {3, R.string.question_ratite,0,0},
                        {1,R.string.question_marmoset,2,R.raw.callithrix_call},
                        {2,R.string.question_pelican,0,0},
                        {3,R.string.question_ratite,0,0},
                        {2,R.string.question_elephant,0,0},
                        {1,R.string.question_tiger,0,0},
                        {3,R.string.question_leopard,1,R.drawable.leopard}
                };

        // Hashmap for the answers
        answerDictionary = new HashMap<>();
        answerDictionary.put(R.string.question_lion,
                new int[]{R.string.answer_lion_fighting, R.string.answer_lion_temperature,
                        R.string.answer_lion_fitness, R.string.answer_lion_protection});
        answerDictionary.put(R.string.question_hummingbird, new int[]{R.string.answer_hummingbird_eight_beat,
                R.string.answer_hummingbird_helicopter, R.string.answer_hummingbird_myth,
                R.string.answer_hummingbird_speed});
        answerDictionary.put(R.string.question_marmoset,new int[]{R.string.answer_marmoset_nightingale,
                R.string.answer_marmoset_mouse,R.string.answer_common_marmoset,R.string.answer_marmoset_songbird});
        answerDictionary.put(R.string.question_pelican, new int[]{R.string.answer_pelican_2l,R.string.answer_pelican_5l,
                R.string.answer_pelican_9l,R.string.answer_pelican_13l});
        answerDictionary.put(R.string.question_elephant, new int[]{R.string.answer_elephant_communication,R.string.answer_elephant_hearing,
                R.string.answer_elephant_attractive,R.string.answer_elephant_temperature});
        answerDictionary.put(R.string.question_tiger, new int[]{R.string.answer_tiger_protection,R.string.answer_tiger_camouflage,
                R.string.answer_tiger_sight,R.string.answer_tiger_temperature});

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

        quizBindings.resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resetQuiz();
            }
        });
    }

    private void showRadioButtons(int questionId, int[] currentAnswers, int additionalMedia){
        removeAdditionalViews("Additional View", radioButtonViewBinding.rootRadioButtons);
        radioButtonViewBinding.question.setText(questionId);
        for (int index = 0; index < currentAnswers.length; index++) {
            RadioButton currentButton = (RadioButton) radioButtonViewBinding.answerGroup.getChildAt(index);
            currentButton.setText(getString(currentAnswers[index]));
        }
        quizBindings.rootView.addView(radioButtonViewBinding.rootRadioButtons, 0);
    }

    private void showCheckBoxes(int questionId, int[] currentAnswers, int additionalMedia){
        removeAdditionalViews("Additional View", checkBoxViewBinding.rootCheckBoxes);
        checkBoxViewBinding.question.setText(questionId);
        for (int index = 0; index < currentAnswers.length; index++) {
            CheckBox currentButton = (CheckBox) checkBoxViewBinding.groupCheckboxes.getChildAt(index);
            currentButton.setText(getString(currentAnswers[index]));
        }
        quizBindings.rootView.addView(checkBoxViewBinding.rootCheckBoxes, 0);
    }

    private void showEditText(int questionId, int additionalMedia){
        removeAdditionalViews("Additional View", openQuestionViewBinding.rootOpenQuestion);
        openQuestionViewBinding.question.setText(questionId);
        if (additionalMedia > 0){
            setAdditionalViews(additionalMedia);
        }
        quizBindings.rootView.addView(openQuestionViewBinding.rootOpenQuestion, 0);
    }

    private void setAdditionalViews(int additionalMedia){
        if (additionalMedia == 1){
            ImageView questionPicture = new ImageView(this);
            questionPicture.setImageResource(questionsArray[questionNumber][3]);
            questionPicture.setAdjustViewBounds(true);
            questionPicture.setTag("Additional View");
            questionPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            openQuestionViewBinding.rootOpenQuestion.addView(questionPicture,1);
        }
        // TODO: add MediaPlayer: seekbar and play/pause button
    }

    private void removeAdditionalViews(Object tag, ViewGroup rootView){
        View additionalView = rootView.findViewWithTag(tag);
        rootView.removeView(additionalView);
    }

    private void setNextQuestion(){
        //TODO: get checked answer and compare if is correct
        questionNumber++;
        int currentQuestion = questionsArray[questionNumber][1];
        int media = questionsArray[questionNumber][2];
        if (questionNumber > 0){
            quizBindings.rootView.removeViewAt(0);
        }
       switch (questionsArray[questionNumber][0]){
           case (1):
               showRadioButtons(currentQuestion, answerDictionary.get(currentQuestion),media);
               break;
           case (2):
               showCheckBoxes(currentQuestion, answerDictionary.get(currentQuestion),media);
               break;
           case (3):
               showEditText(currentQuestion,media);
               break;
       }
    }

    private void resetQuiz(){
        Intent startIntent = new Intent(this, MainActivity.class);
        startActivity(startIntent);
    }

}
