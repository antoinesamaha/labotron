import 'package:flutter/material.dart';
import 'package:focui/main.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'src/instrument/instrument_list_view.dart';

import 'package:focui/src/menu/menu.dart';
import 'package:focui/src/settings/config.dart';

void main() async {
  // Initialize the instrument service with authentication
  Config.appName = "Labotron";
  Config.appIcon = Icons.local_hospital;
  Config.seedColor = const Color(0xFF00695C); // clinical teal brand color
  Config.menuItems = const [
    Menu(Icons.science, "Instruments", "/instruments", "instrument",
        widgetClassName: "InstrumentListView"),
    Menu(Icons.science, "Test Mapping", "/test_label_maps", "test_label_map"),
    Menu(Icons.flutter_dash, "Samples", "/lab_samples", "lab_sample"),
    // Menu(Icons.label, "Tests", "/lab_tests", "lab_test"),
    Menu(Icons.label, "Communication Logs", "/communication_logs",
        "communication_log"),
    Menu(Icons.group, "App Groups", "/app_groups", "app_group"),
    // Menu(Icons.message, "Messages", "/lab_messages", "lab_message"),
    Menu(Icons.grade, "Groups", "/test_groups", "test_group"),
    // Menu(Icons.flag, "Entities", "/entities", "Entity"),
    // Menu(Icons.flag, "Sample Json", "/samplejson", "Sample JSON"),
    Menu(Icons.people, "Users", "/users", "FUSER"),
    Menu(Icons.memory_rounded, "Object Monitor", "/monitor/objects", ""),
  ];

  Config.widgetClassRegistry = {
    'FocListView': (metaEntity) => FocListView(metaEntity: metaEntity),
    'InstrumentListView': (metaEntity) =>
        InstrumentListView(metaEntity: metaEntity),
    // Add other custom widget classes here
  };

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
