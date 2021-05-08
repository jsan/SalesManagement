# Sales Management Application
Manage your products, your clients, your sellers and your sales. 


As administrator user, you can catalog products to your stock, catalog clients and sellers users. 

A seller user, if active, can make sale orders registration to clients from the database.
 
A sale order has in it, the buyer client, 
product or a list of products, quantity per product with total price, 
an order discount, freight fee, delivery address, delivery date...).
A sales order can be saved, issued, send to the client email as an order invoice, or it can be canceled.

The dash-board page has information about your sales, such as 
how many items you have in stock remaining, clients registered, average amount of sales. 
sales tracked by a sales chart...

Login as administrator to: http://jmbrewer.herokuapp.com/

user: admin@brewer.com

pass: admin

Step by step first Sale.
 
- on the left menu choose products and the click new product on the right to include a product

- on the left menu choose clients to include a a buyer

- on the left menu choose users to include a seller (or you can use your admin user to register a sale too)

- on the left menu choose register a order to make a sale.

- see the status of your sales on the dash-board.


Some Technical specification:
Java 1.8.201 / Spring MVC / Spring Security / Thymeleaf server pages /
JPA / Hibernate / MySql / JPQL / Hibernate criteria / 
Production Server Heroku: Deploy Tomcat-based Java Web Applications with Webapp Runner

Portuguese
----------
Aplicativo de gerenciamento de vendas
Gerencie seus produtos, seus clientes, seus vendedores e suas vendas.

Como usuário administrador, você pode catalogar produtos para seu estoque, catalogar clientes e usuários vendedores.

Um usuário vendedor, se ativo, pode fazer o registro de pedidos de venda para clientes a partir do banco de dados.

Uma ordem de venda contém, como cliente comprador, produto ou lista de produtos, quantidade por produto com preço total, desconto na encomenda, taxa de frete, endereço de entrega, data de entrega ...). Um pedido de venda pode ser salvo, emitido, enviado ao e-mail do cliente como uma fatura de pedido ou pode ser cancelado.

A página do painel contém informações sobre suas vendas, como quantos itens você tem em estoque, clientes cadastrados, quantidade média de vendas. vendas rastreadas por um gráfico de vendas ...

Faça login como administrador em: http://jmbrewer.herokuapp.com/

usuário: admin@brewer.com

passar: admin

Passo a passo primeira venda.

no menu esquerdo escolha produtos e clique em novo produto à direita para incluir um produto

no menu à esquerda, escolha clientes para incluir um comprador

no menu esquerdo, escolha usuários para incluir um vendedor (ou você pode usar seu usuário administrador para registrar uma venda)

no menu à esquerda, selecione registrar um pedido para fazer uma venda.

veja o status de suas vendas no painel.

Algumas especificações técnicas: Java 1.8.201 / Spring MVC / Spring Security / páginas do servidor Thymeleaf / JPA / Hibernate / MySql / JPQL / critérios do Hibernate / Production Server Heroku: implantar aplicativos da Web Java baseados em Tomcat com Webapp Runner
