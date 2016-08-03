/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores rule information.
 *
 * @author csroli 
 */
public class PgRule {
    /**
     * Command attribute of the rule.
     */
    private String command;
    /**
     * Name of the rule.
     */
    private String name;
    /**
     * Name of the relation the rule is defined on.
     */
    private String relationName;
    /**
     * Whether the rule should be fired on DELETE.
     */
    private boolean onDelete;
    /**
     * Whether the rule should be fired on INSERT.
     */
    private boolean onInsert;
    /**
     * Whether the rule should be fired on UPDATE.
     */
    private boolean onUpdate;
    /**
     * Whether the rule should be fired on SELECT.
     */
    private boolean onSelect;
    /**
     * Whether the ALSO attribute set.
     */
    private boolean also;
    /**
     * Whether the INSTEAD attribute set.
     */
    private boolean instead;
    /**
     * WHERE condition.
     */
    private String where;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Getter for {@link #comment}.
     *
     * @return {@link #comment}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link #comment}.
     *
     * @param comment {@link #comment}
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Creates and returns SQL for creation of rule.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(255);
        sbSQL.append("CREATE RULE ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(" AS ON ");
        if (isOnInsert()) {
            sbSQL.append("INSERT");
        }

        if (isOnUpdate()) {
            sbSQL.append("UPDATE");
        }

        if (isOnDelete()) {
            sbSQL.append("DELETE");
        }

        if (isOnSelect()) {
            sbSQL.append("SELECT");
        }

        sbSQL.append(System.getProperty("line.separator"));

        sbSQL.append("\tTO ");
        sbSQL.append(PgDiffUtils.getQuotedName(getRelationName()));

        if (where != null && !where.isEmpty()) {
            sbSQL.append(" WHERE ");
            sbSQL.append(where);
        }
				sbSQL.append(System.getProperty("line.separator"));

        sbSQL.append("\tDO ");
        if (isAlso()) {
            sbSQL.append("ALSO ");
        }
        if (isInstead()) {
            sbSQL.append("INSTEAD ");
        }
        sbSQL.append(getCommand());
        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON RULE ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ON ");
            sbSQL.append(PgDiffUtils.getQuotedName(relationName));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL for dropping the rule.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP RULE IF EXISTS " + PgDiffUtils.getQuotedName(getName()) + " ON "
                + PgDiffUtils.getQuotedName(getRelationName()) + " CASCADE;";
    }


    /**
     * Setter for {@link #command}.
     *
     * @param function {@link #command}
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Getter for {@link #command}.
     *
     * @return {@link #command}
     */
    public String getCommand() {
        return command;
    }

    /**
     * Setter for {@link #name}.
     *
     * @param name {@link #name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link #onDelete}.
     *
     * @param onDelete {@link #onDelete}
     */
    public void setOnDelete(final boolean onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * Getter for {@link #onDelete}.
     *
     * @return {@link #onDelete}
     */
    public boolean isOnDelete() {
        return onDelete;
    }

    /**
     * Setter for {@link #onInsert}.
     *
     * @param onInsert {@link #onInsert}
     */
    public void setOnInsert(final boolean onInsert) {
        this.onInsert = onInsert;
    }

    /**
     * Getter for {@link #onInsert}.
     *
     * @return {@link #onInsert}
     */
    public boolean isOnInsert() {
        return onInsert;
    }

    /**
     * Setter for {@link #also}.
     *
     * @param also {@link #also}
     */
    public void setAlso(final boolean also) {
        this.also = also;
    }

    /**
     * Getter for {@link #also}.
     *
     * @return {@link #also}
     */
    public boolean isAlso() {
        return also;
    }

    /**
     * Setter for {@link #instead}.
     *
     * @param instead {@link #instead}
     */
    public void setInstead(final boolean instead) {
        this.instead = instead;
    }

    /**
     * Getter for {@link #instead}.
     *
     * @return {@link #instead}
     */
    public boolean isInstead() {
        return instead;
    }

    /**
     * Setter for {@link #onUpdate}.
     *
     * @param onUpdate {@link #onUpdate}
     */
    public void setOnUpdate(final boolean onUpdate) {
        this.onUpdate = onUpdate;
    }

    /**
     * Getter for {@link #onUpdate}.
     *
     * @return {@link #onUpdate}
     */
    public boolean isOnUpdate() {
        return onUpdate;
    }

    /**
     * Getter for {@link #onSelect}.
     *
     * @return {@link #onSelect}
     */
    public boolean isOnSelect() {
        return onSelect;
    }

    /**
     * Setter for {@link #onSelect}.
     *
     * @param onSelect {@link #onSelect}
     */
    public void setOnSelect(final boolean onSelect) {
        this.onSelect = onSelect;
    }

    /**
     * Setter for {@link #relationName}.
     *
     * @param relationName {@link #relationName}
     */
    public void setRelationName(final String relationName) {
        this.relationName = relationName;
    }

    /**
     * Getter for {@link #relationName}.
     *
     * @return {@link #relationName}
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Getter for {@link #where}.
     *
     * @return {@link #where}
     */
    public String getWhere() {
        return where;
    }

    /**
     * Setter for {@link #where}.
     *
     * @param where {@link #where}
     */
    public void setWhere(final String where) {
        this.where = where;
    }

    @Override
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgRule) {
            final PgRule rule = (PgRule) object;
            equals = command.equals(rule.getCommand())
                    && name.equals(rule.getName())
                    && (where == null ? rule.getWhere() == null : where.equals(rule.getWhere()))
                    && (onDelete == rule.isOnDelete())
                    && (onInsert == rule.isOnInsert())
                    && (onUpdate == rule.isOnUpdate())
                    && (onSelect == rule.isOnSelect())
                    && (also == rule.isAlso())
                    && (instead == rule.isInstead())
                    && relationName.equals(rule.getRelationName());
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return (getClass().getName() + "|" 
                + command + "|" + name + "|" + onDelete + "|" + onInsert + "|"
                + onUpdate + "|" + onSelect + "|" + also + "|" + instead + "|" + relationName).hashCode();
    }
}
