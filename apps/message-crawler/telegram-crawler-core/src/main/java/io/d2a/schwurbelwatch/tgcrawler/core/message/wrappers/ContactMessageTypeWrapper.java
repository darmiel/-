package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.Contact;
import org.drinkless.tdlib.TdApi.MessageContact;

public class ContactMessageTypeWrapper implements MessageTypeWrapper<MessageContact> {

  @Override
  public Class<MessageContact> getTypeClass() {
    return MessageContact.class;
  }

  @Override
  public int getConstructor() {
    return MessageContact.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.CONTACT;
  }

  @Override
  public void execute(final MessageContact content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // a contact has no caption
    builder.textCaption(null);

    final Contact contact = content.contact;
    final JsonObject object = new JsonObject();
    object.addProperty("phoneNumber", contact.phoneNumber);
    object.addProperty("firstName", contact.firstName);
    object.addProperty("lastName", contact.lastName);
    object.addProperty("vcard", contact.vcard);
    object.addProperty("userId", contact.userId);

    extra.add("contact", object);
  }
}
