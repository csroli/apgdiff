/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgRule;

/**
 * Parses CREATE TRIGGER statements.
 *
 * @author fordfrog
 */
public class CreateRuleParser {

    /**
     * Parses CREATE TRIGGER statement.
     *
     * @param database            database
     * @param statement           CREATE RULE statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE");

        // Optional OR EXISTS, irrelevant for our purposes
        parser.expectOptional("OR", "REPLACE");

				parser.expect("RULE");

        final String ruleName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(ruleName);

        final PgRule rule = new PgRule();
        rule.setName(objectName);

				parser.expect("AS", "ON");

				if (parser.expectOptional("INSERT")) {
						rule.setOnInsert(true);
				} else if (parser.expectOptional("UPDATE")) {
						rule.setOnUpdate(true);
				} else if (parser.expectOptional("DELETE")) {
						rule.setOnDelete(true);
				} else if (parser.expectOptional("SELECT")) {
						rule.setOnSelect(true);
				} else {
						parser.throwUnsupportedCommand();
				}

        parser.expect("TO");

        final String relationName = parser.parseIdentifier();

        rule.setRelationName(ParserUtils.getObjectName(relationName));

        if (parser.expectOptional("WHERE")) {
            rule.setWhere(parser.getExpressionUntil("DO"));
        }

        parser.expect("DO");
				if (parser.expectOptional("ALSO")) {
						rule.setAlso(true);
				} else if (parser.expectOptional("INSTEAD")) {
						rule.setInstead(true);
				}
        rule.setCommand(parser.getRest());

				final PgSchema schema = database.getSchema(
								ParserUtils.getSchemaName(relationName, database));
				schema.getRelation(rule.getRelationName()).addRule(rule);
    }

    /**
     * Creates a new CreateTableParser object.
     */
    private CreateRuleParser() {
    }
}
