<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>Title</title>
	<jsp:include page="/WEB-INF/views/fragment/static-header.jsp"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/style.css" type="text/css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/paginate.css" type="text/css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<style>
.table-list thead > tr:first-child{ background: #f8f8f8; }
.table-list th, .table-list td { text-align: center; }
.table-list .left { text-align: left; padding-left: 5px; }

.table-list .subject { width: 150px; color: #787878; }
.table-list .date { width: 200px; color: #787878; }
.table-list .hit { width: 70px; color: #787878; }
.table-list .heart { width: 70px; color: #787878; }
</style>
</head>
<script>
    let menuIndex = 5
</script>
<body>
<div class="whole-container">

	<header>
		<jsp:include page="/WEB-INF/views/fragment/menubar.jsp"/>
	</header>

	<div class="main-container shadow-lg">
		<div class="sub-menu w-100">

			<div style="display: flex; flex-direction: column; width: 100%">
				<div class="btn-group" role="group" aria-label="Basic outlined example" style="height: 40px">
					<button class="btn btn-outline-primary" onclick="location.href='${pageContext.request.contextPath}/mypage/productLikeList.do'"> 내가 찜 한 상품 </button>
					<button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/mypage/recipeLikeList.do'"> 내가 좋아요 한 레시피 </button>
					<button class="btn btn-outline-primary" onclick="location.href='${pageContext.request.contextPath}/mypage/recipeBoardMyList.do'"> 내가 작성한 글 </button>
					<button class="btn btn-outline-primary" onclick="location.href='${pageContext.request.contextPath}/mypage/orderMyList.do'"> 나의 주문내역 </button>
				</div>
			</div>
		</div>

		<div class="content-container">
			<div><h3><i class="bi bi-bookmark-heart"></i>내가 좋아요 한 조합레시피 글</h3></div>
			
			<div>
				<table>
					<tr>
						<td width="50%"> ${likedataCount}개(${page}/${total_page} 페이지) </td>
						<td align="right">&nbsp;</td>
					</tr>	
				</table>
				
				<table>
					<thead>
					<tr class="table-list">
						<th class="subject"> 레시피 제목 </th>
						<th class="date"> 작성일 </th>
						<th class="hit"> 조회수 </th>
						<th class="heart"> 좋아요 수 </th>
					</tr>
					</thead>
					
					<tbody>
						<c:forEach var="dto" items="${list}" varStatus="status">
						<tr>
							<td class=left>
								<a href="${pageContext.request.contextPath}/recipe/recipe.do?id=${dto.id}">${dto.subject}</a>
							</td>
							<td> ${dto.createdDate} </td>
							<td> ${dto.hitCount} </td>
							<td><i class="fa-solid fa-heart" style="color: red;"></i> ${dto.recipeLikeCount} </td>
						</tr>	
						</c:forEach>
					</tbody>
				</table>
				
				<div class="page-navigation">
					${likedataCount == 0 ? "등록된 게시물이 없습니다." : paging}
				</div>
				
			</div>
		</div>
	</div>
</div>

<script>
    $(document).ready(function () {
        selectMenu(menuIndex)
    })
</script>
<footer>
	<jsp:include page="/WEB-INF/views/fragment/footer.jsp"/>
</footer>
</body>
</html>
