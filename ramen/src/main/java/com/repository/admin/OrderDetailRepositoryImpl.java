package com.repository.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.DTO.OrderBundle;
import com.DTO.OrderItem;
import com.DTO.OrderStatistics;
import com.util.DBConn;
import com.util.DBUtil;

public class OrderDetailRepositoryImpl implements OrderDetailRepository{

private Connection conn = DBConn.getConnection();

	// 송장번호 등록
	@Override
	public void setDeliveryNumber(Long orderId, Long deliveryId) {
		// 송장번호 등록
		// 테이블 : order_bundle 컬럼: delivery_id
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE order_bundle "
					+ " SET delivery_id = ? "
					+ " WHERE id = ?" ;
			//UPDATE shipments SET status = '배송중' WHERE invoice_number = '송장번호';
			pstmt = conn.prepareStatement(sql);
			 
			pstmt.setLong(1, deliveryId);
			pstmt.setLong(2, orderId);
		
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt);
		}
	}

	// 전체 주문내역 확인 >> orderBundle 데이터만 출력 (OrderBundle 내 OrderItem List 출력X)
	@Override
	public List<OrderBundle> findOrderAll(int offset, int size, String condition, String keyword, Long StatusId) {
		List<OrderBundle> OrderBundleList = new ArrayList<OrderBundle>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			// 주문번호, 맴버아이디, 송장번호, 결제일, 받는분, 전화번호, 우편번호, 주소1, 주소2, 회원이메일, 합계
			sb.append("SELECT b.id, a.id, b.delivery_id, b.created_date, ");
			sb.append(" b.receive_name, b.tel, b.post_num, b.address1, b.address2, a.email, sum(c.final_price) as 합계 ");
			sb.append(" FROM member a  ");
			sb.append(" INNER JOIN order_bundle b ON a.id = b.member_id");
			sb.append(" INNER JOIN order_item c ON b.id = c.order_id");
			sb.append(" INNER JOIN order_status d ON c.status_id = d.id");

			// status 상태조건, condition 검색조건
			if (StatusId == 1 && condition.equals("all")) { 
				// 1.결제완료 & 검색 null
				sb.append(" WHERE d.id = 1");
			} else if (StatusId == 1 && condition.equals("a.email")) { 
				// 1.결제완료 & 회원이메일 검색
				sb.append(" WHERE d.id = 1 AND a.email = ?");
			} else if (StatusId == 1 && condition.equals("b.id")) { 
				// 1.결제완료 & 주문번호 검색
				sb.append(" WHERE d.id = 1 AND b.id = ?");
			} else if (StatusId == 2 && condition.equals("all")) { 
				// 2.배송중 & 검색 null
				sb.append(" WHERE d.id = 1");
			} else if (StatusId == 2 && condition.equals("a.email")) {
				// 2.배송중 & 회원이메일 검색
				sb.append(" WHERE d.id = 2 AND a.email = ?");
			} else if (StatusId == 2 && condition.equals("b.id")) {
				// 2.배송중 & 주문번호 검색
				sb.append(" WHERE d.id = 2 AND b.id = ?");
			} else if (StatusId == 3 && condition.equals("all")) { 
				// 3.배송완료 & 검색 null
				sb.append(" WHERE d.id = 3");
			} else if (StatusId == 3 && condition.equals("a.email")) { 
				// 3.배송완료 & 회원이메일 검색
				sb.append(" WHERE d.id = 3 AND a.email = ?");
			} else if (StatusId == 3 && condition.equals("b.id")) { 
				// 3.배송완료 & 주문번호 검색
				sb.append(" WHERE d.id = 3 AND b.id = ?");
			} else if (StatusId == 4 && condition.equals("all")) { 
				// 3.주문취소 & 검색 null
				sb.append(" WHERE d.id = 3");
			} else if (StatusId == 4 && condition.equals("a.email")) { 
				// 3.주문취소 & 회원이메일 검색
				sb.append(" WHERE d.id = 3 AND a.email = ?");
			} else { 
				// 3.주문취소 & 주문번호 검색
				sb.append(" WHERE d.id = 3 AND b.id = ?");
			}
			sb.append(" GROUP BY b.id, a.id, b.delivery_id, b.created_date, b.receive_name, b.tel, b.post_num, b.address1, b.address2, a.email ");
			sb.append(" ORDER BY b.created_date DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				OrderBundle ob = new OrderBundle();
				
				// 주문번호, 맴버아이디, 송장번호, 결제일, 받는분, 전화번호, 우편번호, 주소1, 주소2, 회원이메일, 합계
				ob.setOrderBundleId(rs.getLong("b.id"));
				ob.setMemberId(rs.getLong("a.id"));
				ob.setDeliveryId(rs.getLong("delivery_id"));
				ob.setCreatedDate(rs.getString("created_date"));
				ob.setReceiveName(rs.getString("receive_name"));
				ob.setTel(rs.getString("tel"));
				ob.setPostNum(rs.getString("post_num"));
				ob.setAddress1(rs.getString("address1"));
				ob.setAddress2(rs.getString("address2"));
				ob.setUserEmail(rs.getString("email"));
				ob.setTotalPrice(rs.getLong("final_price"));
				
				OrderBundleList.add(ob);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return OrderBundleList;
	}
	
	// 상세 주문내역 확인 >> orderBundle, orderitem 데이터 출력 (OrderBundle 내 OrderItem List 출력O)	
	@Override
	public OrderBundle findOrderDetail(int offset, int size, String condition, String keyword, Long StatusId, Long orderBundleId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		OrderBundle orderBundle = new OrderBundle();
		List<OrderItem> orderItems = new ArrayList<>();
		// ***** 상세리스트 조건 다는 법. 기간조건 다는법 ***** 
		try {
			// 주문번호, 맴버아이디, 송장번호, 결제일, 받는분, 전화번호, 우편번호, 주소1, 주소2, 회원이메일, 합계
			sb.append("SELECT b.id, a.id, b.delivery_id, b.created_date, ");
			sb.append(" b.receive_name, b.tel, b.post_num, b.address1, b.address2, a.email, sum(c.final_price) as 합계 ");
			sb.append(" FROM member a  ");
			sb.append(" INNER JOIN order_bundle b ON a.id = b.member_id");
			sb.append(" INNER JOIN order_item c ON b.id = c.order_id");
			sb.append(" INNER JOIN order_status d ON c.status_id = d.id");
			// WHERE절
			sb.append(" GROUP BY b.id, a.id, b.delivery_id, b.created_date, b.receive_name, b.tel, b.post_num, b.address1, b.address2, a.email ");
			sb.append(" ORDER BY b.created_date DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				// 주문번호, 맴버아이디, 송장번호, 결제일, 받는분, 전화번호, 우편번호, 주소1, 주소2, 회원이메일, 합계
				orderBundle.setOrderBundleId(rs.getLong("b.id"));
				orderBundle.setMemberId(rs.getLong("a.id"));
				orderBundle.setDeliveryId(rs.getLong("delivery_id"));
				orderBundle.setCreatedDate(rs.getString("created_date"));
				orderBundle.setReceiveName(rs.getString("receive_name"));
				orderBundle.setTel(rs.getString("tel"));
				orderBundle.setPostNum(rs.getString("post_num"));
				orderBundle.setAddress1(rs.getString("address1"));
				orderBundle.setAddress2(rs.getString("address2"));
				orderBundle.setUserEmail(rs.getString("email"));
				orderBundle.setTotalPrice(rs.getLong("final_price"));
			} 
			
			pstmt.close();
			pstmt = null;
			rs.close();
			rs = null;
			
			// 입력받은 orderBundleId에 대한 orderItems 조회 쿼리
			// 주문상세번호, 상품번호, 주문번호, 주문상태번호, 수량, 단가, 상품별합계, 상품명, 주문상태명
			sb.append("SELECT b.id, a.id, b.delivery_id, b.created_date, ");
			sb.append(" b.receive_name, b.tel, b.post_num, b.address1, b.address2, a.email, sum(c.final_price) as 합계 ");
			sb.append(" FROM member a  ");
			sb.append(" INNER JOIN order_bundle b ON a.id = b.member_id");
			sb.append(" INNER JOIN order_item c ON b.id = c.order_id");
			sb.append(" INNER JOIN product d ON d.id = c.product_id");
			sb.append(" INNER JOIN order_status e ON c.status_id = e.id");
			sb.append(" WHERE b.id = ?");
			sb.append(" ORDER BY b.created_date DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
		
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			// orderItems 조회
			while (rs.next()) {
				OrderItem orderItem = new OrderItem();
				
				// 주문상세번호, 상품번호, 주문번호, 주문상태번호, 수량, 단가, 상품별합계, 상품명, 주문상태명
				orderItem.setOrderBundleId(rs.getLong("c.id"));
				orderItem.setProductId(rs.getLong("product_id"));
				orderItem.setOrderBundleId(rs.getLong("b.id"));
				orderItem.setStatusId(rs.getLong("status_id"));
				orderItem.setQuantity(rs.getInt("quantity"));
				orderItem.setPrice(rs.getLong("price"));
				orderItem.setFinalPrice(rs.getLong("final_price"));
				orderItem.setProductName(rs.getString("d.name"));
				orderItem.setStatusName(rs.getString("status_name"));

				orderItems.add(orderItem);
			} 
			
			orderBundle.setOrderItems(orderItems);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return orderBundle;
	}
	
	// 데이터 개수
	@Override
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM order_item";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return result;
	}
		
	// 검색에서의 데이터 개수
	  //1) 전체 주문리스트
	  //2) 회원 주문리스트  -- 회원 검색
	  //3) 주문상태별 주문리스트 -- 주문상태별 검색
	  //4) 주문번호별 내역 -- 주문번호별 검색
	@Override
	public int dataCount(String condition, String keyword) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM order_item oi ";
				//+ " JOIN member1 m ON b.userId = m.userId "; // 맴버테이블, 주문상태 테이블, 번들테이블 조인하기
				
			if (condition.equals("memberId")) { 
				// 회원이메일 검색
				sql += "  WHERE email = ?  "; 
				
			} else if (condition.equals("status_name")) { 
				// 주문상태별 검색
				sql += "  WHERE status_name = ? ";
				
			} else { 
				// 주문번호별 검색
				sql += "  WHERE orderBundleId = ? ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, keyword);
			if (condition.equals("all")) {
				pstmt.setString(2, keyword);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return result;
	}

	// 기간 조회(1일 | 1개월 | 6개월 | 1년 | 전체) + 상품별 매출 통계
	@Override
	public OrderStatistics SalesStatisticsByProduct()  {
		OrderStatistics orderStatistics = new OrderStatistics();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

        // 현재 날짜 구하기
        LocalDate now = LocalDate.now();
        // 1일 전
        LocalDate oneDayAgo = now.minusDays(1);
        Period oneDayperiod = Period.between(oneDayAgo, now);
        // 1개월 전
        LocalDate oneMonthAgo = now.minusMonths(1);
        Period oneMonthperiod = Period.between(oneMonthAgo, now);
        // 6개월 전
        LocalDate sixMonthAgo = now.minusMonths(6);
        Period sixMonthperiod = Period.between(sixMonthAgo, now);
        // 12개월 전
        LocalDate twelveMonthAgo12 = now.minusMonths(12);
        Period twelveMonthperiod = Period.between(twelveMonthAgo12, now);
        
		try {
			// 
			sb.append("SELECT b.product_id, a.name, sum(b.quantity) as 판매수량, ");
			sb.append(" sum(b.price) as 정가, sum(b.final_price) as 최종매출액 ");
			sb.append(" FROM order_item b ");
			sb.append(" JOIN product a ON a.id = b.product_id ");
			sb.append(" JOIN order_bundle c ON c.id = b.order_id ");
			sb.append(" WHERE b.status_id <= 3");
			
			// 기간 조건(1일 | 1개월 | 6개월 | 1년 | 전체)
			if (oneDayperiod.getDays() <= 1) { 
				// 1일
				sb.append(" AND created_date >= TO_CHAR(SYSDATE -1)");
			} else if (oneMonthperiod.getMonths() <= 1) { 
				// 1개월
				sb.append(" AND created_date >= ADD_MONTHS(SYSDATE, -1)");
			} else if(sixMonthperiod.getMonths() <= 6) {
				// 6개월
				sb.append(" AND created_date >= ADD_MONTHS(SYSDATE, -6)");
			} else if(twelveMonthperiod.getMonths() <= 12) {
				// 1년
				sb.append(" AND created_date >= ADD_MONTHS(SYSDATE, -12)");
			} else {
				// 전체
			}
			
			sb.append(" GROUP BY b.product_id, a.name");
			sb.append(" ORDER BY product_id ");

			pstmt = conn.prepareStatement(sb.toString());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				orderStatistics.setProductid(rs.getLong("product_id"));
				orderStatistics.setProductname(rs.getString("a.name"));
				orderStatistics.setSumquantity(rs.getLong("sum(b.quantity)"));
				orderStatistics.setSumprice(rs.getLong("sum(b.price)"));
				orderStatistics.setSumfinal_price(rs.getLong("sum(b.final_price)"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return orderStatistics;
	}
	
	// 회원별 주문리스트
	@Override
	public List<OrderBundle> findOrderByMemberId(Long MemberId) {
		// 회원 > 주문내역 확인
		// 맴버아이디를 받아와서 맴버별 주문내역 확인하기(마이페이지에 보이는 내역)
		List<OrderBundle> list = new ArrayList<OrderBundle>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
		/*
			//주문번호, 처리상태, 주문일자, 주문상세번호, 상품아이디, 상품명, 수량, 정가, 최종가격, 유저이메일, 송장번호, 받는 분, 전화번호, 우편번호, 주소1, 주소2
			sb.append("SELECT b.id, d.status_name, b.created_date, c.id, c.product_id, e.name, ");
			sb.append(" c.quantity, c.price, c.final_price, a.email, b.delivery_id, ");
			sb.append(" b.receive_name, b.tel, b.post_num, b.address1, b.address2 ");
			sb.append(" FROM member a, order_bundle b, order_item c, order_status d, product e");
			sb.append(" WHERE a.email = ?"); // 이메일로 해야할지 맴버 아이디로 해야할지.. 마이페이지 보이는 내역과 관리자페이지 주문리스트에서 검색했을 때 모두 고려해야함
			sb.append(" AND b.id = c.order_id");
			sb.append(" AND c.status_id = d.id");
			sb.append(" AND c.product_id = e.id");
			sb.append(" ORDER BY b.created_date DESC ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, memberId); // 이메일 받아서 회원주문리스트만 출력하기
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				OrderBundle odto = new OrderBundle();
				
				//주문번호, 처리상태, 주문일자, 주문상세번호, 상품아이디, 상품명, 수량, 정가, 최종가격, 유저이메일, 송장번호, 받는 분, 전화번호, 우편번호, 주소1, 주소2
				odto.setOrderBundleId(rs.getLong("b.id"));
				//odto.setStatusName(rs.getLong("status_name"));
				odto.setCreatedDate(rs.getString("created_date"));
				odto.setOrderItemId(rs.getLong("c.id"));
				odto.setProductId(rs.getLong("product_id"));
				//odto.setProductName(rs.getLong("name"));
				odto.setQuantity(rs.getInt("quantity"));
				odto.setPrice(rs.getLong("price"));
				odto.setFinalPrice(rs.getLong("final_price"));
				//odto.setEmail(rs.getLong("email")); // 유저 이메일
				odto.setDeliveryId(rs.getLong("delivery_id"));
				odto.setReceiveName(rs.getString("receive_name"));
				odto.setTel(rs.getString("tel"));
				odto.setPostNum(rs.getString("post_num"));
				odto.setAddress1(rs.getString("address1"));
				odto.setAddress2(rs.getString("address2"));
				
				list.add(odto);
			}
		*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResource(pstmt,rs);
		}
		return list;
	}


}
