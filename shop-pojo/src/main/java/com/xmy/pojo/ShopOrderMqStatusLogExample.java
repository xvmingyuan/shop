package com.xmy.pojo;

import java.util.ArrayList;
import java.util.List;

public class ShopOrderMqStatusLogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ShopOrderMqStatusLogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andOrderIdIsNull() {
            addCriterion("order_id is null");
            return (Criteria) this;
        }

        public Criteria andOrderIdIsNotNull() {
            addCriterion("order_id is not null");
            return (Criteria) this;
        }

        public Criteria andOrderIdEqualTo(Long value) {
            addCriterion("order_id =", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotEqualTo(Long value) {
            addCriterion("order_id <>", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThan(Long value) {
            addCriterion("order_id >", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("order_id >=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThan(Long value) {
            addCriterion("order_id <", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdLessThanOrEqualTo(Long value) {
            addCriterion("order_id <=", value, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdIn(List<Long> values) {
            addCriterion("order_id in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotIn(List<Long> values) {
            addCriterion("order_id not in", values, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdBetween(Long value1, Long value2) {
            addCriterion("order_id between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andOrderIdNotBetween(Long value1, Long value2) {
            addCriterion("order_id not between", value1, value2, "orderId");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusIsNull() {
            addCriterion("goods_status is null");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusIsNotNull() {
            addCriterion("goods_status is not null");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusEqualTo(Integer value) {
            addCriterion("goods_status =", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusNotEqualTo(Integer value) {
            addCriterion("goods_status <>", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusGreaterThan(Integer value) {
            addCriterion("goods_status >", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("goods_status >=", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusLessThan(Integer value) {
            addCriterion("goods_status <", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusLessThanOrEqualTo(Integer value) {
            addCriterion("goods_status <=", value, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusIn(List<Integer> values) {
            addCriterion("goods_status in", values, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusNotIn(List<Integer> values) {
            addCriterion("goods_status not in", values, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusBetween(Integer value1, Integer value2) {
            addCriterion("goods_status between", value1, value2, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("goods_status not between", value1, value2, "goodsStatus");
            return (Criteria) this;
        }

        public Criteria andGoodsResultIsNull() {
            addCriterion("goods_result is null");
            return (Criteria) this;
        }

        public Criteria andGoodsResultIsNotNull() {
            addCriterion("goods_result is not null");
            return (Criteria) this;
        }

        public Criteria andGoodsResultEqualTo(String value) {
            addCriterion("goods_result =", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultNotEqualTo(String value) {
            addCriterion("goods_result <>", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultGreaterThan(String value) {
            addCriterion("goods_result >", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultGreaterThanOrEqualTo(String value) {
            addCriterion("goods_result >=", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultLessThan(String value) {
            addCriterion("goods_result <", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultLessThanOrEqualTo(String value) {
            addCriterion("goods_result <=", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultLike(String value) {
            addCriterion("goods_result like", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultNotLike(String value) {
            addCriterion("goods_result not like", value, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultIn(List<String> values) {
            addCriterion("goods_result in", values, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultNotIn(List<String> values) {
            addCriterion("goods_result not in", values, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultBetween(String value1, String value2) {
            addCriterion("goods_result between", value1, value2, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andGoodsResultNotBetween(String value1, String value2) {
            addCriterion("goods_result not between", value1, value2, "goodsResult");
            return (Criteria) this;
        }

        public Criteria andCouponStatusIsNull() {
            addCriterion("coupon_status is null");
            return (Criteria) this;
        }

        public Criteria andCouponStatusIsNotNull() {
            addCriterion("coupon_status is not null");
            return (Criteria) this;
        }

        public Criteria andCouponStatusEqualTo(Integer value) {
            addCriterion("coupon_status =", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusNotEqualTo(Integer value) {
            addCriterion("coupon_status <>", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusGreaterThan(Integer value) {
            addCriterion("coupon_status >", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("coupon_status >=", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusLessThan(Integer value) {
            addCriterion("coupon_status <", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusLessThanOrEqualTo(Integer value) {
            addCriterion("coupon_status <=", value, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusIn(List<Integer> values) {
            addCriterion("coupon_status in", values, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusNotIn(List<Integer> values) {
            addCriterion("coupon_status not in", values, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusBetween(Integer value1, Integer value2) {
            addCriterion("coupon_status between", value1, value2, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("coupon_status not between", value1, value2, "couponStatus");
            return (Criteria) this;
        }

        public Criteria andCouponResultIsNull() {
            addCriterion("coupon_result is null");
            return (Criteria) this;
        }

        public Criteria andCouponResultIsNotNull() {
            addCriterion("coupon_result is not null");
            return (Criteria) this;
        }

        public Criteria andCouponResultEqualTo(String value) {
            addCriterion("coupon_result =", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultNotEqualTo(String value) {
            addCriterion("coupon_result <>", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultGreaterThan(String value) {
            addCriterion("coupon_result >", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultGreaterThanOrEqualTo(String value) {
            addCriterion("coupon_result >=", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultLessThan(String value) {
            addCriterion("coupon_result <", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultLessThanOrEqualTo(String value) {
            addCriterion("coupon_result <=", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultLike(String value) {
            addCriterion("coupon_result like", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultNotLike(String value) {
            addCriterion("coupon_result not like", value, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultIn(List<String> values) {
            addCriterion("coupon_result in", values, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultNotIn(List<String> values) {
            addCriterion("coupon_result not in", values, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultBetween(String value1, String value2) {
            addCriterion("coupon_result between", value1, value2, "couponResult");
            return (Criteria) this;
        }

        public Criteria andCouponResultNotBetween(String value1, String value2) {
            addCriterion("coupon_result not between", value1, value2, "couponResult");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusIsNull() {
            addCriterion("user_money_status is null");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusIsNotNull() {
            addCriterion("user_money_status is not null");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusEqualTo(Integer value) {
            addCriterion("user_money_status =", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusNotEqualTo(Integer value) {
            addCriterion("user_money_status <>", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusGreaterThan(Integer value) {
            addCriterion("user_money_status >", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("user_money_status >=", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusLessThan(Integer value) {
            addCriterion("user_money_status <", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusLessThanOrEqualTo(Integer value) {
            addCriterion("user_money_status <=", value, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusIn(List<Integer> values) {
            addCriterion("user_money_status in", values, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusNotIn(List<Integer> values) {
            addCriterion("user_money_status not in", values, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusBetween(Integer value1, Integer value2) {
            addCriterion("user_money_status between", value1, value2, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserMoneyStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("user_money_status not between", value1, value2, "userMoneyStatus");
            return (Criteria) this;
        }

        public Criteria andUserResultIsNull() {
            addCriterion("user_result is null");
            return (Criteria) this;
        }

        public Criteria andUserResultIsNotNull() {
            addCriterion("user_result is not null");
            return (Criteria) this;
        }

        public Criteria andUserResultEqualTo(String value) {
            addCriterion("user_result =", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultNotEqualTo(String value) {
            addCriterion("user_result <>", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultGreaterThan(String value) {
            addCriterion("user_result >", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultGreaterThanOrEqualTo(String value) {
            addCriterion("user_result >=", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultLessThan(String value) {
            addCriterion("user_result <", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultLessThanOrEqualTo(String value) {
            addCriterion("user_result <=", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultLike(String value) {
            addCriterion("user_result like", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultNotLike(String value) {
            addCriterion("user_result not like", value, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultIn(List<String> values) {
            addCriterion("user_result in", values, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultNotIn(List<String> values) {
            addCriterion("user_result not in", values, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultBetween(String value1, String value2) {
            addCriterion("user_result between", value1, value2, "userResult");
            return (Criteria) this;
        }

        public Criteria andUserResultNotBetween(String value1, String value2) {
            addCriterion("user_result not between", value1, value2, "userResult");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}