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

	async function aktiverScheduler(){
		setHenterStatus(true);
		//Send request /scheduler
		await fetch(`/testnav-levende-arbeidsforhold-scheduler/scheduler?intervall=${apiData.find(d => d.navn == "intervall")?.verdi}`).then(res => {
			if (res.ok) {
				setTimeout(()=>{
					fetchStatusScheduler();
					setHenterStatus(false);
				}, 200)
			}
		});
	}


	async function deaktiverScheduler(){
		setHenterStatus(true);
		await fetch('/testnav-levende-arbeidsforhold-scheduler/scheduler/stopp').then(res => {
			if (res.ok) {
				//Sjekk om body er true?
				setTimeout(()=>{
					fetchStatusScheduler();
					setHenterStatus(false);
				}, 200)
			}
		});

	}

	async function fetchStatusScheduler() {
		const data = await fetch('/testnav-levende-arbeidsforhold-scheduler/scheduler/status')
			.then(res => res.json())
			.catch(err => console.error(err));
		setStatusData(data);
	}

	useEffect(() => {
		async function fetchData() {
			await fetch('/testnav-levende-arbeidsforhold-ansettelse/api')
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
			{!statusData.status ? <Button loading={henterStatus} onClick={aktiverScheduler}>Aktiver</Button> : <Button loading={henterStatus} onClick={deaktiverScheduler}>Deaktiver</Button> }
			<AppstyringTable data={apiData} setData={setApiData}/>
		</>
	)
}