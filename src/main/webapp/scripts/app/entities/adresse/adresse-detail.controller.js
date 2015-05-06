'use strict';

angular.module('membershipApp')
    .controller('AdresseDetailController', function ($scope, $stateParams, Adresse) {
        $scope.adresse = {};
        $scope.load = function (id) {
            Adresse.get({id: id}, function(result) {
              $scope.adresse = result;
            });
        };
        $scope.load($stateParams.id);
    });
