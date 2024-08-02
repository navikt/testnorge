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
	 * Callback-funksjon som behandler feil ved API-spørringer. Dersom en feilmelding ikke er spesifisert, så betyr det
	 * at feilen ved spørringen skjedde på klienten sin side og kjører med default parameter verdi
	 */
	function feilHandtering(feilMelding : string = "Spørring feilet, sjekk internettforbindelsen og prøv igjen!"){
		alert(feilMelding);
	}

	/**
	 * Funksjon som aktiverer jobb-scheduler
	 */
	async function aktiverScheduler(){

		let intervall = apiData.find(d => d.navn == "intervall");

		if (intervall){
			await sendSporringScheduler(`${SCHEDULER_DOMENE}/scheduler?intervall=${intervall.verdi}`);
		} else {
			feilHandtering("Intervall er ikke spesifisert")
		}
	}

	/**
	 * Funksjon som deaktiverer jobb-scheduler
	 */
	async function deaktiverScheduler(){
		if (window.confirm("Vil du deaktivere jobben? Du kan aktivere ny jobb igjen når som helst.")){
			await sendSporringScheduler(`${SCHEDULER_DOMENE}/scheduler/stopp`);
		}
	}

	/**
	 * Funksjon som kan brukes til å sende spørring til scheduler appen sitt API for å enten aktiver eller deaktivere
	 * scheduler basert på url parameteren
	 * @param url URL til endepunktet i scheduler APIet som spørring skal sendes til
	 */
	async function sendSporringScheduler(url : string){
		setHenterStatus(true);
		await fetch(url).then(async res => {

			setTimeout(()=>{
				if (!res.ok) {
					feilHandtering(`${res.body}`);
				}

				fetchStatusScheduler();
				setHenterStatus(false);
			}, 200)
		}).catch(feilHandtering);
	}

	/**
	 * Funksjon som sender spørring til scheduler appen sitt API for å hente ut status på om scheduleren er aktiv
	 * eller ikke
	 */
	async function fetchStatusScheduler() {
		const data = await fetch(`${SCHEDULER_DOMENE}/scheduler/status`)
			.then(res => res.json())
			.catch(feilHandtering);
		setStatusData(data);
	}

	/**
	 * Funksjon som henter ut dataen for parameterne som brukes i jobben (aka ansettelses-jobben) som kjøres av
	 * scheduleren for det gitte intervallet
	 */
	async function hentParametere() {
		await fetch(`${ANSETTELSE_DOMENE}/api`)
			.then(res => res.json())
			.then(res => {
				res.map((r: FetchData) => optionsData.push(r))

			}).catch(err => console.error(err));
		setApiData(optionsData);
	}

	useEffect(() => {
		hentParametere();
		fetchStatusScheduler();
	}, []);


	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{marginBottom: '15px'}}>
				Denne siden er under utvikling.
			</Alert>
			<StatusBox nesteKjoring={statusData.nesteKjoring} status={statusData.status}/>
			{!statusData.status ? <Button loading={henterStatus} onClick={aktiverScheduler} style={{marginBottom: '8px'}}>Aktiver</Button> : <Button loading={henterStatus} onClick={deaktiverScheduler} variant={"danger"}>Deaktiver</Button> }
			<AppstyringTable data={apiData} setData={setApiData}/>
		</>
	)
}