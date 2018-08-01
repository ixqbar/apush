package cn.linjujia.mobile.model;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.linjujia.mobile.R;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements MessageItemTouchHelper {

	public class ViewHolder extends RecyclerView.ViewHolder {

		TextView messageTitleView;
		TextView messageDateView;
		TextView messageContentView;

		public ViewHolder(View view, int viewType) {
			super(view);
			if (viewType == MessageAdapter.VIEW_TYPE_IS_PUSH_MESSAGE) {
				messageTitleView = view.findViewById(R.id.messageTitleView);
				messageDateView = view.findViewById(R.id.messageDateView);
			}

			messageContentView = view.findViewById(R.id.messageContentView);
		}
	}

	final static int VIEW_TYPE_IS_PUSH_MESSAGE = 1;
	final static int VIEW_TYPE_IS_SETTING_MESSAGE = 2;

	private Context context;
	private List<Message> messageList;

	public MessageAdapter(Context context, List<Message> messageList) {
		this.context = context;
		this.messageList = messageList;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return VIEW_TYPE_IS_SETTING_MESSAGE;
		}

		return VIEW_TYPE_IS_PUSH_MESSAGE;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == VIEW_TYPE_IS_PUSH_MESSAGE) {
			view = View.inflate(context, R.layout.message_item, null);
		} else {
			view = View.inflate(context, R.layout.settings_item, null);
		}

		ViewHolder holder = new ViewHolder(view, viewType);
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Message message = messageList.get(position);

		if (position > 0) {
			holder.messageTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			holder.messageTitleView.setTextColor(Color.parseColor("#194D6B"));
			holder.messageTitleView.setText(message.getTitle());

			holder.messageDateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			holder.messageDateView.setTextColor(Color.parseColor("#BEC8AB"));
			holder.messageDateView.setText(message.getDate());


			holder.messageContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			holder.messageContentView.setTextColor(Color.parseColor("#554A38"));
			holder.messageContentView.setText("\u3000" + Html.fromHtml(message.content, Html.FROM_HTML_MODE_COMPACT));
		} else {
			holder.messageContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			holder.messageContentView.setTextColor(Color.parseColor("#DF4B4A"));
			holder.messageContentView.setText("Token:" + message.getContent());
		}
	}

	@Override
	public int getItemCount() {
		return messageList.size();
	}

	@Override
	public void onItemMove(int fromPosition, int toPosition) {
		if (fromPosition == 0 || toPosition == 0) {
			return;
		}

		Collections.swap(messageList, fromPosition, toPosition);
		notifyItemMoved(fromPosition, toPosition);
	}

	@Override
	public void onItemDismiss(int position) {
		if (position == 0) {
			return;
		}

		Message message = messageList.get(position);
		if (message != null) {
			LitePal.delete(Message.class, message.getId());
		}
		messageList.remove(position);
		notifyItemRemoved(position);

		Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		toast.setText("delete success");
		toast.show();
	}
}
