# Mybatis DS Generator: 如何更优雅的使用Mybatis

标签（空格分隔）：Mybatis

---

为了更优雅的使用Mybatis，本文尝试在mybatis genrator的基础上进行无侵入式的扩展，用于支持Mybatis Dynamic SQL。扩展后的项目叫做Mybatis3 Dynamic SQL Generator，简称为Mybatis DS Generator，用于自动生成Dynamic SQL风格的Model
和Mapper类，支持多表一对一和一对多的关联关系，支持枚举或自定义类型，支持泛型，支持lombok注解，通过引入Maven依赖来使用，简单方便，有完整使用示例和最佳实践指南。项目地址：
[mybatis ds generator项目的github地址](https://github.com/yuchengxin/mybatis-ds-generator)

如果对Mybatis Dynamic SQL原理有兴趣可以跳转至这篇文章了解：[Mybatis Dynamic SQL原理解析]

# 一、背景
MyBatis是一款优秀的持久层框架，它支持自定义SQL、存储过程以及高级映射。使用MyBatis免除了几乎所有的JDBC代码、参数设置以及获取结果集的工作，并且MyBatis支持使用简单的XML或注解来配置、映射原生信息，将接口和Java的POJO(Plain Old Java Objects,普通Java对象)映射成数据库中的记录。极大的灵活性和强大的功能让Mybatis成为Java持久框架的事实标准。
mybatis官网：[Mybatis官网]

然而使用Mybatis依然无法避免模板式的枯燥的重复的开发，一般而言使用Mybatis的开发步骤就是定义关系模型（关系表），根据关系模型创建java的实体类（Pojo），然后在xml格式的mapper文件中通过半自动化的sql语句定义增删改查等具体的业务逻辑。这些都是一些固定的、重复的工作，而且xml格式的mapper文件很容易出错，相信手写过mapper的开发者都深有体会。

## 1.1 Mybatis Generator
Mybatis官方也深知此弊端，所以官方在Mybatis项目之外还提供了另外一个项目，叫做Mybatis Generator，MyBatis Generator(MBG)是MyBatis的代码生成器，可以按照关系表自动生成Mapper和XML映射文件，还会生成对应的Example类，做为条件对像查询，避免了写复杂SQL的工作，简化了复杂业务逻辑的开发。缺点也很明显，**Mybatis Generator只能对单表生成基本的CRUD，不能处理多表的关联关系**，但是关系模型中一对一、一对多是常见的关系类型，如果无法支持这两种关系类型，基本也就告别了生产环境。另外Generator功能单一，不支持带枚举的自定义类型、分页等常见功能，如果要使用需要自定义插件才行。使用过的人会发现，mybatis generator只能解决一点点的问题，大部分复杂的逻辑仍然要自己实现，几乎无法直接用于生产。
Mybatis Generator官网：[Mybatis Generator官网]

## 1.2 Mybatis Plus
在这之上又有了国人开源的Mybatis Plus项目，这个项目的初始目的初衷就是增强Mybatis和Mybatis Generator的功能，提高开发效率，但是又对Mybatis本身没有侵入性。这个项目是一个很优秀的项目，支持自定义模板的代码生成、物理分页、性能分析等各种功能，并且自定义了一套条件构造器，可以通过类似Lambda的方式来使用这些条件构造器（让用户能够使用java语言书写复杂sql）。需要注意的是Mybatis Plus默认了Mybatis的单表模型概念，只会对单表生成各种简单或复杂的CRUD操作，对于多表关联关系依然不支持自动生成。

就像Mybatis Plus宣称的那样，Mybatis Plus没有对Mybatis进修改，其底层仍然是Mybatis的通用使用方式，即通过xml格式的Mapper文件来对关系表和java对象进行映射，至少到目前版本（截至2021年1月份最新版本为3.4.2），Mybatis Plus没有集成新的Mybatis Dynamic SQL的计划和动向，事实上Mybatis Dynamic SQL的目的也是让用户使用java lambda的方式来写sql，而Mybatis Plus已经定义了一套自己的条件构造器规则，用于使用lambda语句来写sql，实际上是走了两条不一样的道路。个人认为Mybatis Plus不会也没必要抛弃自己的条件构造器，所以除非另起炉灶，个人揣测Mybatis Plus不会基于Mybatis Dynamic SQL进行强化。

## 1.3 Mybatis Dynamic SQL
传统Mybatis的使用方式是使用xml格式的mapper文件定义半结构化的sql来实现具体的业务；后来Mybatis推出了注解的方式，可以在注解上直接书写sql模板。xml文件使用起来最为灵活，能够定义极为复杂的业务；注解使用起来方便，更符合java程序员的习惯，但是对复杂sql支持并不友好。

这两种方式都是直接书写sql模板的，从风格上来说是两种语言的混合，那有没有一种既符合java程序员编码习惯，又支持复杂sql能够适应各种业务，并且代码清晰风格统一的使用方式呢？答案是还真有，Mybatis官方推出了Mybatis Dynamic SQL，需要注意的是此处的Mybatis Dynamic SQL并不是指Mybatis的动态sql能力，而是Mybatis的一个独立的项目，官网地址为：[Mybatis Dynamic Sql官网]

Mybatis Dynamic SQL这个项目从名字也可以看出是对Mybatis动态sql的极致利用和支持，实际上是对动态sql能力的类sql语言建模，通俗一点来说就是用java语言来模拟sql语言的书写方式，从而避免使用xml格式的mapper文件书写半结构化的sql，或者在注解上书写sql。可能描述起来依然抽象，但是举个例子就很直观了, 比如要实现如下的sql：
```sql
select id, animal_name, body_weight, brain_weight from animal_data where id in (1,5,7) and body_weight is between 1.0 and 3.0 order by id desc, body_weight;
```
使用xml的方式，需要在Mapper中进行定义：
```xml
<select id="getAnimalData" resultMap="AnimalDataResult">
        SELECT
            id,
            animal_name,
            body_weight,
            brain_weight
        FROM
            animal_data
        WHERE id IN
            <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
                #{item}
            </foreach>
        AND body_weight BETWEEN
            #{minWeight} AND #{maxWeight}
        ORDER BY
            id DESC, body_weight
</select>
```
而使用Mybatis Dynamic SQL的书写风格如下： 
```java
public List<AnimalData> getAnimalData(List<Long> idList, double minWeight, double maxWeight) {
    SelectStatementProvider selectStatement = select(id, animalName, bodyWeight, brainWeight)
        .from(animalData)
        .where(id, isIn(idList))
        .and(bodyWeight, isBetween(minWeight).and(maxWeight))
        .orderBy(id.descending(), bodyWeight)
        .build()
        .render(RenderingStrategies.MYBATIS3);
    return mapper.selectMany(selectStatement);
}

...

// mapper接口中的方法签名
@SelectProvider(type=SqlProviderAdapter.class, method="select")
@ResultMap("AnimalDataResult")
List<AnimalData> selectMany(SelectStatementProvider selectStatement);
```
可以看到Mybatis Dynamic SQL的书写风格非常类似SQL语言，而且底层不再依赖Mapper文件中定义的通用查询sql（mybatis plus的条件构造器需要依赖），也就是说用户书写的java代码将是sql语句的直接定义和直接执行，通过java语句即可直接书写复杂的SQL语句。这就是Mybatis Dynamic SQL的强大之处，也是Mybatis官方推荐的Mybatis新的使用方式。

关于Mybatis Dynamic SQL更多原理性的阐释，请跳转：[Mybatis Dynamic SQL原理解析]

为了支持Mybatis Dynamic Sql，**mybatis官方扩展了原有的mybatis generator**，只要将配置文件中的targetRuntime指定为MyBatis3DynamicSql即可生成DynamicSql风格的Model和Mapper文件，如下：
```xml
<context id="MysqlContext" defaultModelType="flat" targetRuntime="MyBatis3DynamicSql">
```
生成的这些Model和Mapper都是java类，没有xml的半格式化mapper映射文件。**这种方式生成的mapper天然支持limit的物理分页，以及批量插入，但是依然不支持包含枚举的自定义类型，也不支持自动按join关系生成Model和Mapper**。


# 二、Mybatis3 DS Generator

背景介绍概略描述了mybatis generator、mybatis plus、 mybatis dynamic sql的概念、功能和优缺点，用一句话总结就是：**mybatis generator是官方工具，能够使用Mybatis Dynamic SQL，但是功能单一，不支持多表关联关系，不能满足实际需求；mybatis plus功能强大，但是已经和mybatis dynamic sql分道扬镳，同样也不支持多表关联关系**

这里要重点强调一下，上面说“不支持多表关联关系”并不是指Mybatis不支持多表关联关系，Mybatis是支持的。如果使用xml，用户可以在xml配置文件中直接书写join的sql，然后在ResultSet中使用association和collection定义一对一和一对多的关联关系；如果使用注解，Mybatis也有@One和@More注解用于定义一对一和一对多的关联关系，这种方式实际是执行了多个sql，并将结果合并成最终的结果（和join的sql语句直接查询出最终结果有所区别）。
上面说的“不支持多表关联关系”指的是Mybatis Generator和Mybatis Plus不能自动生成多表关联关系的Model、Mapper类或相关配置文件。

如果你刚好想使用mybatis dynamic sql，你的关系表设计又刚好有关联关系，你希望能根据多表关联关系自动生成Mapper、Model类，还支持自定义类型，支持lombok等周边功能，那么你需要的可能正是本项目：Mybatis3 Dynamic SQL Generator(之后简写为**Mybatis DS Generator**)。Mybatis DS Generator基于插件的形式对Mybatis Generator进行扩展，对Mybatis和Mybatis Generator都是零侵入。

## 2.1 Mybatis DS Generator功能清单
 
### 2.1.1 自定义一对一、一对多的多表关联关系
Myabtis DS Generator最为主要的功能就是对join关系进行了支持，也就是
A join B on A.col1 = B.col2 join C on A.col3 = C.col4 ...这种场景, 理论上只受到join语句本身的限制，没有表数量的限制，但是参与join的表越多sql执行效率越低，一般情况下也就两到三张表进行join，如果过多地表做join可能得考虑是否是设计出现了问题。使用方式为：
先在mybatis-generator-config.xml中增加Join方法插件：
```xml
<plugin type="com.catyee.generator.plugin.JoinMethodPlugin"/>
```
然后在ds-generator-config.xml中定义join关系：
```xml
<joinConfig targetPackage="com.catyee.mybatis.example.mapper" targetProject="src/main/resources">
    <joinEntry leftTable="exam_class_grade">
        <joinTarget rightTable="exam_student" property="students" leftTableColumn="id" rightTableColumn="grade_id" joinType="MORE"/>
        <joinTarget rightTable="exam_teacher" property="regulator" leftTableColumn="regulator_id" rightTableColumn="id" joinType="ONE"/>
    </joinEntry>
</joinConfig>
```
以上定义的join关系对应的sql为：
```sql
select ... from exam_class_grade left join exam_student on exam_class_grade.id = exam_student.grade_id left join on exam_class_grade.regulator_id on exam_teacher.id
```
Mybatis DS Generator以一个补充的xml配置文件进行配置(即上文提到的ds-generator-config.xml)，Mybatis Generator自身也有一个xml格式的配置文件(即上文提到的mybatis-generator-config.xml)，所以总共有两个配置文件，这两个配置文件名称可自定义，这里只是举例说明。这两个配置文件要进行区分。

注意：暂不支持多级关联关系，比如A和B是一对多的关联关系，B和C又是一对多的关联关系，这种场景暂不支持。

### 2.1.2 自定义类型，并支持泛型
Mybatis Generator可以在table标签下通过columnOverride的子标签对特定列进行设置，从而配置特定的java类型以及类型handler，但是无法处理泛型，Myabtis DS Generator对泛型进行了支持，使用方式如下：
先在mybatis-generator-config.xml中使用javaTypeResolver：
```xml
<javaTypeResolver type="com.catyee.generator.resolver.JavaTypeCustomResolver">
    <property name="forceBigDecimals" value="false"/>
    <property name="useJSR310Types" value="true"/>
</javaTypeResolver>
```
然后在ds-generator-config.xml的customTypeConfig标签下增加自定义类型的配置：
```xml
<!--枚举类型-->
<customType columnName="grade_type" javaType="com.catyee.mybatis.example.custom.entity.GradeType"/>

<!--List<String>类型-->
<customType columnName="tech_courses" javaType="java.util.List" typeHandler="com.catyee.mybatis.example.custom.handler.StringListHandler">
    <genericType javaType="String"/>
</customType>

<!--Map<String, Integer>类型-->
<customType columnName="score" javaType="java.util.Map" typeHandler="com.catyee.mybatis.example.custom.handler.ScoreMapHandler">
    <genericType javaType="String"/>
    <genericType javaType="Integer"/>
</customType>
```
注意：暂不支持多层级的泛型，比如Map< Integer, List< String>> 

### 2.1.3 Lombok注解
 lombok可以有效的减少get、set等模板代码的编写，提升代码简洁性的利器，相关lombok的使用方式及原理可以自行查看资料。Myabtis DS Generator在Model类上集成了四个最为常用的lombok注解，这四个注解已经满足绝大多数的使用场景。
```java
@Data // 提供get、set、hashcode、equals、toString方法
@Builder // 提供Builder静态类，方便构建对象
@AllArgsConstructor // 提供全参构造器
@NoArgsConstructor // 提供无参构造器
```
在generator中使用lombok的方式：
在mybatis-generator-config.xml中增加lombok插件，如下：
```xml
<plugin type="com.catyee.generator.plugin.AnnotationPlugin"/>
```
### 2.1.4 去除多余注释
 Mybatis Gernerator会生成很多的多余的注释，比如这个类是怎么生成的，生成的时间，针对的是哪张表或者哪个字段，其实这些注释没有特别大的用处，反而影响代码的简洁度。Mybatis DS Generator中实现了无注释插件，可以生成没有注释的model和mapper，使用方式：
在mybatis-generator-config.xml中使用无注释插件：
```xml
<commentGenerator type="com.catyee.generator.comment.NonCommentGenerator"/>
```

### 2.1.5 扩展了多表操作的工具类
 新增了MyBatis3CustomUtils工具类，工具类中扩展了join、groupBy、查询语句count、分页等常见的操作，直接调用即可，更加方便地实现Dao层逻辑。
 新增了对insert ignore into语法的支持（需要数据库支持才能使用）

### 2.1.6 显示完整sql和执行时间
Mybatis在debug级别下也可以显示sql但是显示的是带有占位符的sql，填充占位符的参数是另外显示的，分成了两个部分，查看起来不直观、不友好。所以Mybatis DS Generator实现了显示完整sql的插件，并且能够显示sql执行时间，方便找出慢sql。该插件和上面列举的其它插件不一样，上面列举的其它插件都是mybatis generator的插件，该插件是mybatis的插件。使用方式:
在mybatis的配置文件(比如Mybatis-config.xml)中增加显示sql和执行时间的plugin:
```xml
<plugins>
    ...
    <plugin interceptor="com.catyee.generator.utils.ShowSqlInterceptor"/>
</plugins>
```
# 三、Mybatis3 DS Generator使用方式
使用Mybatis3 DS Generator需要在持久层模块和Mybatis Generator插件的依赖中都添加以下的Maven依赖：
```xml
<dependency>
    <groupId>com.catyee.mybatis</groupId>
    <artifactId>ds-generator-core</artifactId>
    <version>1.2.0</version>
</dependency>
```
Mybatis3 DS Generator依赖Java8，所以JDK至少要是1.8以上版本。Mybatis DS Generator主要依赖了如下第三方包：
```xml
<dependency>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-core</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4<version>
</dependency>
<dependency>
    <groupId>org.mybatis.dynamic-sql</groupId>
    <artifactId>mybatis-dynamic-sql</artifactId>
    <version>1.2.1</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId >
    <artifactId>h2</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```
其中**mybatis-generator-core必须为1.4.0版本及以上，mybatis-dynamic-sql必须1.1.4及以上**。

Mybatis3 DS Generator有自己的配置文件，在配置文件中可以进行关联关系和自定义类型的配置，需要在pom文件的properties标签中指明配置文件的地址，需要是绝对路径：
```xml
<mybatis.ds.generator.config>${basedir}/src/main/resources/generator/ds-generator-config.xml</mybatis.ds.generator.config>
```

# 四、Mybatis3 DS Generator最佳实践
**项目的Example模块是一个完整的持久层模块示例，通过这个示例给出Mybatis3 DS Generator最佳实践的一个范本**

Example模块以一个虚构场景进行关系建模，虚构场景如下：
一个补习班机构有多个补习班，补习班分为上午班、下午班以及全天班，每个班级有n个学生和一个班主任；班主任由一些资深的教师担任，也参与授课，学生来自国内外，收费标准不同；该机构还收集了学生每次考试的成绩，用于制定针对性的训练。

将虚构场景抽象为4张关系表，分别是班级表(exam_class_grade), 教师表(exam_teacher), 学生表（exam_student）和历史成绩表（exam_history_score）。
一个班级有n个学生和一个班主任，分别是一对多和一对一的关联关系。学生每考试一次生成一条成绩记录，所以学生和历史成绩也是一对多的关联关系，但是由于暂时不支持多级的关联关系，所以只配置班级与学生、老师的关联关系。

以下将从**持久层规划、配置、实现上讲解example的最佳实践**，希望通过最佳实践示例讲解清楚如何最优的使用Mybatis Generator和Mybatis DS Generator。

## 4.1 持久层规划
如图为持久层的规划，主要是目录结构的规划。如图model和mapper目录存放mybatis generator自动生成的model和mapper类；dao目录存放dao层的实现；custorm目录存放自定义的枚举、实体类、以及类型handler。

持久层中，Model类和Mapper类将由Mybatis Generator自动生成，**对于自动生成的类，不建议手动修改，因为下一次自动生成会覆盖掉手动修改的内容**。
Dao层是实现具体业务逻辑的部分，如果使用Mybatis DS Generator，**Dao层不会自动生成**。
市面也有可以自动生成Dao层的工具，但是Dao层的自动生成往往只能生成简单的增删改查方法，实际生产中业务逻辑都是比较复杂的，简单的增删改查往往不能满足需求，所以自动生成的意义不大，这也是Mybatis Generator不自动生成Dao层的理由，同时也是我们使用Mybatis Dynamic SQL的理由，Mybatis Dynamic SQL以Java的方式书写复杂sql，所见即所得，简化Dao层的开发，同时使逻辑更清晰，代码更易懂。
![持久层规划][1、持久层规划]

## 4.2 关系表定义
Mybatis Generator的原理是扫描数据库中的关系表，并根据配置生成对应的Model和Mapper类，所以做好持久层规划之后第二步就是定义关系表，最自然的想法是在本地数据库中先把关系表创建出来，然后让Mybatis Generator去连接数据库，这样做的坏处是每次修改关系表都需要去数据库中修改，同时也不利于团队配合，因为其他人不会连接你的数据库环境。

更好的方式是利用内存数据库H2，每次运行generator的时候先创建内存数据库，在内存数据库中自动创建出内存表，generator根据内存表创建Model和Mapper，运行结束销毁掉内存数据库，**如果未来业务扩展或者变动导致关系结构发生改变，每次都只需要修改关系表的定义文件，然后重新运行mybatis generator就可以重新生成model和mapper类，达到更新的目的**，可以减少修改出错的几率并简化开发，同时也利于团队配合和项目移植，还便于单元测试。

如图，schema目录下有两个关系表定义文件，一个是h2的一个是mysql的，h2的定义文件用于Mybatis Genrator自动生成以及单元测试；Mysql的定义文件则用于实际的运行。
![关系表定义][4、关系表定义]

## 4.3 Mybatis Generator和Mybatis DS Generator的配置
在pom文件中引入Mybatis DS Generator的依赖，进行如下配置：
```xml
<properties>
    <!--mybatis generator配置文件的位置-->
    <generator.config>${basedir}/src/main/resources//generator/mybatis-generator-config.xml</generator.config>
    <!--mybatis ds generator配置文件的位置-->
    <mybatis.ds.generator.config>${basedir}/src/main/resources/generator/ds-generator-config.xml</mybatis.ds.generator.config>
</properties>

...

<!--使用mybatis generator插件-->
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>${mybatis.generator.version}</version>
    <configuration>
        <verbose>true</verbose>
        <overwrite>true</overwrite>
        <contexts>MysqlContext</contexts>
        <configurationFile>${generator.config}</configurationFile>
        <outputDirectory>${basedir}/src/main/java</outputDirectory>
        <includeCompileDependencies>true</includeCompileDependencies>
        <includeAllDependencies>true</includeAllDependencies>
        <!--在开始生成Model和Mapper之前初始化h2数据库，以sql文件的方式初始化-->
        <sqlScript>${basedir}/src/main/resources/schema/table_init_in_h2.sql</sqlScript>
        <jdbcURL>jdbc:h2:mem:example;database_to_upper=false;MODE=MySQL;DB_CLOSE_DELAY=-1</jdbcURL>
        <jdbcDriver>org.h2.Driver</jdbcDriver>
        <jdbcUserId>sa</jdbcUserId>
        <jdbcPassword>123</jdbcPassword>
    </configuration>
    <dependencies>
        <!--引入h2数据库和Mybatis DS Generator的依赖-->
        <dependency>
            <groupId>com.h2database</groupId >
            <artifactId>h2</artifactId>
            <version>${h2.version}</version>
        </dependency>
        <dependency>
            <groupId>com.catyee.mybatis</groupId>
            <artifactId>ds-generator-core</artifactId>
            <version>${ds-generator.version}</version>
        </dependency>
    </dependencies>
</plugin>

```
在generator目录下创建Mybatis Generator和Mybatis DS Generator的配置文件：
![配置文件][2、配置文件]
具体内容请自行查看，都有详细的注释。

## 4.4 运行Mybatis Generator进行自动生成
进入example目录(配置了Mybatis Generator插件的pom文件所在的目录)，直接运行以下命令:
```shell 
mvn mybatis-generator:generate -Djavax.xml.accessExternalDTD=all
```
生成完的结果：
![生成结果][3、生成结果]

如果未来业务扩展或者变动导致关系结构发生改变，每次都只需要修改schema文件，然后重新调用命令就可以重新生成model和mapper类。

## 4.5 实现Dao层
以具有关联关系的ClassGrade类为例：
插入的时候由于对应多表，需要每张表单独插入，由于是多表操作，还要加上事务。插入的时候Mybatis会自动填充主键，注意插入的顺序。更新、删除和插入一样，需要注意是多表操作。
```java
@Transactional
public ClassGrade create(ClassGrade grade) {
    Teacher regulator = grade.getRegulator();
    if (regulator != null) {
        teacherMapper.insert(regulator);
        grade.setRegulatorId(regulator.getId());
    }
    gradeMapper.insert(grade);
    List<Student> students = grade.getStudents();
    if (students != null && !students.isEmpty()) {
        students.forEach(student -> student.setGradeId(grade.getId()));
        studentMapper.insertMultiple(students);
    }
    return grade;
}
```
查询的时候调用Mapper类的leftJoinSelectOne和leftJoinSelect方法，不用进行多表操作了。**注意如果使用join进行查询，在Mybatis中不要用limit进行物理分页（思考为什么）**
```java
// 查询join结果的时候要使用leftJoin对应的方法
// 另外由于join的特殊性，不能使用limit进行物理分页，否则可能得到错误的结果
public ClassGrade get(long id) {
    return gradeMapper.leftJoinSelectOne(c -> c.where(ClassGradeDynamicSqlSupport.id, isEqualTo(id))).orElse(null);
}

public List<ClassGrade> getWithGradeType(GradeType type) {
    return gradeMapper.leftJoinSelect(c -> c.where(ClassGradeDynamicSqlSupport.gradeType, isEqualTo(type)));
}
```
以下是一个更为灵活的实现sql示例，用于只更新表的一部分字段，该方法在HistoryScoreDao中可以看到：
```java
// 只更新history_score表的部分字段
public HistoryScore updateScore(long scoreId, Map<String, Integer> scores) {
    int totalScore = scores.values().stream().mapToInt(s -> s).sum();
    UpdateStatementProvider updateStatementProvider = UpdateDSL.update(historyScore)
            .set(score).equalTo(scores)
            .set(HistoryScoreDynamicSqlSupport.totalScore).equalTo(totalScore)
            .where(id, isEqualTo(scoreId))
            .build()
            .render(RenderingStrategies.MYBATIS3);
    mapper.update(updateStatementProvider);
    return mapper.selectByPrimaryKey(scoreId).orElse(null);
}
```
example模块中的custom目录下还有一个自定义Mapper和Model的使用示例，当需要对自动生成的Mapper和Model进行扩展的时候就可以进行自定义的扩展，组合使用，即保留灵活度，又能充分提高效率。更多使用方式可直接查看Dao层的源码，灵活使用Mybatis Dynamic SQL，几乎可以实现一切sql，Mybatis Dynamic SQL的使用方法可以去官网详细查看和学习。

## 4.6 编写单元测试
最后一步就是编写单元测试了，持久层的单元测试相对而言简单，可以做到覆盖率100%，实例中每个Dao层的方法都编写了单元测试，使用h2数据库进行测试，方便团队合作、项目移植或跑ci流程。进入example目录，直接运行maven clean test即可进行测试。

# 五、注意事项
1、暂不支持多层泛型
2、暂不支持多层join关联
3、注意h2和mysql的语法差异
4、Mybatis中对于join查询不能使用limit进行物理分页
5、注意依赖包的最低版本要求

联系方式：983052176@qq.com

[1、持久层规划]: https://s3.ax1x.com/2021/02/05/yGpj74.png
[2、配置文件]: https://s3.ax1x.com/2021/02/06/yYvPpt.png
[3、生成结果]: https://s3.ax1x.com/2021/02/06/yYvF6f.png
[4、关系表定义]: https://s3.ax1x.com/2021/02/06/yYvi1P.png

[Mybatis Dynamic SQL原理解析]: https://blog.csdn.net/zhongguoyu27/article/details/115833914
[Mybatis官网]: https://mybatis.org/mybatis-3
[Mybatis Generator官网]: http://mybatis.org/generator/
[Mybatis Dynamic Sql官网]: https://mybatis.org/mybatis-dynamic-sql/docs/introduction.html