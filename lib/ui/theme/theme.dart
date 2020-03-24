import 'package:flutter/material.dart';

enum MyThemeKeys {
  LIGHT,
  DARK,
}

class MyThemes {
//  static final ThemeData _lightTheme = ThemeData(
//    brightness: Brightness.light,
//    primaryColor: Color(0xFFE91E63),
//    primaryColorDark: Color(0xFFC2185B),
//    primaryColorLight: Color(0xFFF8BBD0),
//    accentColor: Color(0xFFFF4081),
//    // 比如 floatingActionButton 的颜色
//    textTheme: TextTheme(
//      body1: TextStyle(color: Color(0xFF333333)), // 普通的 Text() widget 的颜色
//      body2: TextStyle(color: Colors.grey[600]),
//    ),
//    primaryTextTheme: TextTheme(
//      title: TextStyle(color: Colors.white), // 比如 appbar 中的 title 的颜色
//    ),
//    primaryIconTheme: IconThemeData(color: Colors.white // 比如 appbar 中的自带的返回键的颜色
//        ),
//    accentIconTheme: IconThemeData(
//        color: Colors
//            .white // 比如 appbar 中 actions 的 Icon 的颜色；再比如 floatingActionButton 中的 Icon 的颜色
//        ),
//  );

  static final ThemeData _lightTheme = ThemeData(
    brightness: Brightness.light,
    primaryColor: Color(0xFFFF5872),
    primaryColorDark: Color(0xFFC56C86),
    accentColor: Color(0xFF725A7A), // 比如 floatingActionButton 的颜色
    textTheme: TextTheme(
      body1: TextStyle(color: Color(0xFF333333)), // 普通的 Text() widget 的颜色
      body2: TextStyle(color: Colors.grey[600]),
    ),
    primaryTextTheme: TextTheme(
      title: TextStyle(color: Colors.white), // 比如 appbar 中的 title 的颜色
    ),
    primaryIconTheme: IconThemeData(color: Colors.white // 比如 appbar 中的自带的返回键的颜色
        ),
    accentIconTheme: IconThemeData(
        color: Colors
            .white // 比如 appbar 中 actions 的 Icon 的颜色；再比如 floatingActionButton 中的 Icon 的颜色
        ),
  );

  static final ThemeData _darkTheme = ThemeData(
    brightness: Brightness.dark,
    primarySwatch: Colors.grey,
    accentColor: Colors.grey[900], // 比如 floatingActionButton 的颜色
    textTheme: TextTheme(
      body1: TextStyle(color: Colors.grey[300]), // 普通的 Text() widget 的颜色
      body2: TextStyle(color: Colors.grey[300]),
    ),
    primaryTextTheme: TextTheme(
      title: TextStyle(color: Color(0xFF757575)), // 比如 appbar 中的 title 的颜色
    ),
    primaryIconTheme:
        IconThemeData(color: Color(0xFF757575) // 比如 appbar 中的自带的返回键的颜色
            ),
    accentIconTheme: IconThemeData(
        color: Color(
            0xFF757575) // 比如 appbar 中 actions 的 Icon 的颜色；再比如 floatingActionButton 中的 Icon 的颜色
        ),
  );

  static ThemeData getTheme(MyThemeKeys key) {
    switch (key) {
      case MyThemeKeys.LIGHT:
        return _lightTheme;
      case MyThemeKeys.DARK:
        return _darkTheme;
      default:
        return _lightTheme;
    }
  }
}

class MyTheme extends StatefulWidget {
  final Widget child;
  final MyThemeKeys initialThemeKey;

  MyTheme({Key key, this.initialThemeKey, @required this.child})
      : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return MyThemeState();
  }

  static _MyTheme _of(BuildContext context) {
    _MyTheme inherited =
        (context.inheritFromWidgetOfExactType(_MyTheme) as _MyTheme);
    return inherited;
  }

  static ThemeData of(BuildContext context) {
    _MyTheme _myTheme = _of(context);
    if (_myTheme == null) {
      return MyThemes.getTheme(MyThemeKeys.LIGHT);
    }

    return _myTheme.data.theme;
  }

  static bool isDark(BuildContext context) {
    bool systemDark = Theme.of(context).brightness == Brightness.dark;
//    bool systemDark =
//        MediaQuery.of(context).platformBrightness == Brightness.dark;
    if (systemDark) {
      return true;
    }

    return of(context).brightness == Brightness.dark;
  }

  static void changeTheme(BuildContext context, MyThemeKeys key) {
    _of(context).data.changeTheme(key);
  }
}

class MyThemeState extends State<MyTheme> {
  ThemeData _theme;

  ThemeData get theme => _theme;

  @override
  void initState() {
    _theme = MyThemes.getTheme(widget.initialThemeKey);
    super.initState();
  }

  void changeTheme(MyThemeKeys key) {
    setState(() {
      _theme = MyThemes.getTheme(key);
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_theme == null) {
      _theme = MyThemes.getTheme(widget.initialThemeKey);

      if (MyTheme.isDark(context)) {
        if (_theme.brightness != Brightness.dark) {
          _theme = MyThemes.getTheme(MyThemeKeys.DARK);
        }
      }
    }

    return _MyTheme(data: this, child: widget.child);
  }
}

class _MyTheme extends InheritedWidget {
  final MyThemeState data;

  _MyTheme({
    Key key,
    this.data,
    @required Widget child,
  }) : super(key: key, child: child);

  @override
  bool updateShouldNotify(InheritedWidget oldWidget) {
    return true;
  }
}
