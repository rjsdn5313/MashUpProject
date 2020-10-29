<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>OpenMap Project</title>

	<%@ include file="/WEB-INF/include/include-header.jspf" %>

</head>
<body>
<div class="container-fluid">
	<table style="width:100%; height:10vh;">
		<tr>
			<td style="width:15%">로고</td>
			<td style="width:85%" class="text-center align-middle">
				<span class='green_window'>
					<input type='text' class='input_text' name='search_keyword' id='search_keyword'/>
				</span>
				<button class='sch_smit' id="sch_smit">검색</button>
			</td>
		</tr>
	</table>
	
	<div class="row">
		<div class="col-sm-2 list_menu"></div>
		<div class="col-sm-2">
			<div class="list-group" id="show" name="show"></div>	
		</div>
		<div class="col-sm-8 map_view" id="map_view"></div>
	</div>
</div>
	<!-- 지도 보여주기 -->
	<script type="text/javascript" src="https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=s56b4f2jk4"></script>
	<script>
		var mapOptions = {
			center: new naver.maps.LatLng(37.3595704, 127.105399),
			zoom: 7,
		    
		    zoomControl: true,
		    zoomControlOptions: {
		        position: naver.maps.Position.TOP_RIGHT
		    }
		};
		var map = new naver.maps.Map(document.getElementById('map_view'), mapOptions);
	</script>
	
	<script type="text/javascript">
		$(document).ready(function(){
			fn_openDataList();
			
			$('#sch_smit').on('click',function(e){					//검색 버튼 클릭
				e.preventDefault();
				fn_searchData();
			});
			
			$("#search_keyword").keydown(function(e){				//엔터키 이벤트
				if(e.keyCode == 13){
					e.preventDefault();
					fn_searchData();
				}
			});
		});
		
		var ChkBoxNum;
		
		//체크박스 가져오기
		function fn_openDataList(){
			$.ajax({
				type:"post",
				url:"<c:url value = '/openDataList.do'/>",
				success:function(data) {
					if (data.length > 0) {
						fn_madeCheckBox(data);
						fn_chckboxEvent();
						ChkBoxNum = data.length;
					}
				},
				error:function(){
					alert("error");
				}
			});
		}
		
		//체크박스 만들기
		function fn_madeCheckBox(data){
			var chbhtml = ""; 
			
			for(i=0; i<data.length; i++){
				chbhtml += "<input type='checkbox' id='chkId_" + i + "' class='chkbox' value='" + data[i] + "' name='checkbox_'" + i + ">"+ data[i] + "<br>";
				$(".list_menu").html(chbhtml);
			}
		}
		
		//체크박스 체크 여부 (체크박스 이벤트)
		function fn_chckboxEvent(){
			$(".chkbox").change(function(){
				var ChkBoxId = $(this).attr("id").split("_");
				ChkBoxId = ChkBoxId[1];
				
		        if($(this).prop("checked") == true){ //체크박스 체크될 경우 
		        	var chk = $(this).val();
		        	var html = "";
		        	
		        	$.ajax({
		        		type: "post",
		        		url: "<c:url value = '/showData.do'/>",
		        		data: {name:chk},
		        		success: function(data){
		        			if (data.length > 0) {
		        				$("#ListDiv_Search").remove();
		        				for(i=0; i<searchMarker.length; i++){
									searchMarker[i].setMap(null);
								}
		        				
		        				html +=  "<div id='ListDiv_" + ChkBoxId + "'>";
		        				for(i=0; i<20; i++) {
		        					fn_madeMarker(data[i]);							// 체크 될 경우 데이터에 맞게 다중 마커 생성 
		        					html += fn_showList(data[i], ChkBoxId, i);			// 체크 될 경우 목록 리스트를 가져와서 보여준다.
		        				}
		        				html += "</div>"
		        				
		        				markersList[ChkBoxId] = markers;
		        				markers = [];
		        				
		        				$("#show").append(html);
		        			
		        				$(".datacl").on("click", function(e){				//리스트를 클릭했을 때
		        					e.preventDefault();
		        			
		        					var selectdata = new naver.maps.LatLng($(this).find("#x").val(), $(this).find("#y").val());
		        					map.setCenter(selectdata);
		        					
		        					var tmpMarker = markersList[$(this).find("#markerChkBoxId").val()][$(this).find("#markeri").val()];	// 마커 가져오기
		        					tmpMarker.setAnimation(naver.maps.Animation.BOUNCE);	// 마커 애니메이션 설정
		        					
		        					map.setOptions({
		        						zoom:13
		        					});
		        				});
		        			}
		        		},
		        		error: function(){
		        			alert("error");
		        		}
		        	});
		       	//체크박스 체크 해제
		        } else {
		        	$("#ListDiv_" + ChkBoxId).remove();
		        	fn_removeMarker(ChkBoxId);
		        	
		        	map.setCenter(new naver.maps.LatLng(37.3595704, 127.105399));
		        	map.setOptions({
						zoom:7
					});
		        }
			});
		}
		
		//마커, 인포윈도우 만들기
		var markersList = [];
		var markers = [];
			
		function fn_madeMarker(data){
			var marker = new naver.maps.Marker({
				map : map,
				position : new naver.maps.LatLng(data.위도, data.경도),
			})
			markers.push(marker);
			
			// 인포윈도우창에 들어갈 내용
			var infocontents = "";
		
			infocontents += "<div class = 'list-group'>"
			infocontents += fn_addData(data);
			infocontents += "</div>"
			
			// 인포윈도우 생성
			var infoWindow = new naver.maps.InfoWindow({
				content : infocontents
			})
			
			naver.maps.Event.addListener(marker, 'mouseover', function(){			// 마커에 마우스를 가져가면 정보창 생성
				infoWindow.open(map, marker);
			})
			
			naver.maps.Event.addListener(marker, 'mouseout', function(){			// 마커에 마우스를 때면 정보창 사라짐
				infoWindow.close();
			})

		}
		
		// 마커 삭제하기
		function fn_removeMarker(ChkBoxId){
			for(i=0; i<markersList[ChkBoxId].length; i++){
				markersList[ChkBoxId][i].setMap(null);
			}
		}
		
		// 목록 리스트 보여주기 (부트스트랩 이용 목록 나누기)
		function fn_showList(data, ChkBoxId, i){
			var html = "";
			html += "<a href='#this' class='list-group-item datacl'>";
			html += fn_addData(data, ChkBoxId, i);
			html += "</a>"
			
			return html;
		}
		
		// 목록리스트 안에 들어갈 내용 (부트스트랩 이용 목록 나누기)
		function fn_addData(data, ChkBoxId, i){
			var datahtml = "";
			
			// 리스트 제목
			if(data.설치장소  !== undefined){							// 무인민원발급정보, 무인교통단속카메라
				datahtml += "<h4 class = 'list-group-item-heading'>" + data.설치장소  + "</h4>";	
			}
			else if(data.촬영방면정보 !== undefined){					// CCTV
				datahtml += "<h4 class = 'list-group-item-heading'>" + data.촬영방면정보  + "</h4>";
			}
			else if(data.대상시설명 !== undefined){					//어린이보호구역
				datahtml += "<h4 class = 'list-group-item-heading'>" + data.대상시설명  + "</h4>";
			}
			
			//리스트 제목 안에 세부내용
			if(data.소재지지번주소  !== undefined){						//공통
				datahtml += "<p class = 'list-group-item-text'>소재지지번주소 : " + data.소재지지번주소  + "</p>";
			}
			if(data.설치위치 !== undefined){							//무인민원발급정보
				datahtml += "<p class = 'list-group-item-text'>설치위치 : " + data.설치위치 + "</p>";
			}
			if(data.설치목적구분  !== undefined){						//CCTV
				datahtml += "<p class = 'list-group-item-text'>설치목적구분 : " + data.설치목적구분  + "</p>";
			}
			if(data.제한속도  !== undefined){							//무인교통단속카메라
				datahtml += "<p class = 'list-group-item-text'>제한속도 : " + data.제한속도 + "</p>";
			}
			if(data.관할경찰서명  !== undefined){							//어린이보호구역
				datahtml += "<p class = 'list-group-item-text'>관할경찰서명  : " + data.관할경찰서명  + "</p>";
			}

			datahtml += "<input type = 'hidden' id = 'x' name = 'x' value='" + data.위도 + "'>"
			datahtml += "<input type = 'hidden' id = 'y' name = 'y' value='" + data.경도 + "'>"
			
			// 마커 위치 MarkersList[markerChkBoxId][markeri]
			datahtml += "<input type = 'hidden' id = 'markerChkBoxId' name = 'markerChkBoxId' value='" + ChkBoxId + "'>"
			datahtml += "<input type = 'hidden' id = 'markeri' name = 'markeri' value='" + i + "'>"
			return datahtml;
		}
		
		var searchMarker = [];
		
		function fn_searchData(){
			var search_keyword = $("#search_keyword").val();
			
			if(search_keyword != ""){
				$.ajax({
					type : "post",
					url : "<c:url value = '/searchData.do'/>",
					data : {search_keyword:search_keyword},
					success : function(data){
						if(data.length > 0){
							// 초기화
							$("#search_keyword").val("");
							
							for(i=0; i<searchMarker.length; i++){
								searchMarker[i].setMap(null);
							}
							
							searchMarker = [];
							
							$("#show").children().remove();
							
							for(i=0; i<ChkBoxNum; i++){
								if($("#chkId_" + i).prop("checked") == true) {
									fn_removeMarker(i);
								}
							}
							
							$("input:checkbox[class=chkbox]").each(function(){
								this.checked = false;
							});
							
							var html = "";
							//
							
							html += "<div id = 'ListDiv_Search'>";
							for(i=0; i<data.length; i++){
								fn_madeMarker(data[i].data);
	        					html += fn_showList(data[i].data);
							}
							html += "</div>";
							
							searchMarker = markers;
							var searchDataListHtml = html;
							
							html = "";
							markers = [];
							
							$("#show").append(searchDataListHtml);
							
							$(".datacl").on("click",function(e){
								e.preventDefault();
	        					var selectdata = new naver.maps.LatLng($(this).find("#x").val(), $(this).find("#y").val());
	        					
	        					map.setCenter(selectdata);
	        					
	        					map.setOptions({
	        						zoom:13
	        					});
							});
						}else{
							alert("검색 정보가 없습니다.");
						}
					},
					error: function() {
						alert("error");
					}
				
				});
			} else {
				alert("검색어를 입혁한 후 검색해주세요.");
				return false;
			}
		}
	</script>
</body>
</html>