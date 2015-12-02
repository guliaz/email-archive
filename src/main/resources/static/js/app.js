'use strict';

var myApp = angular.module('myApp', [
    'emailAppServices',
    'emailFilters',
    'ngRoute',
    'AppControllers'
]);

myApp.config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
            when('/emails', {
                templateUrl: 'partials/emails.html',
                controller: 'EmailListCtrl'
            }).when('/cap', {
                templateUrl: 'partials/cap.html',
                controller: 'CapController'
            }).otherwise({
                redirectTo: '/emails'
            });
        }
    ]
);