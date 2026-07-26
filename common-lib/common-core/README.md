# common-core

Thư viện dùng chung cho các Spring Boot service, cung cấp:

- API success/error response.
- Exception và technical error code.
- Validation/global exception handling.
- Message tiếng Anh, tiếng Việt và application override.
- Spring Boot auto-configuration.

## Sử dụng

Khai báo dependency:

```xml
<dependency>
    <groupId>com.ecommerce</groupId>
    <artifactId>common-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Trả response thành công:

```java
return ApiResponse.success(product, "Product created");
```

Định nghĩa error code nghiệp vụ trong service:

```java
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND;

    public String code() {
        return name();
    }

    public HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public String messageKey() {
        return "product.error.not-found";
    }

    public String defaultMessage() {
        return "Product {0} was not found";
    }
}
```

Ném exception:

```java
throw new NotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND, productId);
```

Service có thể khai báo `product.error.not-found` trong `messages.properties` và
`messages_vi.properties`. Nếu không có message tương ứng, common-core sử dụng
`defaultMessage()`.

`GlobalExceptionHandler` được đăng ký tự động. Service có thể cung cấp bean
`CommonMessageResolver` hoặc `GlobalExceptionHandler` riêng để thay thế cấu hình mặc định.

## Kiểm tra

```powershell
.\mvnw.cmd -B -ntp clean verify
```
