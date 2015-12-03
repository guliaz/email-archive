'use strict';

var AppControllers = angular.module('AppControllers', []);

AppControllers.controller('CapController', ['$scope',
    function ($scope) {
        $scope.friends = [
            {name: 'Vikram', age: 25, gender: 'boy'}
        ];
        $scope.friends.forEach(function (a) {
            console.log(a.name);
        });
    }
]);

AppControllers.controller('EmailListCtrl', ['$scope', 'Db', 'Count',
    function ($scope, Db, Count) {

        $scope.orderProp = "message_id",
            $scope.page = 1,
            $scope.numPerPage = 100,
            $scope.maxSize = 10,
            $scope.pageActive = "active",
            $scope.pageNotActive = "",
            $scope.linkDisabled = "disabled";


        var numEmails = Count.get();
        $scope.totalEmails = numEmails;
        $scope.emails = Db.query({page: $scope.page, number: $scope.numPerPage});

        $scope.showHide = function (email) {
            console.log(email)
            if (email.show === true) {
                email.show = !email.show;
                if (email.showBody === true)
                    email.showBody = !email.showBody;
            }
            else {
                email.show = !email.show;
            }
        };

        $scope.showText = function (email) {
            email.showBody = !email.showBody;
        };

        $scope.getPage = function (num) {
            $scope.page = num;
            $scope.emails = Db.query({page: num, number: $scope.numPerPage});
        };

        $scope.getNumber = function (num) {
            if (num != null && num != 0 && $scope.numPerPage != null && $scope.numPerPage != 0) {
                var val = Math.round(num / $scope.numPerPage);
                return new Array(val);
            } else return new Array(0);
        };

        $scope.updatePerPage = function (newPerPage) {
            $scope.numPerPage = newPerPage;
            $scope.page = 1;
            $scope.emails = Db.query({page: $scope.page, number: newPerPage});
        };

        $scope.downloadFile = function (fileName) {
            Db.query({file_name: fileName});
        }
    }])
;