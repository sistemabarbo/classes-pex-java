@isTest(seeAllData=true)
private class TesteGlobal {
	
	// sfdx force:source:deploy -p "C:\Users\Douglas Molina\Documents\professional\workspaces\hospcom\force-app\main\default" -l RunSpecifiedTests -r TesteGlobal
	
	@isTest static void Teste1(){
		Order pedidoOriginal = [
			SELECT 	QuoteId, AccountId, EffectiveDate, Status,  Necess_rio_Treinamento__c, Pedido_da_Linha_de_OPME__c, 
					Entrega_parcial__c, Prazo_de_entrega__c, Faturamento_Feito__c, Vendedor__c
			FROM	Order
			WHERE 	Id = '8015A000002fQTw'
		];
		Order pedidoNovo = pedidoOriginal.clone(false, false, false, false);
		pedidoNovo.RecordTypeId = Schema.getGlobalDescribe().get('Order').getDescribe().getRecordTypeInfosByName().get('Hospcom').getRecordTypeId();
		insert pedidoNovo;

		update new User(Id=UserInfo.getUserId(), Ignorar_erros__c=true);
		pedidoNovo.Status = 'Ativo';
		pedidoNovo.Acao_interna__c=  true;
		update pedidoNovo;
		update new User(Id=UserInfo.getUserId(), Ignorar_erros__c=true);


		List<OrderItem> itensPedido = [Select Id, Status__c FROM OrderItem WHERE OrderId = :pedidoNovo.Id];
		for(OrderItem itemPedido : itensPedido)
			itemPedido.Status__c = 'Novo';
		update itensPedido;
	}

	@isTest static void Teste2(){
		Order pedidoOriginal = [
			SELECT 	Ordem_de_trabalho__c, AccountId, EffectiveDate, Status,  Necess_rio_Treinamento__c, Pedido_da_Linha_de_OPME__c, 
					Entrega_parcial__c, Prazo_de_entrega__c, Faturamento_Feito__c, Vendedor__c, Condicao_de_pagamento__c,
					Forma_de_pagamento2__c, Frete__c
			FROM	Order
			WHERE 	Id = '8015A000001ZCgQQAW'
		];
		Order pedidoNovo = pedidoOriginal.clone(false, false, false, false);
		pedidoNovo.RecordTypeId = Schema.getGlobalDescribe().get('Order').getDescribe().getRecordTypeInfosByName().get('Hospcom').getRecordTypeId();
		insert pedidoNovo;
	}

	@isTest static void Teste3(){
		Order pedidoOriginal = [
			SELECT 	Demonstracao__c, AccountId, EffectiveDate, Status,  Necess_rio_Treinamento__c, Pedido_da_Linha_de_OPME__c, 
					Entrega_parcial__c, Prazo_de_entrega__c, Faturamento_Feito__c, Vendedor__c, Condicao_de_pagamento__c,
					Forma_de_pagamento2__c, Frete__c
			FROM	Order
			WHERE 	Id = '8015A000002fv8aQAA'
		];
		Order pedidoNovo = pedidoOriginal.clone(false, false, false, false);
		pedidoNovo.RecordTypeId = Schema.getGlobalDescribe().get('Order').getDescribe().getRecordTypeInfosByName().get('Hospcom').getRecordTypeId();
		insert pedidoNovo;
	}

	@isTest static void Teste4(){
		
		Order pedidoOriginal = [
			SELECT 	AccountId, EffectiveDate, Status,  Necess_rio_Treinamento__c, Pedido_da_Linha_de_OPME__c, 
					Entrega_parcial__c, Prazo_de_entrega__c, Vendedor__c
			FROM	Order
			WHERE 	Id = '8015A000002fQTw'
		];
		Order pedidoNovo = new Order();
		pedidoNovo.AccountId = pedidoOriginal.AccountId;
		pedidoNovo.EffectiveDate = pedidoOriginal.EffectiveDate;
		pedidoNovo.Necess_rio_Treinamento__c= pedidoOriginal.Necess_rio_Treinamento__c;
		pedidoNovo.Pedido_da_Linha_de_OPME__c= pedidoOriginal.Pedido_da_Linha_de_OPME__c;
		pedidoNovo.Prazo_de_entrega__c= pedidoOriginal.Prazo_de_entrega__c;
		pedidoNovo.Vendedor__c= pedidoOriginal.Vendedor__c;
		pedidoNovo.Status = 'Rascunho';
		pedidoNovo.Faturamento_Feito__c = '001i00000084BQy';
		pedidoNovo.RecordTypeId = [SELECT Id FROM RecordType WHERE SObjectType='Order' AND DeveloperName='Representacao'].Id;
		pedidoNovo.Pricebook2Id = '01s5A000004fbcR';
		insert pedidoNovo;
		
		update new User(Id=UserInfo.getUserId(), Ignorar_erros__c=true);
		pedidoNovo.Status = 'Ativo';
		pedidoNovo.Acao_interna__c=  true;
		update pedidoNovo;
		update new User(Id=UserInfo.getUserId(), Ignorar_erros__c=false);

		List<OrderItem> itensNovosPedido = new List<OrderItem>();
		itensNovosPedido.add(new OrderItem(
			OrderId = pedidoNovo.Id,
			Status__c = 'Novo',
			PricebookEntryId = '01u5A000017V1WBQA0',
			Item__c = 1,
			Quantity = 1,
			UnitPrice = 1
		));

		insert itensNovosPedido;

		for(OrderItem itemPedido : itensNovosPedido){
			itemPedido.Status__c = 'Reservado';
		}
		update itensNovosPedido;
		
	}


	
}
