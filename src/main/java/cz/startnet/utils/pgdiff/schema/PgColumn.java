package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores column information.
 *
 * @author fordfrog
 */
public class PgColumn {

    /**
     * Pattern for parsing NULL arguments.
     */
    private static final Pattern PATTERN_NULL =
            Pattern.compile("^(.+)[\\s]+NULL$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for parsing NOT NULL arguments.
     */
    private static final Pattern PATTERN_NOT_NULL = Pattern.compile(
            "^(.+)[\\s]+NOT[\\s]+NULL$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for parsing DEFAULT value.
     */
    private static final Pattern PATTERN_DEFAULT = Pattern.compile(
            "^(.+)[\\s]+DEFAULT[\\s]+(.+)$", Pattern.CASE_INSENSITIVE);
    /**
     * Specific statistics value.
     */
    private Integer statistics;
    /**
     * Default value of the column.
     */
    private String defaultValue;
    /**
     * Name of the column.
     */
    private String name;
    /**
     * Type of the column.
     */
    private String type;
    /**
     * Determines whether null value is allowed in the column.
     */
    private boolean nullValue = true;

    /**
     * Creates a new PgColumn object.
     *
     * @param name name of the column
     */
    public PgColumn(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #defaultValue}.
     *
     * @param defaultValue {@link #defaultValue}
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for {@link #defaultValue}.
     *
     * @return {@link #defaultValue}
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns full definition of the column.
     *
     * @param addDefaults whether default value should be added in case NOT
     *        NULL constraint is specified but no default value is set
     *
     * @return full definition of the column
     */
    public String getFullDefinition(final boolean addDefaults) {
        final StringBuilder sbDefinition = new StringBuilder(100);
        sbDefinition.append(PgDiffUtils.getQuotedName(name));
        sbDefinition.append(' ');
        sbDefinition.append(type);

        if ((defaultValue != null) && (defaultValue.length() > 0)) {
            sbDefinition.append(" DEFAULT ");
            sbDefinition.append(defaultValue);
        } else if (!nullValue && addDefaults) {
            final String defaultColValue = PgColumnUtils.getDefaultValue(type);

            if (defaultColValue != null) {
                sbDefinition.append(" DEFAULT ");
                sbDefinition.append(defaultColValue);
            }
        }

        if (!nullValue) {
            sbDefinition.append(" NOT NULL");
        }

        return sbDefinition.toString();
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
     * Setter for {@link #nullValue}.
     *
     * @param nullValue {@link #nullValue}
     */
    public void setNullValue(final boolean nullValue) {
        this.nullValue = nullValue;
    }

    /**
     * Getter for {@link #nullValue}.
     *
     * @return {@link #nullValue}
     */
    public boolean getNullValue() {
        return nullValue;
    }

    /**
     * Setter for {@link #statistics}.
     *
     * @param statistics {@link #statistics}
     */
    public void setStatistics(final Integer statistics) {
        this.statistics = statistics;
    }

    /**
     * Getter for {@link #statistics}.
     *
     * @return {@link #statistics}
     */
    public Integer getStatistics() {
        return statistics;
    }

    /**
     * Setter for {@link #type}.
     *
     * @param type {@link #type}
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Getter for {@link #type}.
     *
     * @return {@link #type}
     */
    public String getType() {
        return type;
    }

    /**
     * Parses definition of the column
     *
     * @param definition definition of the column
     */
    public void parseDefinition(final String definition) {
        String string = definition;

        Matcher matcher = PATTERN_NOT_NULL.matcher(string);

        if (matcher.matches()) {
            string = matcher.group(1).trim();
            setNullValue(false);
        } else {
            matcher = PATTERN_NULL.matcher(string);

            if (matcher.matches()) {
                string = matcher.group(1).trim();
                setNullValue(true);
            }
        }

        matcher = PATTERN_DEFAULT.matcher(string);

        if (matcher.matches()) {
            string = matcher.group(1).trim();
            setDefaultValue(matcher.group(2).trim());
        }

        setType(string);
    }
}
