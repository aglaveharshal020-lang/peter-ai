
package com.peterai.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView output;
    private TextToSpeech tts;
    private static final int SPEECH_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);

        layout.setBackgroundColor(0xFF101820);

        TextView title = new TextView(this);
        title.setText("PETER AI");
        title.setTextSize(34);
        title.setTextColor(0xFF00E5FF);
        title.setGravity(Gravity.CENTER);

        output = new TextView(this);
        output.setText("Hello! I am Peter AI.\nYour personal assistant.");
        output.setTextSize(19);
        output.setTextColor(0xFFFFFFFF);
        output.setGravity(Gravity.CENTER);
        output.setPadding(10, 50, 10, 50);

        Button speak = new Button(this);
        speak.setText("TALK TO PETER");

        Button hello = new Button(this);
        hello.setText("TEST VOICE");

        layout.addView(title);
        layout.addView(output);
        layout.addView(speak);
        layout.addView(hello);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            }
        });

        speak.setOnClickListener(v -> startListening());

        hello.setOnClickListener(v ->
            speakText("Hello! I am Peter AI. How can I help you?")
        );
    }

    private void startListening() {
        Intent intent = new Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        );

        try {
            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (Exception e) {
            output.setText("Speech recognition is unavailable.");
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST
                && resultCode == RESULT_OK
                && data != null) {

            ArrayList<String> results =
                data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                );

            if (results != null && !results.isEmpty()) {
                String command = results.get(0);

                output.setText("You: " + command);

                speakText("You said " + command);
            }
        }
    }

    private void speakText(String message) {
        if (tts != null) {
            tts.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "PETER_AI"
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
