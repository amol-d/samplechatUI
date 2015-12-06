package chatapp.sample.amol.com.samplechatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import chatapp.sample.amol.com.samplechatapp.adapter.ChatAdapter;
import chatapp.sample.amol.com.samplechatapp.model.Message;
import chatapp.sample.amol.com.samplechatapp.service.ChatService;
import chatapp.sample.amol.com.samplechatapp.utils.Constants;
import chatapp.sample.amol.com.samplechatapp.utils.Util;

public class ChatActivity extends AppCompatActivity {

    private ResponseReceiver receiver;
    private Handler handler;
    private EditText etMessage;
    private ImageButton btSend;
    private RecyclerView rvChat;
    private ArrayList<Message> mMessages;
    private ChatAdapter chatRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        etMessage = (EditText) findViewById(R.id.messageEditText);
        btSend = (ImageButton) findViewById(R.id.sendMessageButton);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);

        // Setting the LayoutManager.
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        //Set LayoutManager to RecyclerView
        rvChat.setLayoutManager(layoutManager);

        // initialize the adapter
        mMessages = new ArrayList<Message>();

        chatRecyclerAdapter = new ChatAdapter(mMessages);
        // attach the adapter to the RecyclerView
        rvChat.setAdapter(chatRecyclerAdapter);

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMessage(etMessage.getText().toString().trim());
            }
        });
    }

    private void sendMessage(String trim) {
        //TODO send message
    }

    @Override
    protected void onResume() {
        super.onResume();
        // create intent filter and register the broadcast receiver for the chat service
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        handler = new Handler();
        refreshMessages();

        // Run the runnable object defined every 100ms
        handler.postDelayed(runnable, 5000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        handler = null;
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            chatRecyclerAdapter.addMessage(Util.getNewMessage());
            rvChat.scrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
            handler.postDelayed(this, 5000);
        }
    };

    private void refreshMessages() {
        // start intent service
        Intent msgIntent = new Intent(this, ChatService.class);
        //TODO send authentication and other required info.
        startService(msgIntent);
    }

    // Broadcast receiver that will receive data from service
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "action_msgs_response";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Message> messages = (ArrayList<Message>) intent.getSerializableExtra(Constants.INTENT_MSGS_EXTRA);
            chatRecyclerAdapter.updateList(messages);
            rvChat.scrollToPosition(messages.size() - 1);
        }
    }
}
