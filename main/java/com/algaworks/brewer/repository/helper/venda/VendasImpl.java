package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filters.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);

		paginacaoUtil.preparar(criteria, pageable);
		
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, totalRegs(filtro));
	}
	

	@Transactional(readOnly= true)
	@Override
	public Venda buscaComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class); // find na Venda
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN); 	// join com Itens
		criteria.add(Restrictions.eq("codigo", codigo)); 				// where "codigo" = codigo
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); 	// agrupa  os registros repetidos
		return (Venda) criteria.uniqueResult();
	}

	@Override
	public BigDecimal valorTotalNoAno() { // JPQL usa propriedades da classe (Venda), sql usa da table
		Optional<BigDecimal> optional = Optional.ofNullable(manager.createQuery("select sum(valorTotal) from Venda "
				+ "where "
				+ "year(dataCriacao) = :ano "
				+ "and status = :status", BigDecimal.class)
		.setParameter("ano", Year.now().getValue())
		.setParameter("status", StatusVenda.EMITIDA)
		.getSingleResult());
		return optional.orElse(BigDecimal.ZERO); // ou seja se a consulta resultar em null, retornar BigDecimal.ZERO
	}

	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager.createQuery("select sum(valorTotal) from Venda "
				+ "where "
				+ "month(dataCriacao) = :mes "
				+ "and status = :status", BigDecimal.class)
		.setParameter("mes", MonthDay.now().getMonthValue())
		.setParameter("status", StatusVenda.EMITIDA)
		.getSingleResult());
		return optional.orElse(BigDecimal.ZERO); // ou seja se a consulta resultar em null, retornar BigDecimal.ZERO
	}

	@Override
	public BigDecimal valorTicketMedio() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager.createQuery("select sum(valorTotal) / count(*) from Venda "
				+ "where "
				+ "year(dataCriacao) = :ano "
				+ "and status = :status", BigDecimal.class)
		.setParameter("ano", Year.now().getValue())
		.setParameter("status", StatusVenda.EMITIDA)
		.getSingleResult());
		return optional.orElse(BigDecimal.ZERO); // ou seja se a consulta resultar em null, retornar BigDecimal.ZERO
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes() {
		// esta query vai ser buscada no xml na pasta resources/sql. esta pasta está definida no JPAconfig.java
		
		List<VendaMes> listaVendaMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();
		
		List<String> listaDatas = new ArrayList<>();
		
		for(VendaMes lvm : listaVendaMes){
			listaDatas.add(lvm.getMes());
		}
		
		LocalDate data = LocalDate.now();
		
		for (int i = 0; i < 6; i++) {
			String dataCompare = String.format("%d/%02d", data.minusMonths(i).getYear(), data.minusMonths(i).getMonthValue());
			if (!listaDatas.contains(dataCompare)) {
				listaVendaMes.add(i, new VendaMes(dataCompare, 0));
			}
		}
		
		
		return listaVendaMes;
	}	
	
	private long totalRegs(VendaFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(VendaFilter filtro, Criteria criteria) {
		criteria.createAlias("cliente", "c");
		
		if (filtro != null) {
			
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", filtro.getCodigo()));
			}
			if (!StringUtils.isEmpty(filtro.getStatus())) {
				criteria.add(Restrictions.eq("status", filtro.getStatus()));
			}
			
			
			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				criteria.add(Restrictions.ge("dataCriacao", desde));
			}
			
			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", ate));
			}
			
			
			if (filtro.getValorMinimo() != null) {
				criteria.add(Restrictions.ge("valorTotal", filtro.getValorMinimo()));
			}
			if (filtro.getValorMaximo() != null) {
				criteria.add(Restrictions.le("valorTotal", filtro.getValorMaximo()));
			}
			
			
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
			}
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteria.add(Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
			

		}
	}

}
