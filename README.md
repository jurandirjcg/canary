# Canary

Classes para auxilio na utilização de consultas JPA 2.1 e chamadas REST

## Exemplos de consultas

Obter lista paginada de pessoas (retornando nome, data de nascimento, sexo, cidade e logradouro) onde o nome inicie com a letra "d", sexo igual masculino, que o resultado seja ordenado por data de nascimento, quantidade de registros por pagina seja 20 e a página de número 5.
```
.
.
.
public class PessoaDAO extends GenericDAO<Pessoa, Long>{
.
.
.
.

public Pagination<Pessoa> paginarPessoa(){
	Pessoa pessoa = new Pessoa();
	pessoa.setNome("d");
	pessoa.setSexo(Sexo.MASCULINO);
	
	CriteriaFilter<Pessoa> cf = getCriteriaFilter(pessoa)
		.addSelect("nome", "dataNascimento", "sexo", "endereco.cidade", "endereco.logradouro")
		.addWhereILike("nome", MatchMode.START)
		.addOrderAsc("dataNascimento");
			
	return getResultPaginate(cf, 5, 20);
}

```

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)