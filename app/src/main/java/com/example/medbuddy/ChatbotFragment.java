package com.example.medbuddy;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class ChatbotFragment extends Fragment {

    private LinearLayout chatMessageLayout;
    private EditText chatInput;
    private ScrollView chatScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        chatMessageLayout = view.findViewById(R.id.chat_message_layout);
        chatInput = view.findViewById(R.id.chat_input);
        chatScrollView = view.findViewById(R.id.chat_scroll_view);
        ImageButton sendButton = view.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    addMessageToChat("You", message);
                    chatInput.setText(""); // Clear input field
                    callGeminiAPI(message); // Call the API method
                }
            }
        });

        return view;
    }

    private void addMessageToChat(String sender, String message) {
        View messageView = LayoutInflater.from(getContext()).inflate(
                sender.equals("You") ? R.layout.item_message_user : R.layout.item_message_bot,
                chatMessageLayout,
                false
        );

        TextView messageText = messageView.findViewById(R.id.message_text);
        messageText.setText(message);

        chatMessageLayout.addView(messageView);

        // Scroll to the bottom of the chat
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void callGeminiAPI(String userMessage) {
        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyCVESdoEoPK1xWlJFJiw3j2JrIbOrps_I4");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("You are MediWise, a knowledgeable and helpful AI assistant designed to provide accurate and reliable health information. You can answer questions about medications, diseases, symptoms, and general health advice.\n\n" +
                        "**Here are some guidelines to follow:**\n\n" +
                        "* **Prioritize accuracy:** Always provide information based on credible medical sources.\n" +
                        "* **Avoid medical diagnosis:** While you can provide information on symptoms and potential causes, do not offer definitive diagnoses.\n" +
                        "* **Promote healthy habits:** Encourage users to adopt healthy lifestyles and consult with healthcare professionals for personalized advice.\n" +
                        "* **Be empathetic:** Respond to user queries with compassion and understanding.\n" +
                        "* **Respect privacy:** Never ask for or share personal health information.\n\n" +
                        "**Example:**\n\n" +
                        "**User:**"+userMessage+ "\n" +
                        "**MediWise:**")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String botResponse = result.getText();
                    getActivity().runOnUiThread(() -> addMessageToChat("Bot", botResponse));
                }

                @Override
                public void onFailure(Throwable t) {
                    getActivity().runOnUiThread(() -> addMessageToChat("Bot", "Error: " + t.toString()));
                }
            }, getActivity().getMainExecutor());
        }
    }
}
//package com.example.medbuddy;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.google.ai.client.generativeai.GenerativeModel;
//import com.google.ai.client.generativeai.java.GenerativeModelFutures;
//import com.google.ai.client.generativeai.type.Content;
//import com.google.ai.client.generativeai.type.GenerateContentResponse;
//
//import com.google.common.util.concurrent.FutureCallback;
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListenableFuture;
//
//public class ChatbotFragment extends Fragment {
//
//    private LinearLayout chatMessageLayout;
//    private EditText chatInput;
//    private ScrollView chatScrollView;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
//
//        chatMessageLayout = view.findViewById(R.id.chat_message_layout);
//        chatInput = view.findViewById(R.id.chat_input);
//        chatScrollView = view.findViewById(R.id.chat_scroll_view);
//        ImageButton sendButton = view.findViewById(R.id.send_button);
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = chatInput.getText().toString().trim();
//                if (!message.isEmpty()) {
//                    addMessageToChat("You", message);
//                    chatInput.setText(""); // Clear input field
//                    callGeminiAPI(message); // Call the API method
//                }
//            }
//        });
//
//        return view;
//    }
//
//    private void addMessageToChat(String sender, String message) {
//        TextView textView = new TextView(getContext());
//        textView.setText(sender + ": " + message);
//        textView.setPadding(8, 8, 8, 8);
//        textView.setTextSize(16);
//        chatMessageLayout.addView(textView);
//
//        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
//    }
//
//    private void callGeminiAPI(String userMessage) {
//        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyCVESdoEoPK1xWlJFJiw3j2JrIbOrps_I4");
//        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
//        Content content = new Content.Builder()
//                .addText("You are MediWise, a knowledgeable and helpful AI assistant designed to provide accurate and reliable health information. You can answer questions about medications, diseases, symptoms, and general health advice.\n\n" +
//                        "**Here are some guidelines to follow:**\n\n" +
//                        "* **Prioritize accuracy:** Always provide information based on credible medical sources.\n" +
//                        "* **Avoid medical diagnosis:** While you can provide information on symptoms and potential causes, do not offer definitive diagnoses.\n" +
//                        "* **Promote healthy habits:** Encourage users to adopt healthy lifestyles and consult with healthcare professionals for personalized advice.\n" +
//                        "* **Be empathetic:** Respond to user queries with compassion and understanding.\n" +
//                        "* **Respect privacy:** Never ask for or share personal health information.\n\n" +
//                        "**Example:**\n\n" +
//                        "**User:**"+userMessage+ "\n" +
//                        "**MediWise:**")
//                .build();
//
//        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
//                @Override
//                public void onSuccess(GenerateContentResponse result) {
//                    String botResponse = result.getText();
//                    getActivity().runOnUiThread(() -> addMessageToChat("Bot", botResponse));
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    getActivity().runOnUiThread(() -> addMessageToChat("Bot", "Error: " + t.toString()));
//                }
//            }, getActivity().getMainExecutor());
//        }
//    }
//}
