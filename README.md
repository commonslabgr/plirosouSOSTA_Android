# plirosouSOSTA
Android app for workers to calculate their salary and be informed about their payment rights

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-red.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Android: SDK 22](https://img.shields.io/badge/Android-SDK%2022-green.svg)](https://developer.android.com/index.html)
![localization: Greece](https://img.shields.io/badge/localization-Greece-blue.svg)

## Description
Currently in Greece, a lot of workers who work by the hour get paid simply the agreed amount multiplied by the hours that they work, even though this is not what they are entitled to. The Greek labor laws give the right to workers to get extra pay for several conditions, but unfortunately most people are unaware of this.
<b>plirosouSOSTA</b> is a simple Android Java application that wants to inform users for their payment rights.
It lets a user to record their working hours. Then according to their agreed payment, age, years of experience the application calculates what they should get paid according to the current (01/2018) Greek labor laws. 
To do this it looks at several factors that by law the worker is entitled get extra payment, those are when someone is working:
* during night
* on a Saturday
* on a Sunday or public Holiday
* overtime hours

## Known issues

* There are some extreme use cases of multiple multipliers, where the calculations of the entitled payment is not correct. This is the case for example when a user records working hours on a Sunday from 12:00pm to 02:00am then some hours should have multiple multipliers (working on a Sunday and night and overtime)
* The tab "From-To" both on History and Entitled Activities is not initiating the Date pickers always as it should. This has to do with an unresolved issue of the Fragment life-cycle and more specifically in detecting correctly when the fragment is visible to the user.

## Extra features

Here are some of the features that we have thought of.

* Support bigger >5.5" and smaller <4.5" screens.
* Support import and export of application data.
* Add calendar view of working days/hours.
* Add the ability the user to set their standard weekly shifts.
* Update code to make implementation for another country's labor laws easier.
