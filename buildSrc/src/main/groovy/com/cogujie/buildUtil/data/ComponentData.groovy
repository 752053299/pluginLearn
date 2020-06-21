package com.cogujie.buildUtil.data

import groovy.transform.ToString

/**
 * Created by wangzhi on 17/2/15.
 */
@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
public class ComponentData {
    public Collection<String> activities
    public Collection<String> services
    public Collection<String> receivers
    public Collection<String> providers

    ComponentData() {
        this.activities = new HashSet<>()
        this.services = new HashSet<>()
        this.receivers = new HashSet<>()
        this.providers = new HashSet<>()
    }

    ComponentData(Collection<String> activities, Collection<String> services, Collection<String> receivers, Collection<String> providers) {
        this.activities = activities
        this.services = services
        this.receivers = receivers
        this.providers = providers
    }
}
