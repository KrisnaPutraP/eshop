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


