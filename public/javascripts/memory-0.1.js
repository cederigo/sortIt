(function() {

  var dataSet,
    currentIdx,
    lastIdx;

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
    img.hide();
    img.load(function() {
      /*center it*/
      var w = img.width(), h = img.height();
      var pw = img.parent().css('width'),ph = img.parent().css('height');
      img.css({
        left: (parseInt(pw) - w) / 2 + 'px',
        top:  (parseInt(ph) - h) / 2 + 'px'
      });
      img.show();
    });
    
    $("#image").replaceWith(img);

  }, vote = function(isForCurrent) {
    
    var lEl = dataSet.eIds[lastIdx];
    var cEl = dataSet.eIds[currentIdx];
    
    $.ajax({
      url: '/relations/vote',
      data: {setId: dataSet.id, aId: cEl, bId: lEl, isForA: isForCurrent},
      success : function(data, textStatus, req) {
        console.log("relation successfully added");
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.log("faled to add relation! " + textStatus);
      }
    });
    
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
    
    lastIdx = currentIdx;
    var rIdx = Math.floor(Math.random() * (dataSet.eIds.length));
    var eId = dataSet.eIds[rIdx];
    loadImage(eId);
    currentIdx = rIdx;

  };
  
  this.yes = function() {
    if(currentIdx != null && lastIdx != null) {
      vote(true);
    }
    next();
  };
  
  this.no = function() {
    if(currentIdx != null && lastIdx != null) {
      vote(false);
    }
    next();
  }

  this.skip = function() {
    if (currentIdx) {
      /*remove from elements and show next*/
      dataSet.eIds.splice(currentIdx, 1);
      next();
    }
  };

})();