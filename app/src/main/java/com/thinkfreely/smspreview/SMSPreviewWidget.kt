package com.thinkfreely.smspreview

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.provider.Telephony
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule


/**
 * Implementation of App Widget functionality.
 */
class SMSPreviewWidget : AppWidgetProvider() {
    companion object {
        val toastList: MutableList<String> = mutableListOf<String>()
        val messageList: MutableList<String> = mutableListOf<String>()
        var messageDisplayed: Boolean = false
        var toastduration: Int = 0
        var widgetEnabled: Boolean = false
        val runneron: AtomicBoolean = AtomicBoolean(false)

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
        widgetEnabled = true
    }

    override fun onDisabled(context: Context) {
        widgetEnabled = false
    }

    suspend fun makeToasts(context: Context) {
        withContext(Dispatchers.Main) {
            if (runneron.getAndSet(true)  == false) {
                while (toastList.isEmpty() == false) {
                    val fmsg = toastList.elementAt(0)
                    toastList.removeAt(0)
                    for (i in 1..toastduration) {
                        Toast.makeText(context, fmsg, Toast.LENGTH_SHORT).show()
                    }
                }
                runneron.set(false)
            }
        }
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
                    if (toastduration != 0) {
                        toastList.add(fmsg)
                        CoroutineScope(IO).launch {
                            makeToasts(context)
                        }
                    }
                    if(widgetEnabled == false) {
                        break
                    }
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