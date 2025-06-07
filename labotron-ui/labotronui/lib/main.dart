import 'package:flutter/material.dart';
import 'package:focui/main.dart';

import 'package:focui/src/menu/menu.dart';
import 'package:focui/src/settings/config.dart';

void main() async {
  Config.appName = "Labotron";
  Config.appIcon = Icons.local_hospital;
  Config.menuItems = const [
    Menu("Pools", "/pools", "pool"),
    Menu("Samples", "/labotron_samples", "labotron_sample"),
    Menu("Instruments", "/instruments", "instrument"),
    Menu("Test groups", "/test_groups", "test_group"),
    Menu("Entities", "/entities", "Entity"),
  ];
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
