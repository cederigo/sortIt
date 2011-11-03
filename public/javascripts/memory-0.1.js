(function() {

  var dataSet, currentIdx;

  var init = function(setId) {
    dataSet = null;
    currentIdx = null;

    $.ajax({
      url : '/dataSets/' + setId,
      dataType : 'json',
      async : false,
      success : function(data, textStatus, req) {
        console.log('got dataSet infos');
        dataSet = data;
        if (dataSet.eIds.length == 0) {
          alert("dataSet " + dataSet.name + " has no elements");
        }
      },
      error : function(jqXHR, textStatus, errorThrown) {
        alert("could not get infos about DataSet:" + textStatus);
      }
    });

  }, loadImage = function(eId) {

    var img = $("<img>");
    img.attr('id', 'image');
    img.attr('src', '/elements/' + eId + '/data');
    $("#image").replaceWith(img);

  };

  window.sortIt = this;

  this.load = function(setId) {
    init(setId);
    next();
  };

  this.next = function() {

    if (!dataSet) {
      return;
    }

    var rIdx = Math.floor(Math.random() * (dataSet.eIds.length + 1));
    var eId = dataSet.eIds[rIdx];
    loadImage(eId);
    currentIdx = eId;

  };

  this.skip = function() {
    if (currentIdx) {
      /*remove from elements and show next*/
      dataSet.eIds.splice(currentIdx, 1);
      next();
    }
  };

})();