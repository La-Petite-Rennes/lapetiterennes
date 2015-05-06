'use strict';

angular.module('membershipApp')
    .controller('TestDetailController', function ($scope, $stateParams, Test, Adhesion) {
        $scope.test = {};
        $scope.load = function (id) {
            Test.get({id: id}, function(result) {
              $scope.test = result;
            });
        };
        $scope.load($stateParams.id);
    });
