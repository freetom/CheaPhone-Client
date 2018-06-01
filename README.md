# CheaPhone-Client
CheaPhone is an application for Android systems, it finds out which is the best combination of rate, offer and eventually options available on the italian market. It base its calculus on the phone usage data

Supports Android from 4.0 to the lastest. <br/>
Why not previous versions? GUI incompatibility; material style<br/>

Below here, some screenshots that show the application and its logo:
<br/>
<p align="center">
  <img src="https://user-images.githubusercontent.com/9014107/40731807-20ad1ccc-6432-11e8-9edc-a46fe74ac8b0.jpg" width="100"/>
</p>
<p align="center">
  <img src="https://user-images.githubusercontent.com/9014107/40731653-d0bf2d2c-6431-11e8-8f62-8524709f620c.jpg" width="200"/>
  <img src="https://user-images.githubusercontent.com/9014107/40731655-d0dc0a82-6431-11e8-935e-87b8b8a33d02.jpg" width="200"/>
  <img src="https://user-images.githubusercontent.com/9014107/40731656-d0f8d0f4-6431-11e8-9738-8d2a28976de0.jpg" width="200"/>
</p>
<br/>
The idea is simple. Using the monitored data from the smartphone; sms,calls,internet traffic, and relative contacted numbers we can easily compute which is the best combination available on the market.<br/>
The application is made for Italian people but can be easily adapted to other countries.

Data:<br/>
The app monitors all the sms, calls, and data traffic, tracking the precise date of these data and associating the various numbers contacted (calls & sms). After the app has been installed and authorized, it can retrieve all the information from the calls' log and the sms' log, but for the data traffic usage there isn't a log, so its monitoring start from the application first time startup. Cheaphone creates a file for each category of data monitored, keeping an updated database with all user's data. To not fill all the secondary memory, there is an expiration period. Data older than that period are erased. In cheaphone is also implemented mutual exclusion in data accessing. So no race condition should happen, causing possibly but unlikely bugs.

Networking:<br/>
A connection to a server system is needed, to update the offers, and to retrieve, and cache the operators of the contacted numbers. The update of the offers occur comparing the local SHA1 hash of the offers file with the hash of the server. If the client has a different hash, the server will update him with a new file, and a notification will be shown to the user. The translation between numbers and operators befall into the server. Equipped with a GSM GPRS modem and a Tim sim card the server is able to use the TIM service over sms technology. Sending a message to 456 to translate a number in an operator. The converted numbers are cached both in the client and in the server, with, clearly, an expiration period after which the association are deleted (numbers may change operators).  The server handles all the cases, it check that the number isn't a home number, it check that it is in the correct format. Then it sends an sms and wait for answer. If the 456 respond with a valid operator, it is returned. If the number does not exists, the cache is filled with INVALID [See cache details in the class]. If the 456 does not respond, there's a timeout to not get stucked and cache has an INVALID more; this is anyhow unlikely. If ambigous answer arrived, maybe for spoofing, the server will retry. We want to achieve a service that cache the better it can all the operators of the needed numbers. Anyway spoofing on sms is possible but the attacker miss the number so..

Algorithm:<br/>
Finally, there is the best combination calculus part. The computation process is enough direct. We have all the infos, so we can infer how many calls, messages and megabytes the user use in a month. We can compute exactly the cost of each offer, with each rate and finding the best option that minimizes the cost. Also we find the possible you and me number and use it if the offer or the option includes it. In the end we show the results, as combinations, showing all the related infos, included added or subtracted costs to pass to that offer, and one or more links to web pages of offer, rate, and options. The final choice rest to the user. This for us is the best way to minimize the cost of telephone usage; in a pay per use system.

Privacy:
Almost all the data are kept lccally in the device. The server only keeps the cache that associate number to operators and I use that data only to make the system works. <br>
Furthermore, cryptography + digital signature is in place. [more infos on the server -> end of the document for reference]

Obliouvsly, the more time the app stay into the system the more it will be accurate.

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


The server's code is also Free Software: https://github.com/freetomas/CheaPhone-Server

Enjoy!
