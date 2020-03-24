import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:news_mobile/page/my_home_page.dart';
import 'package:news_mobile/page/news_details_page.dart';
import 'package:news_mobile/router.dart';
import 'package:news_mobile/ui/theme/theme.dart';
import 'package:orientation/orientation.dart';

void main() {
//  debugPaintSizeEnabled = true;
  WidgetsFlutterBinding.ensureInitialized();
  OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp).then((_) {
    runApp(MyTheme(
      initialThemeKey: MyThemeKeys.LIGHT,
      child: MyApp(),
    ));
  });
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'news',
      theme: MyTheme.of(context),
      darkTheme: MyThemes.getTheme(MyThemeKeys.DARK),
      initialRoute: '/',
      onGenerateRoute: (RouteSettings settings) {
        if (settings.name == NewsDetailsPage.routeName) {
          Map<String, dynamic> args =
              settings.arguments as Map<String, dynamic>;
          return NewsDetailsPage.getPageRouteBuilder(
            args["news"],
            args["details"],
          );
        }

        return MaterialPageRoute(builder: (context) {
          return MyHomePage(title: 'news');
        });
      },
      routes: Router.routes,
    );
  }
}
