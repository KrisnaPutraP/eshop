# Module 1: Coding Standard

## Reflection 1

> You already implemented two new features using Spring Boot. Check again your source code and evaluate the coding standards that you have learned in this module. Write clean code principles and secure coding practices that have been applied to your code.  If you find any mistake in your source code, please explain how to improve your code. Please write your reflection inside the repository's README.md file.

Coding standards that I've implemented are:

### 1. Meaningful Names
I've always used descriptive and non-ambiguous variable, function, class, and argument names. Descriptive names is necessary to avoid confusion. I'm ensuring that no comments are needed to describe any of my variable, function, or class.
Example:

```java
 @PostMapping("/edit")
    public String editProductPost(@ModelAttribute Product product) {
        service.update(product);
        return "redirect:/product/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") String productId) {
        service.deleteById(productId);
        return "redirect:/product/list";
    }
```

### 2. Functions
I'm ensuring that all of my functions only do one single job. 

### 3. Comments
I'm avoiding comment usage for obvious stuff. For this particular module, I haven't written any comments because there are no complex logic or algorithms that I needed to explain. I only write TODO comments for pending work.

### 4. Objects and Data Structures
In order to follow OOP principles, I'm making sure that there are no unnecessary dependencies between variables by keeping some variables private. For this module, there are not many data structures that I can implement except List and Iterator. Additionaly, I've made `productData` final to avoid accidental reassignment.

```java
    private final List<Product> productData = new ArrayList<>();
```

### 5. Error Handling
In this module, I've added new dependency for input validation to make sure that the product name can't be empty and the product quantity must be at least 1. 

```
    dependencies {
        ...
        implementation("org.springframework.boot:spring-boot-starter-validation")
        ...
    }
```

Then, I use that dependency inside the model package:

```java
    package id.ac.ui.cs.advprog.eshop.model;
    
    import lombok.Getter;
    import lombok.Setter;
    
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Min;
    
    @Getter @Setter
    public class Product {
        private String productId;
    
        @NotBlank(message = "Product name is required")
        private String productName;
    
        @Min(value = 1, message = "Quantity must be at least 1")
        private int productQuantity;
    }
```

Also, I've made changes in controller package, particularly for creating and editing products:

```java
    @PostMapping("/create")
    public String createProductPost(@Valid @ModelAttribute Product product,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "createProduct";
        }
        service.create(product);
        return "redirect:list";
    }
```

## Reflection 2

> After writing the unit test, how do you feel? How many unit tests should be made in a class? How to make sure that our unit tests are enough to verify our program? It would be good if you learned about code coverage. Code coverage is a metric that can help you understand how much of your source is tested. If you have 100% code coverage, does that mean your code has no bugs or errors? 

After writing these unit tests, I feel more confident about my code's reliability. As I wrote more tests, I realized there were many edge cases I hadn't considered, like what happens with negative quantities or empty product names with only whitespaces. Each new test case helped me uncover potential issues I might have missed. 

As for how many unit tests should be in a class, I think it may vary. Of course, larger application projects should require more unit tests. But, for this particular module, instead of focusing on quantity, I tried to cover all the important scenarios. For example, in my ProductService tests, I needed tests for successful operations, edge cases, and error conditions.

Regarding code coverage, I've learned about it during this exercise. While it's a useful metric, I've realized that having 100% code coverage doesn't guarantee bug-free code. For instance, my tests might execute every line of code, but they might not test all possible combinations of inputs or scenarios. One example that I've encountered earlier, my product update unit tests had 100% coverage of the update method, but they initially missed empty names with only whitespaces. The code was fully "covered" but wasn't catching these invalid inputs. This taught me that code coverage is just one tool in ensuring code quality, not the ultimate goal.

> Suppose that after writing the CreateProductFunctionalTest.java along with the corresponding test case, you were asked to create another functional test suite that verifies the number of items in the product list. You decided to create a new Java class similar to the prior functional test suites with the same setup procedures and instance variables.
What do you think about the cleanliness of the code of the new functional test suite? Will the new code reduce the code quality? Identify the potential clean code issues, explain the reasons, and suggest possible improvements to make the code cleaner!


After writing the new functional test suite, I started to notice some issues in my test codebase. The most obvious issue is code duplication, because I'm literally copying the same setup procedures and instance variables from `CreateProductFunctionalTest.java` into my new test class. This definitely goes against the DRY (Don't Repeat Yourself) principle. For example, if I need to change how the setup works (like switching from Chrome to Firefox), I'd have to change it in multiple places. This makes maintenance a lot harder and increases the chance of introducing inconsistencies.

There are several ways to improve this:

1. Create a base test class:
```java
public abstract class BaseProductFunctionalTest {
    protected WebDriver driver;
    protected String baseUrl;
    
    @BeforeEach
    void setUp() {
        // Common setup code here
    }
    
    @AfterEach
    void tearDown() {
        // Common cleanup code here
    }
}
```

2. Then have our specific test classes inherit from it:
```java
public class CreateProductFunctionalTest extends BaseProductFunctionalTest {
    @Test
    void testCreateProduct() {
        // Test-specific code here
    }
}

public class ProductListFunctionalTest extends BaseProductFunctionalTest {
    @Test
    void testProductListCount() {
        // Test-specific code here
    }
}
```

Another approach is tog create test helper methods in a utility class for common operations like "createProduct" or "countProductList" that can be reused across different test classes.
This would make our test code much cleaner, easier to maintain, and more aligned with clean code principles. It also makes it easier to add new functional test suites in the future since they can just inherit the common setup code instead of duplicating it.

