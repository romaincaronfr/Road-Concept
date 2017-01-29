/**
 * Created by Romain on 02/10/2016.
 */

var userModel = null;
$(document).ready(function () {
    app.loadTemplates(["loginView", "mapView", "mapTableView", "navBarView", "userView", 'waitView', "adminManaView", "adminPanelOptionsView", "logoutView", "mapDetailsPageView", "mapPopUpInfoVisuView", "mapPopUpInfoVisuEachView", "adminManaUsersView", 'adminUserRowView', "importModalView", "mapEditionView", "mapPopUpEditRoadsView", "mapPopUpEditRedlightsView", "mapPopUpEditRondPointView", "mapPopUpCreateRoadsView", "mapPopUpCreateRedlightsView", "mapPopUpCreateRondPointView", "modalRemoveMapView", "adminModalUserView", "mapSimulationView", "simulationHomeView", "simulationCreationView", "mapPopUpCreateHabitationZoneView", "mapPopUpCreateWorkZoneView", 'waitMapEditionView', "homeSimulationOverTableView", "homeSimulationInProgressTableView", "mapCarSimulationView", "modalRemoveSimuView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
});


