$(document).ready(function() {

    $("#rebuildLink").click(function() {
        $.ajax({
            type: "GET",
            url: "/api/rebuild",
            success: function() {
                alert("Rebuild done!");
            }
        });
    });

    $("#commitLink").click(function() {
        $.ajax({
            type: "GET",
            url: "/api/commit",
            success: function(data) {
                if("ok" === data) {
                    alert("Commited!");
                } else {
                    alert("Failed to commit.");
                }
            }
        });
    });

    $("#statusLink").click(function() {
        $.ajax({
            type: "GET",
            url: "/api/status",
            success: function(data) {
                if("error" === data) {
                    alert("Failed to get status.");
                } else {
                    alert(data);
                }
            }
        });
    });

    $("#pushLink").click(function() {
        $.ajax({
            type: "GET",
            url: "/api/push",
            success: function(data) {
                if("error" === data) {
                    alert("Failed to push.");
                } else {
                    alert("Pushed!");
                }
            }
        });
    });
    
});