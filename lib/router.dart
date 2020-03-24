import 'package:flutter/material.dart';

import 'page/my_home_page.dart';

class Router {
  static final Map<String, WidgetBuilder> routes = {
    "/": (context) => MyHomePage(title: 'news'),
  };
}
