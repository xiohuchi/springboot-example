package com.sample.service;

import com.sample.domain.City;

public interface CityService {
    City findCityById(Long id);

    void saveCity(City city);

    void updateCity(City city);

    void deleteCity(Long id);
}
