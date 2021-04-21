package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

public class Calculadora {

	public int somar(int a, int b) {
		// TODO Auto-generated method stub
		System.out.println("Estou escutando o método somar");
		return a + b;
	}

	public int subtrair(int a, int b) {
		// TODO Auto-generated method stub
		return a-b;
	}

	public int divide(int a, int b) throws NaoPodeDividirPorZeroException {
		// TODO Auto-generated method stub
		if(b == 0) {
			throw new NaoPodeDividirPorZeroException();
		}
		return a / b;
	}
	
	public int divide(String a, String b) {
		return Integer.valueOf(a) / Integer.valueOf(b);
	}
	
	public void imprime() {
		System.out.println("Passou aqui");
	}

}
