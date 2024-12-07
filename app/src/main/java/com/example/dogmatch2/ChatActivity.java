package com.example.dogmatch2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.dogmatch2.MessageChat;
import com.example.dogmatch2.Message;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    FloatingActionButton sendButton;
    List<Message> messageList;
    MessageChat messageChat;
    public static final MediaType JSON
            = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text1);
        messageEditText = findViewById(R.id.message_edit_text1);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageChat = new MessageChat(messageList);
        recyclerView.setAdapter(messageChat);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_chat);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_maps) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_chat) {
                return true;
            } else if (itemId == R.id.bottom_user) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageChat.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageChat.getItemCount());
            }
        });
    }
    void addResponse(String response) {
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callAPI(String question) {
        // Add a "Typing..." message to the chat
        messageList.add(new Message("Typing...", Message.SENT_BY_BOT));

        // Create the JSON body for the request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 256);
            jsonBody.put("temperature", 1);  // Fixed typo here
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

        // Create the request body
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        // Build the request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        // Make the API call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Convert the response body to a string
                        String responseBody = response.body().string();
                        // Parse the response body as JSON
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        addResponse("Failed to parse response: " + e.getMessage());
                    }
                } else {
                    addResponse("Failed to load response: " + response.body().string());
                }
            }
        });
    }
}




