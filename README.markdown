Librus API
==========

Java library for [Librus](http://dziennik.librus.pl/).

Mainly developed for the Android app, but should work on any JRE (uses Jsoup).

## Features

At present, it supports:

 * login/logout
 * fetching notice list

Future plans:

 * list of grades
 * detection of 'new' objects (given an abstract model of local storage)

## Usage example

    Librus librus = new Librus();

    // log in
    librus.login(username, password);

    // use it
    String realName = librus.getRealName(); // get first and last name of the user
    List<Notice> notices = librus.getNotices(); // obvious

    // log out (pretty much unnecessary, as session cookies aren't kept anywhere)
    librus.logout();


Easy as it should be.

Contributions welcome - the usual GitHub way (issues, pull requests).

Happy coding!
