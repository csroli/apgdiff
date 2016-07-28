
CREATE RULE test_table_rule AS ON UPDATE
	TO test_table WHERE id > 10
	DO ALSO NOTHING;
