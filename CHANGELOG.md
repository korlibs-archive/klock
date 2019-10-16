# Changelog

## [1.7.5](https://github.com/korlibs/klock/compare/1.7.4...1.7.5)

* Fix android artifact publication. Fixes [#72](https://github.com/korlibs/klock/issues/72)

## [1.7.4](https://github.com/korlibs/klock/compare/1.7.3...1.7.4)

* Android MIN SDK is now 16. Fixes [#66](https://github.com/korlibs/klock/issues/66)
* Fixed bug in `Year.fromDays`. Fixes [#67](https://github.com/korlibs/klock/issues/67)
* Updated `package.json` included in JS jar files were referencing an invalid Kotlin version. Fixes [#68](https://github.com/korlibs/klock/issues/68)

## [1.7.3](https://github.com/korlibs/klock/compare/1.7.2...1.7.3)

* Fixed InvalidMutabilityException issues [#64](https://github.com/korlibs/klock/issues/64)

## [1.7.2](https://github.com/korlibs/klock/compare/1.7.1...1.7.2)

* Support optional format parts [#27](https://github.com/korlibs/klock/issues/27)

## [1.7.1](https://github.com/korlibs/klock/compare/1.7.0...1.7.1)

* Supports TimeZone parsing. Fixes [#14](https://github.com/korlibs/klock/pull/14)
* Added SerializableDateTime wrapping a DateTime to allow serialization along DateTimeTz. Fixes [#17](https://github.com/korlibs/klock/pull/17)
* New PatternDateFormat parsing system. Fixes [#33](https://github.com/korlibs/klock/pull/33)
* Fixes a NumberFormatException. Fixes [#37](https://github.com/korlibs/klock/pull/37)
* Verifies that #38 is fixed. Fixes [#38](https://github.com/korlibs/klock/pull/38)
* klock-locale now includes ExtendedTimezoneNames with TimeZone names for parsing. Fixes [#41](https://github.com/korlibs/klock/pull/41)
* Now publishing to NPM: <https://www.npmjs.com/package/@korlibs/klock> Fixes [#54](https://github.com/korlibs/klock/pull/54)
* Fixes a bug when generating a span from a DateTimeRange. Fixes [#63](https://github.com/korlibs/klock/pull/63)

## [1.7.0](https://github.com/korlibs/klock/compare/1.5.0...1.7.0)

* Kotlin 1.3.50
* Added Date and Time classes
* Revamped DateTimeRange
* Added DateTimeRangeSet
* Some fixes to ISO8601
* Fixed: [#61](https://github.com/korlibs/klock/pull/61) thanks to AndreasMattsson 

## 1.4.0

* Kotlin 1.3.30
* Add support for ISO-8601 parsing and formatting

## 1.3.1

* Add locale support

## 1.2.2

* `DateTime`, `min`, `max`, `clamp`

## 1.2.0

* Gradle 5.2 and Kotlin 1.3.21 without metadata publishing

