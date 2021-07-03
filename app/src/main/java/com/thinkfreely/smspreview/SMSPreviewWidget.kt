package com.thinkfreely.smspreview

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class SMSPreviewWidget : AppWidgetProvider() {
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
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        if (intent.action != "android.provider.Telephony.SMS_RECEIVED") {
                return super.onReceive(context, intent)
        }
        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val views = RemoteViews(context.packageName, R.layout.s_m_s_preview_widget)
        for (message in smsMessages) {
            views.setTextViewText(R.id.appwidget_text, message.displayMessageBody)
            Log.i("SMSPreviewWidget", message.displayMessageBody + "From Widget")
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

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}