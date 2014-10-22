# lein-dotenv

A Leiningen plugin to load variables from `.env` into the environment of your
project, in a manner compatible with [Foreman][], the Ruby [dotenv gem][], and
many other tools and libraries.  This is a development convenience to enable a
seamless transition to a [12 factor](http://12factor.net/config) deployment,
which mandates configuration in the environment.

This plugin works by overriding the environment Leiningen spawns your project
with, which provides two key advantages over a plain library approach:

* Java doesn't allow changing the environment at runtime, which means a
  library would instead have settle for setting system properties or some
  other crude approximation, defeating much of the purpose.
* A library would have to be loaded and activated manually, needlessly
  concerning your application with development concerns, not to mention load
  order issues.

[Foreman]: https://github.com/ddollar/foreman
[dotenv gem]: https://github.com/bkeepers/dotenv

## Setup

To install at the user level, put `[lein-dotenv "RELEASE"]` into the
`:plugins` vector of your `:user` profile in `~/.lein/profiles.clj`.

    {:user {:plugins [[lein-dotenv "RELEASE"]]}}

The plugin will also work on a per project level if added to `project.clj`.
This may be preferable if you want to avoid imposing user profile
requirements on all contributors.

    {defproject myproject "0.1.0-SNAPSHOT"
      {:profiles {:dev {:plugins [[lein-dotenv "RELEASE"]]}}}}

## Usage

Create a file named `.env` at the root of your project and use shell syntax to
declare your variables.

     HELLO=Hello
     GREETING="$HELLO, world!"
     LITERAL='$VARs are not interpolated in single quotes'

The `.env` file is also used by [Foreman][] and similar tools

## License

Copyright © Tim Pope

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
