## JPA整合
jpa相关内容就不多接受了,可以自行查阅官方文档,这里就说一下整合思路, `BaseEntity`来设置审计字段,所有entity都必须继承该类,
重写了基础实现类 `BaseRepository`来扩展实现原生查询方法, `ColumnToBean`类型转换,详细代码可以去github上查阅,具体代码如下:
```java
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>,
        PagingAndSortingRepository<T, ID> {
    /**
     * Title: executeNativeQuery
     * Description: 原生sql查询
     *
     * @param queryParams 查询参数
     *
     * @return java.util.List<E>
     *
     * @author zhangshuai 2019/8/16
     *
     */
    <E> List<E> executeNativeQuery(QueryParams queryParams);

    /**
     * Title: executeNativeSingleQuery
     * Description: 查询单条记录
     *
     * @param queryParams 查询参数
     *
     * @return E
     *
     * @author zhangshuai 2019/8/16
     *
     */
    <E> E executeNativeSingleQuery(QueryParams queryParams);

    /**
     * Title: executeCountNativeQuery
     * Description: 查询记录数
     *
     * @param sql 查询sql
     * @param paramList 查询条件
     * @param aliasParams 别名查询参数
     *
     * @return java.lang.Long
     *
     * @author zhangshuai 2019/8/27
     *
     */
    Long executeCountNativeQuery(String sql, List<Object> paramList, Map<String, Object> aliasParams);

    /**
     * Title: executePageNativeQuery
     * Description: 查询分页信息
     *
     * @param queryParams 查询参数
     *
     * @return com.pay.admin.common.page.PageData<T>
     *
     * @author zhangshuai 2019/8/26
     *
     */
    <E> PageData<E> executePageNativeQuery(QueryParams queryParams);
}
```
```java
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1646695318257712435L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "create_by")
    private String createBy;

    @CreatedDate
    @Column(name = "gmt_create", updatable = false)
    private LocalDateTime create;

    @Column(name = "modified_by")
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "gmt_modified")
    private LocalDateTime modified;

}
```
```java
@Slf4j
public class ColumnToBean extends AliasedTupleSubsetResultTransformer {
    private static final long serialVersionUID = -9086396692233593699L;
    private static final String STR_UNDERLINE = "_";

    private static final String STR_EMPTY = "";


    private final Class resultClass;
    private boolean isInitialized;
    private String[] aliases;
    private Setter[] setters;

    public ColumnToBean(Class resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        isInitialized = false;
        this.resultClass = resultClass;
    }


    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result;
        try {
            if (!isInitialized) {
                initialize(aliases);
            } else {
                check(aliases);
            }
            result = resultClass.newInstance();
            for (int i = 0; i < aliases.length; i++) {
                if (setters[i] != null && tuple[i] != null) {
                    Method method = setters[i].getMethod();
                    if (method == null) {
                        throw new RuntimeException("Could not find DB Column:" + aliases[i] + " Setter Method!");
                    }
                    Class<?> param = method.getParameterTypes()[0];
                    Class<?> tupleClass = tuple[i].getClass();
                    //若目标参数类型和当前参数类型不匹配 ,尝试进行转换
                    if (!param.equals(tupleClass)) {
                        if (Number.class.isAssignableFrom(tupleClass)) {
                            Number num = (Number) tuple[i];
                            if (Long.class.equals(param) || long.class.equals(param)) {
                                setters[i].set(result, num.longValue(), null);
                            } else if (Integer.class.equals(param) || int.class.equals(param)) {
                                setters[i].set(result, num.intValue(), null);
                            } else if (Boolean.class.equals(param) || boolean.class.equals(param)) {
                                setters[i].set(result, num.intValue() == 1, null);
                            } else if (Float.class.equals(param) || float.class.equals(param)) {
                                setters[i].set(result, num.floatValue(), null);
                            } else if (Double.class.equals(param) || double.class.equals(param)) {
                                setters[i].set(result, num.doubleValue(), null);
                            } else if (Short.class.equals(param) || short.class.equals(param)) {
                                setters[i].set(result, num.shortValue(), null);
                            } else if (BigDecimal.class.equals(param)) {
                                setters[i].set(result, num, null);
                            }  else {
                                throw new RuntimeException("Unsupported type conversion ：" + tuple[i].getClass());
                            }
                            //如果tuple为参数的子类,直接设置
                            //如java.util.Date; java.sql.Date;
                        } else if (param.isAssignableFrom(tupleClass)) {
                            setters[i].set(result, tuple[i], null);
                            //处理数据库类型定义为大字段Clob的数据，将其转换成字符串类型
                        } else if (tuple[i] instanceof Clob) {
                            Clob clob = (Clob) tuple[i];
                            setters[i].set(result, clobToString(clob), null);
                        } else if (LocalDateTime.class.equals(param)) {
                            Timestamp timestamp = (Timestamp) tuple[i];
                            setters[i].set(result, timestamp.toLocalDateTime(), null);
                        } else {
                            throw new RuntimeException("Unsupported type conversion ：" + tuple[i].getClass());
                        }
                    } else {
                        setters[i].set(result, tuple[i], null);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("不能实例化类：" + resultClass.getName(),e);
            throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
        }
        return result;
    }

    private void initialize(String[] aliases) {
        PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
                PropertyAccessStrategyBasicImpl.INSTANCE,
                PropertyAccessStrategyFieldImpl.INSTANCE,
                PropertyAccessStrategyMapImpl.INSTANCE
        );
        this.aliases = new String[aliases.length];
        setters = new Setter[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                this.aliases[i] = alias;
                setters[i] = getSetterByColumnName(alias, propertyAccessStrategy);
            }
        }
        isInitialized = true;
    }

    /**
     * 根据数据库字段名在POJO查找JAVA属性名的Setter方法，参数就是数据库字段名
     */
    private Setter getSetterByColumnName(String alias, PropertyAccessStrategyChainedImpl propertyAccessStrategy) {
        // 取得POJO所有属性名
        Field[] fields = resultClass.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            throw new RuntimeException("POJO [" + resultClass.getName() + "] does not contain any attributes.");
        }
        // 把字段名中所有的下杠去除
        String proName = alias.replaceAll(STR_UNDERLINE, STR_EMPTY).toLowerCase();
        String lowerAlias = alias.toLowerCase();
        for (Field field : fields) {
            // 如果不去掉下划线的字段名称和属性名对的上，或者去除下杠的字段名如果和属性名对得上，就取这个SETTER方法
            //DTO中的字段名称,变成小写
            String fieldName = field.getName().toLowerCase();
            boolean match = fieldName.equals(lowerAlias)
                    || fieldName.equals(proName);
            if (match) {
                return propertyAccessStrategy.buildPropertyAccess(resultClass, field.getName()).getSetter();
            }
        }
        //没找到对应的字段
//        throw new RuntimeException("Could not find DB Column[" + alias + "] Java POJO property!");
        return null;
    }

    /**
     * 将Clob类型数据转换成字符串
     */
    private static String clobToString(Clob clob) {
        String reString = "";
        Reader is = null;
        try {
            is = clob.getCharacterStream();
            // 得到流
            BufferedReader br = new BufferedReader(is);
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                //执行循环将字符串全部取出赋值给StringBuilder由StringBuilder转成STRING
                sb.append(s);
            }
            reString = sb.toString();
        } catch (SQLException | IOException e) {
            log.error("Get Clob Content Failed", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("I/O Exception", e);
                }
            }
        }
        return reString;
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException( "aliases are different from what is cached; aliases="
                    + Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnToBean that = (ColumnToBean) o;
        return resultClass.equals(that.resultClass) && Arrays.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        int result = resultClass.hashCode();
        result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
        return result;
    }

}
```
自动填充创建人, 需要实现`AuditorAware`并且在启动类里增加`@EnableJpaAuditing`注解, `@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)`
注解设置jpa的默认实现类为自己重新的类.
```java
@Component
public class UserAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Objects.isNull(UserContextHelper.getCurrentUser()) ?
        Optional.empty() : Optional.of(UserContextHelper.getCurrentUser().getUsername());
    }
}
```
```java
@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);
    }

}
```
jpa处理枚举类型,所有枚举类型都继承 `BaseEnum`,然后通过实现`AttributeConverter`接口来自动转换枚举.
```java
@Getter
public enum TrueOrFalseEnum implements BaseEnum<Integer> {
    /**是否枚举转换*/
    TRUE(1, "是"),
    FAILURE(0, "否");
    private final Integer code;
    private final String text;

    TrueOrFalseEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getText() {
        return this.text;
    }
}
```
```java
@Converter(autoApply = true)
public class TrueOrFalseConvert implements AttributeConverter<TrueOrFalseEnum, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TrueOrFalseEnum attribute) {
        return Objects.isNull(attribute) ? null : attribute.getCode();
    }

    @Override
    public TrueOrFalseEnum convertToEntityAttribute(Integer dbData) {
        return Objects.isNull(dbData) ? null : BaseEnum.findByCode(TrueOrFalseEnum.class, dbData);
    }
}
```
到此jpa整合基本完成了,后续会继续对jpa查询进行封装,等用到的时候在处理.
