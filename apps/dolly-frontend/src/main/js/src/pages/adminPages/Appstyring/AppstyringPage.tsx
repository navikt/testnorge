import {AdminAccessDenied} from '@/pages/adminPages/AdminAccessDenied'
import {Alert} from '@navikt/ds-react'
import {AppstyringTable} from '@/pages/adminPages/Appstyring/AppstyringTable'
import {erDollyAdmin} from '@/utils/DollyAdmin'
import React, {useEffect, useState} from "react";
import {FetchData} from "@/pages/adminPages/Appstyring/util/Typer";

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied/>
	}

	const [apiData , setApiData] = useState<Array<FetchData>>([]);
	let optionsData: FetchData[] = [];

	useEffect(() => {
		async function fetchData() {
			const data = await fetch('/testnav-levende-arbeidsforhold-ansettelsev2/api')
				.then(res => res.json())
				.then(res => {
					res.map((r: FetchData) => optionsData.push(r))

				}).catch(err => console.error(err));
			console.log(optionsData)
			setApiData(optionsData);
		}
		fetchData();
	}, []);
	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{marginBottom: '15px'}}>
				Denne siden er under utvikling.
			</Alert>
			<AppstyringTable data={apiData} setData={setApiData}/>
		</>
	)
}
