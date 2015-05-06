'use strict';

angular.module('membershipApp')
    .controller('CoordonneesController', function ($scope, Coordonnees) {
        $scope.coordonneess = [];
        $scope.loadAll = function() {
            Coordonnees.query(function(result) {
               $scope.coordonneess = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Coordonnees.update($scope.coordonnees,
                function () {
                    $scope.loadAll();
                    $('#saveCoordonneesModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Coordonnees.get({id: id}, function(result) {
                $scope.coordonnees = result;
                $('#saveCoordonneesModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Coordonnees.get({id: id}, function(result) {
                $scope.coordonnees = result;
                $('#deleteCoordonneesConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Coordonnees.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCoordonneesConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.coordonnees = {adresse1: null, adresse2: null, codePostal: null, ville: null, email: null, telephone: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
