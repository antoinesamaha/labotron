import 'dart:async';
import 'package:flutter/material.dart';
import 'package:focui/src/entities/foc_entity_feature/foc_service.dart';
import 'package:focui/src/entities/meta_feature/meta_entity.dart';

class InstrumentStatusWidget extends StatefulWidget {
  final MetaEntity metaEntity;
  final int instrumentId;
  final Duration pollInterval;

  const InstrumentStatusWidget({
    super.key,
    required this.metaEntity,
    required this.instrumentId,
    this.pollInterval = const Duration(seconds: 5),
  });

  @override
  State<InstrumentStatusWidget> createState() => _InstrumentStatusWidgetState();
}

class _InstrumentStatusWidgetState extends State<InstrumentStatusWidget> {
  Timer? _timer;
  bool? _started;
  bool? _connected;

  @override
  void initState() {
    super.initState();
    _fetchStatus();
    _timer = Timer.periodic(widget.pollInterval, (_) => _fetchStatus());
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _fetchStatus() async {
    try {
      final entity = await FocService().fetchItemDetails(
        widget.metaEntity,
        widget.instrumentId.toString(),
      );
      if (mounted) {
        setState(() {
          final s = entity.properties['started'];
          _started = s == true || s == 1 || s?.toString().toLowerCase() == 'true';
          final c = entity.properties['connected'];
          _connected = c == true || c == 1 || c?.toString().toLowerCase() == 'true';
        });
      }
    } catch (_) {
      // Silently ignore polling errors
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_started == null && _connected == null) return const SizedBox.shrink();
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        border: Border(bottom: BorderSide(color: Colors.grey.shade300)),
      ),
      child: Row(
        children: [
          _StatusChip(label: 'Driver', active: _started ?? false, activeColor: Colors.green),
          const SizedBox(width: 8),
          _StatusChip(label: 'Connected', active: _connected ?? false, activeColor: Colors.blue),
          const Spacer(),
          Icon(Icons.sync, size: 13, color: Colors.grey.shade400),
          const SizedBox(width: 4),
          Text(
            'Live',
            style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
          ),
        ],
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  final String label;
  final bool active;
  final Color activeColor;

  const _StatusChip({
    required this.label,
    required this.active,
    required this.activeColor,
  });

  @override
  Widget build(BuildContext context) {
    final color = active ? activeColor : Colors.red;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        border: Border.all(color: color),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(active ? Icons.check_circle : Icons.cancel, color: color, size: 14),
          const SizedBox(width: 4),
          Text(
            label,
            style: TextStyle(color: color, fontWeight: FontWeight.w600, fontSize: 12),
          ),
        ],
      ),
    );
  }
}
