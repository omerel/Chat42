package com.example.omer.chat42;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by omer on 19/11/2016.
 */

public class ChatAdapter extends BaseAdapter implements Constants {

    private Activity context;
    private final List<ChatMessage> chatMessages;
    private String deviceAddress;
    private Messenger mMessenger;


    public ChatAdapter(Activity context, List<ChatMessage> chatMessagesHistory, Messenger messenger){
        this.context = context;
        this.chatMessages = chatMessagesHistory;
        this.mMessenger = messenger;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.item_chat_bubbles, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = deviceAddress.equals(chatMessage.getSenderAddress());
        //to simulate whether it me or other sender
        setAlignment(holder,myMsg);

        holder.txtMessage.setText(chatMessage.getMessage());

        if (chatMessage.getPicture() != null){
            holder.picture.setImageBitmap(chatMessage.getPicture());
            holder.picture.setVisibility(View.VISIBLE);
            holder.picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Send the picture to the activity
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("picture",chatMessage.getPicture());
                    Message msg = Message.obtain(null, OPEN_PICTURE);
                    msg.setData(bundle);
                    try {
                        mMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        else{
            holder.picture.setVisibility(View.GONE);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_TEMPLATE);
        String date = sdf.format(chatMessage.getDateTime());
        holder.txtInfo.setText(date);
        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void setMyAddress(String address){
        this.deviceAddress = address;
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.contentBubble.setBackgroundResource(R.drawable.bubble_right);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentBubble.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentBubble.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);
            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.picture.getLayoutParams();
            holder.picture.setLayoutParams(layoutParams);
        } else {
            holder.contentBubble.setBackgroundResource(R.drawable.bubble_left);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentBubble.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentBubble.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);


            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.picture.getLayoutParams();
            holder.picture.setLayoutParams(layoutParams);


        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.textView_item_chat);
        holder.content = (LinearLayout) v.findViewById(R.id.item_chat_content);
        holder.contentBubble = (LinearLayout) v.findViewById(R.id.item_chat_bubble);
        holder.txtInfo = (TextView) v.findViewById(R.id.text_view_msg_info);
        holder.picture = (ImageView)v.findViewById(R.id.imageView_item_chat);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentBubble;
        public ImageView picture;
    }
}
