import 'package:flutter/material.dart';
import 'package:focui/main.dart';

import 'package:focui/src/settings/settings_controller.dart';
import 'package:focui/src/settings/settings_service.dart';
import 'package:focui/src/app.dart';
import 'package:focui/src/auth/login_page.dart';

void main() async {
  neoFocMain();
  // Set up the SettingsController, which will glue user settings to multiple
  // Flutter Widgets.
  // final settingsController = SettingsController(SettingsService());

  // Load the user's preferred theme while the splash screen is displayed.
  // This prevents a sudden theme change when the app is first displayed.
  // await settingsController.loadSettings();

  // Run the app and pass in the SettingsController. The app listens to the
  // SettingsController for changes, then passes it further down to the
  // SettingsView.
  //runApp(MyApp(settingsController: settingsController));

  // runApp(MaterialApp(
  //   debugShowCheckedModeBanner: false,
  //   home: LoginPage(
  //     logo: Icon(Icons.local_hospital, size: 80, color: Colors.blueAccent),
  //     appName: "Labotron",
  //   ),
  // ));
}
