<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
	<title>Title</title>
	<jsp:include page="/WEB-INF/views/fragment/static-header.jsp"/>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resource/css/style.css" type="text/css">

	<style>

	table{
		width:100%;
	}
	
	th{
		font-size: 15px;
		width: 40px;
		text-align: center;
		padding-top: 10px;
		padding-bottom: 10px;
		border-top: 2px solid gray;
		border-bottom: 1px solid gray;
	}
	
	td{
		border-bottom: 1px solid gray;
	}
	
	hr{
		border: 2px solid gray;
	}
	
	.title{
		font-weight: bold;
		font-size: 20px;
	}	
	
	.btn {
		border-radius: 0.5;
		border : 1px solid lightgray;
	}
	
	.btn:hover{
		background: lightgray;
	}
	
	.btnchangeNum{
		font-size: 5px;
	}
	
	#chkAll, #chkAll2{
		width: 15px;
		height: 15px;
		border: 2px solid #bcbcbc;
		cursor: pointer;
	}
	
	.orderItem{
		 text-align: center;
		 height: 80px
	}

	.item1{
		width : 25%;
	}
	
	.item2{
		width : 15%;
	}
	
	
	.sold-out{
		background:#eee;
	}
	</style>
	
<script type="text/javascript">


        $(function() {

            $("#chkAll").click(function(){
                if($(this).is(":checked")) {
                    $("input[name=productIds]").prop("checked", true);
                } else {
                    $("input[name=productIds]").prop("checked", false);
                }
            });

            $("#btnDeleteList").click(function(){
                let cnt = $("input[name=productIds]:checked").length;
                if(cnt === 0) {
                    alert("삭제할 물품을 먼저 선택하세요.");
                    return false;
                }

                if(confirm("선택한 물품을 삭제 하시겠습니까 ?")) {
                    const f = document.listForm;
                    f.action="${pageContext.request.contextPath}/cart/list_delete.do";
                    f.submit();
                }
            });

            $("#btnOrder").click(function(){
                let cnt = $("input[name=productIds]:checked").length;

                if(cnt === 0) {
                    alert("결제할 물품을 먼저 선택하세요.");
                    return false;
                }

                if(confirm("선택한 물품을 결제 하시겠습니까 ?")) {
                    const f = document.listForm;
                    f.action="${pageContext.request.contextPath}/order/order.do";
                    f.submit();
                }
            });

            //수량 옵션
            $('.count :button').on({
                'click': function () {
                    $.fn.checkQuantity(this);
                },
            });

			// 수량 체크 함수
            $.fn.checkQuantity = function (obj) {
                let countEl = $(obj).parent('.count').find('.quantitys');
                let currentCount = countEl.val();
                let remain = $(obj).parent().next().text();
				let result = true;

                let min = 1;

                if($(obj).hasClass('minus')){
                    if(currentCount > min){
                        currentCount--;
                    }
                }else if($(obj).hasClass('plus')){
                    if (currentCount < remain) {
                        currentCount++;
                    } else {
                        alert("이 상품은 최대 " + remain + "개 까지 구매 가능합니다.")
                        currentCount = remain;
                    }
                }
                countEl.val(currentCount);

                if (currentCount > remain) {
                    result = false;
                }
                return result;
            }
        });

        $(function(){
            $(".btnChangeNum").click(function(){
                let check = $.fn.checkQuantity(this);
				if (!check) {
                    alert('최대 수량을 넘었습니다.')
					return;
                }

                var $count = $(this).parent('.count').find('.quantitys');
                var quantity = $count.val();
                var $pid = $(this).closest("tr").find('.productIds');
                var productId = $pid.val();

                let url = "${pageContext.request.contextPath}/cart/num_update.do?productId="+ productId +"&quantity="+quantity;
                location.href= url;
            });
        });

	</script>
</head>
<body>
<div class="whole-container">

	<header>
		<jsp:include page="/WEB-INF/views/fragment/menubar.jsp"/>
	</header>

	<div class="main-container shadow-lg">
		<div class="product-container" style="overflow:auto; height:680px; margin-bottom:20px">
			<form name="listForm" method="post">
				<p class="title">장바구니</p>
				<table>
					<thead>
					<c:if test="${dataCount != 0}">
						<tr>
							<th class="chk" >
								<input type="checkbox" name="chkAll" id="chkAll">        
							</th>
							<th>상품이미지</th>
							<th>품명</th>
							<th>총수량</th>
							<th>남은수량</th>
							<th>소계</th>
						</tr>
					</c:if>
					
					<c:if test="${dataCount == 0}">
						<p style="text-align: center"> 장바구니가 비어있습니다 🗑</p>
					</c:if>
					
					</thead>
					
					<tbody>
						<c:forEach var="cart" items="${list}">
									<tr>
										<td class="orderItem">
											<input type="checkbox" name="productIds" value="${cart.productId}" class="productIds" id="chkAll2">
										</td>
										<td class="item1 orderItem"><img class="product-img" src="${pageContext.request.contextPath}/resource/picture/1.png" style="height: 100px;"></td>
										<td class="item2 orderItem">${cart.productName}</td>
										<td class="item1 orderItem count">
											<button type="button" class="minus btn">-</button> 
											<input type="text" class="quantitys" name= "quantitys" value="${cart.quantity}" style="width:50px; text-align: center"> 
											<button type="button" class="plus btn">+</button>
											<button type="button" class= "btnChangeNum btn">수량변경</button>
										</td>
										<td class="item2 orderItem count2">${cart.remainQuantity}</td>
										<td class="item2 orderItem">${cart.price*cart.quantity}</td>
									</tr>
						</c:forEach>
						
						
					</tbody>
				</table>
			
			</form>
		</div>
		<div>	
					<c:if test="${dataCount != 0}">
					<button type="button" class="btn" id="btnDeleteList">삭제</button>
					<button type="button" class="btn" id="btnOrder">결제</button>
					</c:if>
			</div>
	</div>
</div>
</body>
</html>
