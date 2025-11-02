import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:focui/src/entities/foc_entity_feature/foc_service.dart';
import 'package:focui/src/app_constants.dart';

class InstrumentService {
  static String _baseUrl = '${AppConstants.apiUrl}/api/instrument';

  /// Headers for API requests
  Map<String, String> get _headers {
    return FocService().headers();
  }

  /// Toggle instrument status (start/stop)
  Future<void> toggleInstrumentStatus(int instrumentId, bool start) async {
    final endpoint = start ? 'start' : 'stop';
    final url = Uri.parse('$_baseUrl/$instrumentId/$endpoint');

    print('=== InstrumentService: Making API call ===');
    print('URL: $url');
    print('Method: POST');
    print('Headers: $_headers');

    try {
      final response = await http.post(
        url,
        headers: _headers,
      );

      print('Response Status Code: ${response.statusCode}');
      print('Response Body: ${response.body}');

      if (response.statusCode == 200 || response.statusCode == 204) {
        // Success
        print(
            '✅ SUCCESS: Instrument $instrumentId ${start ? 'started' : 'stopped'} successfully');
      } else {
        // Handle error response
        final errorMessage = _parseErrorMessage(response);
        print('❌ ERROR: Status ${response.statusCode} - $errorMessage');
        throw Exception(
            'Failed to ${start ? 'start' : 'stop'} instrument: $errorMessage');
      }
    } catch (e) {
      print('❌ EXCEPTION: Error toggling instrument status: $e');
      print('Exception type: ${e.runtimeType}');
      rethrow;
    }
  }

  /// Start an instrument
  Future<void> startInstrument(int instrumentId) async {
    return toggleInstrumentStatus(instrumentId, true);
  }

  /// Stop an instrument
  Future<void> stopInstrument(int instrumentId) async {
    return toggleInstrumentStatus(instrumentId, false);
  }

  /// Get instrument status
  Future<Map<String, dynamic>> getInstrumentStatus(int instrumentId) async {
    final url = Uri.parse('$_baseUrl/$instrumentId/status');

    try {
      final response = await http.get(
        url,
        headers: _headers,
      );

      if (response.statusCode == 200) {
        return json.decode(response.body) as Map<String, dynamic>;
      } else {
        final errorMessage = _parseErrorMessage(response);
        throw Exception('Failed to get instrument status: $errorMessage');
      }
    } catch (e) {
      print('Error getting instrument status: $e');
      rethrow;
    }
  }

  /// Parse error message from response
  String _parseErrorMessage(http.Response response) {
    try {
      final errorBody = json.decode(response.body);
      return errorBody['message'] ?? errorBody['error'] ?? 'Unknown error';
    } catch (e) {
      return 'HTTP ${response.statusCode}: ${response.reasonPhrase}';
    }
  }

  /// Batch operation to start multiple instruments
  Future<List<String>> startMultipleInstruments(List<int> instrumentIds) async {
    List<String> results = [];

    for (int id in instrumentIds) {
      try {
        await startInstrument(id);
        results.add('Instrument $id started successfully');
      } catch (e) {
        results.add('Failed to start instrument $id: $e');
      }
    }

    return results;
  }

  /// Batch operation to stop multiple instruments
  Future<List<String>> stopMultipleInstruments(List<int> instrumentIds) async {
    List<String> results = [];

    for (int id in instrumentIds) {
      try {
        await stopInstrument(id);
        results.add('Instrument $id stopped successfully');
      } catch (e) {
        results.add('Failed to stop instrument $id: $e');
      }
    }

    return results;
  }

  /// Check if the service is available
  Future<bool> isServiceAvailable() async {
    try {
      final url = Uri.parse('$_baseUrl/health');
      final response = await http
          .get(
            url,
            headers: _headers,
          )
          .timeout(const Duration(seconds: 5));

      return response.statusCode == 200;
    } catch (e) {
      print('Service availability check failed: $e');
      return false;
    }
  }
}
