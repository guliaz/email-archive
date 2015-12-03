'use strict';

var emailAppServices = angular.module('emailAppServices', ['ngResource']);

emailAppServices.factory('Count', ['$resource',
    function ($resource) {
        return $resource('/emails/count', {});
    }]);

emailAppServices.factory('Db', ['$resource',
    function ($resource) {
        return $resource('/emails/list', {}, {
            query: {method: 'GET', isArray: true}
        });
    }]);

console.log("In services");