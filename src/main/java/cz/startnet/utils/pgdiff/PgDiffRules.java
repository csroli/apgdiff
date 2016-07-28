/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgRelation;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgRule;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs rules.
 *
 * @author fordfrog
 */
public class PgDiffRules {

    /**
     * Outputs statements for creation of new rules.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createRules(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgRelation newRelation : newSchema.getRels()) {
            final PgRelation oldRelation;

            if (oldSchema == null) {
                oldRelation = null;
            } else {
                oldRelation = oldSchema.getRelation(newRelation.getName());
            }

            // Add new rules
            for (final PgRule rule : getNewRules(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(rule.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping rules.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropRules(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgRelation newRelation : newSchema.getRels()) {
            final PgRelation oldRelation;

            if (oldSchema == null) {
                oldRelation = null;
            } else {
                oldRelation = oldSchema.getRelation(newRelation.getName());
            }

            // Drop rules that no more exist or are modified
            for (final PgRule rule :
                    getDropRules(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(rule.getDropSQL());
            }
        }
    }

    /**
     * Returns list of rules that should be dropped.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of rules that should be dropped
     */
    private static List<PgRule> getDropRules(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgRule> list = new ArrayList<PgRule>();

        if (newRelation != null && oldRelation != null) {
            final List<PgRule> newRules = newRelation.getRules();

            for (final PgRule oldRule : oldRelation.getRules()) {
                if (!newRules.contains(oldRule)) {
                    list.add(oldRule);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of rules that should be added.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of rules that should be added
     */
    private static List<PgRule> getNewRules(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgRule> list = new ArrayList<PgRule>();

        if (newRelation != null) {
            if (oldRelation == null) {
                list.addAll(newRelation.getRules());
            } else {
                for (final PgRule newRule : newRelation.getRules()) {
                    if (!oldRelation.getRules().contains(newRule)) {
                        list.add(newRule);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Outputs statements for rule comments that have changed.
     *
     * @param writer           writer
     * @param oldSchema        old schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterComments(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (PgRelation oldRelation : oldSchema.getRels()) {
            final PgRelation newRelation = newSchema.getRelation(oldRelation.getName());

            if (newRelation == null) {
                continue;
            }

            for (final PgRule oldRule : oldRelation.getRules()) {
                final PgRule newRule =
                        newRelation.getRule(oldRule.getName());

                if (newRule == null) {
                    continue;
                }

                if (oldRule.getComment() == null
                        && newRule.getComment() != null
                        || oldRule.getComment() != null
                        && newRule.getComment() != null
                        && !oldRule.getComment().equals(
                        newRule.getComment())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON RULE ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newRule.getName()));
                    writer.print(" ON ");
                    writer.print(PgDiffUtils.getQuotedName(
                            newRule.getRelationName()));
                    writer.print(" IS ");
                    writer.print(newRule.getComment());
                    writer.println(';');
                } else if (oldRule.getComment() != null
                        && newRule.getComment() == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON RULE ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newRule.getName()));
                    writer.print(" ON ");
                    writer.print(PgDiffUtils.getQuotedName(
                            newRule.getRelationName()));
                    writer.println(" IS NULL;");
                }
            }
        }
    }

    /**
     * Creates a new instance of PgDiffRules.
     */
    private PgDiffRules() {
    }
}
