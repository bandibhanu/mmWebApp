package com.mmbank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;

@WebServlet("*.mm")
public class MMBankWebProject extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;

	@Override
	public void init() throws ServletException {
		super.init();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = connection
					.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();

		switch (path) {
		case "/AddNewAccount.mm":
			response.sendRedirect("AddAccount.html");
			break;
		case "/createAccount.mm":
			String accountHolderName = request.getParameter("name");
			double accountBalance = Double.parseDouble(request
					.getParameter("amount"));
			boolean salary = request.getParameter("rdSalaried")
					.equalsIgnoreCase("no") ? false : true;

			try {
				savingsAccountService.createNewAccount(accountHolderName,
						accountBalance, salary);
				response.sendRedirect("index.html");

			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case "/update.mm":
			
			response.sendRedirect("updateAccount.html");
			break;
			
		case "/updateAccount.mm":
			
			int account_id = Integer.parseInt(request
					.getParameter("accountNumber"));
				
			break;
			
		case "/getAllSavingsAccountDetails.mm":
			
		
			List<SavingsAccount> account;
			try {
				account = savingsAccountService.getAllSavingsAccount();
				request.setAttribute("accounts",account);
				dispatcher=request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request,response);
				response.sendRedirect("index.html");
				
				
				//System.out.println(account);
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			break;
		
		case "/closeAccount.mm":
			response.sendRedirect("deleteAccount.html");
			break;
			
		case "/deleteAccount.mm":

			 account_id = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				savingsAccountService.deleteAccount(account_id);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
                       
		case "/withdraw.mm":
			response.sendRedirect("withdraw.html");
			break;

		case "/withdrawmoney.mm":
			SavingsAccount savingsAccount = null;

			int accountNumber = Integer.parseInt(request
					.getParameter("accountNumber"));
			double amount = Double.parseDouble(request.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNumber);
				savingsAccountService.withdraw(savingsAccount, amount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/deposit.mm":
			
			response.sendRedirect("deposit.html");
			break;
			
		case "/depositamount.mm":
			
			savingsAccount = null;
			accountNumber = Integer.parseInt(request
					.getParameter("accountNumber"));
			amount = Double.parseDouble(request.getParameter("amount"));

			try {
				savingsAccount = savingsAccountService
						.getAccountById(accountNumber);
				savingsAccountService.deposit(savingsAccount, amount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		
		case "/fundTransfer.mm":
			response.sendRedirect("fundTransfer.html");
			break;
			
		case "/amountTransfer.mm":
			savingsAccount = null;
			int sender = Integer.parseInt(request
					.getParameter("senderAccountNumber"));
			int receiver = Integer.parseInt(request
					.getParameter("receiverAccountNumber"));
			amount = Double.parseDouble(request.getParameter("amount"));

			try {
				SavingsAccount senderSavingsAccount = savingsAccountService
						.getAccountById(sender);
				SavingsAccount receiverSavingsAccount = savingsAccountService
						.getAccountById(receiver);
				savingsAccountService.fundTransfer(senderSavingsAccount,
						receiverSavingsAccount, amount);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
			
				e.printStackTrace();
			}
			break;

		case "/searchAccount.mm":

			response.sendRedirect("SearchForms.jsp");
			break;
			
		case "/searchForm.mm":
			accountNumber = Integer.parseInt(request
					.getParameter("txtAccountNumber"));
			try {
				SavingsAccount account1 = savingsAccountService
						.getAccountById(accountNumber);
				// System.out.println(account);
				request.setAttribute("account", account1);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;

		case "/sortByName.mm":
			
			try {
				Collection<SavingsAccount> accounts = savingsAccountService
						.getAllSavingsAccount();
				Set<SavingsAccount> accountSet = new TreeSet<>(
						new Comparator<SavingsAccount>() {
							@Override
							public int compare(SavingsAccount arg0,
									SavingsAccount arg1) {
								return arg0
										.getBankAccount()
										.getAccountHolderName()
										.compareTo(
												arg1.getBankAccount()
														.getAccountHolderName());
							}
						
						});
				accountSet.addAll(accounts);
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
				
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		
		case "/checkCurrentBalance.mm":
			response.sendRedirect("checkCurrentBalance.html");
			break;
		case "/checkBalance.mm":
			int accountNumberform = Integer.parseInt(request
					.getParameter("accountNumber"));
			try {
				accountBalance = savingsAccountService
						.checkCurrentBalance(accountNumberform);
				System.out.println(accountBalance);
			} catch (ClassNotFoundException | SQLException
					| AccountNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:

			break;
		}

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

}
