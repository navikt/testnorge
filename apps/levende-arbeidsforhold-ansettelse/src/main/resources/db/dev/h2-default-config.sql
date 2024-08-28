-- Default verdier for kjøring med H2

insert into jobb_parameter values('stillingsprosent','Stillingsprosent','80','{20,30,40,50,60,70,80,90,100}');
insert into jobb_parameter values('intervall','Intervall for neste kjøring (timer)','1','{1,5,12,24,168}');
insert into jobb_parameter values('antallOrganisasjoner','Antall Organisasjoner','1','{1,5,10,15,20,25,30,35,40,45,50}');
insert into jobb_parameter values('antallPersoner','Antall Personer','1','{1,10,15,20,25,30,35,40,45,50,1001}');
insert into jobb_parameter values('arbeidsforholdType','Arbeidsforhold Type','ordinaertArbeidsforhold','{forenkletOppgjoersordning,frilanserOppdragstakerHonorarPersonerMm,maritimtArbeidsforhold,ordinaertArbeidsforhold}');
commit;