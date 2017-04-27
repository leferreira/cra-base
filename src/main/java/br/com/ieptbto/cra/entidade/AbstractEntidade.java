package br.com.ieptbto.cra.entidade;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 
 * @author Lefer
 * 
 * @param <T>
 */
@MappedSuperclass
public abstract class AbstractEntidade<T> implements Serializable, Comparable<T> {

	private static final long serialVersionUID = 1L;

	@Transient
	public abstract int getId();

	public abstract int compareTo(T entidade);
}