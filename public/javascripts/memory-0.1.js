(function() {

  var dataSet,
    currentIdx,
    lastIdx,
    votesCollected;

  var init = function(setId) {
    dataSet = null;
    currentIdx = null;
    votesCollected = 0;

    $.ajax({
      url : '/dataSets/' + setId,
      dataType : 'json',
      async : false,
      success : function(data, textStatus, req) {
        console.log('got dataSet infos');
        dataSet = data;
        if (dataSet.eIds.length == 0) {
          alert("dataSet " + dataSet.name + " has no elements");
        } else {
          currentIdx = 0;
          loadImage();
        }
      },
      error : function(jqXHR, textStatus, errorThrown) {
        alert("could not get infos about DataSet:" + textStatus);
      }
    });

  }, loadImage = function() {
    
    var eId = dataSet.eIds[currentIdx];
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
        votesCollected++;
      },
      error: function(jqXHR, textStatus, errorThrown) {
        console.log("faled to add relation! " + textStatus);
      }
    });
    
  };

  window.sortIt = this;

  this.start = function(setId) {
    init(setId);
  };
  
  this.resume = function() {
    votesCollected = 0;
  };

  this.next = function() {

    if (!dataSet) {
      return;
    }
    
    lastIdx = currentIdx;
    currentIdx = (currentIdx + 1) % dataSet.eIds.length;
    
    loadImage();

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
  };  
  
  this.done = function() {
    if(votesCollected > 10) {
      return true;
    } else {
      return false;
    }
  }

})();