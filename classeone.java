public class IntegracaoNotaFiscal {
    // Chama a integração e processa as notas fiscais
    @future(callout=true)    
    public static void processarNotasFiscais() {
        
        Datetime agora = Datetime.now();
        Datetime dozeHorasAtras = agora.addHours(-12);
        
        // Formata as datas para o formato 'yyyy-MM-dd HH:mm:ss'
        String agoraStr = agora.format('yyyy-MM-dd HH:mm:ss');
        String dozeHorasAtrasStr = dozeHorasAtras.format('yyyy-MM-dd HH:mm:ss');
        
        System.debug('Data e hora atual: ' + agoraStr);
        System.debug('Data e hora doze horas atrás: ' + dozeHorasAtrasStr);
        
        String endpoint = 'https://api.arquivei.com.br/v1/nfe/received?created_at[from]=' + EncodingUtil.urlEncode(dozeHorasAtrasStr, 'UTF-8') + '&created_at[to]=' + EncodingUtil.urlEncode(agoraStr, 'UTF-8');
        system.debug(endpoint);
        HttpRequest req = new HttpRequest();
        req.setEndpoint(endpoint);
        req.setMethod('GET');
        req.setHeader('Content-Type', 'application/json');
        req.setHeader('X-API-ID', 'e798c8d494ea8982a13788f478fe71970a0303ce');
        req.setHeader('X-API-KEY', 'e36c3cb859461c069b03990a117f305c7d3fa90a');
        
        Http http = new Http();
        HttpResponse res = http.send(req);
        
        system.debug('resposta ' + res.getBody());
        
        if (res.getStatusCode() == 200) {
            Map<String, Object> responseBody = (Map<String, Object>) JSON.deserializeUntyped(res.getBody());
            
            if (responseBody != null) {
                List<Object> notas = (List<Object>) responseBody.get('data');
                
                if (notas != null && !notas.isEmpty()) {
                    // Coleta todas as chaves de acesso das notas recebidas
                    Set<String> accessKeys = new Set<String>();
                    for (Object notaObj : notas) {
                        Map<String, Object> nf = (Map<String, Object>) notaObj;
                        if (nf != null) {
                            String accessKey = (String) nf.get('access_key');
                            if (accessKey != null) {
                                accessKeys.add(accessKey);
                            }
                        }
                    }
                    // Consulta todas as notas fiscais existentes em ambos os objetos
                    List<String> todasNotasExistentesSet = new List<String>();
                    
                    // Consulta e adiciona as chaves de acesso de Nota_Fiscal_de_Pedido_de_Compra__c
                    for (Nota_Fiscal_de_Pedido_de_Compra__c nota : 
                         [SELECT Chave_de_Acesso__c FROM Nota_Fiscal_de_Pedido_de_Compra__c]) {
                             todasNotasExistentesSet.add(nota.Chave_de_Acesso__c);
                         }
                    
                    // Consulta e adiciona as chaves de acesso de Nota_buscada__c
                    for (Nota_buscada__c nota : 
                         [SELECT Chave_de_Acesso__c FROM Nota_buscada__c]) {
                             todasNotasExistentesSet.add(nota.Chave_de_Acesso__c);
                         }
                    
                    // Lista para novas notas fiscais a serem inseridas
                    List<Nota_buscada__c> novasNotas = new List<Nota_buscada__c>();
                    
                    for (Object notaObj : notas) {
                        Map<String, Object> nf = (Map<String, Object>) notaObj;
                        
                        if (nf != null) {
                            String accessKey = (String) nf.get('access_key');
                            
                            String xmlBase64 = (String) nf.get('xml');
                            
                            if (accessKey != null && xmlBase64 != null && !todasNotasExistentesSet.contains(accessKey)) {
                                
                                system.debug('Entrou');
                                // Decodifica o XML de base64
                                Blob xmlBlob = EncodingUtil.base64Decode(xmlBase64);
                                String xml = xmlBlob.toString();
                                
                                // Extrai os valores do XML
                                String Fornecedor = extractValueFromXML(xml, 'xNome');
                                String CNPJ = extractValueFromXML(xml, 'CNPJ');
                                String UF = extractValueFromXML(xml, 'UF');
                                String valorNF = extractValueFromXML(xml, 'vNF');
                                String emissao = extractValueFromXML(xml, 'dhEmi');
                                String nNfe = extractValueFromXML(xml, 'nNF');
                                String natOp = extractValueFromXML(xml, 'natOp');
                                String venc = extractValueFromXML(xml, 'dVenc');
                                String comprador;
                                
                                // Encontrar a posição da primeira ocorrência de '</CNPJ><xNome>'
                                Integer firstOccurrence = xml.indexOf('</CNPJ><xNome>');
                                
                                // Encontrar a posição da segunda ocorrência de '</CNPJ><xNome>', começando após a primeira
                                Integer secondOccurrence = xml.indexOf('</CNPJ><xNome>', firstOccurrence + '</CNPJ><xNome>'.length());
                                
                                // Extrair o conteúdo entre a segunda ocorrência de '</CNPJ><xNome>' e a próxima ocorrência de '</xNome><enderDest>'
                                if (secondOccurrence != -1) {
                                    comprador = xml.substring(secondOccurrence + '</CNPJ><xNome>'.length(), xml.indexOf('</xNome><enderDest>', secondOccurrence));
                                }
                                
                                System.debug('Comprador: ' + comprador); 
                                
                                Date dataEmissao = Date.valueOf(emissao.substringBefore('T'));
                                Date dataVenc;
                                if(venc != '')
                                    dataVenc = Date.valueOf(venc);
                                
                                // Cria a nova nota fiscal
                                Nota_buscada__c novaNota = new Nota_buscada__c();
                                novaNota.Name = nNfe;
                                novaNota.UF__c = UF;
                                novaNota.Natureza_de_Operacao__c = natOp;
                                novaNota.CNPJ_Fornecedor__c = CNPJ;
                                novaNota.Nome_do_Fornecedor__c = Fornecedor;
                                novaNota.Chave_de_Acesso__c = accessKey;
                                novaNota.Valor_da_nota__c = Decimal.valueOf(valorNF);
                                novaNota.Data_de_emissao__c = dataEmissao;
                                novaNota.Comprador__c = comprador;
                                if(dataVenc != null){
                                    novaNota.Data_de_vencimento__c = dataVenc;
                                }
                                novaNota.XML__c = xml;
                                novasNotas.add(novaNota);
                            }
                        }
                    }
                    
                    // Insere as novas notas fiscais no Salesforce
                    if (!novasNotas.isEmpty()) {
                        insert novasNotas;
                        string log = '';
                        for(Nota_buscada__c nota : novasNotas){
                            log = log +  '\n Nº: ' + nota.Name +  
                                + '\n Chave de acesso:' + nota.Chave_de_Acesso__c + 
                                + '\n Fornecedor:' + nota.Nome_do_Fornecedor__c + 
                                + '\n Data de emissão:' + nota.Data_de_emissao__c + 
                                + '\n XML:' + nota.XML__c;                            
                        }
                        
                        //Cria e carrega um documento com as informações para o log
                        Document logDoc = new Document(
                            Name = String.valueOf(System.Now().format('yyyy/MM/dd HH:mm:ss')) + ' - Notas de Entrada ',
                            Body = Blob.valueOf(log),
                            ContentType = 'text/plain',
                            Type = 'txt',
                            IsPublic = false,
                            IsInternalUseOnly = true,
                            FolderId = '00lU40000081M9x'        
                        );
                        insert logDoc;
                    }
                }
            }
        }
    }
    
    // Método auxiliar para extrair o valor de um campo específico do XML
    private static String extractValueFromXML(String xml, String tagName) {
        return xml.substringAfter('<' + tagName + '>').substringBefore('</' + tagName + '>');
    }
    @future(callout=true)        
    public static void processarNotasFiscaisServ() {
        Datetime agora = Datetime.now();
        Datetime duasHorasAtras = agora.addHours(-12);
        
        // Formata as datas para o formato 'yyyy-MM-dd HH:mm:ss'
        String agoraStr = agora.format('yyyy-MM-dd HH:mm:ss');
        String duasHorasAtrasStr = duasHorasAtras.format('yyyy-MM-dd HH:mm:ss');
        
        System.debug('Data e hora atual: ' + agoraStr);
        System.debug('Data e hora duas horas atrás: ' + duasHorasAtrasStr);
        
        String endpoint = 'https://api.arquivei.com.br/v1/nfse/received?created_at[from]=' + EncodingUtil.urlEncode(duasHorasAtrasStr, 'UTF-8') + '&created_at[to]=' + EncodingUtil.urlEncode(agoraStr, 'UTF-8');
        
        HttpRequest req = new HttpRequest();
        req.setEndpoint(endpoint);
        req.setMethod('GET');
        req.setHeader('Content-Type', 'application/json');
        req.setHeader('X-API-ID', 'e798c8d494ea8982a13788f478fe71970a0303ce');
        req.setHeader('X-API-KEY', 'e36c3cb859461c069b03990a117f305c7d3fa90a');
        
        Http http = new Http();
        HttpResponse res = http.send(req);
        
        if (res.getStatusCode() == 200) {
            Map<String, Object> responseBody = (Map<String, Object>) JSON.deserializeUntyped(res.getBody());
            
            if (responseBody != null) {
                List<Object> notas = (List<Object>) responseBody.get('data');
                
                if (notas != null && !notas.isEmpty()) {
                    // Coleta todas as chaves de acesso das notas recebidas
                    Set<String> accessKeys = new Set<String>();
                    for (Object notaObj : notas) {
                        Map<String, Object> nf = (Map<String, Object>) notaObj;
                        if (nf != null) {
                            String xmlBase64 = (String) nf.get('xml');
                            if (xmlBase64 != null) {
                                Blob xmlBlob = EncodingUtil.base64Decode(xmlBase64);
                                String xml = xmlBlob.toString();
                                string accessKey = xml.substringAfter('<CodigoVerificacao>').substringBefore('</CodigoVerificacao>');
                                accessKeys.add(accessKey);
                            }
                        }
                    }
                    // Consulta todas as notas fiscais existentes em ambos os objetos
                    List<String> todasNotasExistentesSet = new List<String>();
                    
                    // Consulta e adiciona as chaves de acesso de Nota_Fiscal_de_Pedido_de_Compra__c
                    for (Nota_Fiscal_de_Pedido_de_Compra__c nota : 
                         [SELECT Chave_de_Acesso__c FROM Nota_Fiscal_de_Pedido_de_Compra__c]) {
                             todasNotasExistentesSet.add(nota.Chave_de_Acesso__c);
                         }
                    
                    // Consulta e adiciona as chaves de acesso de Nota_buscada__c
                    for (Nota_buscada__c nota : 
                         [SELECT Chave_de_Acesso__c FROM Nota_buscada__c]) {
                             todasNotasExistentesSet.add(nota.Chave_de_Acesso__c);
                         }
                    
                    system.debug('TODAS NOTAS: ' + todasNotasExistentesSet);
                    
                    // Lista para novas notas fiscais a serem inseridas
                    List<Nota_buscada__c> novasNotas = new List<Nota_buscada__c>();
                    
                    for (Object notaObj : notas) {
                        Map<String, Object> nf = (Map<String, Object>) notaObj;
                        
                        if (nf != null) {              
                            String xmlBase64 = (String) nf.get('xml');
                            // Decodifica o XML de base64
                            Blob xmlBlob = EncodingUtil.base64Decode(xmlBase64);
                            String xml = xmlBlob.toString();
                            string accessKey = xml.substringAfter('<CodigoVerificacao>').substringBefore('</CodigoVerificacao>');
                            
                            if (accessKey != null && xmlBase64 != null && !todasNotasExistentesSet.contains(accessKey)) {                             
                                
                                // Extrai os valores do XML
                                String Fornecedor = extractValueFromXML(xml, 'RazaoSocial');
                                String CNPJ = extractValueFromXML(xml, 'Cnpj');
                                String UF = extractValueFromXML(xml, 'Uf');
                                String valorNF = extractValueFromXML(xml, 'ValorServicos');
                                String emissao = extractValueFromXML(xml, 'DataEmissao');
                                String nNfe = extractValueFromXML(xml, 'Numero');
                                //String natOp = extractValueFromXML(xml, 'Discriminacao');
                                String comprador = xml.substringAfter('</IdentificacaoTomador><RazaoSocial>').substringBefore('</RazaoSocial>');                                               
                                
                                // Cria a nova nota fiscal
                                Nota_buscada__c novaNota = new Nota_buscada__c();
                                novaNota.Name = nNfe;
                                novaNota.UF__c = UF;
                                novaNota.Natureza_de_Operacao__c = 'Serviço';
                                novaNota.CNPJ_Fornecedor__c = CNPJ;
                                novaNota.Nome_do_Fornecedor__c = Fornecedor;
                                novaNota.Chave_de_Acesso__c = accessKey;
                                novaNota.Valor_da_nota__c = Decimal.valueOf(valorNF);
                                novaNota.Data_de_emissao__c = date.valueOf(emissao.substringBefore('T'));
                                novaNota.Comprador__c = comprador;
                                
                                novaNota.XML__c = xml;
                                novasNotas.add(novaNota);
                            }
                        }
                    }
                    
                    // Insere as novas notas fiscais no Salesforce
                    if (!novasNotas.isEmpty()) {
                        insert novasNotas;
                        string log = '';
                        for(Nota_buscada__c nota : novasNotas){
                            log = log +  '\n Nº: ' + nota.Name +  
                                + '\n Chave de acesso:' + nota.Chave_de_Acesso__c + 
                                + '\n Fornecedor:' + nota.Nome_do_Fornecedor__c + 
                                + '\n Data de emissão:' + nota.Data_de_emissao__c + 
                                + '\n XML:' + nota.XML__c;                            
                        }
                        
                        //Cria e carrega um documento com as informações para o log
                        Document logDoc = new Document(
                            Name = String.valueOf(System.Now().format('yyyy/MM/dd HH:mm:ss')) + ' - Notas de Serviço ',
                            Body = Blob.valueOf(log),
                            ContentType = 'text/plain',
                            Type = 'txt',
                            IsPublic = false,
                            IsInternalUseOnly = true,
                            FolderId = '00lU40000081M9x'        
                        );
                        insert logDoc;
                    }
                }
            }
        }
    }
}
