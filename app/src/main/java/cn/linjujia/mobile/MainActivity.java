package cn.linjujia.mobile;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import cn.linjujia.mobile.model.Message;
import cn.linjujia.mobile.model.MessageAdapter;
import cn.linjujia.mobile.model.MessageItemTouchCallback;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;

import android.app.AlertDialog;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	public static String LOG_TAG = MainActivity.class.getName();

	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;

	SwipeRefreshLayout swipeRefreshLayout;
	int lastMessageId = 0;

	List<Message> allMessageList;
	Message settingMessage;
	MessageAdapter messageAdapter;

	private void loadDBMessage() {
		List<Message> messageList = LitePal.order("id desc").find(Message.class);
		if (messageList.isEmpty() == false) {
			lastMessageId = messageList.get(0).getId();
			allMessageList.addAll(messageList);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// /data/data/com.linjujia.mobile/shared_prefs
		sharedPreferences = getSharedPreferences(MainActivity.class.getName(), MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();

		boolean sharedInit = sharedPreferences.getBoolean("dataInit", false);
		if (false == sharedInit) {
			sharedPreferencesEditor.putBoolean("dataInit", true);
			sharedPreferencesEditor.commit();
			Log.v(LOG_TAG, "shared first init");
		} else {
			Log.v(LOG_TAG, "shared already init");
		}

		allMessageList = new ArrayList<>();

		settingMessage = new Message();
		settingMessage.setType(Message.MESSAGE_TYPE_IS_SETTING);
		settingMessage.setContent(sharedPreferences.getString("token", ""));
		allMessageList.add(settingMessage);

		LitePal.initialize(this);
		SQLiteDatabase db = LitePal.getDatabase();

		Pushy.listen(this);
		new RegisterForPushNotificationsAsync().execute();

		loadDBMessage();

		messageAdapter = new MessageAdapter(this, allMessageList);

		swipeRefreshLayout = findViewById(R.id.swipeRefresh);
		swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#FF8D1B"),Color.parseColor("#45FF5C"));
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				List<Message> result = LitePal.where("id>?", String.valueOf(lastMessageId)).order("id desc").find(Message.class);
				if (result.isEmpty() == false) {
					lastMessageId = result.get(0).getId();
					allMessageList.addAll(1, result);
					messageAdapter.notifyDataSetChanged();

					Context context = getApplicationContext();

					Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
					toast.setText("refresh success");
					toast.show();

					NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
					notificationManager.cancelAll();
				}
				swipeRefreshLayout.setRefreshing(false);
			}
		});

		RecyclerView recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
		recyclerView.setAdapter(messageAdapter);

		MessageItemTouchCallback messageItemTouchCallback = new MessageItemTouchCallback(messageAdapter);
		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(messageItemTouchCallback);
		itemTouchHelper.attachToRecyclerView(recyclerView);
	}

	private class RegisterForPushNotificationsAsync extends AsyncTask<String, Void, RegistrationResult> {

		@Override
		protected RegistrationResult doInBackground(String... params) {
			RegistrationResult result = new RegistrationResult();

			try {
				result.deviceToken = Pushy.register(MainActivity.this);
			} catch (PushyException exc) {
				result.error = exc;
			}

			return result;
		}

		@Override
		protected void onPostExecute(RegistrationResult result) {
			if (isFinishing()) {
				return;
			}

			if (result.error != null) {
				Log.e(LOG_TAG, "Registration failed: " + result.error.getMessage());

				new AlertDialog.Builder(MainActivity.this).setTitle(R.string.error)
					.setMessage(result.error.getMessage())
					.setPositiveButton(R.string.ok, null)
					.create()
					.show();
			} else {
				Log.i(LOG_TAG, "Device token: " + result.deviceToken);

				sharedPreferencesEditor.putString("token", result.deviceToken);
				sharedPreferencesEditor.commit();

				settingMessage.setContent(result.deviceToken);
				messageAdapter.notifyDataSetChanged();
			}
		}
	}
}
