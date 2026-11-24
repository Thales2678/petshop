# Sistema de Gerenciamento de Atendimento para Pet Shop

Projeto da disciplina Programação Orientada a Objetos (UFERSA, campus Pau dos Ferros).

Java 17 ou superior + PostgreSQL, gerenciado com Maven.

## Como executar

1. Instale o JDK, o Maven e o PostgreSQL.
2. Crie o banco (uma única vez):

   ```
   psql -U postgres -c "CREATE DATABASE petshop"
   ```

   As tabelas são criadas automaticamente na primeira na execução (`src/main/resources/schema.sql`).
3. Defina as variáveis de ambiente de conexão (`src/main/java/petshop/Banco.java`). 


      | Variável | Padrão |
   |---|---|
   | `PETSHOP_DB_URL` | `jdbc:postgresql://localhost:5432/petshop` |
   | `PETSHOP_DB_USER` | `postgres` |
   | `PETSHOP_DB_PASSWORD` | `postgres` |

4. Execute:

   ```
   mvn compile exec:java
   ```

   Ou no VS Code, também dá para abrir `Main.java` e clicar em **Run**.
   
## Primeiro acesso

Se não existir nenhum funcionário, o sistema cria o administrador inicial:

- e-mail: `admin@petshop.com`
- senha: `admin123`

Entre com ele, cadastre os demais funcionários e altere a senha em **Funcionários > Editar**.

## Perfis e telas

| Perfil | Acesso |
|---|---|
| Administrador | Clientes, animais, funcionários, serviços, agendamentos e relatórios |
| Atendente | Clientes, animais, serviços (consulta) e agendamentos |
| Prestador de serviço | Seus próprios agendamentos, com a ação de concluir |
| Cliente | Seus dados, seus animais, serviços ativos e seus próprios agendamentos |

## Estrutura

```
src/main/java/petshop         Classes do diagrama (Usuario, Cliente, Funcionario, Animal, Servico,
                              Agendamento, Cargo, StatusAgendamento) e apoio (Banco, Validacao, Main)
src/main/java/petshop/gui     Telas (interface gráfica) 
src/main/resources/schema.sql Tabelas do PostgreSQL
```



## Como os dados são salvos

- **Ao abrir o programa** (`Main.main`): `Banco.carregar()` lê todas as tabelas do PostgreSQL uma única vez e monta as listas em memória (cada classe tem a sua lista).
- **Durante o uso**: todos os métodos (cadastrar, listar, buscar, atualizar...) trabalham apenas nas listas.
  Não há consultas ao banco.
- **Ao fechar a janela** (`Main.encerrar`): `Banco.salvar()` apaga o conteúdo das tabelas e grava novamente tudo o que está nas listas, em uma única transação.


