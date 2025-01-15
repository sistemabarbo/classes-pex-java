trigger ItemDePedidoDeCompra on Item_de_pedido_de_compra__c (after insert, after update) {
	
    String   acao    			= Trigger.isInsert?'insert':Trigger.isUpdate?'update':'delete';
    String   momento 			= Trigger.isBefore?'before':'after';
	List<Id> itens_compra_id	= new List<Id>();
    
    // identifica itens de compra alterados
    if(momento == 'after'){
        if(acao == 'insert'){
            for(Item_de_pedido_de_compra__c item_compra : Trigger.new){
                itens_compra_id.add(item_compra.Id);
            }
        }
        else if(acao == 'update'){
            Integer cont=0;
            for(Item_de_pedido_de_compra__c item_compra : Trigger.new){
                if((Trigger.old[cont].Prazo_de_recebimento__c != item_compra.Prazo_de_recebimento__c)||
				   ((Trigger.old[cont].Quantidade_comprada__c != item_compra.Quantidade_comprada__c))){
					itens_compra_id.add(item_compra.Id);
                }
                cont++;
            }
        }
    }
    
    // se mecheu no prazo, no item de compra
    if(itens_compra_id.size() > 0){
		
		// obtem requisições + itens de compra <c/ s/ data>
		List<Requisicao_de_item__c> requisicoes_item = [
			SELECT	Id, Prazo_de_recebimento__c, Comprar__c, Item_de_pedido_de_compra__c, 
					Item_de_pedido_de_compra__r.Prazo_de_recebimento__c, Item_de_pedido_de_compra__r.Quantidade_comprada__c
			FROM 	Requisicao_de_item__c
			WHERE	Item_de_pedido_de_compra__c IN :itens_compra_id
		];
		List<Requisicao_de_item__c> requisicoes_item_bak = requisicoes_item.deepClone(true, true, true);
		
		// obtem itens de fornecedores + fornecedores <c/ data>
		List<Item_de_fornecedor__c> itens_fornecedor = [
			SELECT	Id, Item_de_pedido_de_compra__c, Fornecedor__r.Prazo_de_recebimento__c
			FROM	Item_de_fornecedor__c
			WHERE	Item_de_pedido_de_compra__c IN :itens_compra_id AND Comprar__c = true
		];
		
		// replica data dos pedidos de compra, nas requisoções de item
		for(Requisicao_de_item__c requisicao_item : requisicoes_item){
			requisicao_item.Prazo_de_recebimento__c = null;
			if(requisicao_item.Item_de_pedido_de_compra__r.Prazo_de_recebimento__c != null){
				requisicao_item.Prazo_de_recebimento__c = requisicao_item.Item_de_pedido_de_compra__r.Prazo_de_recebimento__c;
			}
			else{
				for(Item_de_fornecedor__c item_fornecedor : itens_fornecedor){
					if(item_fornecedor.Item_de_pedido_de_compra__c == requisicao_item.Item_de_pedido_de_compra__c){
						requisicao_item.Prazo_de_recebimento__c = item_fornecedor.Fornecedor__r.Prazo_de_recebimento__c;
					}
				}
			}
			
			requisicao_item.Comprar__c = false;
			if(requisicao_item.Item_de_pedido_de_compra__r.Quantidade_comprada__c == 0 || 
			   requisicao_item.Item_de_pedido_de_compra__r.Quantidade_comprada__c == null)
				requisicao_item.Comprar__c = false;
			else 
				requisicao_item.Comprar__c = true;
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
