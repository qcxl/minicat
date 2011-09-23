/*
 *  Copyright 2010 Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.acra.sender;

import org.acra.ACRA;
import org.acra.collector.CrashReportData;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import com.fanfou.app.R;

import android.content.Context;
import android.content.Intent;

/**
 * Send reports through an email intent. The user will be asked to chose his
 * preferred email client. Included report fields can be defined using
 * {@link org.acra.annotation.ReportsCrashes#customReportContent()}. Crash receiving mailbox has to be
 * defined with {@link ReportsCrashes#mailTo()}.
 */
public class EmailIntentSender implements ReportSender {

    private final Context mContext;

    public EmailIntentSender(Context ctx) {
        mContext = ctx;
    }

    @Override
    public void send(CrashReportData errorContent) throws ReportSenderException {

//        final String subject = errorContent.get(ReportField.PACKAGE_NAME) + " Crash Report";
    	final String subject = "饭否客户端崩溃报告";
        final String body = buildBody(errorContent);

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        emailIntent.setType("text/plain");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mContext.getString(R.string.crash_mail_header)+body);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { ACRA.getConfig().mailTo() });
        mContext.startActivity(emailIntent);
    }

    private String buildBody(CrashReportData errorContent) {
        ReportField[] fields = ACRA.getConfig().customReportContent();
        if(fields.length == 0) {
            fields = ACRA.DEFAULT_MAIL_REPORT_FIELDS;
        }

        final StringBuilder builder = new StringBuilder();
        for (ReportField field : fields) {
            builder.append(field.toString()).append("=");
            builder.append(errorContent.get(field));
            builder.append('\n');
        }
        return builder.toString();
    }
}
