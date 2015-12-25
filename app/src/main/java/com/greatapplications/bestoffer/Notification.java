/**

 Copyright 2014 Bortoli Tomas

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package com.greatapplications.bestoffer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.greatapplications.bestoffergui.MainActivity;

public class Notification {
	public static void showNotification(Context c, String text){
		int notificationId = 001;
		// Build intent for notification content
		Intent viewIntent = new Intent(c, MainActivity.class);
		viewIntent.putExtra("0", 1);
		PendingIntent viewPendingIntent =
		        PendingIntent.getActivity(c, 0, viewIntent, 0);

		NotificationCompat.Builder notificationBuilder =
		        new NotificationCompat.Builder(c)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(Constants.applicationName)
		        .setContentText(text)
		        .setContentIntent(viewPendingIntent)
		        .setAutoCancel(true);

		// Get an instance of the NotificationManager service
		NotificationManagerCompat notificationManager =
		        NotificationManagerCompat.from(c);

		// Build the notification and issues it with notification manager.
		notificationManager.notify(notificationId, notificationBuilder.build());
	}
}
