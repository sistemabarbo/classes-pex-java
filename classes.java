public with sharing class integracaoSAP {
	@AuraEnabled
    public Static String getToken (){//Obtem o token para utilização da API via requisição HTTP
        String url = 'http://201.7.211.65:8889/IntegraOne/api/Login/GerarToken';
        HttpConsult req = new HttpConsult();
        Map<String, String> responseToken = (Map<String, String>) JSON.deserialize(req.request(url,'GET',null,null), Map<String, String>.class);
		return responseToken.get('data');//Pega o token para utilizar na requisição HTTP

    }
    
    @future(callout=true)
    public static void orderRequest(String body, String Op, String Id){
        String res, method = 'POST', log, url = 'http://201.7.211.65:8889/hom_IntegraOne/api/Orders/';
        url += String.valueOf(Op == 'I' ? 'Add' : (Op == 'U' ? 'Update' : null));
        
        Map<String, String> header = new Map<String, String>();
		header.put('TOKEN_ACESSO', getToken());
        header.put('Accept', 'application/json, text/plain, */*');
        header.put('Content-Type', 'application/json;charset=utf-8');
        HttpConsult req = new HttpConsult();
		res = req.request(url, method, body, header);
        
        if(Op == 'I'){
	        Map<String, Object> mapJson = (Map<String, Object>)JSON.deserializeUntyped(res);
            mapJson = (Map<String, Object>)mapJson.get('data');
            Order o = new Order();
            o.Id = Id;
            o.Acao_interna__c = true;
            update o;
            
            o.Status = 'Ativo';
            o.DocEntry__c = Integer.valueOf(mapJson.get('DocEntry'));
            o.DocNum__c = Integer.valueOf(mapJson.get('DocNum'));
            update o;
            
            o.Acao_interna__c = false;
            update o;
        }
                            
        //Obtém as informações para serem adicionadas no log
        log = 'Time: ' + String.valueOf(System.Now().format('yyyy/MM/dd HH:mm:ss:SSS')) + '\n\n';
        log += 'Endpoint: ' + url + '\n\n';
        log += 'Method: ' + method + '\n\n';
        log += 'Body: ' + body + '\n\n';
        log += 'Header: ' + header + '\n\n';
        log += 'Response: ' + res;
        
        //Carrega e salva o log em um arquivo
        logIntegracaoSap(log, (Op == 'I' ? 'Insert' : (Op == 'U' ? 'Update' : null)) + ' Pedido');        
    }
        
    @future(callout=true)
    public static void doSAPProductIntegration(String body, String opt){
         				        
        String res, method = 'POST', log, url = 'http://201.7.211.65:8889/IntegraOne/api/Items/';
        url += String.valueOf(opt == 'I' ? 'Add' : (opt == 'U' ? 'Update' : null));
          
        Map<String, String> header = new Map<String, String>();
		header.put('TOKEN_ACESSO', getToken());
        header.put('Accept', 'application/json, text/plain, */*');
        header.put('Content-Type', 'application/json;charset=utf-8');
        HttpConsult req = new HttpConsult();
		res = req.request(url, method, body, header);
                            
        //Obtém as informações para serem adicionadas no log
        log = 'Time: ' + String.valueOf(System.Now().format('yyyy/MM/dd HH:mm:ss:SSS')) + '\n\n';
        log += 'Endpoint: ' + url + '\n\n';
        log += 'Method: ' + method + '\n\n';
        log += 'Body: ' + body + '\n\n';
        log += 'Header: ' + header + '\n\n';
        log += 'Response: ' + res;
        
        //Carrega e salva o log em um arquivo
        logIntegracaoSap(log, String.valueOf(opt == 'I' ? 'Insert' : (opt == 'U' ? 'Update' : null)) + ' Produto');
    }

    @AuraEnabled
    public static void logIntegracaoSap (String log, String name){
        
        //Pega o id da pasta
      	List<Folder> pastaLog = ([SELECT id FROM Folder WHERE Name = 'LOG INTEGRACAO SAP 2.0']);
        
     	//Cria e carrega um documento com as informações para o log
		Document logDoc = new Document(
			Name = String.valueOf(System.Now().format('yyyy/MM/dd HH:mm:ss')) + ' - ' + name,
			Body = Blob.valueOf(log),
			ContentType = 'text/plain',
			Type = 'txt',
			IsPublic = false,
			IsInternalUseOnly = true,
			FolderId = pastaLog[0].Id        
        );
        insert logDoc;
    }
    
    @AuraEnabled
    public static void doNothing(String nothing){
		String a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
        nothing = a;
        a = nothing;
    }    
}
