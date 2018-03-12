package de.cordulagloge.android.animalquiz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import de.cordulagloge.android.animalquiz.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding bindings = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindings.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startQuizIntent = new Intent(getApplicationContext(), QuizActivity.class);
                String playersName = bindings.playersName.getText().toString();
                startQuizIntent.putExtra("payersName", playersName);
                startActivity(startQuizIntent);
            }
        });
    }
}
