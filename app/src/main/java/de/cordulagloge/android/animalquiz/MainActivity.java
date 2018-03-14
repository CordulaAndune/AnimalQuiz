package de.cordulagloge.android.animalquiz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import de.cordulagloge.android.animalquiz.databinding.ActivityMainBinding;
import de.cordulagloge.android.animalquiz.databinding.ToastViewBinding;

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
                if (playersName.isEmpty()) {
                    showToast();
                } else {
                    startQuizIntent.putExtra("playersName", playersName);
                    startActivity(startQuizIntent);
                }
            }
        });
    }

    private void showToast() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ToastViewBinding toastViewBinding = ToastViewBinding.inflate(inflater);
        Toast toastPlayersName = new Toast(this);
        toastPlayersName.setView(toastViewBinding.rootToast);
        toastViewBinding.toastText.setText(getString(R.string.enter_name_before_start));
        toastPlayersName.setDuration(Toast.LENGTH_SHORT);
        toastPlayersName.show();
    }
}
