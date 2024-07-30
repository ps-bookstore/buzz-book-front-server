// package store.buzzbook.front.controller.payment.service;
//
// import org.springframework.stereotype.Service;
//
// import store.buzzbook.front.controller.payment.PayInfo;
// import store.buzzbook.front.controller.payment.PayInfoAdaptor;
// import store.buzzbook.front.controller.payment.PayResult;
// import store.buzzbook.front.controller.payment.toss.TossPayInfoAdaptor;
//
// @Service
// public class PayResultService {
// 	//성공
// 	void success(long orderId, PayInfo payInfo){
//
// 		//payInfo
// 		if(payInfo.getPayType().equals("신용카드")){
// 			//어떤 adaptor 선택..
//
// 			PayInfoAdaptor payInfoAdaptor =resolver("");
//
// 			PayResult payResult = payInfoAdaptor.covert(payInfo);
// 		}
//
// 		//payresult -> api
// 	}
//
// 	PayInfoAdaptor resolver(String method){
// 		return null;
// 	}
// }
