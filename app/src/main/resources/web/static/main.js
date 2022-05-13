$(function(){
	let ws = new WebSocket('ws://'+ location.host +'/api/ws');
	
	function buildTable(obj, to){
		let div = $('<tbody>');
		
		for(let i in obj){
			let tr = $('<tr>');
			
			for(let j in obj[i]){
				let td = $('<td>');
				
				td.html(obj[i][j])
				
				tr.append(td);
			}
			
			div.append(tr);
		}
		
		to.html(div);
	}
	
	ws.onmessage = function(event){
		let json = JSON.parse(event.data);
		
		$('#instruction').html(json.instruction);
		
		let env = [];
		
		for(let i in json.env.vars){
			env.push([i, json.env.vars[i].value]);
		}
		
		buildTable(env, $('.vars table'));
		
		if(json.console)
			$('.console').append($('<pre>').html(json.console));
	};
	
	ws.onclose = function(event){
		$('#error').attr('active', true);
		$('#error pre').html(Error(`WebSocket the connection was closed abnormally, e.g., without sending or receiving a Close control frame [${event.code}]`).stack);
	};
	
	ws.onerror = function(event){
		$('#error').attr('active', true);
		$('#error pre').html(Error('WebSocket error').stack);
	};
	
	$('#butt-restart').on('click', function(){
		ws.send('restart');
		return false;
	});
	
	$('#butt-stop').on('click', function(){
		ws.send('stop');
		return false;
	});
	
	$('#butt-play').on('click', function(){
		ws.send('start');
		return false;
	});
	
	$('#butt-next').on('click', function(){
		ws.send('next');
		return false;
	});
});