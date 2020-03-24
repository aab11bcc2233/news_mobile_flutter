import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:news_mobile/model/resp.dart';

const String _DOMAIN = "";
const String _PUB_PATH = "";
const String BASE_URL = _DOMAIN + _PUB_PATH;

final Dio httpClient = Dio();

typedef Future<Response> RunRequest();

class Api {
  Api._();

  static Future<Resp> topNews() {
    return _request(() => httpClient.get(Url._topNews));
  }

  static Future<Resp> newsList() {
    return _request(() => httpClient.get(Url._newsList));
  }

  static Future<Resp> newsDetails(String id) {
    return _request(
        () => httpClient.get(Url._newsDetails, queryParameters: {"id": id}));
  }

  static Future<Resp> _request(RunRequest run) async {
    try {
      var resp = await run();
      String jsonResp = resp.data.toString();
      if (resp.statusCode != 200) {
        var jsonData = json.decode(jsonResp);
        String errorMsg = jsonData["error"];
        if (errorMsg == null || errorMsg.isEmpty) {
          errorMsg = resp.statusMessage;
        }

        return Resp(null, true, errorMsg, resp);
      }

      return Resp(jsonResp, false, "success", resp);
    } on DioError catch (e) {
      var msg = e.message;
      if ((msg != null && msg.contains("timed out")) || e.type == DioErrorType.CONNECT_TIMEOUT) {
        msg = "连接超时";
      }
      return Resp(null, true, msg, null);
    }
  }
}

class Url {
  Url._();

  static String get _topNews {
    return _getURL("/top");
  }

  static String get _newsDetails {
    return _getURL("/details");
  }

  static String get _newsList {
    return _getURL("/list");
  }

  static String _getURL(String path) {
    return BASE_URL + path;
  }
}
