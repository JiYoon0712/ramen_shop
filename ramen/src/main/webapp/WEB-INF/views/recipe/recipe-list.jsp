<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Recipe</title>
	<jsp:include page="/WEB-INF/views/fragment/static-header.jsp"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/style.css" type="text/css">

	<style>
		.product-container {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            grid-auto-rows: 300px;
            padding: 20px;
            gap: 30px;
            height: 90%;
			overflow: auto;
        }

        .product-item {
            width: 100%;
            height: 100%;
            border-radius: 10px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            gap: 10px;
            padding: 15px;
            transition: 0.5s;
            background: #ffffff;
            border: 1px solid #DFE2E6;
        }

        .product-img {
            width: 100%;
            height: 150px;
            object-fit: cover;
            border-radius: 5px;
        }

        .product-item:hover {
            cursor: pointer;
            background: #e8e8e8;
            /*filter: brightness(95%);*/
        }

        .sub-menu button {
            border-radius: 5px;
        }

        .sub-menu {
            width: 100%;
            height: 40px;
            display: flex;
            flex-direction: row;
            gap: 5px;
            justify-content: space-between;
			margin-bottom: 20px;
			padding: 0 20px;
        }

		a {
			text-decoration: none;
			color: black;
		}

        .search-box {
            border: 1px solid #DFE2E6;
            border-radius: 5px;
            width: 150px;
            padding: 0 5px;

        }
        
        select:hover {cursor: pointer;}
        select option:hover {cursor: pointer;}
	</style>
</head>
<script>
    let menuIndex = 3
    
    function searchList() {
		const f = document.searchForm;
		f.submit();
	}
    
    function sendRadio() {
		const r = document.radioForm;
		r.submit();
	}
</script>
<body>
<div class="whole-container">

	<header>
		<jsp:include page="/WEB-INF/views/fragment/menubar.jsp"/>
	</header>

	<div class="main-container shadow-lg">
		<div class="content-container">
			<div class="sub-menu">
				<form name="radioForm" action="${pageContext.request.contextPath}/recipe/list.do" method="post">
					<div class="btn-group" role="group" aria-label="Basic radio toggle button group">
						<input type="radio" class="btn-check" name="btnradio" id="btnradio1" value="btnradio1" autocomplete="off" onclick="sendRadio();" checked>
						<label class="btn btn-outline-primary" for="btnradio1">최신순</label>
		
						<input type="radio" class="btn-check" name="btnradio" id="btnradio2" value="btnradio2" autocomplete="off" onclick="sendRadio();">
						<label class="btn btn-outline-primary" for="btnradio2">조회순</label>
						
						<input type="radio" class="btn-check" name="btnradio" id="btnradio3" value="btnradio3" autocomplete="off" onclick="sendRadio();">
						<label class="btn btn-outline-primary" for="btnradio3">좋아요순</label>
					</div>
				</form>
				
				<div>
					<button type="button" class="btn btn-success" onclick="location.href='${pageContext.request.contextPath}/recipe/write-recipe.do';">글올리기</button>
					
				</div>
				
			</div>
			
			<div class="sub-menu">
				<form name="searchForm" action="${pageContext.request.contextPath}/recipe/list.do" method="post" style="display: flex; flex-direction: row; height: 100%; width: 100%; gap: 8px;">
					<button type="button" class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/recipe/list.do';" title="새로고침" style="height: 100%;"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					<select name="condition" class="form-select" style="width: 125px;">
						<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
						<option value="nickname" ${condition=="nickname"?"selected='selected'":"" }>작성자</option>
						<option value="created_date"  ${condition=="created_date"?"selected='selected'":"" }>등록일</option>
						<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
						<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
					</select>
					<input type="text" name="keyword" value="${keyword}" class="form-control" style="width: 345px;">
					<button type="button" class="btn btn-primary" onclick="searchList();">검색</button>
				</form>
			</div>
			
			<div class="product-container">
				<c:forEach var="recipe" items="${list}">
					<a class="product-item shadow" href="${recipeUrl}${recipeUrl.contains('keyword') ? '&' : '?'}id=${recipe.id}"> 
						<img class="product-img" src="${pageContext.request.contextPath}/resource/picture/1.png">
						<div>${recipe.subject}</div>
						<div>${recipe.nickname}</div>
						<div>조회 수 : ${recipe.hitCount}</div>
						<div><i class="fa-solid fa-heart" style="color: red;"></i> ${recipe.recipeLikeCount}</div>
						<div>${recipe.createdDate}</div>
					</a>
				</c:forEach>
			</div>
	
		</div>
	</div>
</div>

<script>
    $(document).ready(function () {
        selectMenu(menuIndex)
    })
</script>
</body>
</html>
