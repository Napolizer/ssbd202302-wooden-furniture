package pl.lodz.p.it.ssbd2023.ssbd02.moz.service.impl;

import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.CLIENT;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.EMPLOYEE;
import static pl.lodz.p.it.ssbd2023.ssbd02.config.Role.SALES_REP;
import static pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState.CANCELLED;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.OptimisticLockException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Order;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.OrderedProduct;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.Product;
import pl.lodz.p.it.ssbd2023.ssbd02.entities.enums.OrderState;
import pl.lodz.p.it.ssbd2023.ssbd02.exceptions.ApplicationExceptionFactory;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.AccountServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.mok.service.api.MailServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.OrderFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.facade.api.ProductFacadeOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.OrderServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.moz.service.api.ProductServiceOperations;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.GenericServiceExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.language.MessageUtil;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.security.CryptHashUtils;
import pl.lodz.p.it.ssbd2023.ssbd02.utils.sharedmod.service.AbstractService;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericServiceExceptionsInterceptor.class,
    LoggerInterceptor.class
})
@DenyAll
public class OrderService extends AbstractService implements OrderServiceOperations {

  @Inject
  private OrderFacadeOperations orderFacade;
  @Inject
  private MailServiceOperations mailService;
  @Inject
  private AccountServiceOperations accountService;
  @Inject
  private ProductServiceOperations productService;
  @Inject
  private ProductFacadeOperations productFacade;

  @Override
  @RolesAllowed(CLIENT)
  public List<Order> findByAccountLogin(String login) {
    return orderFacade.findByAccountLogin(login);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findByState(OrderState orderState) {
    return orderFacade.findByState(orderState);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order create(Order order, String login, Map<Long, Integer> orderedProductsMap) {
    Double totalPrice = 0.0;
    List<OrderedProduct> orderedProducts = new ArrayList<>();

    Account account = accountService.getAccountByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    for (Long productId : orderedProductsMap.keySet()) {
      Product product = productService.find(productId)
          .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

      if (orderedProductsMap.get(productId) > product.getAmount()) {
        throw ApplicationExceptionFactory.createInvalidProductAmountException();
      }

      OrderedProduct orderedProduct = OrderedProduct.builder()
          .amount(orderedProductsMap.get(productId))
          .price(product.getPrice())
          .product(product)
          .order(order)
          .build();
      orderedProducts.add(orderedProduct);
      totalPrice += orderedProduct.getPrice() * orderedProductsMap.get(productId);

      if (product.getCreatedBy() != null) {
        if (product.getCreatedBy().getLogin().equals(login) && product.getUpdatedBy() == null) {
          throw ApplicationExceptionFactory.createProductCreatedByException();
        }
      }

      if (product.getUpdatedBy() != null) {
        if (product.getUpdatedBy().getLogin().equals(login)) {
          throw ApplicationExceptionFactory.createProductUpdatedByException();
        }
      }

      product.setAmount(product.getAmount() - orderedProduct.getAmount());
      product.setIsUpdatedBySystem(true);
      productFacade.update(product);
    }

    Address address = Address.builder()
            .country(account.getPerson().getAddress().getCountry())
            .city(account.getPerson().getAddress().getCity())
            .street(account.getPerson().getAddress().getStreet())
            .streetNumber(account.getPerson().getAddress().getStreetNumber())
            .postalCode(account.getPerson().getAddress().getPostalCode())
            .build();

    order.setRecipientFirstName(account.getPerson().getFirstName());
    order.setRecipientLastName(account.getPerson().getLastName());
    order.setDeliveryAddress(address);
    order.setTotalPrice(totalPrice);
    order.setAccount(account);
    order.setOrderedProducts(orderedProducts);
    order.setOrderState(OrderState.CREATED);
    return orderFacade.create(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order createWithGivenShippingData(Order order, String login, Map<Long, Integer> orderedProductsMap) {
    Double totalPrice = 0.0;
    List<OrderedProduct> orderedProducts = new ArrayList<>();

    for (Long productId : orderedProductsMap.keySet()) {
      Product product = productService.find(productId)
          .orElseThrow(ApplicationExceptionFactory::createProductNotFoundException);

      if (orderedProductsMap.get(productId) > product.getAmount()) {
        throw ApplicationExceptionFactory.createInvalidProductAmountException();
      }

      OrderedProduct orderedProduct = OrderedProduct.builder()
          .amount(orderedProductsMap.get(productId))
          .price(product.getPrice())
          .product(product)
          .order(order)
          .build();
      orderedProducts.add(orderedProduct);
      totalPrice += orderedProduct.getPrice() * orderedProductsMap.get(productId);

      //TODO edit products amount in database
      product.setAmount(product.getAmount() - orderedProduct.getAmount());
      productFacade.update(product);
      //TODO make impossible to create order for employee that changed these products recently
    }
    Account account = accountService.getAccountByLogin(login)
        .orElseThrow(ApplicationExceptionFactory::createAccountNotFoundException);

    order.setTotalPrice(totalPrice);
    order.setAccount(account);
    order.setOrderedProducts(orderedProducts);
    order.setOrderState(OrderState.CREATED);
    return orderFacade.create(order);
  }

  @Override
  public Order archive(Long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Order update(Long id, Order entity) {
    throw new UnsupportedOperationException();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Optional<Order> find(Long id) {
    return orderFacade.find(id);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAll() {
    return orderFacade.findAll();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllPresent() {
    return orderFacade.findAllPresent();
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public List<Order> findAllArchived() {
    return orderFacade.findAllArchived();
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order cancelOrder(Long id, String hash, String login) {
    List<Order> clientOrders = findByAccountLogin(login);
    Order order = Order.builder().build();
    for (Order clientOrder : clientOrders) {
      if (clientOrder.getId().equals(id)) {
        order = clientOrder;
      }
    }

    if (order == null) {
      throw ApplicationExceptionFactory.createOrderNotFoundException();
    }

    if (order.getOrderState().equals(OrderState.IN_DELIVERY)) {
      throw ApplicationExceptionFactory.createOrderAlreadyInDeliveryException();
    }

    if (order.getOrderState().equals(OrderState.DELIVERED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyDeliveredException();
    }

    if (order.getOrderState().equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyCancelledException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setOrderState(CANCELLED);
    return orderFacade.update(order);
  }


  @Override
  @RolesAllowed(EMPLOYEE)
  public Order cancelOrderAsEmployee(Long id, String hash) {
    Order order = find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);

    if (order.getOrderState().equals(OrderState.IN_DELIVERY)) {
      throw ApplicationExceptionFactory.createOrderAlreadyInDeliveryException();
    } else if (order.getOrderState().equals(OrderState.DELIVERED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyDeliveredException();
    } else if (order.getOrderState().equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createOrderAlreadyCancelledException();
    }

    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    order.setOrderState(CANCELLED);
    return orderFacade.update(order);
  }

  @Override
  @RolesAllowed(CLIENT)
  public Order observeOrder(Long id, String hash, String login) {
    List<Order> orders = findByAccountLogin(login);
    Order clientOrder = Order.builder().build();
    for (Order order : orders) {
      if (order.getId().equals(id)) {
        clientOrder = order;
        break;
      }
    }

    if (clientOrder == null) {
      throw ApplicationExceptionFactory.createOrderNotFoundException();
    }

    if (clientOrder.getObserved()) {
      throw ApplicationExceptionFactory.createOrderAlreadyObservedException();
    }

    if (!CryptHashUtils.verifyVersion(clientOrder.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }

    clientOrder.setObserved(true);
    return orderFacade.update(clientOrder);
  }

  @Override
  @RolesAllowed(EMPLOYEE)
  public Order changeOrderState(Long id, OrderState state, String hash) {
    Order order = orderFacade.find(id).orElseThrow(ApplicationExceptionFactory::createOrderNotFoundException);
    if (state.ordinal() <= order.getOrderState().ordinal() || state.equals(CANCELLED)) {
      throw ApplicationExceptionFactory.createInvalidOrderStateTransitionException();
    }
    if (!CryptHashUtils.verifyVersion(order.getSumOfVersions(), hash)) {
      throw new OptimisticLockException();
    }
    String oldOrderState = order.getOrderState().name();
    order.setOrderState(state);
    String newOrderState = order.getOrderState().name();
    StringBuilder orderedProducts = new StringBuilder();
    for (OrderedProduct orderedProduct : order.getOrderedProducts()) {
      orderedProducts.append(orderedProduct.getProduct().getProductGroup().getName()).append("\n");
    }
    order = orderFacade.update(order);

    if (order.getObserved()) {
      mailService.sendEmailAboutOrderStateChange(order.getAccount().getEmail(), order.getAccount().getLocale(),
          String.valueOf(orderedProducts), oldOrderState, newOrderState);
    }
    return order;
  }

  @Override
  @RolesAllowed(SALES_REP)
  public byte[] generateReport(LocalDateTime startDate, LocalDateTime endDate, String locale) {
    if (startDate.isAfter(endDate)) {
      throw ApplicationExceptionFactory.createInvalidDateException();
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    List<Object[]> data = orderFacade.findOrderStatsForReport(startDate, endDate);
    String[] headers = {MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_HEADER1),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_HEADER2),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_HEADER3),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_HEADER4),
            MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_HEADER5)};
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Sheet1");
      Row titleRow = sheet.createRow(0);
      Cell titleCell = titleRow.createCell(0);
      titleCell.setCellValue(MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_TITLE1)
              + startDate.toLocalDate().format(formatter)
              + MessageUtil.getMessage(locale, MessageUtil.MessageKey.REPORT_TITLE2)
              + endDate.toLocalDate().format(formatter));

      CellRangeAddress titleRange = new CellRangeAddress(0, 2, 0, headers.length - 1);
      sheet.addMergedRegion(titleRange);
      CellStyle titleStyle = workbook.createCellStyle();
      titleStyle.setAlignment(HorizontalAlignment.CENTER);
      Font titleFont = workbook.createFont();
      titleFont.setBold(true);
      titleFont.setFontHeightInPoints((short) 18);
      titleStyle.setFont(titleFont);
      titleCell.setCellStyle(titleStyle);

      Row headerRow = sheet.createRow(3);
      CellStyle headerStyle = workbook.createCellStyle();
      Font headerFont = workbook.createFont();
      headerFont.setFontHeightInPoints((short) 13);
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);
      headerStyle.setBorderBottom(BorderStyle.THIN);
      headerStyle.setBorderTop(BorderStyle.THIN);
      headerStyle.setBorderLeft(BorderStyle.THIN);
      headerStyle.setBorderRight(BorderStyle.THIN);

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      for (int i = 0; i < data.size(); i++) {
        Row dataRow = sheet.createRow(i + 4);
        for (int j = 0; j < data.get(i).length; j++) {
          Cell cell = dataRow.createCell(j);
          Object value = data.get(i)[j];
          CellStyle cellStyle = workbook.createCellStyle();
          cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
          cellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());

          if (value instanceof Long convertedValue) {
            cell.setCellValue(convertedValue);
            if (j == 2) {
              if (convertedValue == 0) {
                cellStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
              } else if (convertedValue <= 3) {
                cellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
              } else {
                cellStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
              }
            }
          } else if (value instanceof String convertedValue) {
            cell.setCellValue(convertedValue);
          } else if (value instanceof Double convertedValue) {
            cell.setCellValue(convertedValue);
            if (j == 4) {
              cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00 PLN"));
            }
          }
          cellStyle.setBorderBottom(BorderStyle.THIN);
          cellStyle.setBorderTop(BorderStyle.THIN);
          cellStyle.setBorderLeft(BorderStyle.THIN);
          cellStyle.setBorderRight(BorderStyle.THIN);
          cell.setCellStyle(cellStyle);
        }
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        workbook.write(outputStream);
        return outputStream.toByteArray();
      } catch (IOException e) {
        throw ApplicationExceptionFactory.createUnknownErrorException(e);
      }
    } catch (IOException e) {
      throw ApplicationExceptionFactory.createUnknownErrorException(e);
    }
  }

  @Override
  public List<Order> findWithFilters(Double orderPrice, Integer orderSize, boolean isCompany) {
    throw new UnsupportedOperationException();
  }

}
