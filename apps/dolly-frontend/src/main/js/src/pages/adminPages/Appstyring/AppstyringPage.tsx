import {AdminAccessDenied} from '@/pages/adminPages/AdminAccessDenied'
import {Alert, Button} from '@navikt/ds-react'
import {AppstyringTable} from '@/pages/adminPages/Appstyring/AppstyringTable'
import {erDollyAdmin} from '@/utils/DollyAdmin'
import React, {useEffect, useState} from "react";
import {FetchData, Jobbstatus} from "@/pages/adminPages/Appstyring/util/Typer";
import {StatusBox} from "@/pages/adminPages/Appstyring/StatusBox";

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied/>
	}

	const [apiData , setApiData] = useState<Array<FetchData>>([]);
	const [statusData , setStatusData] = useState<Jobbstatus>({nesteKjoring: "", status: false});
	const [henterStatus, setHenterStatus] = useState(false);

	let optionsData: FetchData[] = [];
	
	const SCHEDULER_DOMENE : string = "/testnav-levende-arbeidsforhold-scheduler";
	const ANSETTELSE_DOMENE : string = "/testnav-levende-arbeidsforhold-ansettelse";

	/**
	 * Funksjon som sender spørring til scheduler appen sitt API for å aktivere jobb-scheduler
	 */
	async function aktiverScheduler(){
		setHenterStatus(true);

		let intervall = apiData.find(d => d.navn == "intervall");
		if(intervall == undefined){
			alert("Finner ikke intervall");
			setHenterStatus(false);
			return;
		}
		await fetch(`${SCHEDULER_DOMENE}/scheduler?intervall=${intervall.verdi}`).then(res => {
			if (res.ok) {
				setTimeout(()=>{
					fetchStatusScheduler();
					setHenterStatus(false);
				}, 200)
			}
		});
	}

	/**
	 * Funksjon som sender spørring til scheduler appen sitt API for å deaktivere jobb-scheduler
	 */
	async function deaktiverScheduler(){

		if (window.confirm("Vil du deaktivere jobben? Du kan aktivere ny jobb igjen når som helst.")){
			setHenterStatus(true);
			await fetch(`${SCHEDULER_DOMENE}/scheduler/stopp`).then(async res => {
				if (res.ok) {
					let data = await res.json();
					if (data && !data.status){
						alert("Deaktivering av scheduler feilet, prøv igjen.");
						setHenterStatus(false);
						return;
					}

					setTimeout(()=>{
						fetchStatusScheduler();
						setHenterStatus(false);
					}, 200)
				}
			});
		}


	}

	/**
	 * Funksjon som sender spørring til scheduler appen sitt API for å hente ut status på om scheduleren er aktiv
	 * eller ikke
	 */
	async function fetchStatusScheduler() {
		const data = await fetch(`${SCHEDULER_DOMENE}/scheduler/status`)
			.then(res => res.json())
			.catch(err => console.error(err));
		setStatusData(data);
	}

	useEffect(() => {
		async function fetchData() {
			await fetch(`${ANSETTELSE_DOMENE}/api`)
				.then(res => res.json())
				.then(res => {
					res.map((r: FetchData) => optionsData.push(r))

				}).catch(err => console.error(err));
			setApiData(optionsData);
		}
		fetchData();
	}, []);

	useEffect(() => {
		fetchStatusScheduler();
	}, []);


	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{marginBottom: '15px'}}>
				Denne siden er under utvikling.
			</Alert>
			<StatusBox nesteKjoring={statusData.nesteKjoring} status={statusData.status}/>
			{!statusData.status ? <Button loading={henterStatus} onClick={aktiverScheduler}>Aktiver</Button> : <Button loading={henterStatus} onClick={deaktiverScheduler} variant={"danger"}>Deaktiver</Button> }
			<AppstyringTable data={apiData} setData={setApiData}/>
		</>
	)
}