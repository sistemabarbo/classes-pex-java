trigger Fornecedor on Fornecedor__c (after insert, after update) {
    
    String acao    			 = Trigger.isInsert?'insert':Trigger.isUpdate?'update':'delete';
    String momento 			 = Trigger.isBefore?'before':'after';
	List<Id> fornecedores_id = new List<Id>();
    
    // identifica fornecedores alterados
    if(momento == 'after'){
        if(acao == 'insert'){
            for(Fornecedor__c fornecedor : Trigger.new){
                fornecedores_id.add(fornecedor.Id);
            }	
        }
        else if(acao == 'update'){
            Integer cont=0;
            for(Fornecedor__c fornecedor : Trigger.new){
                if(Trigger.old[cont].Prazo_de_recebimento__c != fornecedor.Prazo_de_recebimento__c){
					fornecedores_id.add(fornecedor.Id);
                }
                cont++;
            }
        }
    }
    
    // se mecheu no prazo, no fornecedor
    if(fornecedores_id.size() > 0){
		
		// obtem itens de fornecedor <s/ data> + fornecedor <c/ data> + item de compra
        List<Item_de_fornecedor__c> itens_fornecedor = [
            SELECT   Id, Item_de_pedido_de_compra__c, Fornecedor__r.Prazo_de_recebimento__c
            FROM     Item_de_fornecedor__c
            WHERE    Fornecedor__c IN :fornecedores_id AND
					 Prazo_de_recebimento__c = null AND
					 Comprar__c = true
            ORDER BY Id DESC
        ];
		List<Id> itens_compra_id = new List<Id>();
		for(Item_de_fornecedor__c item_fornecedor : itens_fornecedor)
			itens_compra_id.add(item_fornecedor.Item_de_pedido_de_compra__c);
		
		// obtem requisicoes + item de compra
		List<Requisicao_de_item__c> requisicoes_item = [
			SELECT	Id, Prazo_de_recebimento__c, Item_de_pedido_de_compra__c
			FROM 	Requisicao_de_item__c
			WHERE	Item_de_pedido_de_compra__c IN :itens_compra_id
		];
		List<Requisicao_de_item__c> requisicoes_item_bak = requisicoes_item.deepClone(true, true, true);
		
		// replica data dos fornecedores, nas requisoções de item
		for(Requisicao_de_item__c requisicao_item : requisicoes_item){
			requisicao_item.Prazo_de_recebimento__c = null;
			for(Item_de_fornecedor__c item_fornecedor : itens_fornecedor){
				if(requisicao_item.Item_de_pedido_de_compra__c == item_fornecedor.Item_de_pedido_de_compra__c){
					requisicao_item.Prazo_de_recebimento__c = item_fornecedor.Fornecedor__r.Prazo_de_recebimento__c;
				}
			}			
		}
		
		// salva alterações
        for(Integer cont_bak = requisicoes_item_bak.size()-1; cont_bak>=0; --cont_bak){
            for(Integer cont = requisicoes_item.size()-1; cont>=0; --cont){
                if(requisicoes_item_bak[cont_bak].Id == requisicoes_item[cont].Id){
                    if(requisicoes_item_bak[cont_bak] == requisicoes_item[cont])
                        requisicoes_item.remove(cont);
                    break;              
                }
            }
        }
        if(requisicoes_item.size()>0){
            update requisicoes_item;
        }
    }
    
}
