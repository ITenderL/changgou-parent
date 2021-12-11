package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 16:40
 * @Description:
 */
@Service
public class SkuServiceImpl implements SkuService {
    
    @Autowired
    private SkuEsMapper skuEsMapper;
    
    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 多条件搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 搜索条件分装
        NativeSearchQueryBuilder nativeSearchQueryBuilder = buildBasicQuery(searchMap);
        // 集合搜索
        Map<String, Object> resultMap = searchList(nativeSearchQueryBuilder);

        //当用户选择了分类，分类分组则不显示
        //if (searchMap == null || StringUtils.isEmpty(searchMap.get("category"))) {
        //    // 分类分组查询实现
        //    List<String> categoryList = getCategoryList(nativeSearchQueryBuilder);
        //    resultMap.put("categoryList", categoryList);
        //}
        //
        //// 当用户选择了品牌，品牌分组则不显示
        //if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand"))) {
        //    // 品牌分组查询实现
        //    List<String> brandList = getBrandList(nativeSearchQueryBuilder);
        //    resultMap.put("brandList", brandList);
        //}
        //
        //// 品牌分组查询spec实现
        //Map<String, Set<String>> specList = getSpecList(nativeSearchQueryBuilder);
        //resultMap.put("specList", specList);

        // 获取分组查询数据
        Map<String, Object> groupMap = searchGroupList(nativeSearchQueryBuilder, searchMap);
        resultMap.putAll(groupMap);

        return resultMap;
    }

    /**
     * 分类分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Object> searchGroupList(NativeSearchQueryBuilder nativeSearchQueryBuilder, Map<String, String> searchMap) {
        /**
         * 分组查询分类集合
         * 1)取别名
         * 2）根据哪个域进行分组
         */
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category"))) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        }
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand"))) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        }
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        Map<String, Object> groupMap = new HashMap<>();
        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()获取分组集合
         * .get("skuCategory")获取指定分组
         */
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("category"))) {
            StringTerms skuCategory = aggregatedPage.getAggregations().get("skuCategory");
            List<String> categoryList = getGroupList(skuCategory);
            groupMap.put("categoryList", categoryList);
        }
        if (searchMap == null || StringUtils.isEmpty(searchMap.get("brand"))) {
            StringTerms skuBrand = aggregatedPage.getAggregations().get("skuBrand");
            List<String> brandList = getGroupList(skuBrand);
            groupMap.put("brandList", brandList);
        }
        StringTerms skuSpec = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = getGroupList(skuSpec);
        Map<String, Set<String>> specMap = putAllSpec(specList);
        groupMap.put("specList", specMap);
        return groupMap;
    }

    public List<String> getGroupList(StringTerms stringTerms) {
        List<String> groupList = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String fieldName = bucket.getKeyAsString();
            groupList.add(fieldName);
        }
        return groupList;
    }


    /**
     * 查询条件构建
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (searchMap != null && searchMap.size() > 0) {
            String keywords = searchMap.get("keywords");
            // 关键词搜索
            if (!StringUtils.isEmpty(keywords)) {
                //nativeSearchQueryBuilder.withQuery(QueryBuilders.queryStringQuery(keywords).field("name"));
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }
            // 分类搜索
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", searchMap.get("category")));
            }

            // 品牌搜索
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", searchMap.get("brand")));
            }

            // 规格搜索
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("spec_")) {
                    String value = entry.getValue();
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value));
                }
            }

            // 价格过滤100-200元
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)){
                price = price.replace("元", "").replace("以上", "");
                String[] prices = price.split("-");
                if (prices != null && prices.length > 0) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    if (prices.length == 2) {
                        boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                    }
                }
            }

            // 排序
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
                nativeSearchQueryBuilder.withSort(new FieldSortBuilder(sortField) // 指定排序域
                                            .order(SortOrder.valueOf(sortRule))); // 指定排序规则
            }
        }

        // 分页查询
        Integer pageNum = convertPage(searchMap);
        Integer size = 3;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, size));
        // 将boolQueryBuilder填充给nativeSearchQueryBuilder
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);
        return nativeSearchQueryBuilder;
    }

    public Integer convertPage(Map<String, String> searchMap) {
        if (searchMap != null) {
            String pageNum = searchMap.get("pageNum");
            try {
                return Integer.parseInt(pageNum);
            }catch (NumberFormatException e) {

            }
        }
        return 1;
    }

    /**
     * 查询集合
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Object> searchList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        // 高亮设置
        HighlightBuilder.Field field = new HighlightBuilder.Field("name"); // 设置高亮域
        // 前缀
        field.preTags("<em style=\"color:red;\">");
        // 后缀
        field.postTags("</em>");
        // 高亮长度,关键词数据长度
        field.fragmentSize(100);


        // 添加高亮
        nativeSearchQueryBuilder.withHighlightFields(field);

        /**
         * 执行搜索响应结果给我
         * 1）搜索条件封装对象
         * 2）搜索结果需要转换的类型
         * 3）AggregatedPage<SkuInfo>搜索结果集封装
         */
        //AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> page = elasticsearchTemplate
                .queryForPage(
                        nativeSearchQueryBuilder.build(), // 条件封装
                        SkuInfo.class,
                        new SearchResultMapper() {// 将结果集封装到该对象
                            @Override
                            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                               List<T> list = new ArrayList<>();
                                // 执行查询，获取所有数据
                                for (SearchHit hit : searchResponse.getHits()) {
                                    // 分析结果集，获取非高亮数据
                                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                                    // 分析结果集，获取高亮数据
                                    HighlightField highlightField = hit.getHighlightFields().get("name");
                                    if (highlightField != null && highlightField.getFragments() != null) {
                                        // 高亮数据读取
                                        Text[] fragments = highlightField.getFragments();
                                        StringBuilder builder = new StringBuilder();
                                        for (Text text : fragments) {
                                            builder.append(text.toString());
                                        }
                                        // 非高亮中指定的域，换成高亮
                                        skuInfo.setName(builder.toString());
                                    }
                                    list.add((T) skuInfo);
                                }
                                // 将数据返回
                                /**
                                 * 1）搜索的集合
                                 * 2）分页对象信息
                                 * 3）搜索及记录总数
                                 */
                                return new AggregatedPageImpl<T>(list, pageable, searchResponse.getHits().getTotalHits());
                            }
                        });

        // 总记录数
        long totalElements = page.getTotalElements();
        // 总页数
        int totalPages = page.getTotalPages();
        // 记录
        List<SkuInfo> content = page.getContent();
        // 返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalElements", totalElements);
        resultMap.put("totalPages", totalPages);
        resultMap.put("rows", content);
        return resultMap;
    }

    /**
     * 分类分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> getCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询分类集合
         * 1)取别名
         * 2）根据哪个域进行分组
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()获取分组集合
         * .get("skuCategory")获取指定分组
         */
        StringTerms skuCategory = aggregatedPage.getAggregations().get("skuCategory");
        List<String> categoryList = new ArrayList<>();
        for (StringTerms.Bucket bucket : skuCategory.getBuckets()) {
            String categoryName = bucket.getKeyAsString();// 其中一个分类的名字
            categoryList.add(categoryName);
        }
        return categoryList;
    }

    /**
     * 品牌分组查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private List<String> getBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询品牌集合
         * 1)取别名
         * 2）根据哪个域进行分组
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()获取分组集合
         * .get("skuBrand")获取指定分组
         */
        StringTerms skuBrand = aggregatedPage.getAggregations().get("skuBrand");
        List<String> brandList = new ArrayList<>();
        for (StringTerms.Bucket bucket : skuBrand.getBuckets()) {
            String categoryName = bucket.getKeyAsString();// 其中一个分类的名字
            brandList.add(categoryName);
        }
        return brandList;
    }

    /**
     * 规格参数查询
     * @param nativeSearchQueryBuilder
     * @return
     */
    private Map<String, Set<String>> getSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        /**
         * 分组查询品牌集合
         * 1)取别名
         * 2）根据哪个域进行分组
         */
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").size(10000));
        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()获取分组集合
         * .get("skuSpec")获取指定分组
         */
        StringTerms skuSpec = aggregatedPage.getAggregations().get("skuSpec");
        List<String> specList = new ArrayList<>();
        for (StringTerms.Bucket bucket : skuSpec.getBuckets()) {
            String categoryName = bucket.getKeyAsString();// 其中一个分类的名字
            specList.add(categoryName);
        }
        Map<String, Set<String>> allSpec = putAllSpec(specList);

        return allSpec;
    }

    private Map<String, Set<String>> putAllSpec(List<String> specList) {
        // 封装specMap返回
        Map<String, Set<String>> allSpec = new HashMap<>();
        // 循环便利specList
        for (String spec : specList) {
            // 将规格json字符串转换成map对象
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Set<String> specSet = allSpec.get(key);
                if (specSet == null) {
                    specSet = new HashSet<>();
                }
                specSet.add(value);
                allSpec.put(key, specSet);
            }
        }
        return allSpec;
    }

    /**
     * 导入索引库
     */
    @Override
    public void importData() {
        Result<List<Sku>> skuResult = skuFeign.findAll();
        List<SkuInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuResult.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfoList) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            // 如果生成动态的域，只需要将该域存入到Map<String, Object>中，会为key生成域，value为域的名字
            skuInfo.setSpecMap(specMap);
        }
        skuEsMapper.saveAll(skuInfoList);
    }
}
