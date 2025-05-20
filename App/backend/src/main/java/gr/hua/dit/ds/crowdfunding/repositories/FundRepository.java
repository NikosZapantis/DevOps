package gr.hua.dit.ds.crowdfunding.repositories;

import gr.hua.dit.ds.crowdfunding.entities.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends JpaRepository<Fund, Integer> {
}
