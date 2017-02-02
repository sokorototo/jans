/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package org.xdi.service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.cache.EhCacheProvider;
import org.jboss.seam.log.Log;

/**
 * Provides operations with cache
 * 
 * @author Yuriy Movchan Date: 01.24.2012
 * @author Yuriy Zabrovarnyy Date: 02.02.2017
 */
@Scope(ScopeType.APPLICATION)
@Name("cacheService")
@AutoCreate
public class CacheService {

	@In(value = "memcachedProvider")
	private CacheProvider<?> cacheProvider;

    @Logger
    private Log log;

	public Object get(String region, String key) {
		if (cacheProvider == null) {
			return null;
		}
		
		return cacheProvider.get(region, key);
	}

	public void put(String region, String key, Object object) {
		if (cacheProvider != null) {
			cacheProvider.put(region, key, object);
		}
	}

	public void remove(String region, String key) {
		if (cacheProvider == null) {
			return;
		}
		
		cacheProvider.remove(region, key);
	}

	@Deprecated // todo we must not stick to ehcache specific classes ! Scheduled for removing!
	public void removeAll(String name) {
		if (cacheProvider instanceof EhCacheProvider) {
			((CacheManager) cacheProvider.getDelegate()).getCache(name).removeAll();
		} else {
			cacheProvider.clear(); // for non ehcache clear all cache (e.g. in memcache we don't have regions)
		}
	}

	public void clear() {
		if (cacheProvider != null) {
			cacheProvider.clear();
		}
	}

	@Deprecated // todo we must not stick to ehcache specific classes ! Scheduled for removing!
	public Cache getCacheRegion(String regionName) {
		Cache cache = null;
		if (cacheProvider instanceof EhCacheProvider) {
			CacheManager cacheManager = (CacheManager) cacheProvider.getDelegate();
			cache = cacheManager.getCache(regionName);
			if (cache == null) {
				log.error("Could not find configuration for region [" + regionName + "]; using defaults.");
			}
		}

		return cache;
	}

	public CacheProvider<?> getCacheProvider() {
		return cacheProvider;
	}

	public static CacheService instance() {
        return (CacheService) Component.getInstance(CacheService.class);
    }

}
