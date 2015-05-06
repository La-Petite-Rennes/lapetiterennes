'use strict';

angular.module('membershipApp')
    .controller('AdherentDetailController', function ($scope, $stateParams, Adherent, Coordonnees, Adhesion) {
        $scope.adherent = {};
        $scope.load = function (id) {
            Adherent.get({id: id}, function(result) {
              $scope.adherent = result;
            });
        };
        $scope.load($stateParams.id);
    });
