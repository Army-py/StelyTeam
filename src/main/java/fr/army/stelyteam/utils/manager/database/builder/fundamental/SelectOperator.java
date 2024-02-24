package fr.army.stelyteam.utils.manager.database.builder.fundamental;


import fr.army.stelyteam.utils.manager.database.builder.FundamentalOperator;

public class SelectOperator extends FundamentalOperator {
    
    private final String headRequest = "SELECT {columns} FROM {table}";

    private final String[] orders;
    
    public SelectOperator(String table, String[] columns, String[] conditions, String[] orders) {
        super(table, columns, conditions);
        this.orders = orders;
    }

    // @Override
    // public String getTable() {
    //     return table;
    // }

    // @Override
    // public List<String> getColumns() {
    //     return columns;
    // }

    // @Override
    // public List<String> getConditions() {
    //     return conditions;
    // }

    @Override
    public String build() {
        StringBuilder builder = new StringBuilder(headRequest);
        builder.replace(builder.indexOf("{columns}"), builder.indexOf("{columns}") + "{columns}".length(), String.join(", ", columns));
        builder.replace(builder.indexOf("{table}"), builder.indexOf("{table}") + "{table}".length(), table);
        if (conditions != null) {
            builder.append(" WHERE ");
            builder.append(String.join(" AND ", conditions));
        }
        if (orders != null){
            builder.append(" ORDER BY ");
            builder.append(String.join(", ", orders));
        }
        builder.append(";");
        return builder.toString();
    }
}
