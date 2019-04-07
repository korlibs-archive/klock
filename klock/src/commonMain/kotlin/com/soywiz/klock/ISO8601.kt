package com.soywiz.klock

object ISO8601 {
    class IsoIntervalFormat(val format: String) : DateTimeSpanFormat {
        override fun format(dd: DateTimeSpan): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): DateTimeSpan? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    class IsoTimeFormat(val basic: String?, val extended: String?) : TimeFormat {
        override fun format(dd: TimeSpan): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): TimeSpan? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    class IsoDateTimeFormat(val basic: String?, val extended: String?) : DateFormat {
        override fun format(dd: DateTimeTz): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): DateTimeTz? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    // Date Calendar Variants
    val DATE_CALENDAR_COMPLETE = IsoDateTimeFormat("YYYYMMDD", "YYYY-MM-DD")
    val DATE_CALENDAR_REDUCED0 = IsoDateTimeFormat(null, "YYYY-MM")
    val DATE_CALENDAR_REDUCED1 = IsoDateTimeFormat("YYYY", null)
    val DATE_CALENDAR_REDUCED2 = IsoDateTimeFormat("YY", null)
    val DATE_CALENDAR_EXPANDED0 = IsoDateTimeFormat("±YYYYYYMMDD", "±YYYYYY-MM-DD")
    val DATE_CALENDAR_EXPANDED1 = IsoDateTimeFormat("±YYYYYYMM", "±YYYYYY-MM")
    val DATE_CALENDAR_EXPANDED2 = IsoDateTimeFormat("±YYYYYY", null)
    val DATE_CALENDAR_EXPANDED3 = IsoDateTimeFormat("±YYY", null)

    // Date Ordinal Variants
    val DATE_ORDINAL_COMPLETE = IsoDateTimeFormat("YYYYDDD", "YYYY-DDD")
    val DATE_ORDINAL_EXPANDED = IsoDateTimeFormat("±YYYYYYDDD", "±YYYYYY-DDD")

    // Date Week Variants
    val DATE_WEEK_COMPLETE = IsoDateTimeFormat("YYYYWwwD", "YYYY-Www-D")
    val DATE_WEEK_REDUCED = IsoDateTimeFormat("YYYYWww", "YYYY-Www")
    val DATE_WEEK_EXPANDED0 = IsoDateTimeFormat("±YYYYYYWwwD", "±YYYYYY-Www-D")
    val DATE_WEEK_EXPANDED1 = IsoDateTimeFormat("±YYYYYYWww", "±YYYYYY-Www")

    // Time Variants
    val TIME_LOCAL_COMPLETE = IsoTimeFormat("hhmmss", "hh:mm:ss")
    val TIME_LOCAL_REDUCED0 = IsoTimeFormat("hhmm", "hh:mm")
    val TIME_LOCAL_REDUCED1 = IsoTimeFormat("hh", null)
    val TIME_LOCAL_FRACTION0 = IsoTimeFormat("hhmmss,ss", "hh:mm:ss,ss")
    val TIME_LOCAL_FRACTION1 = IsoTimeFormat("hhmm,mm", "hh:mm,mm")
    val TIME_LOCAL_FRACTION2 = IsoTimeFormat("hh,hh", null)

    // Time UTC Variants
    val TIME_UTC_COMPLETE = IsoTimeFormat("hhmmssZ", "hh:mm:ssZ")
    val TIME_UTC_REDUCED0 = IsoTimeFormat("hhmmZ", "hh:mmZ")
    val TIME_UTC_REDUCED1 = IsoTimeFormat("hhZ", null)
    val TIME_UTC_FRACTION0 = IsoTimeFormat("hhmmss,ssZ", "hh:mm:ss,ssZ")
    val TIME_UTC_FRACTION1 = IsoTimeFormat("hhmm,mmZ", "hh:mm,mmZ")
    val TIME_UTC_FRACTION2 = IsoTimeFormat("hh,hhZ", null)

    // Time Relative Variants
    val TIME_RELATIVE0 = IsoTimeFormat("±hhmm", "±hh:mm")
    val TIME_RELATIVE1 = IsoTimeFormat("±hh", null)

    // Date + Time Variants
    val DATETIME_COMPLETE = IsoDateTimeFormat("YYYYMMDDTHHMMSS", "YYYY-MM-DDTHH:MM:SS")

    // Interval Variants
    val INTERVAL_COMPLETE0 = IsoIntervalFormat("PnnYnnMnnDTnnHnnMnnS")
    val INTERVAL_COMPLETE1 = IsoIntervalFormat("PnnYnnW")

    val INTERVAL_REDUCED0 = IsoIntervalFormat("PnnYnnMnnDTnnHnnM")
    val INTERVAL_REDUCED1 = IsoIntervalFormat("PnnYnnMnnDTnnH")
    val INTERVAL_REDUCED2 = IsoIntervalFormat("PnnYnnMnnD")
    val INTERVAL_REDUCED3 = IsoIntervalFormat("PnnYnnM")
    val INTERVAL_REDUCED4 = IsoIntervalFormat("PnnY")

    val INTERVAL_DECIMAL0 = IsoIntervalFormat("PnnYnnMnnDTnnHnnMnn,nnS")
    val INTERVAL_DECIMAL1 = IsoIntervalFormat("PnnYnnMnnDTnnHnn,nnM")
    val INTERVAL_DECIMAL2 = IsoIntervalFormat("PnnYnnMnnDTnn,nnH")
    val INTERVAL_DECIMAL3 = IsoIntervalFormat("PnnYnnMnn,nnD")
    val INTERVAL_DECIMAL4 = IsoIntervalFormat("PnnYnn,nnM")
    val INTERVAL_DECIMAL5 = IsoIntervalFormat("PnnYnn,nnW")
    val INTERVAL_DECIMAL6 = IsoIntervalFormat("PnnY")

    val INTERVAL_ZERO_OMIT0 = IsoIntervalFormat("PnnYnnDTnnHnnMnnS")
    val INTERVAL_ZERO_OMIT1 = IsoIntervalFormat("PnnYnnDTnnHnnM")
    val INTERVAL_ZERO_OMIT2 = IsoIntervalFormat("PnnYnnDTnnH")
    val INTERVAL_ZERO_OMIT3 = IsoIntervalFormat("PnnYnnD")

    // Detects and parses all the variants
    val DATE = object : DateFormat {
        override fun format(dd: DateTimeTz): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): DateTimeTz? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    val TIME = object : TimeFormat {
        override fun format(dd: TimeSpan): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): TimeSpan? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    val INTERVAL = object : DateTimeSpanFormat {
        override fun format(dd: DateTimeSpan): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun tryParse(str: String, doThrow: Boolean): DateTimeSpan? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
