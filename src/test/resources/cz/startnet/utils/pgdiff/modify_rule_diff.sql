
DROP RULE IF EXISTS test_table_rule ON test_table CASCADE;

CREATE RULE test_table_rule AS ON DELETE
	TO test_table WHERE id > 15
	DO INSTEAD NOTHING;
