package com.vany.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vany.exception.ResourceNotFoundException;
import com.vany.model.Bar;
import com.vany.model.RecvStateBar;
import com.vany.repositeroy.BarRepo;
import com.vany.repositeroy.RevcStateRepo;
import com.vany.services.UserName;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecvStateController {

	@Autowired
	BarRepo barRepo;

	@Autowired
	RevcStateRepo revcRepo;

	@Autowired
	UserName userName;

	public List<RecvStateBar> getListRecivedBarByUser() {
		List<Bar> barList = barRepo.findByDaoUser(userName.getUser());
		// here we decaler the temp result of openstate object for later we return this
		// object
		List<RecvStateBar> result = new ArrayList<RecvStateBar>();
		// in this for loop we whatever get bar item data we return form that we fetch
		// the specfic openstate data and add to temp vairlbe
		for (Bar barItem : barList) {
			result.addAll(barItem.getRecvSateBar());
		}
		// here we print the result
		System.out.println("Your Data" + result);
		return result;
	}

	// Get All RecevState Records
	@GetMapping(value = "/bar/receviceState")
	public List<RecvStateBar> getAllRecevState() {
		return getListRecivedBarByUser();
	}

	// Get All RecevState Records by Date
	@GetMapping(value = "/bar/receviceState/date/{Date}")
	public List<RecvStateBar> getAllRecevStateByDate(@PathVariable(value = "Date") String userDate) {
		// This Line Get All Open state Bar data According to user.
		List<RecvStateBar> fetchOpenStateBar = getListRecivedBarByUser();
		// This Line Declare The empty result of OpenState Bar Which will return By
		// Later
		List<RecvStateBar> result = new ArrayList<RecvStateBar>();
		// Here We Checked the and Filter Data According to date
		for (RecvStateBar barItem : fetchOpenStateBar) {
			if (barItem.getCreatedAt().equals(userDate)) {
				result.add(barItem);
			}
		}
		// Here We what We Get result will be return
		return result;
	}

	// Get RecevState Record By Id
	@GetMapping(value = "/bar/receviceState/id/{recvStateId}")
	public Optional<RecvStateBar> getRecevStateFindByItemId(@PathVariable(value = "recvStateId") Integer rpid,
			Pageable pageable) {
		return revcRepo.findById(rpid);
	}

	// Save RecevState Record
	@PostMapping(value = "/bar/{itemId}/receviceState/save")
	public RecvStateBar saveOpenState(@PathVariable(value = "itemId") Integer bid, @RequestBody RecvStateBar recvStateBar) {
		Bar findBar = barRepo.findById(bid).orElseThrow(() -> new ResourceNotFoundException("Bar Item ", "id", bid));
		// Here We Get Current Item Qty and Plus With New RecevState Bar Qty So we Get New Qty.
		Long newQty = findBar.getItemQty()+ recvStateBar.getReceQty(); 
		findBar.setItemQty(newQty);
		recvStateBar.setBar(findBar);
		return revcRepo.saveAndFlush(recvStateBar);

	}

	// Save All RecevState Record
	@PostMapping(value = "/bar/{itemId}/receviceState/saveAll")
	public List<RecvStateBar> saveRecevStateBatch(@PathVariable(value = "itemId") Integer bid,
			@RequestBody List<RecvStateBar> recvStateBar) {
		Bar findBar = barRepo.findById(bid)
				.orElseThrow(() -> new ResourceNotFoundException("Recevice State Item ", "id", bid));
		for (RecvStateBar data : recvStateBar) {
			Long newQty = findBar.getItemQty()+ data.getReceQty(); 
			findBar.setItemQty(newQty);
			data.setBar(findBar);
		}
		return revcRepo.saveAll(recvStateBar);
	}

	// Update a RecevState Record
	@PutMapping("/bar/{itemId}/receviceState/{recvStateId}")
	public RecvStateBar updateRecevState(@PathVariable(value = "itemId") Integer bid,
			@PathVariable(value = "recvStateId") Integer rcid, @RequestBody RecvStateBar recvStateBar) {
		if (!barRepo.existsById(bid)) {
			throw new ResourceNotFoundException("Open State Item ", "id", bid);
		}
		RecvStateBar findRecvState = revcRepo.findById(rcid)
				.orElseThrow(() -> new ResourceNotFoundException("Recevice State Not Found ", "id", rcid));
		findRecvState.setCreatedAt(recvStateBar.getCreatedAt());
		findRecvState.setOpenSateBar(recvStateBar.getOpenSateBar());
		findRecvState.setReceQty(recvStateBar.getReceQty());
		findRecvState.setTpNo(recvStateBar.getTpNo());
		return revcRepo.saveAndFlush(findRecvState);
	}

	// Delete a RecevState Record
	@DeleteMapping("/bar/{itemId}/receviceState/{recvStateId}")
	public ResponseEntity<?> deleteRecevState(@PathVariable(value = "itemId") Integer bid,
			@PathVariable(value = "recvStateId") Integer rcid) {
		RecvStateBar findRecvState = revcRepo.findById(rcid).orElseThrow(() -> new ResourceNotFoundException(
				"Recevice State not found with bar item id " + bid + " and recevstate Id " + rcid, null, bid));
		revcRepo.delete(findRecvState);
		return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
	}

}
