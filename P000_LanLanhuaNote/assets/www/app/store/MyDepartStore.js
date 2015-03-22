/*
 * File: app/store/MyDepartStore.js
 *
 * This file was generated by Sencha Architect version 3.0.4.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Sencha Touch 2.2.x library, under independent license.
 * License of Sencha Architect does not include license for Sencha Touch 2.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('MyApp.store.MyDepartStore', {
    extend: 'Ext.data.Store',
    alias: 'store.myDepartStore',

    requires: [
        'MyApp.model.MyDepartModel',
        'Ext.data.proxy.JsonP',
        'Ext.data.reader.Json'
    ],

    config: {
        autoLoad: true,
        model: 'MyApp.model.MyDepartModel',
        pageSize: 30,
        storeId: 'MyDepartStore',
        proxy: {
            type: 'jsonp',
            url: 'http://211.142.200.10:8080/IMPMobileService.ashx?action=getunderdept',
            reader: {
                type: 'json',
                rootProperty: 'deptmsg'
            }
        }
    }
});