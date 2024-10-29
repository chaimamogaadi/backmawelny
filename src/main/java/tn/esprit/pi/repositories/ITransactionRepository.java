package tn.esprit.pi.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pi.entities.Investment;
import tn.esprit.pi.entities.Transaction;
import tn.esprit.pi.entities.Type_transaction;
import tn.esprit.pi.entities.User;

import java.util.List;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction,Integer> {

    List<Transaction> findByType(Type_transaction type);
   // List<Transaction> findByInvestment(Investment investment);

  //List<Transaction> findByInvestment(Investment investment);
  List<Transaction> findByUser(User user);
}
