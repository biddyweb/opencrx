TYPE=VIEW
query=select `ip`.`OBJECT_ID` AS `item_position`,`b`.`OBJECT_ID` AS `single_booking` from (((`crx`.`oocke1_depotreportitem` `ip` join `crx`.`oocke1_depotreport` `r` on((`ip`.`P$$PARENT` = `r`.`OBJECT_ID`))) join `crx`.`oocke1_bookingperiod` `bp` on((`r`.`BOOKING_PERIOD` = `bp`.`OBJECT_ID`))) join `crx`.`oocke1_booking` `b` on(((`b`.`POSITION` = `ip`.`POSITION`) and (`b`.`VALUE_DATE` >= `bp`.`PERIOD_STARTS_AT`) and ((`b`.`VALUE_DATE` < `bp`.`PERIOD_ENDS_AT_EXCLUSIVE`) or isnull(`bp`.`PERIOD_ENDS_AT_EXCLUSIVE`)) and ((`b`.`BOOKING_STATUS` >= `r`.`BOOKING_STATUS_THRESHOLD`) or (`r`.`BOOKING_STATUS_THRESHOLD` = 0) or isnull(`r`.`BOOKING_STATUS_THRESHOLD`)))))
md5=4281d3252ce68cd6703d1afa21edcc37
updatable=1
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT
\n    ip.object_id AS item_position,
\n    b.object_id AS single_booking
\nFROM
\n    OOCKE1_DEPOTREPORTITEM ip
\nINNER JOIN
\n    OOCKE1_DEPOTREPORT r
\nON
\n    ip.p$$parent = r.object_id
\nINNER JOIN
\n    OOCKE1_BOOKINGPERIOD bp
\nON
\n    r.booking_period = bp.object_id
\nINNER JOIN
\n    OOCKE1_BOOKING b
\nON
\n    b.position = ip.position AND
\n    b.value_date >= bp.period_starts_at AND
\n    ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL)) AND
\n    ((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0) OR (r.booking_status_threshold IS NULL))
