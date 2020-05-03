package com.hirohiro716.database;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.Array;

/**
 * SQLのWHERE句をプレースホルダとバインド変数を使用して作成するクラス。<br>
 * 最終的に下記のような、WHEREで使用するプレースホルダと、PreparedStatementで使用するバインド変数を作成することができる。<br>
 * "column1 = ? AND column2 = ?" と new Object[] {"検索値1", "検索値2"}
 * 
 * @author hiro
 *
 */
public class WhereSet {
    
    /**
     * 新しい検索条件を追加する。
     * 
     * @param isNegate
     * @param column
     * @param comparison
     * @param value
     */
    public void add(boolean isNegate, String column, Comparison comparison, Object value) {
        Where where = new Where(column, comparison, value);
        where.setNegate(isNegate);
        this.wheres.add(where);
    }
    
    /**
     * 新しい検索条件を追加する。
     * 
     * @param column
     * @param comparison
     * @param value
     */
    public final void add(ColumnInterface column, Comparison comparison, Object value) {
        this.add(false, column.getFullPhysicalName(), comparison, value);
    }

    /**
     * 新しい検索条件を追加する。
     * 
     * @param column
     * @param comparison
     * @param value
     */
    public final void add(String column, Comparison comparison, Object value) {
        this.add(false, column, comparison, value);
    }
    
    /**
     * 新しい検索条件をNOT演算子で追加する。
     * 
     * @param column
     * @param comparison
     * @param value
     */
    public final void addNegate(ColumnInterface column, Comparison comparison, Object value) {
        this.add(true, column.getFullPhysicalName(), comparison, value);
    }
    
    /**
     * 新しい検索条件をNOT演算子で追加する。
     * 
     * @param column
     * @param comparison
     * @param value
     */
    public final void addNegate(String column, Comparison comparison, Object value) {
        this.add(true, column, comparison, value);
    }
    
    /**
     * 新しい検索条件をBETWEEN演算子で追加する。
     * 
     * @param isNegate
     * @param column
     * @param value1
     * @param value2
     */
    public void addBetween(boolean isNegate, String column, Object value1, Object value2) {
        Where where = new Where(column, Comparison.BETWEEN, value1, value2);
        where.setNegate(isNegate);
        this.wheres.add(where);
    }
    
    /**
     * 新しい検索条件をBETWEEN演算子で追加する。
     * 
     * @param column
     * @param value1
     * @param value2
     */
    public final void addBetween(ColumnInterface column, Object value1, Object value2) {
        this.addBetween(false, column.getFullPhysicalName(), value1, value2);
    }

    /**
     * 新しい検索条件をBETWEEN演算子で追加する。
     * 
     * @param column
     * @param value1
     * @param value2
     */
    public final void addBetween(String column, Object value1, Object value2) {
        this.addBetween(false, column, value1, value2);
    }
    
    /**
     * 新しい検索条件をNOT演算子＋BETWEEN演算子で追加する。
     * 
     * @param column
     * @param value1
     * @param value2
     */
    public final void addBetweenNegate(ColumnInterface column, Object value1, Object value2) {
        this.addBetween(true, column.getFullPhysicalName(), value1, value2);
    }

    /**
     * 新しい検索条件をNOT演算子＋BETWEEN演算子で追加する。
     * 
     * @param column
     * @param value1
     * @param value2
     */
    public final void addBetweenNegate(String column, Object value1, Object value2) {
        this.addBetween(true, column, value1, value2);
    }
    
    /**
     * 新しい検索条件をIN演算子で追加する。
     * 
     * @param isNegate
     * @param column
     * @param values
     */
    public void addIn(boolean isNegate, String column, Object... values) {
        Where where = new Where(column, Comparison.IN, values);
        where.setNegate(isNegate);
        this.wheres.add(where);
    }
    
    /**
     * 新しい検索条件をIN演算子で追加する。
     * 
     * @param column
     * @param values
     */
    public final void addIn(ColumnInterface column, Object... values) {
        this.addIn(false, column.getFullPhysicalName(), values);
    }

    /**
     * 新しい検索条件をIN演算子で追加する。
     * 
     * @param column
     * @param values
     */
    public final void addIn(String column, Object... values) {
        this.addIn(false, column, values);
    }
    
    /**
     * 新しい検索条件をNOT演算子＋IN演算子で追加する。
     * 
     * @param column
     * @param values
     */
    public final void addInNegate(ColumnInterface column, Object... values) {
        this.addIn(true, column.getFullPhysicalName(), values);
    }

    /**
     * 新しい検索条件をNOT演算子＋IN演算子で追加する。
     * 
     * @param column
     * @param values
     */
    public final void addInNegate(String column, Object... values) {
        this.addIn(true, column, values);
    }
    
    /**
     * 新しい検索条件をIS NULL演算子で追加する。
     * 
     * @param isNegate
     * @param column
     */
    public void addIsNull(boolean isNegate, String column) {
        Where where = new Where(column, Comparison.IS_NULL);
        where.setNegate(isNegate);
        this.wheres.add(where);
    }
    
    /**
     * 新しい検索条件をIS NULL演算子で追加する。
     * 
     * @param column
     */
    public final void addIsNull(ColumnInterface column) {
        this.addIsNull(false, column.getFullPhysicalName());
    }

    /**
     * 新しい検索条件をIS NULL演算子で追加する。
     * 
     * @param column
     */
    public final void addIsNull(String column) {
        this.addIsNull(false, column);
    }
    
    /**
     * 新しい検索条件をNOT演算子＋IS NULL演算子で追加する。
     * 
     * @param column
     */
    public final void addIsNullNegate(ColumnInterface column) {
        this.addIsNull(true, column.getFullPhysicalName());
    }

    /**
     * 新しい検索条件をNOT演算子＋IS NULL演算子で追加する。
     * 
     * @param column
     */
    public final void addIsNullNegate(String column) {
        this.addIsNull(true, column);
    }
    
    private List<Where> wheres = new ArrayList<>();
    
    /**
     * 追加済みのWhereインスタンスを取得する。
     * 
     * @return 結果。
     */
    public Where[] getWheres() {
        return this.wheres.toArray(new Where[] {});
    }
    
    /**
     * 追加済みのWhereインスタンスをすべて削除する。
     */
    public void clear() {
        this.wheres.clear();
    }
    
    /**
     * 追加済みのWhereインスタンスの数を取得する。
     * 
     * @return 結果。
     */
    public int size() {
        return this.wheres.size();
    }
    
    /**
     * 追加済みのWhereインスタンスの中からカラムが一致するものを取得する。一致するものがなければnullを返す。
     * 
     * @param column
     * @return Whereインスタンス、またはnull。
     */
    public Where findWhereFromColumn(String column) {
        for (Where where : this.wheres) {
            if (where.getColumn().equals(column)) {
                return where;
            }
        }
        return null;
    }
    
    /**
     * 追加済みのWhereインスタンスの中からカラムが一致するものを取得する。一致するものがなければnullを返す。
     * 
     * @param column
     * @return Whereインスタンス、またはnull。
     */
    public final Where findWhereFromColumn(ColumnInterface column) {
        return this.findWhereFromColumn(column.getFullPhysicalName());
    }
    
    /**
     * WHERE句で使用できるプレースホルダを作成する。
     * 
     * @return 結果。
     */
    public String buildPlaceholderClause() {
        StringBuilder builder = new StringBuilder();
        for (Where where : this.wheres) {
            if (builder.length() > 0) {
                builder.append(" AND ");
            }
            builder.append(where.buildPlaceholderClause());
        }
        return builder.toString();
    }
    
    /**
     * WHERE句のプレースホルダに対するバインド変数の配列を作成する。
     * 
     * @return 結果。
     */
    public Object[] buildParameters() {
        List<Object> parameters = new ArrayList<>();
        for (Where where : this.wheres) {
            switch (where.getComparison()) {
            case EQUAL:
            case NOT_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case LIKE:
            case SIMILARTO:
            case REGEXP:
                parameters.add(where.getValue());
                break;
            case BETWEEN:
                parameters.add(where.getValue());
                parameters.add(where.getValue2());
                break;
            case IN:
                for (Object value : where.getValues()) {
                    parameters.add(value);
                }
                break;
            case IS_NULL:
                break;
            }
        }
        return parameters.toArray();
    }
    
    @Override
    public WhereSet clone() {
        WhereSet clone = new WhereSet();
        List<Where> cloneWheres = new ArrayList<>();
        for (Where where : this.wheres) {
            cloneWheres.add(where.clone());
        }
        clone.wheres.clear();
        clone.wheres.addAll(cloneWheres);
        return clone;
    }
    
    /**
     * 比較演算子の列挙型。
     * 
     * @author hiro
     */
    public enum Comparison {
        /**
         * 等しい。
         */
        EQUAL("="),
        /**
         * 等しくない。
         */
        NOT_EQUAL("!="),
        /**
         * 小なり。
         */
        LESS("<"),
        /**
         * 小なりイコール。
         */
        LESS_EQUAL("<="),
        /**
         * 大なり。
         */
        GREATER(">"),
        /**
         * 大なりイコール。
         */
        GREATER_EQUAL(">="),
        /**
         * IN。
         */
        IN("IN"),
        /**
         * NULL。
         */
        IS_NULL("IS NULL"),
        /**
         * LIKEを使用したワイルドカード。
         */
        LIKE("LIKE"),
        /**
         * BETWEENを使用した範囲指定。
         */
        BETWEEN("BETWEEN"),
        /**
         * SIMILAR TOを使用した正規表現によるパターンマッチ。
         */
        SIMILARTO("SIMILAR TO"),
        /**
         * REGEXPを使用した正規表現によるパターンマッチ。
         */
        REGEXP("REGEXP"),
        ;
        
        /**
         * コンストラクタ。
         * 
         * @param operator 演算子。
         */
        private Comparison(String operator) {
            this.operator = operator;
        }
        
        private String operator;
        
        /**
         * 比較演算子を取得する。
         * 
         * @return 比較演算子。
         */
        public String getOperator() {
            return this.operator;
        }
    }
    
    /**
     * SQLのWHERE句をプレースホルダとバインド変数を使用して作成するクラス。<br>
     * 最終的に下記のような、WHEREで使用するプレースホルダと、PreparedStatementで使用するバインド変数を作成することができる。<br>
     * "column1 IN (?, ?, ?)" と new Object[] {"検索値1", "検索値2", "検索値3}
     * 
     * @author hiro
     *
     */
    public static class Where implements Cloneable {
        
        /**
         * コンストラクタ。
         * 
         * @param column
         * @param comparison
         * @param values
         */
        public Where(String column, Comparison comparison, Object... values) {
            this.column = column;
            this.comparison = comparison;
            this.setValues(values);
        }
        
        /**
         * コンストラクタ。
         * 
         * @param column
         * @param comparison
         * @param values
         */
        public Where(ColumnInterface column, Comparison comparison, Object... values) {
            this(column.getFullPhysicalName(), comparison, values);
        }
        
        private String column;
        
        /**
         * カラム名を取得する。
         * 
         * @return 結果。
         */
        public String getColumn() {
            return this.column;
        }
        
        /**
         * カラム名をセットする。
         * 
         * @param column
         */
        public void setColumn(String column) {
            this.column = column;
        }
        
        private Comparison comparison;
        
        /**
         * 比較演算子を取得する。
         * 
         * @return 結果。
         */
        public Comparison getComparison() {
            return this.comparison;
        }
        
        /**
         * 比較演算子をセットする。
         * 
         * @param comparison
         */
        public void setComparison(Comparison comparison) {
            this.comparison = comparison;
        }
        
        private List<Object> values;
        
        /**
         * 検索値を取得する。
         * 
         * @return 結果。
         */
        public Object getValue() {
            try {
                return this.values.get(0);
            } catch (Exception exception) {
                return null;
            }
        }
        
        /**
         * 2番目の検索値を取得する。
         * 
         * @return 結果。
         */
        public Object getValue2() {
            try {
                return this.values.get(1);
            } catch (Exception exception) {
                return null;
            }
        }
        
        /**
         * 検索値をセットする。
         * 
         * @param value
         */
        public void setValue(Object value) {
            this.setValues(value);
        }
        
        /**
         * すべての検索値を取得する。
         * 
         * @return 結果。
         */
        public Array<Object> getValues() {
            return new Array<>(this.values);
        }
        
        /**
         * 検索値をセットする。
         * 
         * @param values
         */
        public void setValues(Object... values) {
            this.values = new ArrayList<>();
            for (Object value : values) {
                this.values.add(Database.convertToBindParameter(value));
            }
        }
        
        private boolean isNegate = false;
        
        /**
         * 検索条件を反転させている場合はtrueを返す。
         * 
         * @return 結果。
         */
        public boolean isNegate() {
            return this.isNegate;
        }
        
        /**
         * 検索条件を反転させる。
         * 
         * @param isNegate
         */
        public void setNegate(boolean isNegate) {
            this.isNegate = isNegate;
        }
        
        /**
         * WHERE句で使用できるプレースホルダを作成する。
         * 
         * @return 結果。
         */
        public String buildPlaceholderClause() {
            StringBuilder builder = new StringBuilder();
            if (this.isNegate) {
                builder.append("NOT ");
            }
            builder.append(this.column);
            builder.append(" ");
            switch (this.comparison) {
            case EQUAL:
            case NOT_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case LIKE:
            case SIMILARTO:
            case REGEXP:
                builder.append(this.comparison.getOperator());
                builder.append(" ?");
                break;
            case BETWEEN:
                builder.append(this.comparison.getOperator());
                builder.append(" ? AND ?");
                break;
            case IN:
                builder.append(this.comparison.getOperator());
                builder.append(" (");
                for (int index = 0; index < this.values.size(); index++) {
                    if (index > 0) {
                        builder.append(", ");
                    }
                    builder.append("?");
                }
                builder.append(")");
                break;
            case IS_NULL:
                builder.append(this.comparison.getOperator());
                break;
            }
            return builder.toString();
        }
        
        @Override
        public Where clone() {
            Where clone = new Where(this.getColumn(), this.getComparison(), this.getValue());
            clone.isNegate = this.isNegate;
            List<Object> cloneValues = new ArrayList<>();
            for (Object value : this.getValues()) {
                if (value instanceof Date) {
                    try {
                        Date cloneDate = new Date();
                        cloneDate.setTime(((Date) value).getTime());
                        cloneValues.add(cloneDate);
                        continue;
                    } catch (Exception exception) {
                    }
                }
                cloneValues.add(value);
            }
            clone.setValues(cloneValues.toArray());
            return clone;
        }
    }
}
