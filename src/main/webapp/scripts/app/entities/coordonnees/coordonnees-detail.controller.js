'use strict';

angular.module('membershipApp')
    .controller('CoordonneesDetailController', function ($scope, $stateParams, Coordonnees) {
        $scope.coordonnees = {};
        $scope.load = function (id) {
            Coordonnees.get({id: id}, function(result) {
              $scope.coordonnees = result;
            });
        };
        $scope.load($stateParams.id);
    });
