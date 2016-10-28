/**
 * Created by Romain on 02/10/2016.
 */

var userModel = null;
$(document).ready(function () {
    console.log("document ready");
    app.loadTemplates(["loginView", "mapView", "mapTableView", "navBarView", "userView", 'waitView', "adminManaView", "adminPanelOptionsView", "logoutView","mapDetailsPageView","mapPopUpInfoVisuView","mapPopUpInfoVisuEachView","adminManaUsersView",'adminUserRowView',"importModalView","mapEditionView"],
        function () {
            app.router = new app.Router();
            Backbone.history.start();
        });
});


