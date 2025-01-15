
trigger ContratoServicoPrivado on Contrato_de_Servi_o__c (before insert, before update) { 
    for (Contrato_de_Servi_o__c cs:trigger.new) {
        if(cs.Editar_Timbre__c == false){
            if(cs.Locador_a__c == '0015A00001yGbVZQA0')
            {
                cs.Timbre__c = 'GDB';
            }
            if(cs.Locador_a__c == '001i00000085QYbAAM')
            {
                cs.Timbre__c = 'HOSPCOM';
            }
            if(cs.Locador_a__c == '0015A00001tSrPaQAK')
            {
                cs.Timbre__c = 'HEALTH SOLUTION';
            }
        }
    
    
            
        Account Locador = [SELECT Name, CNPJ__c, CPF__pc, BillingStreet, BillingCity, BillingState, BillingPostalCode, BillingAddress, Raz_o_Social__c 
        FROM Account 
        WHERE Id = :cs.Locador_a__c];
       
        Account Locatario = [SELECT Name, CNPJ__c, CPF__pc, BillingStreet, BillingCity, BillingState, BillingPostalCode, BillingAddress, Raz_o_Social__c 
        FROM Account 
        WHERE Id = :cs.Locat_rio_a__c];
        
        String Locadora = '';
        String Phrase = Locador.BillingStreet + ', ' + Locador.BillingCity + ', ' + Locador.BillingState + ', ' + Locador.BillingPostalCode;
    
          Set<String> forceLower = new Set<String>{'de', 'the', 'para', 'e', 'a', 'o', 'at' ,'an', 'mas', 'se', 'ou', 'nem'};
        
          if(phrase != null && phrase.length() > 0){
            String[] splitPhrase = phrase.trim().split(' ');
                    
            for(integer i = 0; i < splitPhrase.size(); i++){
              if(!forceLower.contains(splitPhrase[i].toLowerCase()) || i == 0 || i == (splitPhrase.size()-1) ){
                Locadora += (splitPhrase[i].substring(0,1).toUpperCase())+(splitPhrase[i].substring(1).toLowerCase())+' ';
              }else{
                Locadora += splitPhrase[i].toLowerCase()+' ';
              }
            }
          }
        string vmensal =  string.valueOF(cs.Valor_Mensal_do_Contrato__c);
        vmensal = vmensal.replace('.',',');
        String Locataria = '';
        String Phrase2 = Locatario.BillingStreet + ', ' + Locatario.BillingCity + ', ' + Locatario.BillingState + ', ' + Locatario.BillingPostalCode;
          
          if(phrase2 != null && phrase2.length() > 0){
            String[] splitPhrase = phrase2.trim().split(' ');
                    
            for(integer i = 0; i < splitPhrase.size(); i++){
              if(!forceLower.contains(splitPhrase[i].toLowerCase()) || i == 0 || i == (splitPhrase.size()-1) ){
                Locataria += (splitPhrase[i].substring(0,1).toUpperCase())+(splitPhrase[i].substring(1).toLowerCase())+' ';
              }else{
                Locataria += splitPhrase[i].toLowerCase()+' ';
              }
            }
          }
         double periodo;
         string speriodo;
         string diames;
        
        if(cs.Dura_o_do_Contrato_meses__c > 1 && cs.Dura_o_do_Contrato_meses__c < 10){
            periodo = cs.Dura_o_do_Contrato_meses__c;
            speriodo = String.valueOf(periodo).left(1);
            diames = 'meses';
        }
        
        if(cs.Dura_o_do_Contrato_meses__c >= 10 && cs.Dura_o_do_Contrato_meses__c < 100){
            periodo = cs.Dura_o_do_Contrato_meses__c;
            speriodo = String.valueOf(periodo).left(2);
            diames = 'meses';
        }
        
        if(cs.Dura_o_do_Contrato_meses__c < 1){
            periodo = math.round(cs.Dura_o_do_Contrato_meses__c*30);
            speriodo = String.valueOf(periodo).left(1);
            diames = 'dia(s)';
        }
        
        if(cs.Dura_o_do_Contrato_meses__c == 1){
            periodo = cs.Dura_o_do_Contrato_meses__c;
            speriodo = String.valueOf(periodo).left(2);
            diames = 'mês';
        }
        
        string servicoaserrealizado = '';
        
        if(cs.Manut_Corretiva_Apenas_Mao_de_Obra__c == true){
            servicoaserrealizado += ' mão de obra qualificada de manutenção corretiva,';
        }
        if(cs.Manut_Corretiva_Mao_de_Obra_e_Pecas__c == true){
            servicoaserrealizado += ' manutenção corretiva com fornecimento de mão de obra qualificada e peças,';
        }
        if(cs.Manut_Preventiva_Apenas_Mao_de_Obra__c == true){
            servicoaserrealizado += ' manutenção preventiva com fornecimento de mão de obra qualificada,';
        }
        if(cs.Manut_Preventiva_Mao_de_Obra_e_Pecas__c == true){
            servicoaserrealizado += ' manutenção preventiva com fornecimento de mão de obra qualificada e peças,';
        }
        if(cs.Calibra_o__c == true){
            servicoaserrealizado += ' calibração,';
        }
        if(cs.Ensaio_de_Seguran_a_El_trica__c == true){
            servicoaserrealizado += ' ensaio de segurança elétrica,';
        }
        if(cs.Qualifica_o__c == true){
            servicoaserrealizado += ' qualificação,';
        }
        
        
        if(cs.RecordTypeId == '0125A000001Qydf'){
             cs.Considera_es_Iniciais__c = '<p style="text-align: justify"> <b>Pelo presente instrumento particular:</b></p>';
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">1. ' + Locador.Raz_o_Social__c + ', inscrita no CNPJ/MF sob o n° ' + Locador.CNPJ__c + ', com sede no endereço: ' + Locadora + 'por seu(s) representante(s) legais abaixo assinado(s), doravante denominada "LOCADORA" e </p>' ;
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">2. ' + Locatario.Raz_o_Social__c +  ', inscrita no CNPJ/MF sob o n° ' + Locatario.CNPJ__c + ', situada no endereço: ' +  Locataria + ', representada neste ato, por seu(s) representante(s) legais abaixo assinado(s), doravante denominado(a) \"LOCATÁRIA\"; e </p>';
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">Celebram o presente Contrato de locação de equipamentos eletromédicos, hospitalares e/ou laboratoriais, doravante o "Contrato", de acordo com as seguintes cláusulas e condições: </p>';
             cs.Considera_es_Iniciais__c += 'que em conjunto  com a "LOCADORA", denominadas Partes.';
        }
        
        if(cs.RecordTypeId == '0125A000001QydL'){
             cs.Considera_es_Iniciais__c = '<p style="text-align: justify"> <b>Pelo presente instrumento particular:</b></p>';
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">1. ' + Locador.Raz_o_Social__c + ', inscrita no CNPJ/MF sob o n° ' + Locador.CNPJ__c + ', com sede no endereço: ' + Locadora + 'por seu(s) representante(s) legais abaixo assinado(s), doravante denominada "CONTRATADA" e </p>' ;
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">2. ' + Locatario.Raz_o_Social__c +  ', inscrita no CNPJ/MF sob o n° ' + Locatario.CNPJ__c + ', situada no endereço: ' +  Locataria + ', representada neste ato por doravante denominado(a) \"CONTRATANTE\"; e em conjunto  com a "CONTRATADA", denominadas Partes. </p>';
             cs.Considera_es_Iniciais__c += '<p style="text-align: justify">Celebram o presente Contrato de serviços em equipamentos eletromédicos, hospitalares e/ou laboratoriais, doravante o "Contrato", de acordo com as seguintes cláusulas e condições: </p>';
        }
        
        
        
        if(cs.Cl_usula_Primeira_Editada__c == false){
            if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Primeira__c = '<p style="text-align: justify;text-decoration:underline;"><b>CLÁUSULA PRIMEIRA - DO OBJETO DO CONTRATO</b></p>';
                 cs.Cl_usula_Primeira__c += '<p style="text-align: justify">1.1. Constitui objeto do presente contrato, a locação de equipamentos que ficarão sob a guarda e disposição da LOCATÁRIA ininterruptamente, durante o prazo contratual. Os equipamentos cujo modelo e quantidade fazem parte deste contrato são:</p>';
            }
            if(cs.RecordTypeId == '0125A000001QydL'){
                cs.Cl_usula_Primeira__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA PRIMEIRA - DO OBJETO</b></p>';
                cs.Cl_usula_Primeira__c += '<p style="text-align: justify;">Constitui objeto do presente contrato, a prestação de serviços pela CONTRATADA de: ' + servicoaserrealizado + 'dos equipamentos da contratante da seguinte forma:</p>';
            }
            
         }
    
        
        if(cs.Cl_usula_Segunda_Editada__c == false){
            if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Segunda__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SEGUNDA - DAS OBRIGAÇÕES DA LOCADORA</b></p>'; 
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.1. A LOCADORA oferece plena garantia do perfeito funcionamento dos equipamentos, quando da respectiva instalação, obedecidas as especificações técnicas, podendo os equipamentos, objeto do presente contrato, ser previamente revisados, dentro dos mais rigorosos padrões técnicos e de controle de qualidade.</p>';
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.2. A LOCADORA entregará e instalará os equipamentos no local indicado pela LOCATÁRIA em perfeitas condições de servir ao uso a que se destina, do que receberá um comprovante da mesma. </p>';      
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.3. É de responsabilidade da LOCADORA, ou por terceiros por ela credenciados, sem qualquer ônus para a LOCATÁRIA, os serviços técnicos, manutenção e reparo dos equipamentos, incluindo todas as peças que se fizerem  necessárias em decorrência do uso normal do equipamento. Esses serviços serão prestados exclusivamente no local de instalação previamente acordado;</p>';
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.4. A LOCADORA deverá realizar os atendimentos de segunda à sexta-feira, das 08h às 18h, salvo feriados. Se necessário que estes serviços sejam prestados fora deste horário, a pedido da LOCATÁRIA, as despesas de atendimento extraordinário serão cobradas. Nas localidades de difícil acesso ou por necessidades técnicas, onde não haja condições de atendimento in loco pela LOCADORA, ou por terceiros credenciados, a assistência será prestada em local previamente acordado entre as partes, correndo os gastos referentes ao transporte do equipamento por conta da LOCADORA;</p>';
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.5. Quando necessário a substituição de partes e peças originais e não haja estas disponíveis no mercado, a LOCADORA utilizará partes e peças, novas ou não, desde que mantenha as especificações técnicas do fabricante;</p>';
                 cs.Cl_usula_Segunda__c += '<p style="text-align: justify">2.6. Os serviços supracitados deverão ser executados em um prazo de até 24 horas, a contar a partir da retirada do equipamento do local onde o mesmo se encontra armazenado;</p>';
            }
            if(cs.RecordTypeId == '0125A000001QydL'){
                cs.Cl_usula_Segunda__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SEGUNDA - DAS OBRIGAÇÕES DA CONTRATANTE:</b></p>';
                cs.Cl_usula_Segunda__c += '<p style="text-align: justify;">- Exercer a fiscalização e orientação quanto às medidas de biossegurança para garantir a eficiência no(s) serviço(s) prestado(s) buscando a exelência na execução das atividades em todo o processo;</p>';
                cs.Cl_usula_Segunda__c += '<p style="text-align: justify;">- Prestar as informações e os esclarecimentos que venham a ser solicitados pela CONTRATADA com relação ao objeto deste contrato;</p>';
                cs.Cl_usula_Segunda__c += '<p style="text-align: justify;">- Efetuar os pagamentos a que se compromissa por meio deste CONTRATO, na forma estabelecida na Proposta Comercial;</p>';
                cs.Cl_usula_Segunda__c += '<p style="text-align: justify;">- Assegurar o livre acesso dos empregados da CONTRATADA, quando devidamente identificados e uniformizados, aos locais em que devam executar suas tarefas;</p>';

            }
         }
        /* Tipo de Registro sandbox: Locação =  012W00000001DJV, Serviços = 012W00000001DJa
Produção: Locação = 0125A000001Qydf, Serviços = "0125A000001QydL"
*/
        
         if(cs.Cl_usula_Terceira_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Terceira__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA TERCEIRA - DAS OBRIGAÇÕES DA LOCATÁRIA</b></p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.1. A LOCATÁRIA irá se responsabilizar pelas despesas de preparação das instalações elétricas são de responsabilidade exclusiva da Locatária, a qual receberá da Locadora as especificações correspondentes;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.2. A LOCATÁRIA compromete-se a devolver os equipamentos descritos no Anexo I nas mesmas condições em que os recebeu ao final do contrato, salvo os desgastes naturais decorrentes do uso normal destes;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.3. A LOCATÁRIA deverá manter os equipamentos em bom estado de conservação, usando-os corretamente;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.4. A LOCATÁRIA não deverá sublocar, ceder nem transferir a locação, total ou parcial;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.5. A LOCATÁRIA não deverá mover os equipamentos do endereço ao qual foram instalados e entregues pela LOCADORA. Qualquer mudança só será permitida mediante o prévio consentimento por escrito da LOCADORA. Quaisquer despesas decorrentes dessas mudanças de local, inclusive, mas não exclusivamente, transporte, montagem, instalação do equipamento no novo local indicado e novas instalações elétricas, correm por conta exclusiva da LOCATÁRIA;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.6. Não introduzir modificações de qualquer natureza nos equipamentos;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.7. Defender e fazer valer todos os direitos de propriedade e de posse da LOCADORA sobre os equipamentos, inclusive impedindo penhora, sequestro, arresto, arrecadação, etc., por terceiros, notificando-os sobre os direitos de propriedade e de posse da mesma sobre os equipamentos, salvo no caso de cumprimento de decisão judicial;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.8. Comunicar imediatamente à LOCADORA qualquer intervenção ou violação por terceiros de qualquer dos seus direitos em relação aos equipamentos;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.9. Permitir o acesso de pessoal autorizado da LOCADORA para realização da manutenção ou reparos dos equipamentos desde que as visitas sejam agendadas e, ainda, para o seu desligamento ou remoção, nas hipóteses cabíveis;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.10. Responsabilizar-se por qualquer dano, prejuízo ou inutilização dos equipamentos, ressalvadas as hipóteses de casos fortuitos ou de força maior, bem como pelo descumprimento de qualquer de suas obrigações previstas neste contrato ou em lei;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.11. Não permitir que terceiros não autorizados ou credenciados pela LOCADORA intervenham nas partes e nos componentes internos dos equipamentos;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.12. Não utilizar o equipamento em outras modalidades que não seja aquela determinada pelo fabricante;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.13. Ao encerramento do contrato caso o locatário não faça a opção de compra e for devolver o equipamento, faze-lo com todos os acessórios entregue no início da locação;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.14. A LOCATÁRIA obriga-se a pagar pontualmente os aluguéis e as faturas de fornecimento de materiais de consumo, via boleto bancário vinculado a nota fiscal da prestação do serviço, tranferência bancária, ou ainda a cobradores da LOCADORA, quando esta assim o admitir por prévio aviso. As faturas não pagas até o vencimento serão acrescidas da variação do IGP-M, aplicada pelos dias de atraso, cominada, também, multa de 2% (dois por cento)  e juros de mora de 1% (um por cento) ao mês ou fração, sem prejuizo das demais sanções aplicáveis, dentre as quais o desligamento temporário dos equipamentos, a suspensão da Assistência Técnica ou a recisão  deste contrato.;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.15. Em caso de não pagamento da locação, a LOCATÁRIA deverá entregar imediatamente à LOCADORA os equipamentos locados, sob pena de responder por apropriação indébita na forma da lei;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">3.16. A recusa da devolução dos equipamentos ou os danos neles produzidos obriga a LOCATÁRIA, ainda ao ressarcimento pelos danos e lucros cessantes, estes pelo período em que o equipamento deixar de ser utilizado pela LOCADORA;</p>';
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Terceira__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA TERCEIRA - DAS OBRIGAÇÕES DA CONTRATADA</b></p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Executar os serviços objetos desse contrato, por meio de técnicos devidamente qualificados e identificados com crachá e uniforme, usando equipamentos de proteção individual de segurança, fornecidos exclusivamente pela CONTRATADA, na execução dos serviços de acordo com as necessidades de cada ambiente e as normativas em vigor;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Cumprir os prazos de exexcução dos serviços;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Em caso de acidente de trabalho, fica desde já a CONTRATADA responsável exclusivamente por qualquer espécie de indenização pleiteada por seus colaboradores e propostos, principalemnte no tocante a reclamações trabalhistas e acidentes de trabalho;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Oferecer atendimento habilitado para responder, por telefone, e-mail ou pessoalmente as dúvidas básicas e problemas que envolvam o objeto deste CONTRATO;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Prestar os serviços contratados através de técnicos e engenheiros, os quais não terão qualquer vínculo empregatício com a CONTRATANTE;</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- A cada serviço realizado, será emitido um ordem de serviço e para serviços metrológicos, quando for o caso, será emitido certificado(s) do(s) equipamento(s);</p>';
                 cs.Cl_usula_Terceira__c += '<p style="text-align: justify">- Facilitar a meticulosa supervisão dos trabalhos, facultando à CONTRATANTE o acesso a todas as informações acerca dos serviços em execução ou já concluídos;</p>';             
             }
         }
         
         if(cs.Cl_usula_Quarta_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Quarta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA QUARTA - DO PRAZO E REGIME DE LOCAÇÃO </b></p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">4.1. A presente locação vigorará pelo prazo de ' + cs.Dura_o_do_Contrato_meses__c + ' ' + cs.Tipo_da_Dura_o__c + ', contados a partir da data de instalação do equipamento, mediante assinatura do termo de entrega/instalação e de recebimento dos equipamentos, que fará parte integrante deste instrumento;</p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">4.2. Entende-se por data de instalação dos equipamentos o dia em que os mesmos passarem a operar e, após os testes necessários, for constatado por ambas as partes estarem os mesmos em condições satisfatórias.</p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">4.3. O(s) valor(es) especificado(s) na tabela do item 1.1 deste contrato, será mensal, para cada equipamento locado; </p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">4.4. A locação terminará na data em que todos os equipamentos tiverem sido devolvidos à LOCADORA e, no caso de devolução em datas diferentes, a locação continuará em vigor em relação a cada equipamento até à data de sua efetiva devolução;</p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">4.5. Encerrando o prazo contratual, o contrato estará rescindido de pleno direito e, se houver vontade das partes, o mesmo poderá ser prorrogado por aditivo contratual;</p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify"></p>';
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Quarta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA QUARTA - DO PRAZO E REGIME DE EXECUÇÃO </b></p>';
                 cs.Cl_usula_Quarta__c += '<p style="text-align: justify">O(s) serviço(s) objeto deste contrato tem prazo de ' + cs.Dura_o_do_Contrato_meses__c + ' ' + cs.Tipo_da_Dura_o__c + ', contados a partir da data de instalação do equipamento, mediante assinatura do presente contrato.;</p>';                 
             }
         }
             
         if(cs.Cl_usula_Quinta_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Quinta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA QUINTA - DO TERMO DE RECEBIMENTO</b></p>';
                 cs.Cl_usula_Quinta__c += '<p style="text-align: justify">5.1. A entrega dos equipamentos será feita à LOCATÁRIA ou a seus prepostos devidamente credenciados através de carta, declarando que recebeu os equipamentos constantes nos anexos e os vistoriou, considerando-os em perfeito estado de funcionamento e aptos para serem utilizados aos fins a que se destinam. Esta deverá assinar os formulários (termo de entrega e termo de vistoria do equipamento) que acompanham a entrega ou enviados através de e-mail, nos locais indicados pela LOCATÁRIA e dentro do prazo de entrega fixado pela LOCADORA;</p>';
                 cs.Cl_usula_Quinta__c += '<p style="text-align: justify"></p>';       
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Quinta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA QUINTA - DA ALTERAÇÃO</b></p>';
                 cs.Cl_usula_Quinta__c += '<p style="text-align: justify">Parágrafo ùnico - Toda e qualquer alteração nos termos desse CONTRATO durante sua vigência, deverá ser feita através de aditivos contratuais, firmado e assinado entre as partes;</p>';
                 cs.Cl_usula_Quinta__c += '<p style="text-align: justify"></p>';       
             }
         }
             
         if(cs.Cl_usula_Sexta_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Sexta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SEXTA - DA MANUTENÇÃO E MATERIAIS DE CONSUMO</b></p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.1. A LOCADORA se obriga a manter os equipamentos em perfeitas condições de funcionamento, sem qualquer ônus para a Locatária, até o final do presente contrato de locação, prorrogado ou não;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.2. No preço mensal da locação, durante a vigência do contrato, não se encontra incluso o fornecimento de materiais de consumo;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.3. Caso seja necessário o fornecimento de quaisquer materiais de consumo objeto deste instrumento será cobrada da LOCATÁRIA, com base nos preços à época vigentes na tabela da LOCADORA, para pagamento contra a entrega da respectiva nota fiscal;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.4. A LOCATÁRIA fica ciente que eventuais danos causados em componentes, acessórios ou no próprio equipamento por culpa dela, e que resultem em troca destes itens, será feita a cobrança a parte;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.5. A LOCADORA está ciente de que a LOCATÁRIA tem pleno direito de adquirir componentes e materiais de outras fontes, desde de que aprovados pelo fabricante do equipamento. Contudo, esta aquisição é exclusivamente por conta e responsabilidade da LOCATÁRIA, sem o direito de reembolso de valores, não podendo estes valores serem adicionados ou descontados ao valor da locação;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">6.6. Tendo em vista que os equipamentos são de propriedade da LOCADORA, a qual é encarregada da manutenção, esta poderá cobrar os custos decorrentes do reparo dos equipamentos danificados que forem identificados com componentes, acessórios ou materiais fornecidos por terceiros e que causem dano ou seja a causa do problema no equipamento. Nesta situação a LOCATÁRIA será notificada por escrito e o custo do reparo será formulado com base nas peças necessárias para o conserto, somada às horas técnicas no valor de R$ 300,00/hora;</p>';               
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Sexta__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SEXTA - DO PREÇO E FORMA DE PAGAMENTO:</b></p>'; 
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">- Pela execução dos serviços objeto deste contrato, a CONTRATANTE pagará a CONTRATADA, mediante déposito bancário, o valor de ' + cs.Valor_Total_do_Contrato__c     +  ' referente a prestação do serviço. Caso a CONTRATANTE prefira o pagamento poderá ser realizado, previamente acordado entre as partes, através de boleto bancário emitido no primeiro dia útil do mês com vencimento todo dia 10 de cada mês;</p>';
                 cs.Cl_usula_Sexta__c += '<p style="text-align: justify">- Nos preços contratados estão incluídos todos os custos com material de consumo, salários, encargos sociais, previdenciários e trabalhistas de todo o pessoal da CONTRATADA, como também fardamento, transporte de qualquer natureza, materiais empregados, inclusive ferramentas, utensílio e equipamentos utilizados, depreciação, aluguéis, administração, impostos, taxas, emolumentos e quaisquer outros custos que direta ou indiretament, se relacionem com o fiel cumprimento pela CONTRATADA das Obrigações;</p>';                 
             }
         }
         
         if(cs.Cl_usula_S_tima_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_S_tima__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SÉTIMA - DO PREÇO E FORMA DE PAGAMENTO</b></p>';
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.1. Pela locação dos equipamentos descritos neste Contrato a LOCATÁRIA pagará à LOCADORA a importância mensal e fixa especificada no item 1.1 deste contrato em reais;</p>';
                 IF(cs.Forma_de_Pagamento__c=='A VISTA'){
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.2. Pagamento à vista.</p>';                       
                 }
                 
                 else{                      
                             cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.2. Fica ajustado que o primeiro aluguel será faturado do dia da instalação do equipamento até o dia ' + cs.Vencimento_dia__c + ' do mês subsequente totalizando um mês de locação, sendo esta a data limite para pagamento da fatura. Os demais aluguéis serão sempre faturados em até 5 dias úteis após o vencimento da fatura anterior com vencimento para todo dia' + cs.Vencimento_dia__c + ' de cada mês subsequente (em se tratando de sábado, domingo ou feriado, o pagamento bancário deverá ser efetivado no próximo dia útil subsequente).</p>';    
                 }
                 
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.3. Eventuais atrasos de faturamento por parte da LOCADORA e consequentes postergações das respectivas datas de vencimento não serão jamais entendidos, em hipótese alguma, como novação contratual e/ou alteração de regra de faturamento acima estabelecida, a qual, quando retomada, prevalecerá sempre.</p>';
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.4. As faturas não pagas até o vencimento serão acrescidas da variação do IGP-M, aplicada pelos dias de atraso, cominada, também, multa de dois por cento (2%) e juros de mora de um por cento (1%) ao mês ou fração, sem prejuízo das demais sanções aplicáveis, dentre as quais o desligamento temporário dos equipamentos, a suspensão da Assistência Técnica ou a rescisão deste contrato.</p>';
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">7.5. Sem prejuízo dos acréscimos moratórios estabelecidos no item acima, a LOCATÁRIA não cumprindo as obrigações deste contrato, será cominada a multa equivalente a 10% (dez por cento) do valor do aluguel anterior a infração vigente à época, mais custos, despesas e honorários advocatícios, em caso de cobrança judicial, ficando ainda a LOCADORA com o direito de considerar rescindido o presente contrato.</p>';
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                
                 cs.Cl_usula_S_tima__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA SÉTIMA - DO REAJUSTE</b></p>';
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify">Será adotada a variação do IGPM como índice para reajuste anual do CONTRATO após o período de 12 (doze) meses.Na hipótese de haver alteração no sistema monetário nacional — modificação de moeda corrente, alteração e/ou criação de índice que atualize os valores ora contratados, aumento em demasia de custos, etc., as partes contratantes alterarão os preços das parcelas mensais dispostas na Cláusula Sexta desse CONTRATO, para o fim especial de adequá-las ao novo sistema e correção desses valores para que prevaleça, entre as partes, o permanente equilíbrio financeiro ora acordado.;</p>';
                 cs.Cl_usula_S_tima__c += '<p style="text-align: justify"></p>';      
             }
         }
         
         if(cs.Cl_usula_Oitava_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Oitava__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA OITAVA - DA RESCISÃO</b></p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">8.1. Esse contrato poderá ser rescindido por qualquer uma das partes, independente de justificativas, bastando para tanta notificação por via protocolada ou por intermédio de cartório de títulos e documentos para a outra parte com antecedência mínima de 30 (trinta) dias;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">8.2. As partes ajustam que na infração de qualquer das cláusulas contratuais por parte da LOCATÁRIA, a LOCADORA poderá, além de rescindir este contrato, como previsto acima, exigir e obter imediata devolução dos equipamentos, cabendo-lhe inclusive, na via judicial, a reintegração \'initio litis\', válido para os fins do inciso II e III do artigo 560 do Código de Processo Civil de 2015, o documento enviado pela LOCADORA solicitando a devolução dos equipamentos;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">8.3. Poderá ainda a LOCADORA, facultativamente, considerar rescindida a locação e retirar o equipamento locado nas hipóteses de falência ou insolvência da LOCATÁRIA;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">8.4. A infração, por qualquer das partes, das obrigações assumidas no presente contrato dará à outra o direito de rescindi-lo, independentemente de intimação judicial ou extrajudicial, bastando para isso aviso por escrito, com prazo de 30 (trinta) dias contados da inadimplência. A parte que infringir o contrato será cominada a multa equivalente a dez por cento (10%) do valor do aluguel anterior a infração vigente à época;</p>';       
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">8.5. O presente contrato será rescindido imediatamente, no caso de não pagamento das mensalidades por um prazo superior a dois meses;</p>'; 
             }
             IF(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Oitava__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA OITAVA - DA RECISÃO</b></p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">Esse CONTRATO poderá ser rescindido por qualquer das partes, na ocorrência de:;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">- Requerimento de recuperação, seja judicial ou extrajudicial ou decretação de falência de qualquer das partes contratantes;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">- Requerimento e/ou decretação de liquidação extrajudicial ou insolvência civil de qualquer das partes contratantes;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">- Descumprimento de uma ou mais cláusulas ou condições contratuais;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">- Inadimplência no cumprimento das obrigações assumidas sob esse CONTRATO;</p>';
                 cs.Cl_usula_Oitava__c += '<p style="text-align: justify">Parágrafo único - Esse CONTRATO poderá ser rescindido por qualquer uma das partes, idependentemete de justificativas, bastando para tanto uma notificação por via protocolada ou por intermédio de cartório de títulos e documentos para a outra parte com antecedência mínima de 30 (trinta) dias, sem ônus e sem prejuízo das obrigações até a data da rescisão.</p>';

                 
             }
         }
         
         if(cs.Cl_usula_Nona_Editada__c == false){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_Nona__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA NONA - DO REAJUSTE E RENOVAÇÃO DO CONTRATO</b></p>';
                 cs.Cl_usula_Nona__c += '<p style="text-align: justify">9.1. Este contrato poderá ser prorrogado por igual e sucessivo período mediante aditivo contratual;</p>';
                 cs.Cl_usula_Nona__c += '<p style="text-align: justify">9.2. Adota-se a variação do IGPM como índice para reajuste do CONTRATO após o período de 12 (doze) meses. Na hipótese de haver alteração no sistema monetário nacional – modificação de moeda corrente, alteração e/ou criação de índice que atualize os valores ora contratados, aumento em demasia de custos, etc., as partes contratantes alterarão os preços das parcelas mensais, para o fim especial de adequá-las ao novo sistema e correção desses valores para que prevaleça, entre as partes, o permanente equilíbrio financeiro ora acordado;</p>';
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                 cs.Cl_usula_Nona__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA NONA - DO FORO</b></p>';
                 cs.Cl_usula_Nona__c += '<p style="text-align: justify">Para dirimir quaisquer dúvidas oriundas desse CONTRATO, as partes elegem o Foro da Comarca de Goiânia/GO, desistindo de qualquer outro, por mais privilégios que seja;</p>';               
             }
         }
         
         if(cs.Cl_usula_D_cima_Editada__c == false ){
             if(cs.RecordTypeId == '0125A000001Qydf'){
                 cs.Cl_usula_D_cima__c = '<p style="text-align: justify; text-decoration: underline;"><b>CLÁUSULA DÉCIMA - DO FORO</b></p>';
                 cs.Cl_usula_D_cima__c += '<p style="text-align: justify">10.1. Para dirimir quaisquer dúvidas oriundas desse CONTRATO, as partes elegem o Foro Central da Comarca de Goiânia/GO, desistindo de qualquer outro, por mais privilegiado que seja.</p>';
             }
             if(cs.RecordTypeId == '0125A000001QydL'){
                  cs.Cl_usula_D_cima__c = '';
             }
         }
    }
}
