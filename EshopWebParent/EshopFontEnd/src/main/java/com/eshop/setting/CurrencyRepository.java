package com.eshop.setting;

import org.springframework.data.repository.CrudRepository;

import com.eshop.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>  {

}
