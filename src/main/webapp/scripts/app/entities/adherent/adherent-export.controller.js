'use strict';

angular.module('membershipApp')
    .controller('AdherentExportController', function ($scope, $http, $stateParams, Adherent, Coordonnees, Adhesion) {
    	// View Model
        $scope.format = 'json';
        
        // View functions
        $scope.export = function () {
        	$http.get('api/adherents/export', {params : {format: $scope.format}})
        		.success(function(result, status, headers) {
        			downloadFile(result, headers);
        		}
        	);
        };
        
        // Functions
        function downloadFile(data, headers) {
        	//Generate a file name
            var fileName = "Adherents";
            
            //Initialize file format you want csv or xls
            if (headers('Content-Type').startsWith('application/json')) {
            	data = JSON.stringify(data);
            }
            var uri = 'data:' + headers('Content-Type') + ';charset=utf-8,' + escape(data);
            
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
