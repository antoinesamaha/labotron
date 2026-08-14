import 'dart:async';
import 'package:flutter/material.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_details_view.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_list_view.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_entity.dart';
import 'instrument_service.dart';
import 'instrument_status_widget.dart';

class InstrumentListView extends FocListView {
  const InstrumentListView({super.key, required super.metaEntity});

  @override
  InstrumentListViewState createState() => InstrumentListViewState();
}

class InstrumentListViewState extends FocListViewState {
  Timer? _refreshTimer;

  @override
  void initState() {
    super.initState();
    _refreshTimer = Timer.periodic(const Duration(seconds: 5), (_) {
      if (mounted) refreshData();
    });
  }

  @override
  void dispose() {
    _refreshTimer?.cancel();
    super.dispose();
  }

  @override
  void editItem(FocEntity item) {
    final rawId = item.id;
    final instrumentId =
        rawId is int ? rawId : int.tryParse(rawId.toString()) ?? 0;
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => FocDetailsView(
          metaEntity: widget.metaEntity,
          itemId: instrumentId.toString(),
          statusWidget: InstrumentStatusWidget(
            metaEntity: widget.metaEntity,
            instrumentId: instrumentId,
          ),
        ),
      ),
    ).then((updatedItem) {
      if (updatedItem != null) refreshData();
    });
  }

  @override
  List<String> getDisplayFieldNames() {
    // Show additional fields specific to instruments
    return ['code', 'name', 'type', 'status'];
  }

  bool _getBool(dynamic item, String key) {
    final v = item.properties[key];
    return v == true || v == 1 || v?.toString().toLowerCase() == 'true';
  }

  @override
  List<DataColumn> getCustomColumns() {
    // Add custom columns specific to instruments
    return [
      const DataColumn(
        label: Text('Status'),
        tooltip: 'Driver On/Off Status',
      ),
      const DataColumn(
        label: Text('Conn'),
        tooltip: 'Socket connection state',
      ),
    ];
  }

  @override
  List<DataCell> getCustomDataCells(dynamic item) {
    final started = _getBool(item, 'started');
    final connected = _getBool(item, 'connected');
    // Add custom data cells for each instrument row
    return [
      DataCell(
        SizedBox.expand(
          child: InkWell(
            onTap: () {
              _toggleInstrumentStatus(item);
            },
            borderRadius: BorderRadius.circular(12),
            child: Container(
              margin: const EdgeInsets.all(2),
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: started ? Colors.green : Colors.red,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Center(
                child: Text(
                  started ? 'On' : 'Off',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
      DataCell(
        Container(
          width: 12,
          height: 12,
          margin: const EdgeInsets.symmetric(horizontal: 8),
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: connected ? Colors.green : (started ? Colors.orange : Colors.grey.shade400),
          ),
        ),
      ),
      // DataCell(
      //   Row(
      //     mainAxisSize: MainAxisSize.min,
      //     children: [
      //       Icon(
      //         Icons.location_on,
      //         size: 16,
      //         color: Colors.grey.shade600,
      //       ),
      //       const SizedBox(width: 4),
      //       Text(
      //         item['location']?.toString() ?? 'Unknown',
      //         style: TextStyle(
      //           color: Colors.grey.shade700,
      //           fontSize: 13,
      //         ),
      //       ),
      //     ],
      //   ),
      // ),
    ];
  }

  void _toggleInstrumentStatus(dynamic item) {
    // Toggle the instrument status
    final currentStatus = item.properties['started'] ?? false;
    final newStatus = !currentStatus;

    // Show a confirmation dialog
    final messenger = ScaffoldMessenger.of(context);
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: Text('${newStatus ? 'Start' : 'Stop'} Instrument'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                  'Are you sure you want to ${newStatus ? 'start' : 'stop'} this instrument?'),
              const SizedBox(height: 8),
              Text(
                'Name: ${item['name'] ?? 'Unknown'}',
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              Text('Code: ${item['code'] ?? 'Unknown'}'),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () async {
                Navigator.of(dialogContext).pop();

                messenger.showSnackBar(
                  SnackBar(
                    content: Text(
                        '${newStatus ? 'Starting' : 'Stopping'} instrument...'),
                    duration: const Duration(seconds: 2),
                  ),
                );

                try {
                  await InstrumentService()
                      .toggleInstrumentStatus(item['id'], newStatus);

                  if (mounted) {
                    refreshData();
                    messenger.showSnackBar(
                      SnackBar(
                        content: Text(
                            'Instrument ${newStatus ? 'started' : 'stopped'} successfully'),
                        backgroundColor:
                            newStatus ? Colors.green : Colors.orange,
                        duration: const Duration(seconds: 3),
                      ),
                    );
                  }
                } catch (error) {
                  if (mounted) {
                    messenger.showSnackBar(
                      SnackBar(
                        content: Text(
                            'Failed to update instrument status: $error'),
                        backgroundColor: Colors.red,
                        duration: const Duration(seconds: 4),
                      ),
                    );
                  }
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: newStatus ? Colors.green : Colors.orange,
                foregroundColor: Colors.white,
              ),
              child: Text(newStatus ? 'Start' : 'Stop'),
            ),
          ],
        );
      },
    );
  }

  @override
  void deleteItem(FocEntity item) {
    // Custom delete behavior for instruments with confirmation
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Delete Instrument'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('Are you sure you want to delete this instrument?'),
              const SizedBox(height: 8),
              Text(
                'Name: ${item['name'] ?? 'Unknown'}',
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              Text('Code: ${item['code'] ?? 'Unknown'}'),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
                // Implement actual delete logic here
                print('Deleting instrument: ${item}');
                // Refresh the list after deletion
                refreshData();
              },
              style: TextButton.styleFrom(foregroundColor: Colors.red),
              child: const Text('Delete'),
            ),
          ],
        );
      },
    );
  }
}
