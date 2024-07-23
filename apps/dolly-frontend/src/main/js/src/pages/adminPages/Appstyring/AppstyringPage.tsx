import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { Alert } from '@navikt/ds-react'
import { AppstyringTable } from '@/pages/adminPages/Appstyring/AppstyringTable'
import { erDollyAdmin } from '@/utils/DollyAdmin'
import data from "@navikt/ds-icons/src/Data";
import React, { useState, useEffect } from "react";
import Request from '@/service/services/Request'
import {JobbParametere} from "@/pages/adminPages/Appstyring/util/AppstyringParametere";

export default () => {
	if (!erDollyAdmin()) {
		return <AdminAccessDenied />
	}

	let data: {parameter: string, verdi: string, verdier: {verdi: string, navn: string}[] }[] = [];
	Request.get('https://testnav-levende-arbeidsforhold-ansettelse.intern.dev.nav.no/api').then(
		(response: JobbParametere) => {
			console.log(response);
		}
	);

	/*
	useEffect(() => {
		fetch("/testnav-levende-arbeidsforhold-ansettelse/api", {
			method: "GET"})
			.then((response) => response.json())
			.then(data => {
			for (const parameter of data) {
				let verdier: {verdi: string, navn: string}[] = [];
				for (const verdi of data.verdier) {
					verdier.push({verdi: verdi.verdi, navn: verdi.navn });
				}
				data.push({parameter: parameter.navn, verdi: parameter.verdi, verdier: verdier});
			}
		})
	.then(json=>console.log(json));
	});

	 */

	//const headers = { 'Authorization': 'Bearer ' };
	/*
	fetch('/testnav-levende-arbeidsforhold-ansettelse/api')

		.then(res=>res.json())
		.then((data) => {
			for (const parameter of data) {
				let verdier: {verdi: string, navn: string}[] = [];
				for (const verdi of data.verdier) {
					verdier.push({verdi: verdi.verdi, navn: verdi.navn });
				}
				data.push({parameter: parameter.navn, verdi: parameter.verdi, verdier: verdier});
			}
		})
		.then(json=>console.log(json));

	 */

	//TODO: Implementer henting av data fra backend
	const dataMock = [
		{ parameter: 'Parameter 1', verdi: 'verdi1' },
		{ parameter: 'Parameter 2', verdi: 'verdi2' },
		{ parameter: 'Parameter 3', verdi: 'verdi3' },
		{ parameter: 'Parameter 4', verdi: 'verdi4' },
		{ parameter: 'Parameter 5', verdi: 'verdi5' },
		{ parameter: 'Parameter 6', verdi: 'verdi6' },
		{ parameter: 'Parameter 7', verdi: 'verdi7' },
		{ parameter: 'Parameter 8', verdi: 'verdi8' },
		{ parameter: 'Parameter 9', verdi: 'verdi9' },
		{ parameter: 'Parameter 10', verdi: 'verdi10' },
	]

	return (
		<>
			<h1>App-styring</h1>
			<Alert variant={'info'} style={{ marginBottom: '15px' }}>
				Denne siden er under utvikling.
			</Alert>
			<AppstyringTable data={data} />
		</>
	)
}
