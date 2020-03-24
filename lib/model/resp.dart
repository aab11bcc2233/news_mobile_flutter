import 'package:dio/dio.dart';

class Resp {
  final String data;
  final hasError;
  final String message;
  final Response response;

  Resp(this.data, this.hasError, this.message, this.response);
}
