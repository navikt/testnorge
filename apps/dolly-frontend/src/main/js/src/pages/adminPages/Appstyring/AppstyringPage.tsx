import {AdminAccessDenied} from '@/pages/adminPages/AdminAccessDenied'
import {Alert} from '@navikt/ds-react'
import {AppstyringTable} from '@/pages/adminPages/Appstyring/AppstyringTable'
import {erDollyAdmin} from '@/utils/DollyAdmin'
import React, {useEffect, useState} from "react";
import {FetchData} from "@/pages/adminPages/Appstyring/util/Typer";
import { Box, VStack } from "@navikt/ds-react";

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied/>
	}

	const [apiData , setApiData] = useState<Array<FetchData>>([]);
	let optionsData: FetchData[] = [];

	const statusBox = () => {
		return (
			<VStack gap="4">
				<Box padding="4" background="surface-alt-3-subtle">

				</Box>
				<Box padding="4" background="surface-success-subtle">

				</Box>
			</VStack>
		);
	};

	useEffect(() => {
		async function fetchData() {
			await fetch('/testnav-levende-arbeidsforhold-ansettelsev2/api')
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
			console.log(data);
		}
		fetchStatus();
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