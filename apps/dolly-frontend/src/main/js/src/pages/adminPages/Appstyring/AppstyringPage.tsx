import {AdminAccessDenied} from '@/pages/adminPages/AdminAccessDenied'
import {Alert} from '@navikt/ds-react'
import {AppstyringTable} from '@/pages/adminPages/Appstyring/AppstyringTable'
import {erDollyAdmin} from '@/utils/DollyAdmin'
import React, {useEffect, useState} from "react";

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied/>
	}

	/*
	const dataMock = [
		{parameter: 'Parameter 1', verdi: 'verdi1'},
		{parameter: 'Parameter 2', verdi: 'verdi2'},
		{parameter: 'Parameter 3', verdi: 'verdi3'},
		{parameter: 'Parameter 4', verdi: 'verdi4'},
		{parameter: 'Parameter 5', verdi: 'verdi5'},
		{parameter: 'Parameter 6', verdi: 'verdi6'},
		{parameter: 'Parameter 7', verdi: 'verdi7'},
		{parameter: 'Parameter 8', verdi: 'verdi8'},
		{parameter: 'Parameter 9', verdi: 'verdi9'},
		{parameter: 'Parameter 10', verdi: 'verdi10'},
	]

	 */

	interface FetchData{
		navn: string
		tekst: string
		verdi: string
		verdier: Array<string>
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
			<AppstyringTable data={apiData}/>
		</>
	)
}
