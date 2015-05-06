'use strict';

angular.module('membershipApp')
    .controller('AdhesionDetailController', function ($scope, $stateParams, Adhesion) {
        $scope.adhesion = {};
        $scope.load = function (id) {
            Adhesion.get({id: id}, function(result) {
              $scope.adhesion = result;
            });
        };
        $scope.load($stateParams.id);
    });
