package com.sample.service.impl;

import com.sample.dao.CityDao;
import com.sample.domain.City;
import com.sample.service.CityAnnotationService;
import com.sample.service.CityService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CityServiceAnnotationImpl implements CityAnnotationService {

    @Resource
    private CityDao cityDao;

    @Cacheable(value = "findCityById")
    @Override
    public City findCityById(Long id) {
        return cityDao.selectById(id);
    }

    @CachePut(value = "saveCity")
    @Override
    public void saveCity(City city) {
        cityDao.insert(city);
    }

    @CachePut(value = "updateCity")
    @Override
    public void updateCity(City city) {
        int ret = cityDao.update(city);
    }

    @CacheEvict(value = "deleteCity")
    @Override
    public void deleteCity(Long id) {
        int ret = cityDao.deleteById(id);
    }
}