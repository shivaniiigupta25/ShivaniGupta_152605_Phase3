package com.cg.paymentwalletapplication.dao;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import com.cg.paymentwalletapplication.dto.CustomerBean;
import com.cg.paymentwalletapplication.exception.PaymentWalletException;
import com.cg.paymentwalletapplication.util.JPAUtil;

public class WalletDaoImpl implements IWalletDao {

	private EntityManager entityManager;

	public WalletDaoImpl() {
		entityManager = JPAUtil.getEntityManager();
	}

	public String createAccount(CustomerBean customerBean) {
		entityManager.getTransaction().begin();
		String builder = "Account Created On:\t" + LocalDateTime.now() + "\n";
		customerBean.setTransaction(builder);
		entityManager.persist(customerBean);
		entityManager.getTransaction().commit();
		return customerBean.getMobileNo();
	}

	public CustomerBean getCustomerDetails(String mobileNumber) {
		entityManager.getTransaction().begin();
		CustomerBean customerBean = entityManager.find(CustomerBean.class, mobileNumber);
		entityManager.getTransaction().commit();
		return customerBean;
	}

	public CustomerBean showBalance(String custContact) {
		entityManager.getTransaction().begin();
		CustomerBean bean = entityManager.find(CustomerBean.class, custContact);
		entityManager.getTransaction().commit();
		return bean;
	}

	public boolean withdrawAmount(double withdrawAmt, String custContact) {
		boolean res = false;
		CustomerBean bean = getCustomerDetails(custContact);
		if (bean != null) {
			res = true;
			entityManager.getTransaction().begin();
			bean.setBalance(bean.getBalance() - withdrawAmt);
			StringBuilder builder = new StringBuilder(
					"Withdrawn\t" + withdrawAmt + "\t" + "Remaining Balance:\t" + bean.getBalance() + "\n");
			bean.setTransaction(bean.getTransaction() + "\n" + builder.toString());
			entityManager.merge(bean);
			entityManager.getTransaction().commit();
		}
		return res;
	}

	public boolean depositAmount(double depositAmt, String custContact) {
		boolean res = false;
		CustomerBean bean = getCustomerDetails(custContact);
		if (bean != null) {
			res = true;
			entityManager.getTransaction().begin();
			bean.setBalance(bean.getBalance() + depositAmt);
			StringBuilder builder = new StringBuilder(
					"Deposited\t" + depositAmt + "\t" + "Remaining Balance:\t" + bean.getBalance() + "\n");
			bean.setTransaction(bean.getTransaction() + "\n" + builder.toString());
			entityManager.merge(bean);
			entityManager.getTransaction().commit();
		}
		return res;
	}

	public boolean fundTransfer(String senderCont, String receiverCont, double custAmount)
			throws PaymentWalletException {
		boolean result = false;
		CustomerBean sender = getCustomerDetails(senderCont);
		CustomerBean receiver = getCustomerDetails(receiverCont);
		if (sender != null && receiverCont != null) {
			result = true;
			entityManager.getTransaction().begin();
			sender.setBalance(sender.getBalance() - custAmount);
			StringBuilder builder1 = new StringBuilder(
					"Transferred\t" + custAmount + "\t" + "Remaining Balance:\t" + sender.getBalance() + "\n");

			sender.setTransaction(sender.getTransaction() + "\n" + builder1.toString());
			receiver.setBalance(receiver.getBalance() + custAmount);
			StringBuilder builder = new StringBuilder(
					"Transferred\t" + custAmount + "\t" + "Remaining Balance:\t" + receiver.getBalance() + "\n");
			receiver.setTransaction(receiver.getTransaction() + "\n" + builder.toString());

			entityManager.merge(sender);
			entityManager.merge(receiver);
			entityManager.getTransaction().commit();
		}
		return result;
	}

	public StringBuilder printTransactions(String custContact) {
		CustomerBean bean = getCustomerDetails(custContact);
		StringBuilder builder = null;
		if (bean != null) {
			String str = bean.getTransaction();
			builder = new StringBuilder(str);
		}
		return builder;
	}

	public CustomerBean login(String custContact, String password) throws PaymentWalletException {
		entityManager.getTransaction().begin();
		CustomerBean bean = entityManager.find(CustomerBean.class, custContact);
		entityManager.getTransaction().commit();
		return bean;
	}

}