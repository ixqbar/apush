package cn.linjujia.mobile.model;

import org.litepal.crud.LitePalSupport;

public class Message extends LitePalSupport {

	final public static int MESSAGE_TYPE_IS_SETTING = 1;
	final public static int MESSAGE_TYPE_IS_NOTICE = 2;

	public int id;
	public String title;
	public String content;
	public String date;
	public int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Message{" +
			"id=" + id +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", date='" + date + '\'' +
			", type=" + type +
			'}';
	}
}
