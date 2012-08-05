TYPE=VIEW
query=select `d_`.`OBJECT_ID` AS `document`,`f`.`OBJECT_ID` AS `folder` from (`crx`.`oocke1_document_` `d_` join `crx`.`oocke1_documentfolder` `f` on((`d_`.`FOLDER` = `f`.`OBJECT_ID`)))
md5=f2060594c7edeecc3f2e714ca7155696
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
\n    d_.object_id AS document,
\n    f.object_id AS folder
\nFROM
\n    OOCKE1_DOCUMENT_ d_
\nINNER JOIN
\n    OOCKE1_DOCUMENTFOLDER f
\nON
\n    d_.folder = f.object_id
