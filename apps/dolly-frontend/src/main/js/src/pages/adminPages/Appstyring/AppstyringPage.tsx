import {AdminAccessDenied} from '@/pages/adminPages/AdminAccessDenied'
import {Alert} from '@navikt/ds-react'
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
	const [statusData , setStatusData] = useState<Jobbstatus>([]);

	let optionsData: FetchData[] = [];

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
		async function fetchStatus() {
			const data = await fetch('/testnav-levende-arbeidsforhold-scheduler/scheduler/status')
				.then(res => res.json())
				.catch(err => console.error(err));
			setStatusData(data);
		}
		fetchStatus();
	}, []);


	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{marginBottom: '15px'}}>
				Denne siden er under utvikling.
			</Alert>
			<StatusBox nesteKjoring={statusData.nesteKjoring} status={statusData.status}/>
			<AppstyringTable data={apiData} setData={setApiData}/>
		</>
	)
}