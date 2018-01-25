'use strict';

angular.module('membershipApp')
    .controller('AdherentExportController', function ($scope, $http, $stateParams, Adherent, Coordonnees, Adhesion) {
    	// View Model
        $scope.format = 'json';
        $scope.properties = {
    		'id': true,
    		'nom': true,
    		'prenom': true,
    		'estBenevole': true,
    		'adresse': true,
    		'codePostal': true,
    		'ville': true,
    		'telephone': true,
    		'email': true,
    		'adhesions': true
        };
        $scope.adhesionState = 'all';

        // View functions
        $scope.export = function () {
        	$http.post('api/adherents/export', {format: $scope.format, adhesionState: $scope.adhesionState, properties: $scope.properties})
        		.then(function(result) {
        			downloadFile(result);
        		}
        	);
        };

        // Functions
        function downloadFile(result) {
        	//Generate a file name
            var data = result.data;
            var headers = result.headers;
            var fileName = "Adherents";

            //Initialize file format you want csv or xls
            if (headers('Content-Type').startsWith('application/json')) {
            	data = JSON.stringify(data);
            }
            var uri = 'data:' + headers('Content-Type') + ';charset=UTF-8,' + escape(data);

            // Now the little tricky part.
            // you can use either>> window.open(uri);
            // but this will not work in some browsers
            // or you will not gAet the correct file extension

            //this trick will generate a temp <a /> tag
            var link = document.createElement("a");
            link.href = uri;

            //set the visibility hidden so it will not effect on your web-layout
            link.style.visibility = "hidden";
            link.download = fileName + "." + $scope.format;

            //this part will append the anchor tag and remove it after automatic click
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    });
