package Exercise6.pages;



import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CRUDProductPage extends BasePage {

    // URL paths
    private static final String PRODUCTS_URL = "/products";

    // Locators for Product List
    private final By addProductBtn = By.cssSelector("button[data-testid='add-product-btn']");
    private final By searchInput = By.cssSelector("input[placeholder*='Search']");
    private final By productTable = By.cssSelector("table[data-testid='product-table']");
    private final By productRows = By.cssSelector("table tbody tr");
    private final By loadingSpinner = By.cssSelector(".loading-spinner");

    // Locators for Product Form (Create/Edit Modal or Page)
    private final By productNameInput = By.id("productName");
    private final By priceInput = By.id("price");
    private final By costPriceInput = By.id("costPrice");
    private final By categoryInput = By.id("category");
    private final By descriptionInput = By.id("description");
    private final By activeCheckbox = By.id("active");
    private final By submitBtn = By.cssSelector("button[type='submit']");
    private final By cancelBtn = By.cssSelector("button[data-testid='cancel-btn']");

    // Locators for validation messages
    private final By errorMessage = By.cssSelector(".error-message, .alert-error");
    private final By successMessage = By.cssSelector(".success-message, .alert-success");
    private final By fieldError = By.cssSelector(".field-error, [role='alert']");

    // Locators for action buttons in table
    private final String editBtnTemplate = "//tr[contains(., '%s')]//button[@data-testid='edit-btn']";
    private final String deleteBtnTemplate = "//tr[contains(., '%s')]//button[@data-testid='delete-btn']";
    private final String toggleBtnTemplate = "//tr[contains(., '%s')]//button[@data-testid='toggle-status-btn']";

    // Confirm dialog
    private final By confirmDeleteBtn = By.cssSelector("button[data-testid='confirm-delete']");
    private final By cancelDeleteBtn = By.cssSelector("button[data-testid='cancel-delete']");

    public CRUDProductPage(WebDriver driver) {
        super(driver);
    }

    // ==================== NAVIGATION ====================

    public CRUDProductPage navigate() {
        driver.get(BASE_URL + PRODUCTS_URL);
        waitForPageLoad();
        return this;
    }

    private void waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
        } catch (Exception e) {
            // Loading spinner might not appear, continue
        }
        waitForVisibility(productTable);
    }

    // ==================== SEARCH & FILTER ====================

    public CRUDProductPage searchProduct(String keyword) {
        type(searchInput, keyword);
        // Wait for search results to update
        try {
            Thread.sleep(500); // Wait for debounce
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public int getProductCount() {
        List<WebElement> rows = driver.findElements(productRows);
        return rows.size();
    }

    public boolean isProductDisplayed(String productName) {
        By productLocator = By.xpath("//tr[contains(., '" + productName + "')]");
        try {
            return driver.findElement(productLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== CREATE PRODUCT ====================

    public CRUDProductPage clickAddProduct() {
        click(addProductBtn);
        waitForVisibility(productNameInput);
        return this;
    }

    public CRUDProductPage fillProductForm(String name, String price, String costPrice,
                                           String category, String description, boolean active) {
        type(productNameInput, name);
        type(priceInput, price);

        if (costPrice != null && !costPrice.isEmpty()) {
            type(costPriceInput, costPrice);
        }

        if (category != null && !category.isEmpty()) {
            type(categoryInput, category);
        }

        if (description != null && !description.isEmpty()) {
            type(descriptionInput, description);
        }

        // Handle checkbox
        WebElement checkbox = driver.findElement(activeCheckbox);
        if (checkbox.isSelected() != active) {
            checkbox.click();
        }

        return this;
    }

    public CRUDProductPage submitForm() {
        click(submitBtn);
        return this;
    }

    public CRUDProductPage cancelForm() {
        click(cancelBtn);
        return this;
    }

    // ==================== UPDATE PRODUCT ====================

    public CRUDProductPage clickEditProduct(String productName) {
        By editBtn = By.xpath(String.format(editBtnTemplate, productName));
        scrollIntoView(editBtn);
        click(editBtn);
        waitForVisibility(productNameInput);
        return this;
    }

    public CRUDProductPage updateProductName(String newName) {
        WebElement nameField = driver.findElement(productNameInput);
        nameField.clear();
        nameField.sendKeys(newName);
        return this;
    }

    public CRUDProductPage updatePrice(String newPrice) {
        WebElement priceField = driver.findElement(priceInput);
        priceField.clear();
        priceField.sendKeys(newPrice);
        return this;
    }

    // ==================== DELETE PRODUCT ====================

    public CRUDProductPage clickDeleteProduct(String productName) {
        By deleteBtn = By.xpath(String.format(deleteBtnTemplate, productName));
        scrollIntoView(deleteBtn);
        click(deleteBtn);
        // Wait for confirmation dialog
        waitForVisibility(confirmDeleteBtn);
        return this;
    }

    public CRUDProductPage confirmDelete() {
        click(confirmDeleteBtn);
        return this;
    }

    public CRUDProductPage cancelDelete() {
        click(cancelDeleteBtn);
        return this;
    }

    // ==================== TOGGLE STATUS ====================

    public CRUDProductPage toggleProductStatus(String productName) {
        By toggleBtn = By.xpath(String.format(toggleBtnTemplate, productName));
        scrollIntoView(toggleBtn);
        click(toggleBtn);
        return this;
    }

    public String getProductStatus(String productName) {
        By statusLocator = By.xpath("//tr[contains(., '" + productName + "')]//span[@data-testid='status']");
        return waitForVisibility(statusLocator).getText();
    }

    // ==================== VALIDATION & MESSAGES ====================

    public String getSuccessMessage() {
        try {
            return waitForVisibility(successMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getErrorMessage() {
        try {
            return waitForVisibility(errorMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getFieldError() {
        try {
            return waitForVisibility(fieldError).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean hasValidationError() {
        try {
            return driver.findElement(fieldError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== ALERT HANDLING ====================

    public String acceptAlertAndGetText() {
        if (isAlertPresent()) {
            Alert alert = switchToAlert();
            String text = alert.getText();
            alert.accept();
            return text;
        }
        return null;
    }

    public String dismissAlertAndGetText() {
        if (isAlertPresent()) {
            Alert alert = switchToAlert();
            String text = alert.getText();
            alert.dismiss();
            return text;
        }
        return null;
    }

    // ==================== TABLE UTILITIES ====================

    public String getProductPrice(String productName) {
        By priceLocator = By.xpath("//tr[contains(., '" + productName + "')]//td[@data-testid='price']");
        return waitForVisibility(priceLocator).getText();
    }

    public String getProductCategory(String productName) {
        By categoryLocator = By.xpath("//tr[contains(., '" + productName + "')]//td[@data-testid='category']");
        return waitForVisibility(categoryLocator).getText();
    }

    public boolean isTableEmpty() {
        try {
            By emptyMessage = By.cssSelector(".empty-message, .no-data");
            return driver.findElement(emptyMessage).isDisplayed();
        } catch (Exception e) {
            return getProductCount() == 0;
        }
    }

    // ==================== FORM VALIDATION ====================

    public String getProductNameValue() {
        return driver.findElement(productNameInput).getAttribute("value");
    }

    public String getPriceValue() {
        return driver.findElement(priceInput).getAttribute("value");
    }

    public String getCostPriceValue() {
        return driver.findElement(costPriceInput).getAttribute("value");
    }

    public boolean isActiveChecked() {
        return driver.findElement(activeCheckbox).isSelected();
    }

    public boolean isSubmitButtonEnabled() {
        return driver.findElement(submitBtn).isEnabled();
    }
}