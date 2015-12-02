'use strict';

var emailAppServices = angular.module('emailAppServices', ['ngResource']);

emailAppServices.factory('Count', ['$resource',
    function ($resource) {
        return $resource('/api/db/?count=count', {});
    }]);

emailAppServices.factory('Db', ['$resource',
    function ($resource) {
        return $resource('/api/db/', {}, {
            query: {method: 'GET', isArray: true}
        });
    }]);

console.log("In services");