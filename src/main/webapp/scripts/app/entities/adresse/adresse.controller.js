'use strict';

angular.module('membershipApp')
    .controller('AdresseController', function ($scope, Adresse) {
        $scope.adresses = [];
        $scope.loadAll = function() {
            Adresse.query(function(result) {
               $scope.adresses = result;
            });
        };
        $scope.loadAll();

        $scope.create = function () {
            Adresse.update($scope.adresse,
                function () {
                    $scope.loadAll();
                    $('#saveAdresseModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Adresse.get({id: id}, function(result) {
                $scope.adresse = result;
                $('#saveAdresseModal').modal('show');
            });
        };

        $scope.delete = function (id) {
            Adresse.get({id: id}, function(result) {
                $scope.adresse = result;
                $('#deleteAdresseConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Adresse.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteAdresseConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.clear = function () {
            $scope.adresse = {adresse1: null, adresse2: null, codePostal: null, ville: null, email: null, telephone: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
