package com.thinkfreely.smspreview

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class SMSPreviewWidget : AppWidgetProvider() {
    companion object {
        val messageList: MutableList<String> = mutableListOf<String>()
        var messageDisplayed: Boolean = false
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context, intent: Intent) {
        val views = RemoteViews(context.packageName, R.layout.s_m_s_preview_widget)
        when(intent.action) {
            "NEXT_MESSAGE" -> {
                Log.i("SMSPreviewWidget", "GOT NEXT MESSAGE")
                if (!messageList.isEmpty()) {
                    val nextmessage = messageList.elementAt(0)
                    messageList.removeAt(0)
                    views.setTextViewText(R.id.appwidget_text, nextmessage)
                } else {
                    views.setTextViewText(R.id.appwidget_text, "")
                    messageDisplayed = false
                }
            }
            "CLEAR_MESSAGES" -> {
                Log.i("SMSPreviewWidget", "GOT CLEAR MESSAGES")
                messageList.clear()
                messageDisplayed = false
                views.setTextViewText(R.id.appwidget_text, "")
            }
            "android.provider.Telephony.SMS_RECEIVED" -> {
                val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                for (message in smsMessages) {
                    val fmsg = "FROM: " + message.displayOriginatingAddress.toString() + "\n" + message.displayMessageBody.toString()
                    if (messageDisplayed == false) {

                        views.setTextViewText(R.id.appwidget_text, fmsg)
                        messageDisplayed = true
                    } else {
                        messageList.add(fmsg)
                    }
                    Log.i("SMSPreviewWidget", messageList.size.toString())
                }
            }
            else -> {
                return super.onReceive(context, intent)
            }

        }
        val widgetManager = AppWidgetManager.getInstance(context)
        val currWidget = ComponentName(context, SMSPreviewWidget::class.java)
        widgetManager?.updateAppWidget(currWidget, views)

    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.s_m_s_preview_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

    val intent = Intent(context, SMSPreviewWidget::class.java)
    intent.setAction("NEXT_MESSAGE")
    val pendingintent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.NextButton, pendingintent)

    val intent2 = Intent(context, SMSPreviewWidget::class.java)
    intent2.setAction("CLEAR_MESSAGES")
    val pendingintent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.clearButton, pendingintent2)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}