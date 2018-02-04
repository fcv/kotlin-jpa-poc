-- Example file on how to populate a bunch of data into PostreSQL table using "random" string and generate_series function
-- based https://stackoverflow.com/questions/3371503/sql-populate-table-with-random-data/3373940#3373940
INSERT INTO person (id, "name", birthday)
	(SELECT nextval('hibernate_sequence') AS id, 'Name #' || count || ' noise#' || md5(random()::text) AS name, date '2001-01-01' + count AS "date"
		FROM generate_series(1, 1000000) count);
